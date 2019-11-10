package net.Indyuce.bountyhunters.api.restriction;

import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.api.Bounty;

public interface BountyRestriction {
	public boolean canClaimBounty(Player claimer, Bounty bounty);
}
