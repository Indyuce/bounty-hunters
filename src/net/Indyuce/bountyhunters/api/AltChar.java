package net.Indyuce.bountyhunters.api;

import org.bukkit.ChatColor;

public class AltChar {
	public static final String square = "█";
	public static final String star = "★";
	public static final String diamond = "♦";

	public static String apply(String s) {
		return ChatColor.translateAlternateColorCodes('&', s.replace("%star%", star).replace("%diamond%", diamond));
	}
}