package net.Indyuce.bountyhunters.command.completion;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class BountiesCompletion implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return null;

		Player p = (Player) sender;
		List<String> list = new ArrayList<String>();

		if (args.length == 1) {
			list.add("leaderboard");
			list.add("titles");
			list.add("quotes");
			if (p.hasPermission("bountyhunters.admin")) {
				list.add("help");
				list.add("reload");
				list.add("remove");
			}
		}

		if (args.length == 2)
			if (args[0].equals("remove"))
				for (Player player : Bukkit.getOnlinePlayers())
					list.add(player.getName());

		if (!args[args.length - 1].isEmpty()) {
			List<String> newList = new ArrayList<String>();
			for (String s : list)
				if (s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					newList.add(s);
			list = newList;
		}

		return list;
	}
}
