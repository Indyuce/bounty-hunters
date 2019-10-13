package net.Indyuce.bountyhunters.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import net.Indyuce.bountyhunters.api.player.PlayerData;

public class PlayerDataManager {
	private static Map<UUID, PlayerData> map = new HashMap<>();

	public boolean isLoaded(UUID uuid) {
		return map.containsKey(uuid);
	}

	public boolean isLoaded(OfflinePlayer player) {
		return isLoaded(player.getUniqueId());
	}

	public PlayerData get(OfflinePlayer player) {
		return get(player.getUniqueId());
	}

	public PlayerData get(UUID uuid) {
		return map.get(uuid);
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
		if (!map.containsKey(player.getUniqueId()))
			map.put(player.getUniqueId(), new PlayerData(player));
	}
}
