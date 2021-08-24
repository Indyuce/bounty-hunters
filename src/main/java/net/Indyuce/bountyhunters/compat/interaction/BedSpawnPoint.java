package net.Indyuce.bountyhunters.compat.interaction;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class BedSpawnPoint implements InteractionRestriction {
	private final int radiusSquared;

	public BedSpawnPoint(ConfigurationSection config) {
		radiusSquared = config.getInt("radius") * config.getInt("radius");
	}

	@Override
	public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
		Location loc = claimer.getBedSpawnLocation();
		Location loc1 = target.getBedSpawnLocation();
		return loc == null || loc1 == null || !loc.getWorld().equals(loc1.getWorld()) || loc.distanceSquared(loc1) > radiusSquared;
	}
}
