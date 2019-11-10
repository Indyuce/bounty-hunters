package net.Indyuce.bountyhunters.comp.social;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import net.Indyuce.bountyhunters.api.event.BountyChangeEvent;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;

public class PartyAndFriendsSupport implements Listener {
	private final PAFPlayerManager manager = PAFPlayerManager.getInstance();

	@EventHandler(priority = EventPriority.LOWEST)
	public void a(BountyClaimEvent event) {
		if (manager.getPlayer(event.getClaimer().getUniqueId()).isAFriendOf(manager.getPlayer(event.getBounty().getTarget().getUniqueId())))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void b(BountyChangeEvent event) {
		if (manager.getPlayer(event.getPlayer().getUniqueId()).isAFriendOf(manager.getPlayer(event.getBounty().getTarget().getUniqueId())))
			event.setCancelled(true);
	}
}
