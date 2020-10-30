package net.Indyuce.bountyhunters.api.restriction;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.BountyHunters;

public class MaxBountyAmount implements BountyRestriction {
	private final int max;

	/**
	 * @param max
	 *            Maximum amount of bounties created/increased by one player
	 */
	public MaxBountyAmount(int max) {
		this.max = max;
	}

	@Override
	public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
		return interaction == InteractionType.CLAIM || BountyHunters.getInstance().getBountyManager().getContributions(claimer).size() < max;
	}
}
