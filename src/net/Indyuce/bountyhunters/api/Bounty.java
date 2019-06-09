package net.Indyuce.bountyhunters.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.bountyhunters.BountyHunters;

public class Bounty {
	private double reward;
	private UUID creator, target;
	private List<UUID> hunters = new ArrayList<>();
	private Map<UUID, Double> up = new HashMap<>();

	/*
	 * creator is nullable since auto-bounties do not have any creator
	 */
	public Bounty(OfflinePlayer creator, OfflinePlayer target, double reward) {
		this(creator == null ? null : creator.getUniqueId(), target.getUniqueId(), reward);
	}

	public Bounty(UUID creator, UUID target, double reward) {
		this.creator = creator;
		this.target = target;
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
		return Bukkit.getOfflinePlayer(creator);
	}

	public OfflinePlayer getTarget() {
		return Bukkit.getOfflinePlayer(target);
	}

	public boolean hasCreator(OfflinePlayer player) {
		return creator != null ? creator.equals(player.getUniqueId()) : false;
	}

	public boolean hasCreator() {
		return creator != null;
	}

	public boolean hasTarget(OfflinePlayer player) {
		return target.equals(player.getUniqueId());
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
		return creator == null;
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
		BountyHunters.getHuntManager().setHunting(player, Bukkit.getOfflinePlayer(target));
		hunters.add(player.getUniqueId());
	}

	public void removeHunter(OfflinePlayer player) {
		if (hasHunter(player))
			BountyHunters.getHuntManager().stopHunting(player);
		hunters.remove(player.getUniqueId());
	}

	public void save(FileConfiguration config) {
		config.set(target.toString() + ".reward", reward);
		config.set(target.toString() + ".creator", hasCreator() ? creator.toString() : null);

		List<String> hunterList = new ArrayList<String>();
		for (UUID hunter : hunters)
			hunterList.add(hunter.toString());
		config.set(target.toString() + ".hunters", hunterList);

		config.createSection(target.toString() + ".up");
		for (UUID p : up.keySet())
			config.set(target.toString() + ".up." + p.toString(), up.get(p));
	}
}
