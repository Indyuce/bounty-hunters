package net.Indyuce.bountyhunters.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.gui.RedeemableHeads;

public class RedeemBountyHeadsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command is only for players.");
			return false;
		}

		new RedeemableHeads((Player) sender).open();
		return true;
	}
}
