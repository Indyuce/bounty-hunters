package net.Indyuce.bountyhunters.version;

import org.bukkit.Sound;

import net.Indyuce.bountyhunters.BountyHunters;

public enum VersionSound {
	
	/*
	 * 1.13+ sound in name(), legacy name as first parameter
	 */
	ENTITY_ENDERMAN_HURT("ENTITY_ENDERMEN_HURT"),
	ENTITY_ENDERMAN_DEATH("ENTITY_ENDERMEN_DEATH"),
	ENTITY_ENDERMAN_TELEPORT("ENTITY_ENDERMEN_TELEPORT"),
	ENTITY_FIREWORK_ROCKET_LARGE_BLAST("ENTITY_FIREWORK_LARGE_BLAST"),
	ENTITY_FIREWORK_ROCKET_TWINKLE("ENTITY_FIREWORK_TWINKLE"),
	ENTITY_FIREWORK_ROCKET_BLAST("ENTITY_FIREWORK_BLAST"),
	ENTITY_ZOMBIE_PIGMAN_ANGRY("ENTITY_ZOMBIE_PIG_ANGRY"),
	BLOCK_NOTE_BLOCK_HAT("BLOCK_NOTE_HAT"),
	BLOCK_NOTE_BLOCK_PLING("BLOCK_NOTE_PLING"),
	ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR("ENTITY_ZOMBIE_ATTACK_DOOR_WOOD"),
	ENTITY_ENDER_DRAGON_GROWL("ENTITY_ENDERDRAGON_GROWL"),
	ENTITY_ENDER_DRAGON_FLAP("ENTITY_ENDERDRAGON_FLAP"),

	;

	private final Sound sound;

	private VersionSound(String legacy) {
		sound = Sound.valueOf(BountyHunters.getInstance().getVersion().isStrictlyHigher(1, 12) ? name() : legacy);
	}

	public Sound toSound() {
		return sound;
	}
}
