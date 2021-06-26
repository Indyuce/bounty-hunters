package net.Indyuce.bountyhunters.api.restriction;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface BountyRestriction {

	/**
	 * @param type
	 *            The interaction type (bounty creation/increase/claim)
	 * @param claimer
	 *            The player interacting with the bounty
	 * @param target
	 *            The bounty target
	 * @return If the player can claim, increase or create a bounty with given
	 *         target
	 */
	public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target);

	public enum InteractionType {

		/**
		 * Bounty creation
		 */
		CREATE,

		/**
		 * Bounty claiming
		 */
		CLAIM,

		/**
		 * Bounty contribution
		 */
		INCREASE;
	}
}
