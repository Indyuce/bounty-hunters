package net.Indyuce.bountyhunters.api.restriction;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.api.Bounty;

public class BedSpawnPoint implements BountyRestriction {
	private final int radiusSquared;

	public BedSpawnPoint(ConfigurationSection config) {
		radiusSquared = config.getInt("radius") * config.getInt("radius");
	}

	@Override
	public boolean canClaimBounty(Player claimer, Bounty bounty) {
		Location loc = claimer.getBedSpawnLocation();
		Location loc1 = bounty.getTarget().getBedSpawnLocation();
		return loc == null || loc1 == null || loc.distanceSquared(loc1) < radiusSquared;
	}
}
