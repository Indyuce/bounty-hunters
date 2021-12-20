package net.Indyuce.bountyhunters.api.player.reward.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Particle effects are cast when a player claims a bounty.
 * After sending some message in the chat, a death animation
 * displays around the player who just got killed.
 */
@FunctionalInterface
public interface ParticleEffect {

    /**
     * Plays particle effect at target location
     */
    public void playParticleEffect(Location loc, Player killed);
}
