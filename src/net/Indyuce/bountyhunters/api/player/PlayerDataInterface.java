package net.Indyuce.bountyhunters.api.player;

import org.bukkit.OfflinePlayer;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.manager.PlayerDataManager;

public interface PlayerDataInterface {
	void addSuccessfulBounties(int value);

	public static PlayerDataInterface get(OfflinePlayer player) {
		PlayerDataManager manager = BountyHunters.getInstance().getPlayerDataManager();
		return manager.isLoaded(player.getUniqueId()) ? manager.get(player) : new OfflinePlayerData(player);
	}
}
