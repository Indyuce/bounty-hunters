package net.Indyuce.bountyhunters.compat.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIParser implements PlaceholderParser {
	@Override
	public String parse(OfflinePlayer player, String string) {
		return PlaceholderAPI.setPlaceholders(player, string);
	}
}
