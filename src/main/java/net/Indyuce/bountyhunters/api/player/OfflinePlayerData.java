package net.Indyuce.bountyhunters.api.player;

import org.bukkit.OfflinePlayer;

public interface OfflinePlayerData {

	/*
	 * used to update player data when a player is offline. must be compatible
	 * with MySQL and YAML
	 */
	public void addSuccessfulBounties(int value);

	/*
	 * used to give a player head to an online player or save it in the head GUI
	 * which he can open later.
	 */
	public void givePlayerHead(OfflinePlayer owner);
}
