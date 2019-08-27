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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.bountyhunters.BountyHunters;

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

	public Bounty(ConfigurationSection section) {
		this(section.contains("creator") ? UUID.fromString(section.getString("creator")) : null, UUID.fromString(section.getName()), section.getDouble("reward"));
		for (String key : section.getStringList("hunters"))
			addHunter(Bukkit.getOfflinePlayer(UUID.fromString(key)));
		if (section.contains("up"))
			for (String key : section.getConfigurationSection("up").getKeys(false))
				setBountyIncrease(Bukkit.getOfflinePlayer(UUID.fromString(key)), section.getDouble("up." + key));
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

	public void addToReward(OfflinePlayer player, double value) {
		reward += value;
		if (player != null)
			setBountyIncrease(player, value);
	}

	public void setBountyIncrease(OfflinePlayer player, double value) {
		up.put(player.getUniqueId(), (up.containsKey(player.getUniqueId()) ? up.get(player.getUniqueId()) : 0) + value);
	}

	public Set<UUID> getPlayersWhoIncreased() {
		return up.keySet();
	}

	public double getIncreaseAmount(OfflinePlayer player) {
		return up.get(player.getUniqueId());
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

		if (BountyHunters.getInstance().getHuntManager().isHunting(player))
			BountyHunters.getInstance().getHuntManager().getTargetBounty(player).removeHunter(player);
		
		BountyHunters.getInstance().getHuntManager().setHunting(player, target);
		hunters.add(player.getUniqueId());
	}

	public void removeHunter(OfflinePlayer player) {
		if (hasHunter(player))
			BountyHunters.getInstance().getHuntManager().stopHunting(player);
		hunters.remove(player.getUniqueId());
	}

	public void save(FileConfiguration config) {
		config.set(target.getUniqueId().toString() + ".reward", reward);
		config.set(target.getUniqueId().toString() + ".creator", hasCreator() ? creator.getUniqueId().toString() : null);

		List<String> hunterList = new ArrayList<>();
		for (UUID hunter : hunters)
			hunterList.add(hunter.toString());
		config.set(target.getUniqueId().toString() + ".hunters", hunterList);

		config.createSection(target.getUniqueId().toString() + ".up");
		for (UUID player : up.keySet())
			config.set(target.getUniqueId().toString() + ".up." + player.toString(), up.get(player));
	}
}
