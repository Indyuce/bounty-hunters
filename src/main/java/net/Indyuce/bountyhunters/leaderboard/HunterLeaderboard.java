package net.Indyuce.bountyhunters.leaderboard;

import net.Indyuce.bountyhunters.leaderboard.profile.HunterProfile;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Comparator;

public class HunterLeaderboard extends Leaderboard<HunterProfile> {

    /**
     * This simply uses 'leaderboard' for backwards compatibility. This
     * should definitely be 'hunter-leaderboard' but this is done to
     * support pre-2.4 data files.
     */
    public HunterLeaderboard() {
        super("leaderboard", Comparator.comparingInt(HunterProfile::getClaimedBounties));
    }

    @Override
    public HunterProfile loadProfile(ConfigurationSection config) {
        return new HunterProfile(config);
    }
}