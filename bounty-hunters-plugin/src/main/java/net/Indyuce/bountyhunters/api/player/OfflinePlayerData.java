package net.Indyuce.bountyhunters.api.player;

import org.bukkit.OfflinePlayer;

public interface OfflinePlayerData {

	/**
	 * Used to update player data when a player is offline. Must be compatible
	 * with MySQL and YAML
	 */
	public void addSuccessfulBounties(int value);

	/**
	 * Used to give a player head to an online player or save it in the head GUI
	 * which they can open later.
	 */
	public void givePlayerHead(OfflinePlayer owner);
}
