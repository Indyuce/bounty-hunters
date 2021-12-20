package net.Indyuce.bountyhunters.leaderboard.profile;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public abstract class LeaderboardProfile {
    private final UUID uuid;
    private final String name;

    /**
     * Caches player data into a profile so that it
     * can be saved in the leaderboard config file
     *
     * @param player Player to retrieve data from
     */
    public LeaderboardProfile(OfflinePlayer player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    /**
     * Used when loading a leaderboard profile from the config
     *
     * @param config Config to read data from
     */
    public LeaderboardProfile(ConfigurationSection config) {
        this.uuid = UUID.fromString(config.getName());
        this.name = config.getString("name");
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void save(FileConfiguration config) {
        config.set(getUniqueId().toString() + ".name", name);
    }
}
