package net.Indyuce.bountyhunters.api;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.Indyuce.bountyhunters.BountyHunters;

public class BountyCommands {
	private List<String> commands;
	private OfflinePlayer killer;
	private Bounty bounty;

	public BountyCommands(String path, Bounty bounty, OfflinePlayer killer) {
		this(BountyHunters.getInstance().getConfig().getStringList("bounty-commands." + path), bounty, killer);
	}

	public BountyCommands(List<String> commands, Bounty bounty, OfflinePlayer killer) {
		this.commands = commands;
		this.bounty = bounty;
		this.killer = killer;
	}

	public void send(OfflinePlayer player) {
		for (String command : commands) {
			if (bounty.hasCreator())
				command = command.replace("%creator%", bounty.getCreator().getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), BountyHunters.getInstance().getPlaceholderParser().parse(player, command.replace("%target%", bounty.getTarget().getName()).replace("%killer%", killer.getName())));
		}
	}
}
