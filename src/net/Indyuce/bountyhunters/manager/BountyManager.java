package net.Indyuce.bountyhunters.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.restriction.BountyRestriction;
import net.Indyuce.bountyhunters.api.restriction.TeamRestriction;
import net.Indyuce.bountyhunters.gui.BountyEditor;
import net.Indyuce.bountyhunters.manager.HuntManager.HunterData;

public abstract class BountyManager {

	/*
	 * warning: bounty ID does NOT correspond to the target UUID! bounty ID used
	 * as key to store bounties however bounty target uuid is stored inside the
	 * bounty class instance
	 */
	private final LinkedHashMap<UUID, Bounty> bounties = new LinkedHashMap<>();

	/*
	 * list of bounty restrictions that must be verified when a bounty is
	 * claimed. makes implementing plugin compatibility and extra options much
	 * easier
	 */
	private final Set<BountyRestriction> restrictions = new HashSet<>();

	public BountyManager() {

		if (BountyHunters.getInstance().getConfig().getBoolean("claim-restrictions.team-mates"))
			registerClaimRestriction(new TeamRestriction());
	}

	public void unregisterBounty(Bounty bounty) {
		bounties.remove(bounty.getId());
		bounty.getHunters().forEach(hunter -> {
			HunterData data = BountyHunters.getInstance().getHuntManager().getData(hunter);
			if (data.isCompassActive())
				data.hideParticles();
			BountyHunters.getInstance().getHuntManager().stopHunting(hunter);
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
		if (bounties.containsKey(bounty.getId())) {
			BountyHunters.getInstance().getLogger().log(Level.WARNING, "Attempted to register bounty with duplicate ID " + bounty.getId());
			return;
		}

		bounties.put(bounty.getId(), bounty);
	}

	public Set<BountyRestriction> getClaimRestrictions() {
		return restrictions;
	}

	public void registerClaimRestriction(BountyRestriction restriction) {
		restrictions.add(restriction);
	}

	public Collection<Bounty> getBounties() {
		return bounties.values();
	}

	@Deprecated
	public boolean hasBounty(OfflinePlayer player) {
		return getBounty(player).isPresent();
	}

	public boolean hasBounty(UUID bountyId) {
		return bounties.containsKey(bountyId);
	}

	public Optional<Bounty> getBounty(OfflinePlayer target) {
		return bounties.values().stream().filter(bounty -> bounty.getTarget().equals(target)).findAny();
	}

	public Bounty getBounty(UUID bountyId) {
		return bounties.get(bountyId);
	}

	public Optional<Bounty> findByName(String name) {
		return bounties.values().stream().filter(bounty -> bounty.getTarget().getName().equalsIgnoreCase(name)).findAny();
	}

	public abstract void saveBounties();
}
