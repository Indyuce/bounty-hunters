package net.Indyuce.bountyhunters.leaderboard;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.leaderboard.profile.BountyProfile;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Comparator;

public class BountyLeaderboard extends Leaderboard<BountyProfile> {
    public BountyLeaderboard() {
        super("bounty-leaderboard", Comparator.comparingDouble(BountyProfile::getCurrentBounty));
    }

    @Override
    public BountyProfile loadProfile(ConfigurationSection config) {
        return new BountyProfile(config);
    }

    /**
     * Called when a bounty is claimed and the leaderboard
     * needs to flush the claimed bounty
     *
     * @param player Player being removed from the leaderboard
     */
    public void remove(OfflinePlayer player) {
        if (!mapped.containsKey(player.getUniqueId()))
            return;

        mapped.remove(player.getUniqueId());

        /*
         * Try to find a new player, that is NOT already
         * in the leaderboard and that might enter it since
         * the one player is leaving
         */
        Bounty newBounty = null;
        for (Bounty bounty : BountyHunters.getInstance().getBountyManager().getBounties())
            if (!mapped.containsKey(bounty.getTarget().getUniqueId()) && (newBounty == null || bounty.getReward() > newBounty.getReward()))
                newBounty = bounty;

        // Found one
        if (newBounty != null)
            mapped.put(newBounty.getTarget().getUniqueId(), new BountyProfile(newBounty.getTarget()));

        updateCached();
    }
}
