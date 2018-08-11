package net.Indyuce.bh.command.completion;

import java.util.ArrayList;
import java.util.List;

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
			list.add("compass");
			if (p.hasPermission("bountyhunters.op")) {
				list.add("help");
				list.add("reload");
			}
		}

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
