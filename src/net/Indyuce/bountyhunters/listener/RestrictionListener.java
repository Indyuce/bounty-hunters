package net.Indyuce.bountyhunters.listener;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.api.event.BountyIncreaseEvent;
import net.Indyuce.bountyhunters.api.restriction.BountyRestriction;

public class RestrictionListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void a(BountyClaimEvent event) {
		if (!check(event.getClaimer(), event.getTarget()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BountyCreateEvent event) {
		if (event.hasCreator() && !check(event.getCreator(), event.getBounty().getTarget()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void c(BountyIncreaseEvent event) {
		if (event.hasPlayer() && !check(event.getPlayer(), event.getBounty().getTarget()))
			event.setCancelled(true);
	}

	private boolean check(Player player, OfflinePlayer target) {
		for (BountyRestriction restriction : BountyHunters.getInstance().getBountyManager().getClaimRestrictions())
			if (!restriction.canInteractWith(player, target))
				return false;
		return true;
	}
}
