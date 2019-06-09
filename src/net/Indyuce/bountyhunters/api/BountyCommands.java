package net.Indyuce.bountyhunters.api;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.Indyuce.bountyhunters.BountyHunters;

public class BountyCommands {
	private List<String> commands;
	private OfflinePlayer target, killer;

	public BountyCommands(String path, OfflinePlayer target, OfflinePlayer killer) {
		this(BountyHunters.plugin.getConfig().getStringList("bounty-commands." + path), target, killer);
	}

	public BountyCommands(List<String> commands, OfflinePlayer target, OfflinePlayer killer) {
		this.commands = commands;
		this.target = target;
		this.killer = killer;
	}

	public void send(OfflinePlayer player) {
		for (String command : commands)
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), BountyHunters.getPlaceholderParser().parse(player, command).replace("%target%", target.getName()).replace("%killer%", killer.getName()));
	}
}
