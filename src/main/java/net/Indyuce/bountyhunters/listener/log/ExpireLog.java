package net.Indyuce.bountyhunters.listener.log;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.event.BountyExpireEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public class ExpireLog implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void a(BountyExpireEvent event) {


        if (event.isExpiring()) {
            if (event.hasPlayer())
                BountyHunters.getInstance().getLogger().log(Level.INFO, "Bounty on " + event.getBounty().getTarget().getName() + " expired as " + event.getPlayer().getName() + " removed his contribution of $" + event.getBounty().getContribution(event.getPlayer()));
            else
                BountyHunters.getInstance().getLogger().log(Level.INFO, "Bounty on " + event.getBounty().getTarget().getName() + " expired due to admin activity");

        } else {
            double old = event.getBounty().getReward();
            if (event.hasPlayer())
                BountyHunters.getInstance().getLogger().log(Level.INFO, "Bounty on " + event.getBounty().getTarget().getName() + " decreased from $" + old + " to $" + (old - event.getAmountRemoved()) + " as " + event.getPlayer() + " removed his contribution of $" + event.getAmountRemoved());
            else
                BountyHunters.getInstance().getLogger().log(Level.INFO, "Bounty on " + event.getBounty().getTarget().getName() + " decreased from $" + old + " to $" + (old - event.getAmountRemoved()) + " due to admin activity");
        }
    }
}
