package net.Indyuce.bountyhunters.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.player.OfflinePlayerData;
import net.Indyuce.bountyhunters.api.player.PlayerData;

public abstract class PlayerDataManager {
	private static Map<UUID, PlayerData> map = new HashMap<>();

	public boolean isLoaded(OfflinePlayer player) {
		return isLoaded(player.getUniqueId());
	}

	public boolean isLoaded(UUID uuid) {
		return map.containsKey(uuid);
	}

	public PlayerData get(OfflinePlayer player) {
		return get(player.getUniqueId());
	}

	public PlayerData get(UUID uuid) {
		return map.get(uuid);
	}

	public OfflinePlayerData getOfflineData(OfflinePlayer player) {
		return map.containsKey(player.getUniqueId()) ? map.get(player.getUniqueId()) : loadOfflineData(player);
	}

	public void unload(UUID uuid) {
		map.remove(uuid);
	}

	public void unload(OfflinePlayer player) {
		map.remove(player.getUniqueId());
	}

	public Collection<PlayerData> getLoaded() {
		return map.values();
	}

	public void load(OfflinePlayer player) {
		if (!map.containsKey(player.getUniqueId())) {

			PlayerData data = new PlayerData(player);

			/*
			 * directly maps the player data into the HashMap however data is
			 * not loaded yet. better for extra plugins not to glitch out and so
			 * they can cache the player data instance if needed for later
			 * calculations
			 */
			map.put(player.getUniqueId(), data);

			/*
			 * load data asynchronously for databases and YAML not to harm main
			 * server thread
			 */
			Bukkit.getScheduler().runTaskAsynchronously(BountyHunters.getInstance(), () -> loadData(data));
		}
	}

	public abstract OfflinePlayerData loadOfflineData(OfflinePlayer player);

	public abstract void loadData(PlayerData data);

	public abstract void saveData(PlayerData data);
}
