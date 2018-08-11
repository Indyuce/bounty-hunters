package net.Indyuce.bh.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class VersionUtils implements Listener {
	public static String version;
	public static String[] splitVersion;

	// returns true if below OR equal
	public static boolean isBelow(int... ver) {
		return Integer.parseInt(splitVersion[0].replace("v", "")) <= ver[0] && Integer.parseInt(splitVersion[1]) <= ver[1];
	}

	public static ItemStack getMainItem(Player p) {
		ItemStack i = p.getItemInHand();
		if (!version.startsWith("v1_8")) {
			i = p.getInventory().getItemInMainHand();
		}
		return i;
	}

	static String getModifiedSound(String path) {
		String sound = path;
		if (version.startsWith("v1_8")) {
			sound = sound.replace("BLOCK_FIRE_EXTINGUISH", "FIZZ");
			sound = sound.replace("BLOCK_NOTE_HAT", "NOTE_STICKS");
			sound = sound.replace("ENTITY_SHEEP_DEATH", "SHEEP_IDLE");
			sound = sound.replace("ENTITY_LLAMA_ANGRY", "HORSE_HIT");
			sound = sound.replace("BLOCK_BREWING_STAND_BREW", "CREEPER_HISS");
			sound = sound.replace("ENTITY_SHULKER_TELEPORT", "ENDERMAN_TELEPORT");
			sound = sound.replace("ENTITY_ZOMBIE_ATTACK_IRON_DOOR", "ZOMBIE_METAL");
			sound = sound.replace("BLOCK_GRAVEL_BREAK", "DIG_GRAVEL");
			sound = sound.replace("BLOCK_SNOW_BREAK", "DIG_SNOW");
			sound = sound.replace("BLOCK_GRAVEL_BREAK", "DIG_GRAVEL");
			sound = sound.replace("ENTITY_PLAYER_LEVELUP", "LEVEL_UP");
			sound = sound.replace("ENTITY_SNOWBALL_THROW", "SHOOT_ARROW");

			sound = sound.replace("ENTITY_", "");
			sound = sound.replace("GENERIC_", "");
			sound = sound.replace("BLOCK_", "");
			sound = sound.replace("_AMBIENT", "");
			sound = sound.replace("_BREAK", "");
			sound = sound.replace("PLAYER_ATTACK_CRIT", "ITEM_BREAK");
			sound = sound.replace("ENDERMEN", "ENDERMAN");
			sound = sound.replace("ARROW_SHOOT", "SHOOT_ARROW");
			sound = sound.replace("UI_BUTTON_", "");
			sound = sound.replace("ENDERMAN_HURT", "ENDERMAN_HIT");
			sound = sound.replace("BLAZE_HURT", "BLAZE_HIT");
			sound = sound.replace("_FLAP", "_WINGS");
			sound = sound.replace("EXPERIENCE_", "");
		}
		return sound;
	}

	public static void sound(Location loc, String sound, float vol, float pitch) {
		String path = getModifiedSound(sound);
		try {
			loc.getWorld().playSound(loc, Sound.valueOf(path), vol, pitch);
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage("§4[Bounty Hunters] Bug with " + sound + ". No such sound found. Please report it to the plugin creator :)");
		}
	}

	public static void sound(Player p, String sound, float vol, float pitch) {
		String path = getModifiedSound(sound);
		try {
			p.playSound(p.getLocation(), Sound.valueOf(path), vol, pitch);
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage("§4[Bounty Hunters] Bug with " + sound + ". No such sound found. Please report it to the plugin creator :)");
		}
	}
}
