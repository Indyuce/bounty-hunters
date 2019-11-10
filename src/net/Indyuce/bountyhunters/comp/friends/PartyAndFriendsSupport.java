package net.Indyuce.bountyhunters.comp.friends;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;

public class PartyAndFriendsSupport implements Listener {
	private final PAFPlayerManager manager = PAFPlayerManager.getInstance();

	@EventHandler(priority = EventPriority.LOWEST)
	public void a(BountyClaimEvent event) {
		if (manager.getPlayer(event.getClaimer().getUniqueId()).isAFriendOf(manager.getPlayer(event.getBounty().getTarget().getUniqueId())))
			event.setCancelled(true);
	}
}
