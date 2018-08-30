package net.Indyuce.bountyhunters.command.completion;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class AddBountyCompletion implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();

		if (args.length == 1)
			for (Player t : Bukkit.getOnlinePlayers())
				list.add(t.getName());

		String lastArg = args[args.length - 1];
		if (!lastArg.isEmpty()) {
			List<String> newList = new ArrayList<String>();
			for (String s : list)
				if (s.toLowerCase().startsWith(lastArg.toLowerCase()))
					newList.add(s);
			list = newList;
		}

		return list;
	}
}
