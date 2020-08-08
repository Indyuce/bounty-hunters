package net.Indyuce.bountyhunters.api.restriction;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface BountyRestriction {

	/*
	 * returns true if the player can claim, increase or create a bounty
	 */
	public boolean canInteractWith(Player claimer, OfflinePlayer target);
}
