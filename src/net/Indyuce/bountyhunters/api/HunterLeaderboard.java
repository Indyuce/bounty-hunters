package net.Indyuce.bountyhunters.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import net.Indyuce.bountyhunters.BountyHunters;

public class HunterLeaderboard {
	private final ConfigFile configFile;
	private final Map<UUID, Integer> mapped = new HashMap<>();

	private List<UUID> cached;

	public HunterLeaderboard(ConfigFile configFile) {
		this.configFile = configFile;

		for (String key : configFile.getConfig().getKeys(false))
			try {
				mapped.put(UUID.fromString(key), configFile.getConfig().getInt(key));
			} catch (IllegalArgumentException exception) {
				BountyHunters.getInstance().getLogger().log(Level.INFO, "Could not load leaderboard key '" + key + "': " + exception.getMessage());
			}

		updateCached();
	}

	public Map<UUID, Integer> mapLeaderboard() {
		return mapped;
	}

	public int getScore(UUID uuid) {
		return mapped.get(uuid);
	}

	public UUID getPosition(int position) {
		return position < cached.size() ? cached.get(position) : null;
	}

	public List<UUID> getCached() {
		return cached;
	}

	public void save() {

		// reset previous keys
		for (String key : configFile.getConfig().getKeys(false))
			configFile.getConfig().set(key, null);

		// apply new keys
		mapped.forEach((uuid, value) -> configFile.getConfig().set(uuid.toString(), value));

		// save file
		configFile.save();
	}

	public void update(UUID uuid, int bounties) {

		/*
		 * if the leaderboard already contains that player, just add one to the
		 * bounties counter; if there is still not at least 16 players in the
		 * cached leaderboard, just add it to the keys and that's all
		 */
		if (mapped.containsKey(uuid) || mapped.size() < 16) {
			mapped.put(uuid, bounties);
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
			int value = mapped.get(key);
			if (value < leastValue) {
				leastKey = key;
				leastValue = value;
			}
		}

		if (bounties >= leastValue) {
			mapped.remove(leastKey);
			mapped.put(uuid, bounties);
			updateCached();
		}
	}

	private void updateCached() {
		cached = new ArrayList<>(mapped.entrySet()).stream().sorted(Entry.comparingByValue()).map(entry -> entry.getKey())
				.collect(Collectors.toList());
		Collections.reverse(cached);
	}
}