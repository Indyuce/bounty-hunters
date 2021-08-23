package net.Indyuce.bountyhunters.comp.database.yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.ConfigFile;
import net.Indyuce.bountyhunters.api.player.OfflinePlayerData;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import net.Indyuce.bountyhunters.manager.LevelManager;
import net.Indyuce.bountyhunters.manager.PlayerDataManager;

public class YAMLPlayerDataManager extends PlayerDataManager {
	@Override
	public void saveData(PlayerData data) {
		ConfigFile config = new ConfigFile("/userdata", data.getUniqueId().toString());

		config.getConfig().set("level", data.getLevel());
		config.getConfig().set("successful-bounties", data.getSuccessfulBounties());
		config.getConfig().set("claimed-bounties", data.getClaimedBounties());
		config.getConfig().set("illegal-kills", data.getIllegalKills());
		config.getConfig().set("illegal-kill-streak", data.getIllegalKillStreak());
		config.getConfig().set("current-title", data.hasTitle() ? data.getTitle().getId() : null);
		config.getConfig().set("current-anim", data.hasAnimation() ? data.getAnimation().getId() : null);
		config.getConfig().set("redeem-heads", data.getRedeemableHeads().stream().map(uuid -> uuid.toString()).collect(Collectors.toList()));

		config.save();
	}

	@Override
	public void loadData(PlayerData data) {
		FileConfiguration config = new ConfigFile("/userdata", data.getUniqueId().toString()).getConfig();

		data.setLevel(config.getInt("level"));
		data.setSuccessfulBounties(config.getInt("successful-bounties"));
		data.setClaimedBounties(config.getInt("claimed-bounties"));
		data.setIllegalKills(config.getInt("illegal-kills"));
		data.setIllegalKillStreak(config.getInt("illegal-kill-streak"));

		if (config.contains("redeem-heads"))
			config.getStringList("redeem-heads").forEach(key -> data.addRedeemableHead(UUID.fromString(key)));

		LevelManager levelManager = BountyHunters.getInstance().getLevelManager();

		if (config.contains("current-anim"))
			try {
				Validate.isTrue(levelManager.hasAnimation(config.getString("current-anim")), "Could not load animation from " + data.getOfflinePlayer().getName());
				data.setAnimation(levelManager.getAnimation(config.getString("current-anim")));
			} catch (IllegalArgumentException exception) {
				data.log(exception.getMessage());
			}

		if (config.contains("current-title"))
			try {
				Validate.isTrue(levelManager.hasTitle(config.getString("current-title")), "Could not load title from " + data.getOfflinePlayer().getName());
				data.setTitle(levelManager.getTitle(config.getString("current-title")));
			} catch (IllegalArgumentException exception) {
				data.log(exception.getMessage());
			}
	}

	@Override
	public OfflinePlayerData loadOfflineData(OfflinePlayer player) {
		return new YAMLOfflinePlayerData(player);
	}

	public class YAMLOfflinePlayerData implements OfflinePlayerData {
		private final ConfigFile config;

		private YAMLOfflinePlayerData(OfflinePlayer player) {
			config = new ConfigFile("/userdata", player.getUniqueId().toString());
		}

		@Override
		public void addSuccessfulBounties(int value) {
			config.getConfig().set("successful-bounties", Math.max(0, value + config.getConfig().getInt("successful-bounties")));

			config.save();
		}

		@Override
		public void givePlayerHead(OfflinePlayer owner) {
			List<String> list = config.getConfig().contains("redeem-heads") ? config.getConfig().getStringList("redeem-heads") : new ArrayList<>();
			list.add(owner.getUniqueId().toString());
			config.getConfig().set("redeem-heads", list);

			config.save();
		}
	}
}
