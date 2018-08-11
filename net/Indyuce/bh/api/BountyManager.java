package net.Indyuce.bh.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.bh.ConfigData;
import net.Indyuce.bh.Main;

public class BountyManager {
	private Map<UUID, Bounty> bounties = new HashMap<UUID, Bounty>();

	public BountyManager() {

		// load bounties in the map
		FileConfiguration data = ConfigData.getCD(Main.plugin, "", "data");
		for (String s : data.getKeys(false))
			registerBounty(Bounty.load(data.getConfigurationSection(s)));
	}

	public void saveBounties() {
		FileConfiguration data = ConfigData.getCD(Main.plugin, "", "data");

		// clear
		for (String s : data.getKeys(false))
			data.set(s, null);

		for (Bounty bounty : getBounties())
			bounty.save(data);

		ConfigData.saveCD(Main.plugin, data, "", "data");

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
