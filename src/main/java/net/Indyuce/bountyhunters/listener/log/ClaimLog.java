package net.Indyuce.bountyhunters.listener.log;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;

public class ClaimLog implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void a(BountyClaimEvent event) {
		BountyHunters.getInstance().getLogger().log(Level.INFO,
				event.getClaimer().getName() + " claimed bounty on " + event.getBounty().getTarget().getName() + " worth $" + event.getBounty().getReward());
	}
}
