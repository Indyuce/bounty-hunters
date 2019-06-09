package net.Indyuce.bountyhunters.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.gui.Leaderboard;

public class HuntersCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is for players only.");
			return true;
		}
		
		if (!sender.hasPermission("bountyhunters.leaderboard")) {
			Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(sender);
			return true;
		}

		new Leaderboard((Player) sender).open();
		return true;
	}
}
