package net.Indyuce.bountyhunters.api.player;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.bountyhunters.api.language.Language;

public class LeaderboardProfile {
	private final UUID uuid;
	private final String name, title;
	private final int successfulBounties, claimedBounties, level;

	/**
	 * Caches player data into a hunter profile so that it can be saved in the
	 * cached leaderboard config file
	 * 
	 * @param data
	 *            Player to retrieve data from
	 */
	public LeaderboardProfile(PlayerData data) {
		this.uuid = data.getUniqueId();
		this.name = data.getOfflinePlayer().getName();
		this.level = data.getLevel();
		this.title = data.hasTitle() ? data.getTitle().format() : Language.NO_TITLE.format();
		this.successfulBounties = data.getSuccessfulBounties();
		this.claimedBounties = data.getClaimedBounties();
	}

	/**
	 * Reads cached hunter profile data
	 * 
	 * @param config
	 *            Config to read data from
	 */
	public LeaderboardProfile(ConfigurationSection config) {
		this.uuid = UUID.fromString(config.getName());
		this.name = config.getString("name");
		this.title = config.getString("title");
		this.level = config.getInt("level");
		this.successfulBounties = config.getInt("successful-bounties");
		this.claimedBounties = config.getInt("claimed-bounties");
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public int getLevel() {
		return level;
	}

	public int getClaimedBounties() {
		return claimedBounties;
	}

	public int getSuccessfulBounties() {
		return successfulBounties;
	}

	public void save(FileConfiguration config) {
		config.set(uuid.toString() + ".name", name);
		config.set(uuid.toString() + ".title", title);
		config.set(uuid.toString() + ".level", level);
		config.set(uuid.toString() + ".successful-bounties", successfulBounties);
		config.set(uuid.toString() + ".claimed-bounties", claimedBounties);
	}
}
