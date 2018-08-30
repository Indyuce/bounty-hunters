package net.Indyuce.bountyhunters.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.version.SpigotPlugin;

public class UpdateNotify implements Listener {
	@EventHandler
	public void a(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!p.hasPermission("bountyhunters.notify-update"))
			return;
		
		SpigotPlugin spigotPlugin = new SpigotPlugin(BountyHunters.plugin, 40610);
		if (spigotPlugin.isOutOfDate())
			for (String s : spigotPlugin.getOutOfDateMessage())
				p.sendMessage(ChatColor.GREEN + "(BountyHunters) " + s);
	}
}
