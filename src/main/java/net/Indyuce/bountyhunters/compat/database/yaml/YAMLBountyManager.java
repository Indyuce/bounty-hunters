package net.Indyuce.bountyhunters.compat.database.yaml;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.ConfigFile;
import net.Indyuce.bountyhunters.manager.BountyManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class YAMLBountyManager extends BountyManager {
    private final String path;

    /**
     * YAML bounty manager with default config folder setup
     */
    public YAMLBountyManager() {
        this("data");
    }

    /**
     * @param path Data file path
     */
    public YAMLBountyManager(String path) {
        this.path = path;
    }

    @Override
    public void loadBounties() {
        final FileConfiguration config = new ConfigFile(path).getConfig();
        for (String key : config.getKeys(false))
            try {
                registerBounty(load(config.getConfigurationSection(key)));
            } catch (RuntimeException exception) {
                BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not load bounty " + key + ": " + exception.getMessage());
            }
    }

    @Override
    public void saveBounties() {
        final ConfigFile data = new ConfigFile(path);

        data.getConfig().getKeys(false).forEach(key -> data.getConfig().set(key, null));
        getBounties().forEach(bounty -> save(bounty, data.getConfig()));

        data.save();
    }

    @NotNull
    private Bounty load(ConfigurationSection config) {
        Validate.notNull(config, "Could not read bounty config");
        String target = Objects.requireNonNull(config.getString("target"), "Could not find bounty target");
        final Bounty bounty = new Bounty(UUID.fromString(config.getName()), Bukkit.getOfflinePlayer(UUID.fromString(target)), config.getDouble("extra"));

        // Default value for backwards compatibility
        bounty.setLastModified(config.getLong("last-modified", System.currentTimeMillis()));
//		for (String key : config.getStringList("hunters"))
//			bounty.addHunter(Bukkit.getOfflinePlayer(UUID.fromString(key)));
        if (config.contains("up"))
            for (String key : config.getConfigurationSection("up").getKeys(false))
                bounty.addContribution(Bukkit.getOfflinePlayer(UUID.fromString(key)), config.getDouble("up." + key));

        return bounty;
    }

    private void save(Bounty bounty, FileConfiguration config) {
        String key = bounty.getId().toString();
        config.set(key + ".target", bounty.getTarget().getUniqueId().toString());
        config.set(key + ".extra", bounty.getExtra());
        config.set(key + ".last-modified", bounty.getLastModified());

//		config.set(key + ".hunters", bounty.getHunters().stream().map(hunter -> hunter.getUniqueId().toString()).collect(Collectors.toList()));

        config.createSection(key + ".up");
        for (OfflinePlayer increase : bounty.getContributors())
            config.set(key + ".up." + increase.getUniqueId().toString(), bounty.getContribution(increase));
    }
}
