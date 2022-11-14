package net.Indyuce.bountyhunters.api;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Bounty {
	private final UUID id;

	private final OfflinePlayer target;
	private final Set<OfflinePlayer> hunters = new HashSet<>();

	private final LinkedHashMap<OfflinePlayer, Double> amount = new LinkedHashMap<>();
	private double extra;

	/**
	 * Changed everytime the bounty is updated, current time by default so newly
	 * created bounties do not have to mind about this option
	 */
	private long lastUpdated = System.currentTimeMillis();

	/**
	 * Used to create a player-generated bounty. The result bounty will have a random UUID
	 *
	 * @param creator Bounty creator
	 * @param target  Bounty target
	 * @param reward  Bounty base reward. Can be increased later
	 */
	public Bounty(OfflinePlayer creator, OfflinePlayer target, double reward) {
		Validate.notNull(target, "Target cannot be null");
		Validate.notNull(target.getName(), "Target cannot be null");
		Validate.notNull(creator, "Creator cannot be null");

		id = UUID.randomUUID();
		this.target = target;
		amount.put(creator, reward);
	}

	/**
	 * Used to create a console/plugin-generated bounty. Result bounty will
	 * have a random UUID
	 *
	 * @param target Bounty target
	 * @param reward Bounty base reward. Can be increased later
	 */
	public Bounty(OfflinePlayer target, double reward) {
		this(UUID.randomUUID(), target, reward);
	}

	/**
	 * Used to load bounties from config files.
	 *
	 * @param id     Bounty ID
	 * @param target Bounty target
	 * @param reward Bounty base reward. Can be increased later
	 */
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

	@Deprecated
	public OfflinePlayer getCreator() {
		for (OfflinePlayer player : amount.keySet())
			return player;
		throw new NullPointerException("Bounty has no contributor");
	}

	public OfflinePlayer getTarget() {
		return target;
	}

	/**
	 * Current implementation of the bounty creator is bad
	 * because it does not support bounties created by console.
	 * <p>
	 * This method just takes the first input from the contribution map
	 * and calls it done. However that contribution map is empty when the
	 * bounty is created from the console, which means the first player
	 * to contribute in the bounty will be chosen as the bounty creator.
	 *
	 * @return Bounty creator
	 */
	@Deprecated
	public boolean hasCreator() {
		return amount.size() > 0;
	}

	@Deprecated
	public boolean hasCreator(OfflinePlayer player) {
		try {
			return getCreator().getUniqueId().equals(player.getUniqueId());
		} catch (NullPointerException noCreator) {
			return false;
		}
	}

	public boolean hasTarget(OfflinePlayer player) {
		return target.getUniqueId().equals(player.getUniqueId());
	}

	public void addReward(double value) {
		setExtra(extra + value);

		setLastModified(System.currentTimeMillis());
	}

	/**
	 * Adds some contribution from the given player. It does not
	 * erase the previous contribution value
	 */
	public void addContribution(OfflinePlayer player, double value) {
		setContribution(player, getContribution(player) + value);
	}

	public void setContribution(OfflinePlayer player, double value) {
		if (value <= 0)
			amount.remove(player);
		else
			amount.put(player, value);

		setLastModified(System.currentTimeMillis());
	}

	public void removeContribution(OfflinePlayer player) {
		amount.remove(player);
	}

	public Set<OfflinePlayer> getContributors() {
		return amount.keySet();
	}

	public boolean hasContributed(OfflinePlayer player) {
		return getContributors().contains(player);
	}

	public double getContribution(OfflinePlayer player) {
		@Nullable Double found = amount.get(player);
		return found == null ? 0 : found;
	}

	public void setExtra(double extra) {
		this.extra = Math.max(0, extra);
	}

	public long getLastModified() {
		return lastUpdated;
	}

    /**
     * Method does not return anything relevant if the
     * inactivity bounty removal option is toggled off.
     *
     * @return Time in millis before the bounty expires
     */
    public long getExpireDelay() {
        final long timeOut = BountyHunters.getInstance().getConfig().getLong("inactive-bounty-removal.time") * 60 * 60 * 1000;
        return Math.max(0, lastUpdated + timeOut - System.currentTimeMillis());
    }

	public void setLastModified(long lastModified) {
		this.lastUpdated = lastModified;
	}

	public Set<OfflinePlayer> getHunters() {
		return hunters;
	}

	public boolean hasHunter(OfflinePlayer player) {
		return hunters.contains(player);
	}

	public void addHunter(OfflinePlayer player) {

		final PlayerData data = PlayerData.get(player);
		if (data.isHunting())
			data.getHunting().getBounty().removeHunter(player);

		data.setHunting(this);
		hunters.add(player);
	}

	public void removeHunter(OfflinePlayer player) {
		if (hasHunter(player)) {
			PlayerData.get(player).stopHunting();
			hunters.remove(player);
		}
	}

	@Override
	public String toString() {

		String amountToString = amount.keySet().stream()
				.map(key -> key + "=" + amount.get(key))
				.collect(Collectors.joining(", ", "{", "}"));

		return "Bounty{" +
				"id=" + id +
				", target=" + target.getUniqueId() +
				", amount=" + amountToString +
				", extra=" + extra +
				", lastUpdated=" + lastUpdated +
				'}';
	}
}
