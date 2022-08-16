package net.Indyuce.bountyhunters.listener;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.api.event.BountyExpireEvent;
import net.Indyuce.bountyhunters.api.event.BountyIncreaseEvent;
import net.Indyuce.bountyhunters.leaderboard.profile.BountyProfile;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BountyLeaderboardListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void a(BountyCreateEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BountyHunters.getInstance(), () -> BountyHunters.getInstance().getBountyLeaderboard().update(new BountyProfile(event.getBounty().getTarget())));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void b(BountyIncreaseEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BountyHunters.getInstance(), () -> BountyHunters.getInstance().getBountyLeaderboard().update(new BountyProfile(event.getBounty().getTarget())));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void c(BountyClaimEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BountyHunters.getInstance(), () -> BountyHunters.getInstance().getBountyLeaderboard().remove(event.getTarget()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void d(BountyExpireEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BountyHunters.getInstance(), () -> BountyHunters.getInstance().getBountyLeaderboard().update(new BountyProfile(event.getBounty().getTarget())));
    }
}
