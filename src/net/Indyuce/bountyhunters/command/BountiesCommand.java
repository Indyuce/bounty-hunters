package net.Indyuce.bountyhunters.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.ConfigFile;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.PlayerData;
import net.Indyuce.bountyhunters.api.event.BountyExpireEvent;
import net.Indyuce.bountyhunters.api.event.BountyExpireEvent.BountyExpireCause;
import net.Indyuce.bountyhunters.gui.BountyList;
import net.Indyuce.bountyhunters.manager.LevelManager.DeathQuote;
import net.Indyuce.bountyhunters.manager.LevelManager.Title;

public class BountiesCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		// open bounties menu
		if (args.length < 1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is for players only.");
				return true;
			}

			if (!sender.hasPermission("bountyhunters.list")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
				return true;
			}

			new BountyList((Player) sender).open();
			return true;
		}

		// help
		if (args[0].equalsIgnoreCase("help")) {
			if (!sender.hasPermission("bountyhunters.admin")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
				return true;
			}

			sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-----------------[" + ChatColor.LIGHT_PURPLE + " BountyHunters Help " + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "]-----------------");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Player Commands");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounty <player> <reward>" + ChatColor.WHITE + " sets a bounty on a player.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties" + ChatColor.WHITE + " shows current bounties.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/hunters" + ChatColor.WHITE + " opens the hunter leaderboard.");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties quotes" + ChatColor.WHITE + " lists available quotes.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties titles" + ChatColor.WHITE + " lists available titles.");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Admin");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties help" + ChatColor.WHITE + " shows the help page.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties reload" + ChatColor.WHITE + " reloads the config file.");
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties remove <player>" + ChatColor.WHITE + " removes a bounty.");
			return true;
		}

		// remove bounty
		if (args[0].equalsIgnoreCase("remove")) {
			if (!sender.hasPermission("bountyhunters.admin")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
				return true;
			}

			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: /bounties remove <player>");
				return true;
			}

			Player player = Bukkit.getPlayer(args[1]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Couldn't find the player called " + args[1] + ".");
				return true;
			}

			if (!BountyHunters.getInstance().getBountyManager().hasBounty(player)) {
				sender.sendMessage(ChatColor.RED + player.getName() + " does not have any bounty on him.");
				return true;
			}

			Bounty bounty = BountyHunters.getInstance().getBountyManager().getBounty(player);
			BountyExpireEvent bountyEvent = new BountyExpireEvent(bounty, BountyExpireCause.ADMIN);
			Bukkit.getPluginManager().callEvent(bountyEvent);
			if (bountyEvent.isCancelled())
				return true;

			BountyHunters.getInstance().getBountyManager().unregisterBounty(bounty);
		}

		// choose title
		if (args[0].equalsIgnoreCase("title") && args.length > 1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is for players only.");
				return true;
			}

			Player player = (Player) sender;
			if (!sender.hasPermission("bountyhunters.title")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
				return true;
			}

			if (!BountyHunters.getInstance().getLevelManager().hasTitle(args[1]))
				return true;

			PlayerData playerData = BountyHunters.getInstance().getPlayerDataManager().get(player);
			if (!playerData.canSelectItem())
				return true;

			Title item = BountyHunters.getInstance().getLevelManager().getTitle(args[1]);
			if (!playerData.hasUnlocked(item))
				return true;

			playerData.setTitle(item);
			playerData.setLastSelect();
			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
			Message.SUCCESSFULLY_SELECTED.format(ChatColor.YELLOW, "%item%", playerData.getTitle().format()).send(player);
		}

		// choose quote
		if (args[0].equalsIgnoreCase("quote") && args.length > 1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is for players only.");
				return true;
			}

			Player player = (Player) sender;
			if (!player.hasPermission("bountyhunters.quote")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
				return true;
			}

			if (!BountyHunters.getInstance().getLevelManager().hasQuote(args[1]))
				return true;

			PlayerData playerData = BountyHunters.getInstance().getPlayerDataManager().get(player);
			if (!playerData.canSelectItem())
				return true;

			DeathQuote item = BountyHunters.getInstance().getLevelManager().getQuote(args[1]);
			if (!playerData.hasUnlocked(item))
				return true;

			playerData.setQuote(item);
			playerData.setLastSelect();
			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
			Message.SUCCESSFULLY_SELECTED.format(ChatColor.YELLOW, "%item%", playerData.getQuote().format()).send(player);
		}

		// choose title
		if (args[0].equalsIgnoreCase("titles")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is for players only.");
				return true;
			}

			Player player = (Player) sender;
			if (!player.hasPermission("bountyhunters.title")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(player);
				return true;
			}

			Message.CHAT_BAR.format(ChatColor.YELLOW).send(player);
			Message.UNLOCKED_TITLES.format(ChatColor.YELLOW).send(player);

			PlayerData playerData = BountyHunters.getInstance().getPlayerDataManager().get(player);
			for (Title title : BountyHunters.getInstance().getLevelManager().getTitles()) {
				if (playerData.hasUnlocked(title))
					BountyHunters.getInstance().getVersionWrapper().sendJson((Player) sender, "{\"text\":\"* " + ChatColor.GREEN + title.format() + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bounties title " + title.getId() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Message.CLICK_SELECT.getMessage() + "\",\"color\":\"white\"}]}}}");
			}
		}

		// quotes list
		if (args[0].equalsIgnoreCase("quotes")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command is for players only.");
				return true;
			}

			Player player = (Player) sender;
			if (!player.hasPermission("bountyhunters.quote")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(player);
				return true;
			}

			Message.CHAT_BAR.format(ChatColor.YELLOW).send(player);
			Message.UNLOCKED_QUOTES.format(ChatColor.YELLOW).send(player);

			PlayerData playerData = BountyHunters.getInstance().getPlayerDataManager().get(player);
			for (DeathQuote quote : BountyHunters.getInstance().getLevelManager().getQuotes()) {
				if (playerData.hasUnlocked(quote))
					BountyHunters.getInstance().getVersionWrapper().sendJson((Player) sender, "{\"text\":\"* " + ChatColor.GREEN + quote.format() + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bounties quote " + quote.getId() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Message.CLICK_SELECT.getMessage() + "\",\"color\":\"white\"}]}}}");
			}
		}

		// reload plugin
		if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("bountyhunters.admin")) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
				return true;
			}

			BountyHunters.getInstance().reloadConfig();
			BountyHunters.getInstance().reloadConfigFiles();
			BountyHunters.getInstance().getLevelManager().reload(new ConfigFile("levels").getConfig());

			FileConfiguration items = new ConfigFile("/language", "items").getConfig();
			for (CustomItem item : CustomItem.values())
				item.update(items.getConfigurationSection(item.name()));

			sender.sendMessage(ChatColor.YELLOW + BountyHunters.getInstance().getName() + " " + BountyHunters.getInstance().getDescription().getVersion() + " reloaded.");
		}

		return false;
	}

}
