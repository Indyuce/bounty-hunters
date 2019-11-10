package net.Indyuce.bountyhunters.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.event.BountyChangeEvent;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.api.restriction.ClaimRestriction;

public class RestrictionListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void a(BountyClaimEvent event) {
		if (!check(event.getClaimer(), event.getBounty()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BountyCreateEvent event) {
		if (!check(event.getCreator(), event.getBounty()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void c(BountyChangeEvent event) {
		if (!check(event.getPlayer(), event.getBounty()))
			event.setCancelled(true);
	}

	private boolean check(Player player, Bounty bounty) {
		for (ClaimRestriction restriction : BountyHunters.getInstance().getBountyManager().getClaimRestrictions())
			if (!restriction.canClaimBounty(player, bounty))
				return false;
		return true;
	}
}
