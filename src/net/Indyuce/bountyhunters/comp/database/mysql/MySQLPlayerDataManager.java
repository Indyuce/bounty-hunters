package net.Indyuce.bountyhunters.comp.database.mysql;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.ConfigFile;
import net.Indyuce.bountyhunters.api.player.OfflinePlayerData;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import net.Indyuce.bountyhunters.comp.database.MySQLProvider;
import net.Indyuce.bountyhunters.manager.LevelManager;
import net.Indyuce.bountyhunters.manager.PlayerDataManager;

public class MySQLPlayerDataManager extends PlayerDataManager {
	private final MySQLProvider provider;

	public MySQLPlayerDataManager(MySQLProvider provider) {
		this.provider = provider;

		Bukkit.getScheduler().runTaskAsynchronously(BountyHunters.getInstance(), () -> initialize());
	}

	private void initialize() {
		try {

			if (!provider.prepareStatement("SELECT * FROM information_schema.tables WHERE TABLE_NAME = 'playerData'").executeQuery().next())
				provider.prepareStatement("CREATE TABLE bounties(target VARCHAR(36), creator VARCHAR(36), reward DECIMAL, hunters TEXT, increased TEXT)").execute();

			
			
		} catch (SQLException exception) {
			BountyHunters.getInstance().getLogger().log(Level.SEVERE, "Could not load player data from database: " + exception.getMessage());
			Bukkit.getPluginManager().disablePlugin(BountyHunters.getInstance());
		}
	}

	@Override
	public void saveData(PlayerData data) {
		ConfigFile config = new ConfigFile("/userdata", data.getUniqueId().toString());

		config.getConfig().set("level", data.getLevel());
		config.getConfig().set("successful-bounties", data.getSuccessfulBounties());
		config.getConfig().set("claimed-bounties", data.getClaimedBounties());
		config.getConfig().set("illegal-kills", data.getIllegalKills());
		config.getConfig().set("illegal-kill-streak", data.getIllegalKillStreak());
		config.getConfig().set("current-title", data.hasTitle() ? data.getTitle().getId() : null);
		config.getConfig().set("current-quote", data.hasQuote() ? data.getQuote().getId() : null);

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

		LevelManager levelManager = BountyHunters.getInstance().getLevelManager();

		if (config.contains("current-quote"))
			try {
				Validate.isTrue(levelManager.hasQuote(config.getString("current-quote")), "Could not load quote from " + data.getOfflinePlayer().getName());
				data.setQuote(levelManager.getQuote(config.getString("current-quote")));
			} catch (IllegalArgumentException exception) {
				data.log(exception.getMessage());
			}

		if (config.contains("current-title"))
			try {
				Validate.isTrue(levelManager.hasTitle("current-title"), "Could not load title from " + data.getOfflinePlayer().getName());
				data.setTitle(BountyHunters.getInstance().getLevelManager().getTitle(config.getString("current-title")));
			} catch (IllegalArgumentException exception) {
				data.log(exception.getMessage());
			}
	}

	@Override
	public OfflinePlayerData loadOfflineData(OfflinePlayer player) {
		return new MySQLOfflinePlayerData(player);
	}

	public class MySQLOfflinePlayerData implements OfflinePlayerData {
		private final UUID uuid;

		private MySQLOfflinePlayerData(OfflinePlayer player) {
			uuid = player.getUniqueId();
		}

		@Override
		public void addSuccessfulBounties(int value) {
			try {
				provider.prepareStatement("UPDATE playerData WHERE uuid = '" + uuid.toString() + "' SET successful_bounties = successful_bounties + 1");
			} catch (SQLException exception) {
				BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not update database player data (successful_bounties): " + exception.getMessage());
			}
		}
	}
}
