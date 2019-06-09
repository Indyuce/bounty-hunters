package net.Indyuce.bountyhunters.version;

import org.bukkit.Sound;

import net.Indyuce.bountyhunters.BountyHunters;

public enum VersionSound {

	// first arg = 1.8 sound name
	// second arg = 1.9-1.12 sound name
	// name() = 1.13+ sound name

	ENTITY_PLAYER_LEVELUP("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"),
	ENTITY_ENDERMAN_HURT("ENDERMAN_HIT", "ENTITY_ENDERMEN_HURT"),
	ENTITY_VILLAGER_NO("VILLAGER_NO", "ENTITY_VILLAGER_NO");

	private Sound sound;

	private VersionSound(String... soundNames) {
		sound = BountyHunters.getVersion().isBelowOrEqual(1, 8) ? Sound.valueOf(soundNames[0]) : (BountyHunters.getVersion().isBelowOrEqual(1, 12) ? Sound.valueOf(soundNames[1]) : Sound.valueOf(name()));
	}

	public Sound getSound() {
		return sound;
	}
}
