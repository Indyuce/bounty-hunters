package net.Indyuce.bountyhunters.api.player;

public interface OfflinePlayerData {

	/*
	 * used to update player data when a player is offline. must be compatible
	 * with MySQL and YAML
	 */
	public void addSuccessfulBounties(int value);
}
