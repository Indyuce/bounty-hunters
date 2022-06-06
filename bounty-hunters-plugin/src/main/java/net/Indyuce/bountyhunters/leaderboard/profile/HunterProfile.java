package net.Indyuce.bountyhunters.leaderboard.profile;

import net.Indyuce.bountyhunters.api.language.Language;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import org.bukkit.configuration.ConfigurationSection;

public class HunterProfile extends LeaderboardProfile {
    private final int successfulBounties, claimedBounties, level;
    private final String title;

    public HunterProfile(PlayerData player) {
        super(player.getOfflinePlayer());

        this.successfulBounties = player.getSuccessfulBounties();
        this.claimedBounties = player.getClaimedBounties();
        this.title = player.hasTitle() ? player.getTitle().format() : Language.NO_TITLE.format();
        this.level = player.getLevel();
    }

    public HunterProfile(ConfigurationSection config) {
        super(config);

        this.successfulBounties = config.getInt("successful-bounties");
        this.claimedBounties = config.getInt("claimed-bounties");
        this.title = config.getString("title");
        this.level = config.getInt("level");
    }

    public int getClaimedBounties() {
        return claimedBounties;
    }

    public int getSuccessfulBounties() {
        return successfulBounties;
    }

    public String getTitle() {
        return title;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public void whenSaved(ConfigurationSection config) {
        config.set("successful-bounties", successfulBounties);
        config.set("claimed-bounties", claimedBounties);
        config.set("title", title);
        config.set("level", level);
    }
}
