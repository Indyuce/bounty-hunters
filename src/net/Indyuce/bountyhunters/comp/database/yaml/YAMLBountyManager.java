package net.Indyuce.bountyhunters.comp.database.yaml;

import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
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
				BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not load bounty " + key);
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

		Bounty bounty = new Bounty(section.contains("creator") ? Bukkit.getOfflinePlayer(UUID.fromString(section.getString("creator"))) : null, Bukkit.getOfflinePlayer(UUID.fromString(section.getName())), section.getDouble("reward"));

		for (String key : section.getStringList("hunters"))
			bounty.addHunter(Bukkit.getOfflinePlayer(UUID.fromString(key)));
		if (section.contains("up"))
			for (String key : section.getConfigurationSection("up").getKeys(false))
				bounty.setBountyIncrease(Bukkit.getOfflinePlayer(UUID.fromString(key)), section.getDouble("up." + key));

		return bounty;
	}

	public void save(Bounty bounty, FileConfiguration config) {
		String key = bounty.getTarget().getUniqueId().toString();
		config.set(key + ".reward", bounty.getReward());
		config.set(key + ".creator", bounty.hasCreator() ? bounty.getCreator().getUniqueId().toString() : null);

		config.set(key + ".hunters", bounty.getHunters().stream().map(uuid -> uuid.toString()).collect(Collectors.toList()));

		config.createSection(key + ".up");
		for (UUID uuid : bounty.getPlayersWhoIncreased())
			config.set(key + ".up." + uuid.toString(), bounty.getIncreaseAmount(uuid));
	}
}
