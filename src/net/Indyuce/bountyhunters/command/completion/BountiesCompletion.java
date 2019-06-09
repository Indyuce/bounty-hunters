package net.Indyuce.bountyhunters.command.completion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class BountiesCompletion implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<>();

		if (args.length == 1) {
			list.add("titles");
			list.add("quotes");
			if (sender.hasPermission("bountyhunters.admin")) {
				list.add("help");
				list.add("reload");
				list.add("remove");
			}
		}

		if (args.length == 2)
			if (args[0].equals("remove"))
				for (Player online : Bukkit.getOnlinePlayers())
					list.add(online.getName());

		return args[args.length - 1].isEmpty() ? list : list.stream().filter(string -> string.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
	}
}
