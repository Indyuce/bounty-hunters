package net.Indyuce.bountyhunters.comp.placeholder;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderAPIParser implements PlaceholderParser {
	@Override
	public String parse(OfflinePlayer player, String string) {
		return PlaceholderAPI.setPlaceholders(player, string);
	}
}
