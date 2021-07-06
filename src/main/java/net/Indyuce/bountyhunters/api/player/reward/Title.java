package net.Indyuce.bountyhunters.api.player.reward;

import org.bukkit.configuration.ConfigurationSection;

public class Title extends LevelUpItem {
    public Title(ConfigurationSection config) {
        this(config.getName().toUpperCase().replace("-", "_"), config.getString("format"), config.getInt("unlock"));
    }

    public Title(String id, String format, int unlock) {
        super(id, format, unlock);
    }
}