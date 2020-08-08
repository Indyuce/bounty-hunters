package net.Indyuce.bountyhunters.comp.database.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.google.gson.JsonParser;

import net.Indyuce.bountyhunters.BountyHunters;
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
			BountyHunters.getInstance().getLogger().log(Level.INFO, "Initializing player data..");

			/*
			 * check if the database has a playerData table, if not initialize
			 * it
			 */
			if (!provider.prepareStatement("SELECT * FROM information_schema.tables WHERE TABLE_NAME = 'playerData'").executeQuery().next())
				provider.prepareStatement("CREATE TABLE playerData(uuid VARCHAR(36), level INT, successful_bounties INT, claimed_bounties INT, illegal_kills INT, illegal_kill_streak INT, current_title TEXT, current_quote TEXT, redeem_heads JSON)").execute();

			/*
			 * check if the playerData table has a redeem_heads column. if not,
			 * initialize the column
			 */
			if (!provider.prepareStatement("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '" + provider.getDatabase() + "' AND TABLE_NAME = 'playerData' AND COLUMN_NAME = 'redeem_heads'").executeQuery().next())
				provider.prepareStatement("ALTER TABLE playerData ADD COLUMN redeem_heads JSON").execute();

			/*
			 * check if the playerData table has a last_updated column. if so,
			 * remove the column
			 */
			if (provider.prepareStatement("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '" + provider.getDatabase() + "' AND TABLE_NAME = 'playerData' AND COLUMN_NAME = 'last_updated'").executeQuery().next())
				provider.prepareStatement("ALTER TABLE playerData DROP COLUMN last_updated").execute();

		} catch (SQLException exception) {
			BountyHunters.getInstance().getLogger().log(Level.SEVERE, "Could not initialize database for player data: " + exception.getMessage());
		}
	}

	@Override
	public void saveData(PlayerData data) {
		try {
			String redeemHeads = data.getRedeemableHeads().stream().map(uuid -> "'" + uuid.toString() + "'").collect(Collectors.toList()).toString();
			redeemHeads = redeemHeads.substring(1, redeemHeads.length() - 1);

			provider.prepareStatement("UPDATE playerData SET level = " + data.getLevel() + ", successful_bounties = " + data.getSuccessfulBounties() + ", claimed_bounties = " + data.getClaimedBounties() + ", illegal_kills = " + data.getIllegalKills() + ", illegal_kill_streak = " + data.getIllegalKillStreak() + ", current_title = " + (data.hasTitle() ? "'" + data.getTitle().getId() + "'" : "NULL") + ", current_quote = " + (data.hasQuote() ? "'" + data.getQuote().getId() + "'" : "NULL") + ", redeem_heads = JSON_ARRAY(" + redeemHeads + ") WHERE uuid = '" + data.getUniqueId().toString() + "'").execute();

		} catch (SQLException exception) {
			BountyHunters.getInstance().getLogger().log(Level.SEVERE, "Could not save player data of " + data.getOfflinePlayer().getName() + ": " + exception.getMessage());
			BountyHunters.getInstance().getLogger().log(Level.SEVERE, "Parsed player data: " + data.toString());
		}
	}

	@Override
	public void loadData(PlayerData data) {
		try {
			ResultSet set = provider.prepareStatement("SELECT * FROM playerData WHERE uuid = '" + data.getUniqueId().toString() + "'").executeQuery();

			/*
			 * initialize player data. must NOT be done when the server shuts
			 * down, might overwhelm it.
			 */
			if (!set.next()) {
				provider.prepareStatement("INSERT INTO playerData VALUES ('" + data.getUniqueId().toString() + "', 0, 0, 0, 0, 0, null, null, JSON_ARRAY())").execute();
				return;
			}

			String redeemHeads = set.getString("redeem_heads");
			if (redeemHeads != null)
				new JsonParser().parse(redeemHeads).getAsJsonArray().forEach(key -> data.addRedeemableHead(UUID.fromString(key.getAsString())));

			data.setLevel(set.getInt("level"));
			data.setSuccessfulBounties(set.getInt("successful_bounties"));
			data.setClaimedBounties(set.getInt("claimed_bounties"));
			data.setIllegalKills(set.getInt("illegal_kills"));
			data.setIllegalKillStreak(set.getInt("illegal_kill_streak"));

			LevelManager levelManager = BountyHunters.getInstance().getLevelManager();

			String titleFormat = set.getString("current_title");
			if (titleFormat != null)
				try {
					Validate.isTrue(levelManager.hasTitle("current-title"), "Could not load title from " + data.getOfflinePlayer().getName());
					data.setTitle(levelManager.getTitle(titleFormat));
				} catch (IllegalArgumentException exception) {
					data.log(exception.getMessage());
				}

			String quoteFormat = set.getString("current_quote");
			if (quoteFormat != null)
				try {
					Validate.isTrue(levelManager.hasQuote(quoteFormat), "Could not load quote from " + data.getOfflinePlayer().getName());
					data.setQuote(levelManager.getQuote(quoteFormat));
				} catch (IllegalArgumentException exception) {
					data.log(exception.getMessage());
				}

		} catch (SQLException exception) {
			BountyHunters.getInstance().getLogger().log(Level.SEVERE, "Could not load player data of " + data.getOfflinePlayer().getName() + ": " + exception.getMessage());
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
				provider.prepareStatement("UPDATE playerData SET successful_bounties = successful_bounties + 1 WHERE uuid = '" + uuid.toString() + "'").execute();
			} catch (SQLException exception) {
				BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not update database player data (successful_bounties): " + exception.getMessage());
			}
		}

		@Override
		public void givePlayerHead(OfflinePlayer owner) {
			try {
				provider.prepareStatement("UPDATE playerData set redeem_heads = JSON_ARRAY_APPEND(redeem_heads, '$', '" + owner.getUniqueId().toString() + "') WHERE uuid = '" + uuid.toString() + "'").execute();
			} catch (SQLException exception) {
				BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not update database player data (redeem_heads): " + exception.getMessage());
			}
		}
	}
}
