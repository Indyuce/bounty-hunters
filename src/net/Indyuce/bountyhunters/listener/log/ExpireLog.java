package net.Indyuce.bountyhunters.listener.log;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.event.BountyExpireEvent;
import net.Indyuce.bountyhunters.api.event.BountyExpireEvent.BountyExpireCause;

public class ExpireLog implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void a(BountyExpireEvent event) {

		if (event.isExpiring()) {
			if (event.getCause() == BountyExpireCause.ADMIN)
				BountyHunters.getInstance().getLogger().log(Level.INFO, "Bounty on " + event.getBounty().getTarget().getName() + " expired due to admin activity.");
			else
				BountyHunters.getInstance().getLogger().log(Level.INFO, "Bounty on " + event.getBounty().getTarget().getName() + " expired as " + event.getPlayer().getName() + " removed his contribution of " + event.getBounty().getContribution(event.getPlayer()) + ".");
		}

		else {
			double old = event.getBounty().getReward();
			if (event.getCause() == BountyExpireCause.ADMIN)
				BountyHunters.getInstance().getLogger().log(Level.INFO, "Bounty on " + event.getBounty().getTarget().getName() + "decreased from " + old + " to " + (old - event.getAmountRemoved()) + " due to admin activity.");
			else
				BountyHunters.getInstance().getLogger().log(Level.INFO, "Bounty on " + event.getBounty().getTarget().getName() + "decreased from " + old + " to " + (old - event.getAmountRemoved()) + " as " + event.getPlayer() + " removed his contribution of " + event.getAmountRemoved());
		}
	}
}
