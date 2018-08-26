package net.Indyuce.bh.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.Indyuce.bh.ConfigData;
import net.Indyuce.bh.Main;
import net.Indyuce.bh.api.Bounty;
import net.Indyuce.bh.api.PlayerData;
import net.Indyuce.bh.gui.BountyList;
import net.Indyuce.bh.gui.Leaderboard;
import net.Indyuce.bh.resource.CustomItem;
import net.Indyuce.bh.resource.Message;
import net.Indyuce.bh.util.Utils;
import net.Indyuce.bh.util.VersionUtils;

public class BountiesCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String commandLabel, String[] args) {

		// open bounties menu
		if (args.length < 1) {
			if (!Main.plugin.checkPl(sender, true))
				return true;

			if (!sender.hasPermission("bountyhunters.gui")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
				return true;
			}

			new BountyList((Player) sender, 1).open();
			return true;
		}

		// help
		if (args[0].equalsIgnoreCase("help")) {
			if (!sender.hasPermission("bountyhunters.op")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
				return true;
			}

			sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-----------------[" + ChatColor.LIGHT_PURPLE + " BountyHunters Help " + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "]-----------------");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounty <player> <reward>" + ChatColor.WHITE + " sets a bounty on a player.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties" + ChatColor.WHITE + " shows current bounties.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties leaderboard/lb" + ChatColor.WHITE + " opens the hunter leaderboard.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties compass" + ChatColor.WHITE + " resets the compass target location.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties quotes" + ChatColor.WHITE + " lists availabel quotes.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties titles" + ChatColor.WHITE + " lists available titles.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties reload" + ChatColor.WHITE + " reloads the config file.");
			return true;
		}

		// testcommand
		if (args[0].equalsIgnoreCase("testremove") && sender.getName().equals("Indyuce")) {
			new ArrayList<Bounty>(Main.getBountyManager().getBounties()).get(0).unregister();
		}

		// leaderboard
		if (args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("lb")) {
			if (!Main.plugin.checkPl(sender, true))
				return true;
			if (!sender.hasPermission("bountyhunters.leaderboard-gui")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
				return true;
			}

			new Leaderboard((Player) sender).open();
		}

		// reset compass location
		if (args[0].equalsIgnoreCase("compass")) {
			if (!Main.plugin.checkPl(sender, true))
				return true;

			Player p = (Player) sender;
			p.setCompassTarget(p.getBedSpawnLocation() == null ? p.getWorld().getSpawnLocation() : p.getBedSpawnLocation());
			Message.TRACKING_COMPASS_RESET.format(ChatColor.YELLOW).send(p);
		}

		// choose title
		if (args[0].equalsIgnoreCase("title")) {
			if (!Main.plugin.checkPl(sender, true))
				return true;

			Player p = (Player) sender;
			if (!sender.hasPermission("bountyhunters.title-cmd")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
				return true;
			}

			if (args.length < 2)
				return true;

			int index = 0;
			try {
				index = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				return true;
			}

			PlayerData playerData = PlayerData.get(p);
			String select = playerData.getUnlocked().get(index);
			playerData.set("current-title", select);
			playerData.save();
			VersionUtils.sound(p, "ENTITY_PLAYER_LEVELUP", 1, 2);
			Message.SUCCESSFULLY_SELECTED.format(ChatColor.YELLOW, "%item%", Utils.applySpecialChars(select)).send(p);
		}

		// choose quote
		if (args[0].equalsIgnoreCase("quote")) {
			if (!Main.plugin.checkPl(sender, true))
				return true;

			Player p = (Player) sender;
			if (!p.hasPermission("bountyhunters.quote-cmd")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
				return true;
			}

			if (args.length < 2)
				return true;

			int index = 0;
			try {
				index = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				return true;
			}

			PlayerData playerData = PlayerData.get(p);
			String select = playerData.getUnlocked().get(index);
			playerData.set("current-quote", select);
			playerData.save();
			VersionUtils.sound(p, "ENTITY_PLAYER_LEVELUP", 1, 2);
			Message.SUCCESSFULLY_SELECTED.format(ChatColor.YELLOW, "%item%", Utils.applySpecialChars(select)).send(p);
		}

		// choose title
		if (args[0].equalsIgnoreCase("titles")) {
			if (!Main.plugin.checkPl(sender, true))
				return true;

			Player p = (Player) sender;
			if (!p.hasPermission("bountyhunters.title-cmd")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(p);
				return true;
			}

			Message.CHAT_BAR.format(ChatColor.YELLOW).send(p);
			Message.UNLOCKED_TITLES.format(ChatColor.YELLOW).send(p);
			FileConfiguration levels = ConfigData.getCD(Main.plugin, "", "levels");
			FileConfiguration config = ConfigData.getCD(Main.plugin, "/userdata", p.getUniqueId().toString());

			List<String> unlocked = config.getStringList("unlocked");
			for (String s : levels.getConfigurationSection("reward.title").getKeys(false)) {
				String title = levels.getString("reward.title." + s);
				if (unlocked.contains(title))
					Main.json.message((Player) sender, "{\"text\":\"* " + ChatColor.GREEN + Utils.applySpecialChars(title) + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bounties title " + unlocked.indexOf(title) + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Message.CLICK_SELECT.getUpdated() + "\",\"color\":\"white\"}]}}}");
			}
		}

		// quotes list
		if (args[0].equalsIgnoreCase("quotes")) {
			if (!Main.plugin.checkPl(sender, true))
				return true;
			Player p = (Player) sender;
			if (!p.hasPermission("bountyhunters.quote-cmd")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(p);
				return true;
			}

			Message.CHAT_BAR.format(ChatColor.YELLOW).send(p);
			Message.UNLOCKED_QUOTES.format(ChatColor.YELLOW).send(p);
			FileConfiguration levels = ConfigData.getCD(Main.plugin, "", "levels");
			FileConfiguration config = ConfigData.getCD(Main.plugin, "/userdata", p.getUniqueId().toString());

			List<String> unlocked = config.getStringList("unlocked");
			for (String s : levels.getConfigurationSection("reward.quote").getKeys(false)) {
				String title = levels.getString("reward.quote." + s);
				if (unlocked.contains(title))
					Main.json.message((Player) sender, "{\"text\":\"* " + ChatColor.GREEN + title + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bounties quote " + unlocked.indexOf(title) + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Message.CLICK_SELECT.getUpdated() + "\",\"color\":\"white\"}]}}}");
			}
		}

		// reload plugin
		if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("bountyhunters.op")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
				return true;
			}
			Main.plugin.reloadConfig();

			FileConfiguration items = ConfigData.getCD(Main.plugin, "/language", "items");
			for (CustomItem i : CustomItem.values())
				i.update(items);

			sender.sendMessage(ChatColor.YELLOW + Main.plugin.getName() + " " + Main.plugin.getDescription().getVersion() + " reloaded.");
		}

		return false;
	}

}
