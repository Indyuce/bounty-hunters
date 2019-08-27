package net.Indyuce.bountyhunters.api.player;

import org.bukkit.OfflinePlayer;

import net.Indyuce.bountyhunters.api.ConfigFile;

public class OfflinePlayerData implements PlayerDataInterface {
	private ConfigFile config;

	OfflinePlayerData(OfflinePlayer player) {
		config = new ConfigFile("/userdata", player.getUniqueId().toString());
	}

	public int getSuccessfulBounties() {
		return config.getConfig().getInt("successful-bounties");
	}

	public void setSuccessfulBounties(int value) {
		config.getConfig().set("successful-bounties", value);
	}

	@Override
	public void addSuccessfulBounties(int value) {
		setSuccessfulBounties(getSuccessfulBounties() + value);
	}

	public void save() {
		config.save();
	}
}
