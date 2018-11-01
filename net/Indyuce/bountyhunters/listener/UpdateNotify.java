package net.Indyuce.bountyhunters.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.Indyuce.bountyhunters.BountyHunters;

public class UpdateNotify implements Listener {
	@EventHandler
	public void a(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("bountyhunters.notify-update"))
			if (BountyHunters.getSpigotPlugin().isOutOfDate())
				for (String s : BountyHunters.getSpigotPlugin().getOutOfDateMessage())
					player.sendMessage(ChatColor.GREEN + "(BountyHunters) " + s);
	}
}
