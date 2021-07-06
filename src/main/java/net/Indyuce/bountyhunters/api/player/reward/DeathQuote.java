package net.Indyuce.bountyhunters.api.player.reward;

import org.bukkit.configuration.ConfigurationSection;

public class DeathQuote extends LevelUpItem {
    public DeathQuote(ConfigurationSection config) {
        this(config.getName().toUpperCase().replace("-", "_"), config.getString("format"), config.getInt("unlock"));
    }

    public DeathQuote(String id, String format, int unlock) {
        super(id, format, unlock);
    }
}