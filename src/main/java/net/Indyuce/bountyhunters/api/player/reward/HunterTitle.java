package net.Indyuce.bountyhunters.api.player.reward;

import org.bukkit.configuration.ConfigurationSection;

public class HunterTitle extends LevelUpItem {
    public HunterTitle(ConfigurationSection config) {
        this(config.getName().toUpperCase().replace("-", "_"), config.getString("format"), config.getInt("unlock"));
    }

    public HunterTitle(String id, String format, int unlock) {
        super(id, format, unlock);
    }
}