package net.Indyuce.bountyhunters.manager;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.manager.HuntManager.HunterData;

public abstract class BountyManager {
	private final LinkedHashMap<UUID, Bounty> bounties = new LinkedHashMap<>();

	public BountyManager() {
		loadBounties();
	}
	
	public void unregisterBounty(Bounty bounty) {
		bounties.remove(bounty.getTarget().getUniqueId());
		bounty.getHunters().forEach(hunter -> {
			OfflinePlayer player = Bukkit.getOfflinePlayer(hunter);
			HunterData data = BountyHunters.getInstance().getHuntManager().getData(player);
			if (data.isCompassActive())
				data.hideParticles();
			BountyHunters.getInstance().getHuntManager().stopHunting(player);
		});
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
		return getBounty(target.getUniqueId());
	}

	public Bounty getBounty(UUID target) {
		return bounties.get(target);
	}

	public abstract void loadBounties();

	public abstract void saveBounties();
}
