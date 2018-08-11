package net.Indyuce.bh.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.Indyuce.bh.Main;
import net.Indyuce.bh.api.Bounty;
import net.Indyuce.bh.api.BountyManager;
import net.Indyuce.bh.api.event.BountyChangeEvent;
import net.Indyuce.bh.api.event.BountyCreateEvent;
import net.Indyuce.bh.listener.Alerts;
import net.Indyuce.bh.resource.BountyCause;
import net.Indyuce.bh.util.Utils;

public class AddBountyCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("bountyhunters.add")) {
			sender.sendMessage(ChatColor.RED + Utils.msg("not-enough-perms"));
			return true;
		}
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + Utils.msg("command-usage").replace("%command%", "/bounty <player> <reward>"));
			return true;
		}
		if (sender instanceof Player)
			if (Main.plugin.getConfig().getStringList("world-blacklist").contains(((Player) sender).getWorld().getName()))
				return true;

		// check for player
		Player t = Bukkit.getPlayer(args[0]);
		if (t == null) {
			sender.sendMessage(ChatColor.RED + Utils.msg("error-player").replace("%arg%", args[0]));
			return true;
		}
		if (!t.isOnline()) {
			sender.sendMessage(ChatColor.RED + Utils.msg("error-player").replace("%arg%", args[0]));
			return true;
		}
		if (sender instanceof Player)
			if (t.getName().equals(((Player) sender).getName())) {
				sender.sendMessage(ChatColor.RED + Utils.msg("cant-set-bounty-on-yourself"));
				return true;
			}

		// permission
		if (t.hasPermission("bountyhunters.imun") && !sender.hasPermission("bountyhunters.bypass-imun")) {
			sender.sendMessage(ChatColor.RED + Utils.msg("bounty-imun"));
			return true;
		}

		// reward
		double reward = 0;
		try {
			reward = Double.parseDouble(args[1]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + Utils.msg("not-valid-number").replace("%arg%", args[1]));
			return true;
		}
		reward = Utils.truncation(reward, 1);

		// min/max check
		double min = Main.plugin.getConfig().getDouble("min-reward");
		double max = Main.plugin.getConfig().getDouble("max-reward");
		if ((reward < min) || (max > 0 && reward > max)) {
			sender.sendMessage(ChatColor.RED + Utils.msg("wrong-reward").replace("%max%", Utils.format(max)).replace("%min%", Utils.format(min)));
			return true;
		}

		// tax calculation
		double tax = reward * Main.plugin.getConfig().getDouble("tax") / 100;
		tax = Utils.truncation(tax, 1);

		// set restriction
		if (sender instanceof Player) {
			Player p = (Player) sender;
			long restriction = Main.plugin.getConfig().getInt("bounty-set-restriction") * 1000;
			long last = Main.plugin.lastBounty.containsKey(p.getUniqueId()) ? Main.plugin.lastBounty.get(p.getUniqueId()) : 0;
			long left = last + restriction - System.currentTimeMillis();

			if (left > 0) {
				sender.sendMessage(ChatColor.RED + Utils.msg("bounty-set-restriction").replace("%left%", "" + left / 1000).replace("%s%", left / 1000 >= 2 ? "s" : ""));
				return true;
			}
		}

		// money restriction
		if (sender instanceof Player)
			if (!Main.getEconomy().has((Player) sender, reward)) {
				sender.sendMessage(ChatColor.RED + Utils.msg("not-enough-money"));
				return true;
			}

		// bounty can be created
		BountyManager bountyManager = Main.getBountyManager();
		reward -= tax;

		// add to existing bounty
		if (bountyManager.hasBounty(t)) {

			// API
			Bounty bounty = bountyManager.getBounty(t);
			BountyChangeEvent e = new BountyChangeEvent(bounty);
			Bukkit.getPluginManager().callEvent(e);
			if (e.isCancelled())
				return true;

			// remove balance
			// set last bounty value
			if (sender instanceof Player) {
				Main.getEconomy().withdrawPlayer((Player) sender, reward + tax);
				Main.plugin.lastBounty.put(((Player) sender).getUniqueId(), System.currentTimeMillis());
			}

			bounty.addToReward(sender instanceof Player ? (Player) sender : null, reward);
			for (Player ent : Bukkit.getOnlinePlayers())
				ent.sendMessage(ChatColor.YELLOW + Utils.msg("bounty-change").replace("%player%", t.getName()).replace("%reward%", Utils.format(bounty.getReward())));
			return true;
		}

		// API
		Bounty bounty = new Bounty(sender instanceof Player ? (Player) sender : null, t, reward);
		BountyCreateEvent e = new BountyCreateEvent(bounty, sender instanceof Player ? BountyCause.PLAYER : BountyCause.CONSOLE);
		Bukkit.getPluginManager().callEvent(e);
		reward = e.getBounty().getReward();
		if (e.isCancelled())
			return true;

		// remove balance
		// set last bounty value
		if (sender instanceof Player) {
			Main.getEconomy().withdrawPlayer((Player) sender, reward + tax);
			Main.plugin.lastBounty.put(((Player) sender).getUniqueId(), System.currentTimeMillis());
		}

		bounty.register();
		Alerts.newBounty(e);

		if (tax > 0)
			sender.sendMessage(ChatColor.RED + Utils.msg("tax-explain").replace("%percent%", "" + Main.plugin.getConfig().getDouble("tax")).replace("%price%", Utils.format(tax)));
		return true;
	}
}
