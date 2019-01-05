package net.Indyuce.bountyhunters.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.ConfigData;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent.BountyCause;

public class BountyManager {
	private Map<UUID, Bounty> bounties = new HashMap<>();

	public BountyManager() {

		// load bounties in the map
		FileConfiguration data = ConfigData.getCD(BountyHunters.plugin, "", "data");
		for (String s : data.getKeys(false))
			registerBounty(Bounty.load(data.getConfigurationSection(s)));
	}

	public void saveBounties() {
		FileConfiguration data = ConfigData.getCD(BountyHunters.plugin, "", "data");

		// clear
		for (String s : data.getKeys(false))
			data.set(s, null);

		for (Bounty bounty : getBounties())
			bounty.save(data);

		ConfigData.saveCD(BountyHunters.plugin, data, "", "data");
	}

	public void unregisterBounty(Bounty bounty) {
		bounties.remove(bounty.getTarget().getUniqueId());
		for (UUID hunter : bounty.getHunters())
			BountyHunters.getHuntManager().stopHunting(Bukkit.getOfflinePlayer(hunter));
	}

	public void registerBounty(Bounty bounty) {
		Bukkit.getPluginManager().callEvent(new BountyCreateEvent(bounty, BountyCause.PLUGIN));
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
