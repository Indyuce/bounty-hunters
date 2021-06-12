package net.Indyuce.bountyhunters.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.player.PlayerData;

public class Bounty {
	private final UUID id;

	private final OfflinePlayer target;
	private final List<OfflinePlayer> hunters = new ArrayList<>();

	private final LinkedHashMap<OfflinePlayer, Double> amount = new LinkedHashMap<>();
	private double extra;

	/*
	 * changed everytime the bounty is updated, current time by default so newly
	 * created bounties do not have to mind about this option
	 */
	private long lastUpdated = System.currentTimeMillis();

	public Bounty(OfflinePlayer creator, OfflinePlayer target, double reward) {
		Validate.notNull(target, "Target cannot be null");
		Validate.notNull(target.getName(), "Couldn't find target");
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
		for (OfflinePlayer player : amount.keySet())
			return player;
		throw new NullPointerException("Bounty has no contributor");
	}

	public OfflinePlayer getTarget() {
		return target;
	}

	/*
	 * TODO Save ONE boolean field to know if the bounty was created by the
	 * console or by a player. That way the creator is simply the first key of
	 * the 'amount' map. ATM there's no way to tell which entity created the
	 * bounty, only which entity contributed last to it
	 */
	public boolean hasCreator() {
		return amount.size() > 0;
	}

	public boolean hasCreator(OfflinePlayer player) {
		return hasCreator() && getCreator().getUniqueId().equals(player.getUniqueId());
	}

	public boolean hasTarget(OfflinePlayer player) {
		return target.getUniqueId().equals(player.getUniqueId());
	}

	public void addReward(double value) {
		setExtra(extra + value);
	}

	/*
	 * ADDS given value the mapped value; does NOT replace it
	 */
	public void addContribution(OfflinePlayer player, double value) {
		setContribution(player, getContribution(player) + value);
	}

	public void setContribution(OfflinePlayer player, double value) {
		lastUpdated = System.currentTimeMillis();
		amount.put(player, value);
	}

	public void removeContribution(OfflinePlayer player) {
		amount.remove(player);
	}

	public Set<OfflinePlayer> getContributors() {
		return amount.keySet();
	}

	public boolean hasContributed(OfflinePlayer player) {
		for (OfflinePlayer contributor : amount.keySet())
			if (contributor.getUniqueId().equals(player.getUniqueId()))
				return true;
		return false;
	}

	public double getContribution(OfflinePlayer player) {
		return hasContributed(player) ? amount.get(player) : 0;
	}

	public void setExtra(double extra) {
		this.extra = Math.max(0, extra);
	}

	public long getLastModified() {
		return lastUpdated;
	}

	public void setLastModified(long lastModified) {
		this.lastUpdated = lastModified;
	}

	public List<OfflinePlayer> getHunters() {
		return hunters;
	}

	public boolean hasHunter(OfflinePlayer player) {
		for (OfflinePlayer hunter : hunters)
			if (hunter.getUniqueId().equals(player.getUniqueId()))
				return true;
		return false;
	}

	public void addHunter(OfflinePlayer player) {

		PlayerData data = BountyHunters.getInstance().getPlayerDataManager().get(player);
		if (data.isHunting())
			data.getHunting().getBounty().removeHunter(player);

		data.setHunting(this);
		hunters.add(player);
	}

	public void removeHunter(OfflinePlayer player) {
		if (hasHunter(player)) {
			BountyHunters.getInstance().getPlayerDataManager().get(player).stopHunting();
			hunters.remove(player);
		}
	}
}
