package net.Indyuce.bountyhunters.leaderboard;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.ConfigFile;
import net.Indyuce.bountyhunters.leaderboard.profile.LeaderboardProfile;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.logging.Level;

public abstract class Leaderboard<T extends LeaderboardProfile> {
    protected final Map<UUID, T> mapped = new LinkedHashMap<>();
    protected final List<UUID> sortedProfiles = new ArrayList<>();
    protected final Comparator<T> profileComparer;

    private final String fileName;

    private static final int MAX_CAPACITY = 16;

    public Leaderboard(String fileName, Comparator<T> profileComparer) {
        this.profileComparer = profileComparer;
        this.fileName = fileName;

        ConfigFile dataFile = getConfigFile();
        for (String key : dataFile.getConfig().getKeys(false))
            try {
                Validate.isTrue(dataFile.getConfig().get(key) instanceof ConfigurationSection, "Outdated leaderboard data type");
                mapped.put(UUID.fromString(key), loadProfile(dataFile.getConfig().getConfigurationSection(key)));
            } catch (IllegalArgumentException exception) {
                BountyHunters.getInstance().getLogger().log(Level.INFO, "Could not load leaderboard key '" + key + "': " + exception.getMessage());
            }

        updateCached();
    }

    /**
     * @param uuid The player UUID
     * @return Cached data of a specific player in the leaderboard
     */
    public T getData(UUID uuid) {
        return mapped.get(uuid);
    }

    /**
     * @param position Leaderboard rank
     * @return Find a player with a specific rank, or empty optional if none
     */
    public Optional<T> getPosition(int position) {
        Validate.isTrue(position > 0, "Position must be greater than 0");

        return position - 1 < sortedProfiles.size() ? Optional.of(mapped.get(sortedProfiles.get(position - 1))) : Optional.empty();
    }

    /**
     * @return The ordered list of UUIDs which corresponds to the leaderboard.
     *         This list is updated everytime a player claims a bounty and makes
     *         retrieving leaderboard positions easier
     */
    public List<UUID> getCached() {
        return sortedProfiles;
    }

    /**
     * @param uuid Player unique ID
     * @return If the given player has a position in the leaderboard
     */
    public boolean isRegistered(UUID uuid) {
        return mapped.containsKey(uuid);
    }

    public void register(T profile) {
        mapped.put(profile.getUniqueId(), profile);
        updateCached();
    }

    private ConfigFile getConfigFile() {
        return new ConfigFile("/cache", fileName);
    }

    /**
     * Called when a player claims a bounty and the claimer needs
     * to update their position.
     * <p>
     * Called when a bounty changes and the bounty leaderboard
     * has to be updated globally.
     *
     * @param profile Profile being updated
     */
    public void update(T profile) {

        /*
         * If the leaderboard already contains that player, just add one to the
         * bounties counter; if there is still not at least 16 players in the
         * cached leaderboard, just add it to the keys and that's all
         */
        if (mapped.containsKey(profile.getUniqueId()) || mapped.size() < MAX_CAPACITY) {
            register(profile);
            return;
        }

        /*
         * If there is more than 16 players in the leaderboard, the plugin will
         * have to remove the player that has the least bounties and will
         * replace it by the newer one IF the newer one has more bounties
         */
        Map.Entry<UUID, T> minEntry = null;

        for (Map.Entry<UUID, T> entry : mapped.entrySet())
            if (minEntry == null || profileComparer.compare(minEntry.getValue(), entry.getValue()) == 1)
                minEntry = entry;

        if (profileComparer.compare(profile, minEntry.getValue()) == 1) {
            mapped.remove(minEntry.getKey());
            register(profile);
        }
    }

    /**
     * Updates the leaderboard position list
     */
    protected void updateCached() {
        sortedProfiles.clear();
        mapped.entrySet().stream().sorted(Map.Entry.comparingByValue(profileComparer)).forEach(entry -> sortedProfiles.add(entry.getKey()));
        Collections.reverse(sortedProfiles);
    }

    /**
     * Empties old leaderboard save file and caches newest leaderboard profiles
     */
    public void save() {
        ConfigFile dataFile = getConfigFile();

        // Reset previous keys
        for (String key : dataFile.getConfig().getKeys(false))
            dataFile.getConfig().set(key, null);

        // Apply new keys
        mapped.values().forEach(profile -> profile.save(dataFile.getConfig()));

        // Save file
        dataFile.save();
    }

    public abstract T loadProfile(ConfigurationSection config);
}
