package net.Indyuce.bountyhunters.comp.database.bounty;

import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.ConfigFile;
import net.Indyuce.bountyhunters.manager.BountyManager;

public class YAMLBountyManager extends BountyManager {
	public YAMLBountyManager() {
		super();
	}
	
	@Override
	public void loadBounties() {

		// load bounties in the map
		FileConfiguration data = new ConfigFile("data").getConfig();
		for (String key : data.getKeys(false))
			try {
				registerBounty(new Bounty(data.getConfigurationSection(key)));
			} catch (IllegalArgumentException exception) {
				BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not load bounty " + key);
			}
	}

	@Override
	public void saveBounties() {
		ConfigFile data = new ConfigFile("data");

		// clear
		for (String s : data.getConfig().getKeys(false))
			data.getConfig().set(s, null);

		for (Bounty bounty : getBounties())
			bounty.save(data.getConfig());

		data.save();
	}
}
