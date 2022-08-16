package net.Indyuce.bountyhunters.listener;

import net.Indyuce.bountyhunters.api.player.PlayerData;
import net.Indyuce.bountyhunters.manager.PlayerDataManager;
import net.Indyuce.bountyhunters.BountyHunters;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void a(PlayerJoinEvent event) {
		BountyHunters.getInstance().getPlayerDataManager().load(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void b(PlayerQuitEvent event) {
		PlayerDataManager manager = BountyHunters.getInstance().getPlayerDataManager();

		/*
		 * Improve performance: save player data as soon as the player leaves
		 * the server. that way server does not have to save all player data
		 * when it shuts down
		 */
		manager.saveData(manager.get(event.getPlayer()));

		// Cannot unload data completely otherwise all cooldowns are reset
		// TODO empty cache after 1 day of inactivity
		// manager.unload(event.getPlayer());
	}

	@EventHandler
	public void c(PlayerDeathEvent event) {
		if (!event.getEntity().hasMetadata("NPC"))
			PlayerData.get(event.getEntity()).setIllegalKillStreak(0);
	}
}
