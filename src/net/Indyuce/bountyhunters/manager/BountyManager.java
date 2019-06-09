package net.Indyuce.bountyhunters.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.ConfigFile;

public class BountyManager {
	private Map<UUID, Bounty> bounties = new HashMap<>();

	public BountyManager() {

		// load bounties in the map
		FileConfiguration data = new ConfigFile("data").getConfig();
		for (String key : data.getKeys(false))
			registerBounty(new Bounty(data.getConfigurationSection(key)));
	}

	public void saveBounties() {
		ConfigFile data = new ConfigFile("data");

		// clear
		for (String s : data.getConfig().getKeys(false))
			data.getConfig().set(s, null);

		for (Bounty bounty : getBounties())
			bounty.save(data.getConfig());

		data.save();
	}

	public void unregisterBounty(Bounty bounty) {
		bounties.remove(bounty.getTarget().getUniqueId());
	}

	public void registerBounty(Bounty bounty) {
		bounties.put(bounty.getTarget().getUniqueId(), bounty);
	}

	public boolean hasBounty(OfflinePlayer player) {
		return bounties.containsKey(player.getUniqueId());
	}

	public Collection<Bounty> getBounties() {
		return bounties.values();
	}

	public Bounty getBounty(OfflinePlayer target) {
		return bounties.get(target.getUniqueId());
	}
}
