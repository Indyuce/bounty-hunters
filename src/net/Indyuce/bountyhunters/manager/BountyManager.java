package net.Indyuce.bountyhunters.manager;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.gui.BountyEditor;
import net.Indyuce.bountyhunters.manager.HuntManager.HunterData;

public abstract class BountyManager {
	private final LinkedHashMap<UUID, Bounty> bounties = new LinkedHashMap<>();

	public void unregisterBounty(Bounty bounty) {
		bounties.remove(bounty.getTarget().getUniqueId());
		bounty.getHunters().forEach(hunter -> {
			OfflinePlayer player = Bukkit.getOfflinePlayer(hunter);
			HunterData data = BountyHunters.getInstance().getHuntManager().getData(player);
			if (data.isCompassActive())
				data.hideParticles();
			BountyHunters.getInstance().getHuntManager().stopHunting(player);
		});

		/*
		 * checks for online admins who opened the bounty editor for that
		 * specific bounty and close GUIs
		 */
		for (Player online : Bukkit.getOnlinePlayers())
			if (online.getOpenInventory() != null && online.getOpenInventory().getTopInventory().getHolder() instanceof BountyEditor)
				if (((BountyEditor) online.getOpenInventory().getTopInventory().getHolder()).getBounty().equals(bounty))
					online.closeInventory();
	}

	public void registerBounty(Bounty bounty) {
		bounties.put(bounty.getTarget().getUniqueId(), bounty);
	}

	public boolean hasBounty(OfflinePlayer player) {
		return hasBounty(player.getUniqueId());
	}

	public boolean hasBounty(UUID uuid) {
		return bounties.containsKey(uuid);
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
	
	public Optional<Bounty> findByName(String name) {
		return bounties.values().stream().filter(bounty -> bounty.getTarget().getName().equalsIgnoreCase(name)).findAny();
	}

	public abstract void saveBounties();
}
