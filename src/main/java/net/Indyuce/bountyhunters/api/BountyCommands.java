package net.Indyuce.bountyhunters.api;

import net.Indyuce.bountyhunters.BountyHunters;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BountyCommands {
	private final List<String> commands;
	private final CommandSender sender;
	private final Bounty bounty;

	public BountyCommands(String path, Bounty bounty, CommandSender sender) {
		this(BountyHunters.getInstance().getConfig().getStringList("bounty-commands." + path), bounty, sender);
	}

	public BountyCommands(List<String> commands, Bounty bounty, CommandSender sender) {
		this.commands = commands;
		this.bounty = bounty;
		this.sender = sender;
	}

	public void send() {
		for (String command : commands) {
			if (bounty.hasCreator())
				command = command.replace("{creator}", bounty.getCreator().getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), BountyHunters.getInstance().getPlaceholderParser().parse(null, command.replace("{target}", bounty.getTarget().getName()).replace("{player}", sender.getName())));
		}
	}
}
