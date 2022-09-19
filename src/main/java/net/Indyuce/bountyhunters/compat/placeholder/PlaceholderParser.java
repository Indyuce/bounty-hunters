package net.Indyuce.bountyhunters.compat.placeholder;

import org.bukkit.OfflinePlayer;

public interface PlaceholderParser {
    String parse(OfflinePlayer player, String string);
}
