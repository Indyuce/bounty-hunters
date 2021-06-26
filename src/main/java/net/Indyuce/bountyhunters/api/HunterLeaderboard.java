package net.Indyuce.bountyhunters.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.player.LeaderboardProfile;
import net.Indyuce.bountyhunters.api.player.PlayerData;

public class HunterLeaderboard {
	private final ConfigFile configFile;
	private final Map<UUID, LeaderboardProfile> mapped = new HashMap<>();

	private List<UUID> cached;

	public HunterLeaderboard(ConfigFile configFile) {
		this.configFile = configFile;

		for (String key : configFile.getConfig().getKeys(false))
			try {
				Validate.isTrue(configFile.getConfig().get(key) instanceof ConfigurationSection, "Outdated leaderboard data type");
				mapped.put(UUID.fromString(key), new LeaderboardProfile(configFile.getConfig().getConfigurationSection(key)));
			} catch (IllegalArgumentException exception) {
				BountyHunters.getInstance().getLogger().log(Level.INFO, "Could not load leaderboard key '" + key + "': " + exception.getMessage());
			}

		updateCached();
	}

	/**
	 * @param uuid
	 *            The player UUID
	 * @return Cached data of a specific player in the leaderboard
	 */
	public LeaderboardProfile getData(UUID uuid) {
		return mapped.get(uuid);
	}

	/**
	 * @param position
	 *            Leaderboard rank
	 * @return Find a player with a specific rank, or empty optional if none
	 */
	public Optional<LeaderboardProfile> getPosition(int position) {
		Validate.isTrue(position > 0, "Position must be greater than 0");

		return position - 1 < cached.size() ? Optional.of(mapped.get(cached.get(position - 1))) : Optional.empty();
	}

	/**
	 * @return The ordered list of UUIDs which corresponds to the leaderboard.
	 *         This list is updated everytime a player claims a bounty and makes
	 *         retrieving leaderboard positions easier
	 */
	public List<UUID> getCached() {
		return cached;
	}

	/**
	 * Empties old leaderboard save file and caches newest leaderboard profiles
	 */
	public void save() {

		// reset previous keys
		for (String key : configFile.getConfig().getKeys(false))
			configFile.getConfig().set(key, null);

		// apply new keys
		mapped.values().forEach(profile -> profile.save(configFile.getConfig()));

		// save file
		configFile.save();
	}

	/**
	 * Method called when a player claims a bounty and needs to update his
	 * position in the leaderboard
	 * 
	 * @param player
	 *            Player who claimed a bounty
	 */
	public void update(PlayerData player) {

		/*
		 * if the leaderboard already contains that player, just add one to the
		 * bounties counter; if there is still not at least 16 players in the
		 * cached leaderboard, just add it to the keys and that's all
		 */
		if (mapped.containsKey(player.getUniqueId()) || mapped.size() < 16) {
			mapped.put(player.getUniqueId(), new LeaderboardProfile(player));
			updateCached();
			return;
		}

		/*
		 * if there is more than 16 players in the leaderboard, the plugin will
		 * have to remove the player that has the least bounties and will
		 * replace it by the newer one IF the newer one has more bounties
		 */
		UUID leastKey = null;
		int leastValue = Integer.MAX_VALUE;

		for (UUID key : mapped.keySet()) {
			LeaderboardProfile data = mapped.get(key);
			if (data.getClaimedBounties() < leastValue) {
				leastKey = key;
				leastValue = data.getClaimedBounties();
			}
		}

		if (player.getClaimedBounties() >= leastValue) {
			mapped.remove(leastKey);
			mapped.put(player.getUniqueId(), new LeaderboardProfile(player));
			updateCached();
		}
	}

	/**
	 * Updates the leaderboard position list
	 */
	private void updateCached() {
		cached = new ArrayList<>(mapped.entrySet()).stream().sorted(Entry.comparingByValue(new Comparator<LeaderboardProfile>() {

			@Override
			public int compare(LeaderboardProfile o1, LeaderboardProfile o2) {
				return Integer.compare(o1.getClaimedBounties(), o2.getClaimedBounties());
			}

		})).map(entry -> entry.getKey()).collect(Collectors.toList());
		Collections.reverse(cached);
	}
}