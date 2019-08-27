package net.Indyuce.bountyhunters.api.player;

import org.bukkit.OfflinePlayer;

public interface PlayerDataInterface {
	void addSuccessfulBounties(int value);

	public static PlayerDataInterface get(OfflinePlayer player) {
		return PlayerData.isLoaded(player.getUniqueId()) ? PlayerData.get(player) : new OfflinePlayerData(player);
	}
}
