package net.Indyuce.bountyhunters.comp.database.yaml;

import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.ConfigFile;
import net.Indyuce.bountyhunters.manager.BountyManager;

public class YAMLBountyManager extends BountyManager {
	public YAMLBountyManager() {

		// load bounties in the map
		FileConfiguration data = new ConfigFile("data").getConfig();
		for (String key : data.getKeys(false))
			try {
				registerBounty(load(data.getConfigurationSection(key)));
			} catch (IllegalArgumentException exception) {
				BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not load bounty " + key + ": " + exception.getMessage());
			}
	}

	@Override
	public void saveBounties() {
		ConfigFile data = new ConfigFile("data");

		data.getConfig().getKeys(false).forEach(key -> data.getConfig().set(key, null));
		getBounties().forEach(bounty -> save(bounty, data.getConfig()));

		data.save();
	}

	public Bounty load(ConfigurationSection section) {
		Validate.notNull(section, "Cuold not read bounty config");

		String target = section.getString("target");
		Validate.notNull(target, "Could not read bounty target");

		Bounty bounty = new Bounty(UUID.fromString(section.getName()), Bukkit.getOfflinePlayer(UUID.fromString(target)), section.getDouble("extra"));

		for (String key : section.getStringList("hunters"))
			bounty.addHunter(Bukkit.getOfflinePlayer(UUID.fromString(key)));
		if (section.contains("up"))
			for (String key : section.getConfigurationSection("up").getKeys(false))
				bounty.addContribution(Bukkit.getOfflinePlayer(UUID.fromString(key)), section.getDouble("up." + key));

		return bounty;
	}

	public void save(Bounty bounty, FileConfiguration config) {
		String key = bounty.getId().toString();
		config.set(key + ".target", bounty.getTarget().getUniqueId());
		config.set(key + ".extra", bounty.getExtra());

		config.set(key + ".hunters", bounty.getHunters().stream().map(uuid -> uuid.toString()).collect(Collectors.toList()));

		config.createSection(key + ".up");
		for (OfflinePlayer increase : bounty.getContributors())
			config.set(key + ".up." + increase.getUniqueId().toString(), bounty.getContribution(increase));
	}
}
