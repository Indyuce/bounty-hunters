package net.Indyuce.bountyhunters.api.restriction;

import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.api.Bounty;

public interface BountyRestriction {

	/*
	 * returns true if the player can claim, increase or create a bounty
	 */
	public boolean canInteractWith(Player claimer, Bounty bounty);
}
