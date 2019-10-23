package net.Indyuce.bountyhunters.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.manager.HuntManager;

public class Bounty {
	private final UUID id;

	private final OfflinePlayer target;
	private final List<UUID> hunters = new ArrayList<>();

	private final LinkedHashMap<OfflinePlayer, Double> amount = new LinkedHashMap<>();
	private double extra;

	public Bounty(OfflinePlayer creator, OfflinePlayer target, double reward) {
		Validate.notNull(target, "Target cannot be null");
		Validate.notNull(creator, "Creator cannot be null");

		id = UUID.randomUUID();
		this.target = target;
		amount.put(creator, reward);
	}

	public Bounty(OfflinePlayer target, double reward) {
		this(UUID.randomUUID(), target, reward);
	}

	public Bounty(UUID id, OfflinePlayer target, double reward) {
		Validate.notNull(target, "Target cannot be null");

		this.id = id;
		this.target = target;
		extra = reward;
	}

	public UUID getId() {
		return id;
	}

	public double getReward() {
		double t = extra;
		for (double d : amount.values())
			t += d;
		return t;
	}

	public double getExtra() {
		return extra;
	}

	public OfflinePlayer getCreator() {
		if (amount.size() != 0)
			for (OfflinePlayer player : amount.keySet())
				return player;
		return null;
	}

	public OfflinePlayer getTarget() {
		return target;
	}

	public boolean hasCreator() {
		return amount.size() > 0;
	}

	public boolean hasCreator(OfflinePlayer player) {
		return hasCreator() && getCreator().equals(player);
	}

	public boolean hasTarget(OfflinePlayer player) {
		return target.equals(player);
	}

	public void addReward(double value) {
		setExtra(extra + value);
	}

	/*
	 * ADDS given value the mapped value; does NOT replace it
	 */
	public void addContribution(OfflinePlayer player, double value) {
		setContribution(player, (amount.containsKey(player) ? amount.get(player) : 0) + value);
	}

	public void setContribution(OfflinePlayer player, double value) {
		amount.put(player, value);
	}

	public void removeContribution(OfflinePlayer player) {
		amount.remove(player);
	}

	public Set<OfflinePlayer> getContributors() {
		return amount.keySet();
	}

	public boolean hasContributed(OfflinePlayer player) {
		return amount.containsKey(player);
	}

	public double getContribution(OfflinePlayer player) {
		return amount.get(player);
	}

	public void setExtra(double extra) {
		this.extra = Math.max(0, extra);
	}

	public boolean isAutoBounty() {
		return !hasCreator();
	}

	public List<UUID> getHunters() {
		return hunters;
	}

	public boolean hasHunter(OfflinePlayer player) {
		for (UUID hunter : hunters)
			if (hunter.equals(player.getUniqueId()))
				return true;
		return false;
	}

	public void addHunter(OfflinePlayer player) {

		HuntManager huntManager = BountyHunters.getInstance().getHuntManager();
		if (huntManager.isHunting(player))
			huntManager.getTargetBounty(player).removeHunter(player);

		huntManager.setHunting(player, this);
		hunters.add(player.getUniqueId());
	}

	public void removeHunter(OfflinePlayer player) {
		if (hasHunter(player)) {
			BountyHunters.getInstance().getHuntManager().stopHunting(player);
			hunters.remove(player.getUniqueId());
		}
	}
}
