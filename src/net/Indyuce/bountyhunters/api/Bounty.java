package net.Indyuce.bountyhunters.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.manager.HuntManager;

public class Bounty {
	private double reward;
	private final OfflinePlayer creator, target;

	private final List<UUID> hunters = new ArrayList<>();
	private final Map<UUID, Double> up = new HashMap<>();

	/*
	 * creator is nullable since auto-bounties do not have any creator
	 */
	@Deprecated
	public Bounty(UUID creator, UUID target, double reward) {
		this(creator == null ? null : Bukkit.getOfflinePlayer(creator), Bukkit.getOfflinePlayer(target), reward);
	}

	public Bounty(OfflinePlayer creator, OfflinePlayer target, double reward) {
		Validate.notNull(target, "Bounty target must not be null");

		this.target = target;
		this.creator = creator;
		this.reward = reward;
	}

	public double getReward() {
		return reward;
	}

	public OfflinePlayer getCreator() {
		return creator;
	}

	public OfflinePlayer getTarget() {
		return target;
	}

	public boolean hasCreator() {
		return creator != null;
	}

	public boolean hasCreator(OfflinePlayer player) {
		return creator != null && creator.equals(player);
	}

	public boolean hasTarget(OfflinePlayer player) {
		return target.equals(player);
	}

	public void addToReward(double value) {
		reward += value;
	}

	public void addToReward(OfflinePlayer player, double value) {
		addToReward(value);
		setBountyIncrease(player, value);
	}

	public void setBountyIncrease(OfflinePlayer player, double value) {
		setBountyIncrease(player.getUniqueId(), value);
	}

	/*
	 * this method ADDS the given value to the mapped value, it does NOT replace it
	 */
	public void setBountyIncrease(UUID uuid, double value) {
		up.put(uuid, (up.containsKey(uuid) ? up.get(uuid) : 0) + value);
	}

	public Set<UUID> getPlayersWhoIncreased() {
		return up.keySet();
	}

	public double getIncreaseAmount(OfflinePlayer player) {
		return getIncreaseAmount(player.getUniqueId());
	}

	public double getIncreaseAmount(UUID uuid) {
		return up.get(uuid);
	}

	public void setReward(double reward) {
		this.reward = reward;
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

		huntManager.setHunting(player, target);
		hunters.add(player.getUniqueId());
	}

	public void removeHunter(OfflinePlayer player) {
		if (hasHunter(player)) {
			BountyHunters.getInstance().getHuntManager().stopHunting(player);
			hunters.remove(player.getUniqueId());
		}
	}
}
