package net.Indyuce.bh.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.bh.Main;
import net.Indyuce.bh.api.event.BountyCreateEvent;
import net.Indyuce.bh.resource.BountyCause;

public class Bounty {
	private double reward;
	private OfflinePlayer creator;
	private OfflinePlayer target;
	private List<OfflinePlayer> hunters;
	private Map<OfflinePlayer, Double> up = new HashMap<OfflinePlayer, Double>();

	public Bounty(OfflinePlayer creator, OfflinePlayer target, double reward) {
		this(creator, target, reward, new ArrayList<OfflinePlayer>());
	}

	public Bounty(OfflinePlayer creator, OfflinePlayer target, double reward, List<OfflinePlayer> hunters) {
		this.creator = creator;
		this.target = target;
		this.reward = reward;
		this.hunters = hunters;
	}

	public double getReward() {
		return reward;
	}

	public boolean hasCreator(OfflinePlayer player) {
		return creator != null ? creator.getName().equals(player.getName()) : false;
	}

	public boolean hasCreator() {
		return creator != null;
	}

	public OfflinePlayer getCreator() {
		return creator;
	}

	public OfflinePlayer getTarget() {
		return target;
	}

	public boolean hasTarget(OfflinePlayer player) {
		return target.getName().equals(player.getName());
	}

	public void addToReward(OfflinePlayer player, double value) {
		this.reward += value;
		if (player != null)
			setBountyIncrease(player, value);
	}

	public void setBountyIncrease(OfflinePlayer player, double value) {
		up.put(player, (up.containsKey(player) ? up.get(player) : 0) + value);
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	public boolean isAutoBounty() {
		return creator == null;
	}

	public List<OfflinePlayer> getHuntingPlayers() {
		return hunters;
	}

	public boolean isHunting(OfflinePlayer player) {
		for (OfflinePlayer hunter : hunters)
			if (hunter.getName().equals(player.getName()))
				return true;
		return false;
	}

	public Set<OfflinePlayer> getPlayersWhoIncreased() {
		return up.keySet();
	}

	public double getIncreaseAmount(OfflinePlayer player) {
		return up.get(player);
	}

	public void removeHunter(OfflinePlayer player) {
		hunters.remove(player);
	}

	public void addHunter(OfflinePlayer player) {
		hunters.add(player);
	}

	public void register() {
		Main.getBountyManager().registerBounty(this);
		Bukkit.getPluginManager().callEvent(new BountyCreateEvent(this, BountyCause.PLUGIN));
	}

	public void unregister() {
		Main.getBountyManager().unregisterBounty(this);
	}

	public void save(FileConfiguration config) {
		config.set(getTarget().getName() + ".reward", getReward());
		config.set(getTarget().getName() + ".creator", hasCreator() ? getCreator().getName() : null);

		List<String> stringHunters = new ArrayList<String>();
		for (OfflinePlayer p : hunters)
			stringHunters.add(p.getName());
		config.set(getTarget().getName() + ".hunters", stringHunters);

		config.createSection(getTarget().getName() + ".up");
		for (OfflinePlayer p : up.keySet())
			config.set(getTarget().getName() + ".up." + p.getName(), up.get(p));
	}

	@SuppressWarnings("deprecation")
	public static Bounty load(ConfigurationSection section) {
		OfflinePlayer target = Bukkit.getOfflinePlayer(section.getName());
		OfflinePlayer creator = section.contains("creator") ? Bukkit.getOfflinePlayer(section.getString("creator")) : null;
		double reward = section.getDouble("reward");

		List<OfflinePlayer> hunters = new ArrayList<OfflinePlayer>();
		for (String s1 : section.getStringList("hunters")) {
			OfflinePlayer t = Bukkit.getOfflinePlayer(s1);
			if (t != null)
				hunters.add(t);
		}

		Bounty bounty = new Bounty(creator, target, reward, hunters);

		if (section.contains("up"))
			for (String s : section.getConfigurationSection("up").getKeys(false))
				bounty.setBountyIncrease(Bukkit.getOfflinePlayer(s), section.getDouble("up." + s));

		return bounty;
	}
}
