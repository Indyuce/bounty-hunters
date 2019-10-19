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
		if (event.getCause() == BountyExpireCause.ADMIN)
			BountyHunters.getInstance().getLogger().log(Level.INFO, "Bounty on " + event.getBounty().getTarget().getName() + " expired due to an admin.");
		else if (event.getCause() == BountyExpireCause.CREATOR)
			BountyHunters.getInstance().getLogger().log(Level.INFO, "Bounty on " + event.getBounty().getTarget().getName() + " was removed by its creator.");
	}
}
