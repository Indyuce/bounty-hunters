package net.Indyuce.bountyhunters.api;

import org.bukkit.ChatColor;

public class AltChar {
	public static final String star = "★";
	public static final String diamond = "◆";
	public static final String square = "█";
	public static final String listDash = "▸";

	public static String apply(String str) {
		return ChatColor.translateAlternateColorCodes('&', str.replace("{star}", star).replace("{diamond}", diamond));
	}
}