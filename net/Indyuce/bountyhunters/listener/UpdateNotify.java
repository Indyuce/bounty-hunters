package net.Indyuce.bountyhunters.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.Indyuce.bountyhunters.BountyHunters;

public class UpdateNotify implements Listener {
	@EventHandler
	public void a(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!p.hasPermission("bountyhunters.notify-update"))
			return;

		if (BountyHunters.getSpigotPlugin().isOutOfDate())
			for (String s : BountyHunters.getSpigotPlugin().getOutOfDateMessage())
				p.sendMessage(ChatColor.GREEN + "(BountyHunters) " + s);
	}
}
