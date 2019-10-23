package net.Indyuce.bountyhunters.command;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.BountyUtils;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.BountyCommands;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.event.BountyChangeEvent;
import net.Indyuce.bountyhunters.api.event.BountyChangeEvent.BountyChangeCause;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent.BountyCause;
import net.Indyuce.bountyhunters.api.language.Message;

public class AddBountyCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("bountyhunters.add")) {
			Message.NOT_ENOUGH_PERMS.format().send(sender);
			return true;
		}
		if (args.length < 2) {
			Message.COMMAND_USAGE.format("command", "/bounty <player> <reward>").send(sender);
			return true;
		}
		if (sender instanceof Player)
			if (BountyHunters.getInstance().getConfig().getStringList("world-blacklist").contains(((Player) sender).getWorld().getName()))
				return true;

		// check for player
		Player target = Bukkit.getPlayer(args[0]);
		if (target == null || !target.isOnline()) {
			Message.ERROR_PLAYER.format("arg", args[0]).send(sender);
			return true;
		}
		if (sender instanceof Player && target.getName().equals(((Player) sender).getName())) {
			Message.CANT_SET_BOUNTY_ON_YOURSELF.format().send(sender);
			return true;
		}

		// permission
		if (target.hasPermission("bountyhunters.immunity") && !sender.hasPermission("bountyhunters.immunity.bypass")) {
			Message.BOUNTY_IMUN.format().send(sender);
			return true;
		}

		// reward
		double reward = 0;
		try {
			reward = Double.parseDouble(args[1]);
		} catch (NumberFormatException exception) {
			Message.NOT_VALID_NUMBER.format("arg", args[1]).send(sender);
			return true;
		}
		reward = BountyUtils.truncate(reward, 1);

		/*
		 * minimum and maximum checks do not apply for console.
		 */
		CommandArguments arguments = new CommandArguments(args);
		if (!sender.hasPermission("bountyhunters.admin") || !arguments.bypassMinMax) {
			double min = BountyHunters.getInstance().getConfig().getDouble("min-reward");
			double max = BountyHunters.getInstance().getConfig().getDouble("max-reward");
			if (reward < min) {
				Message.REWARD_MUST_BE_HIGHER.format("min", new NumberFormat().format(min)).send(sender);
				return true;
			}
			if (max > 0 && reward > max) {
				Message.REWARD_MUST_BE_LOWER.format("max", new NumberFormat().format(max)).send(sender);
				return true;
			}
		}

		// set restriction
		if (sender instanceof Player) {
			Player player = (Player) sender;
			long restriction = BountyHunters.getInstance().getConfig().getInt("bounty-set-restriction") * 1000;
			long left = BountyHunters.getInstance().getPlayerDataManager().get(player).getLastBounty() + restriction - System.currentTimeMillis();

			if (left > 0) {
				Message.BOUNTY_SET_RESTRICTION.format("left", "" + left / 1000, "s", left / 1000 > 1 ? "s" : "").send(sender);
				return true;
			}
		}

		// money restriction
		if (sender instanceof Player)
			if (!BountyHunters.getInstance().getEconomy().has((Player) sender, reward)) {
				Message.NOT_ENOUGH_MONEY.format().send(sender);
				return true;
			}

		/*
		 * right now, bounty will be registered if the event is not cancelled.
		 * calculate tax and update reward
		 */
		double tax = Math.max(0, Math.min(1, BountyHunters.getInstance().getConfig().getDouble("bounty-tax.bounty-creation") / 100));
		if (!sender.hasPermission("bountyhunters.admin") || !arguments.noTax)
			reward *= 1 - tax;

		// add to existing bounty
		Optional<Bounty> currentBounty = BountyHunters.getInstance().getBountyManager().getBounty(target);
		if (currentBounty.isPresent()) {

			// API
			Bounty bounty = currentBounty.get();
			BountyChangeEvent bountyEvent = new BountyChangeEvent(bounty, sender instanceof Player ? BountyChangeCause.PLAYER : BountyChangeCause.CONSOLE);
			Bukkit.getPluginManager().callEvent(bountyEvent);
			if (bountyEvent.isCancelled())
				return true;

			// remove balance
			// set last bounty value
			if (sender instanceof Player) {
				BountyHunters.getInstance().getEconomy().withdrawPlayer((Player) sender, BountyUtils.truncate(reward / (1 - tax), 1));
				BountyHunters.getInstance().getPlayerDataManager().get((Player) sender).setLastBounty();
			}

			new BountyCommands("increase." + bountyEvent.getCause().name().toLowerCase().replace("_", "-"), bounty, sender).send();
			if (sender instanceof Player)
				bounty.addContribution((Player) sender, reward);
			else
				bounty.addReward(reward);
			bountyEvent.sendAllert();
			return true;
		}

		// API
		Bounty bounty = new Bounty(sender instanceof Player ? (Player) sender : null, target, reward);
		BountyCreateEvent bountyEvent = new BountyCreateEvent(bounty, sender instanceof Player ? (Player) sender : null, sender instanceof Player ? BountyCause.PLAYER : BountyCause.CONSOLE);
		Bukkit.getPluginManager().callEvent(bountyEvent);
		if (bountyEvent.isCancelled())
			return true;

		// remove balance
		// set last bounty value
		if (sender instanceof Player) {
			BountyHunters.getInstance().getEconomy().withdrawPlayer((Player) sender, BountyUtils.truncate(reward / (1 - tax), 1));
			BountyHunters.getInstance().getPlayerDataManager().get((Player) sender).setLastBounty();
		}

		new BountyCommands("place." + bountyEvent.getCause().name().toLowerCase().replace("_", "-"), bounty, sender).send();
		BountyHunters.getInstance().getBountyManager().registerBounty(bounty);
		bountyEvent.sendAllert();

		if (tax > 0)
			Message.TAX_EXPLAIN.format("percent", "" + BountyUtils.truncate(tax * 100, 1), "price", new NumberFormat().format(tax * bounty.getReward())).send(sender);
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
