package net.Indyuce.bountyhunters.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.player.PlayerData;

public class PlayerListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(PlayerJoinEvent event) {
		BountyHunters.getInstance().getPlayerDataManager().load(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void b(PlayerQuitEvent event) {
		PlayerData playerData = BountyHunters.getInstance().getPlayerDataManager().get(event.getPlayer());
		playerData.saveFile();
		BountyHunters.getInstance().getPlayerDataManager().unload(playerData.getUniqueId());
	}

	@EventHandler
	public void c(PlayerDeathEvent event) {
		BountyHunters.getInstance().getPlayerDataManager().get(event.getEntity()).resetStreaks();
	}
}
