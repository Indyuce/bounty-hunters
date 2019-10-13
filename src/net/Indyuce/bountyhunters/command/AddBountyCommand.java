package net.Indyuce.bountyhunters.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.BountyUtils;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.BountyCommands;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.event.BountyChangeEvent;
import net.Indyuce.bountyhunters.api.event.BountyChangeEvent.BountyChangeCause;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent.BountyCause;
import net.Indyuce.bountyhunters.manager.BountyManager;

public class AddBountyCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("bountyhunters.add")) {
			Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
			return true;
		}
		if (args.length < 2) {
			Message.COMMAND_USAGE.format(ChatColor.RED, "%command%", "/bounty <player> <reward>").send(sender);
			return true;
		}
		if (sender instanceof Player)
			if (BountyHunters.getInstance().getConfig().getStringList("world-blacklist").contains(((Player) sender).getWorld().getName()))
				return true;

		// check for player
		Player target = Bukkit.getPlayer(args[0]);
		if (target == null) {
			Message.ERROR_PLAYER.format(ChatColor.RED, "%arg%", args[0]).send(sender);
			return true;
		}
		if (!target.isOnline()) {
			Message.ERROR_PLAYER.format(ChatColor.RED, "%arg%", args[0]).send(sender);
			return true;
		}
		if (sender instanceof Player)
			if (target.getName().equals(((Player) sender).getName())) {
				Message.CANT_SET_BOUNTY_ON_YOURSELF.format(ChatColor.RED).send(sender);
				return true;
			}

		// permission
		if (target.hasPermission("bountyhunters.immunity") && !sender.hasPermission("bountyhunters.immunity.bypass")) {
			Message.BOUNTY_IMUN.format(ChatColor.RED).send(sender);
			return true;
		}

		// reward
		double reward = 0;
		try {
			reward = Double.parseDouble(args[1]);
		} catch (NumberFormatException exception) {
			Message.NOT_VALID_NUMBER.format(ChatColor.RED, "%arg%", args[1]).send(sender);
			return true;
		}
		reward = BountyUtils.truncation(reward, 1);

		/*
		 * minimum and maximum checks do not apply for console.
		 */
		CommandArguments arguments = new CommandArguments(args);
		if (!(sender instanceof ConsoleCommandSender) || !arguments.bypassMinMax) {
			double min = BountyHunters.getInstance().getConfig().getDouble("min-reward");
			double max = BountyHunters.getInstance().getConfig().getDouble("max-reward");
			if (reward < min) {
				Message.REWARD_MUST_BE_HIGHER.format(ChatColor.RED, "%min%", new NumberFormat().format(min)).send(sender);
				return true;
			}
			if (max > 0 && reward > max) {
				Message.REWARD_MUST_BE_LOWER.format(ChatColor.RED, "%max%", new NumberFormat().format(max)).send(sender);
				return true;
			}
		}

		// tax calculation
		double tax = sender instanceof ConsoleCommandSender && arguments.noTax ? 0 : BountyUtils.truncation(reward * BountyHunters.getInstance().getConfig().getDouble("tax") / 100, 1);

		// set restriction
		if (sender instanceof Player) {
			Player player = (Player) sender;
			long restriction = BountyHunters.getInstance().getConfig().getInt("bounty-set-restriction") * 1000;
			long left = BountyHunters.getInstance().getPlayerDataManager().get(player).getLastBounty() + restriction - System.currentTimeMillis();

			if (left > 0) {
				Message.BOUNTY_SET_RESTRICTION.format(ChatColor.RED, "%left%", "" + left / 1000, "%s%", left / 1000 > 1 ? "s" : "").send(sender);
				return true;
			}
		}

		// money restriction
		if (sender instanceof Player)
			if (!BountyHunters.getInstance().getEconomy().has((Player) sender, reward)) {
				Message.NOT_ENOUGH_MONEY.format(ChatColor.RED).send(sender);
				return true;
			}

		// bounty can be created
		BountyManager bountyManager = BountyHunters.getInstance().getBountyManager();
		reward -= tax;

		// add to existing bounty
		if (bountyManager.hasBounty(target)) {

			// API
			Bounty bounty = bountyManager.getBounty(target);
			BountyChangeEvent bountyEvent = new BountyChangeEvent(bounty, sender instanceof Player ? BountyChangeCause.PLAYER : BountyChangeCause.CONSOLE);
			Bukkit.getPluginManager().callEvent(bountyEvent);
			if (bountyEvent.isCancelled())
				return true;

			// remove balance
			// set last bounty value
			if (sender instanceof Player) {
				BountyHunters.getInstance().getEconomy().withdrawPlayer((Player) sender, reward + tax);
				BountyHunters.getInstance().getPlayerDataManager().get((OfflinePlayer) sender).setLastBounty();
			}

			new BountyCommands("increase." + bountyEvent.getCause().name().toLowerCase().replace("_", "-"), bounty, sender).send();
			bounty.addToReward(sender instanceof Player ? (Player) sender : null, reward);
			bountyEvent.sendAllert();
			return true;
		}

		// API
		Bounty bounty = new Bounty(sender instanceof Player ? (Player) sender : null, target, reward);
		BountyCreateEvent bountyEvent = new BountyCreateEvent(bounty, sender instanceof Player ? BountyCause.PLAYER : BountyCause.CONSOLE);
		Bukkit.getPluginManager().callEvent(bountyEvent);
		reward = bountyEvent.getBounty().getReward();
		if (bountyEvent.isCancelled())
			return true;

		// remove balance
		// set last bounty value
		if (sender instanceof Player) {
			BountyHunters.getInstance().getEconomy().withdrawPlayer((Player) sender, reward + tax);
			BountyHunters.getInstance().getPlayerDataManager().get((OfflinePlayer) sender).setLastBounty();
		}

		new BountyCommands("place." + bountyEvent.getCause().name().toLowerCase().replace("_", "-"), bounty, sender).send();
		BountyHunters.getInstance().getBountyManager().registerBounty(bounty);
		bountyEvent.sendAllert();

		if (tax > 0)
			Message.TAX_EXPLAIN.format(ChatColor.RED, "%percent%", "" + BountyHunters.getInstance().getConfig().getDouble("tax"), "%price%", new NumberFormat().format(tax)).send(sender);
		return true;
	}

	public class CommandArguments {
		final boolean bypassMinMax, noTax;

		public CommandArguments(String[] args) {
			bypassMinMax = has(args, "bmm");
			noTax = has(args, "nt");
		}

		private boolean has(String[] args, String arg) {
			for (String checked : args)
				if (checked.equalsIgnoreCase("-" + "arg"))
					return true;
			return false;
		}
	}
}
