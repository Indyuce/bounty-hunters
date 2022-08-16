package net.Indyuce.bountyhunters.command.completion;

import net.Indyuce.bountyhunters.BountyHunters;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BountiesCompletion implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<>();

		if (args.length == 1) {
			list.add("titles");
			list.add("animations");
			if (sender.hasPermission("bountyhunters.admin")) {
				list.add("edit");
				list.add("remove");
				list.add("reload");
				list.add("help");
			}
		}

		if (args.length == 2)
			if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit"))
				BountyHunters.getInstance().getBountyManager().getBounties().forEach(bounty -> list.add(bounty.getTarget().getName()));

		return args[args.length - 1].isEmpty() ? list
				: list.stream().filter(string -> string.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
	}
}
