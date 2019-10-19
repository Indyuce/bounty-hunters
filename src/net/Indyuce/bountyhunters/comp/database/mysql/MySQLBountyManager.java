package net.Indyuce.bountyhunters.comp.database.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.comp.database.MySQLProvider;
import net.Indyuce.bountyhunters.manager.BountyManager;

public class MySQLBountyManager extends BountyManager {
	private final MySQLProvider provider;

	public MySQLBountyManager(MySQLProvider provider) {
		this.provider = provider;

		Bukkit.getScheduler().runTaskAsynchronously(BountyHunters.getInstance(), () -> loadBounties());
	}

	private void loadBounties() {

		try {

			if (!provider.prepareStatement("SELECT * FROM information_schema.tables WHERE TABLE_NAME = 'bounties'").executeQuery().next())
				provider.prepareStatement("CREATE TABLE bounties(target VARCHAR(36), creator VARCHAR(36), reward DECIMAL, hunters TEXT, increased TEXT)").execute();

			ResultSet result = provider.prepareStatement("SELECT * FROM bounties").executeQuery();

			while (result.next()) {

				try {

					String creatorFormat = result.getString("creator");
					Bounty bounty = new Bounty(creatorFormat == null ? null : Bukkit.getOfflinePlayer(UUID.fromString(creatorFormat)),
							Bukkit.getOfflinePlayer(UUID.fromString(result.getString("target"))), result.getDouble("reward"));

					JsonObject increased = (JsonObject) new JsonParser().parse(result.getString("increased"));
					increased.entrySet().forEach(entry -> bounty.setBountyIncrease(UUID.fromString(entry.getKey()), entry.getValue().getAsDouble()));

					JsonArray hunters = (JsonArray) new JsonParser().parse(result.getString("hunters"));
					hunters.forEach(key -> bounty.addHunter(Bukkit.getOfflinePlayer(UUID.fromString(key.getAsString()))));

					registerBounty(bounty);
				} catch (IllegalArgumentException exception) {
					BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not load bounty from database: " + exception.getMessage());
				}
			}

		} catch (SQLException exception) {
			BountyHunters.getInstance().getLogger().log(Level.SEVERE, "Could not load bounty data from database: " + exception.getMessage());
		}
	}

	@Override
	public void saveBounties() {
		try {
			provider.prepareStatement("TRUNCATE TABLE bounties").execute();

			PreparedStatement save = provider.prepareStatement("INSERT INTO bounties VALUES (?,?,?,?,?)");
			for (Bounty bounty : BountyHunters.getInstance().getBountyManager().getBounties()) {

				JsonArray hunters = new JsonArray();
				bounty.getHunters().forEach(uuid -> hunters.add(uuid.toString()));

				JsonObject increased = new JsonObject();
				bounty.getPlayersWhoIncreased().forEach(key -> increased.addProperty(key.toString(), bounty.getIncreaseAmount(key)));

				save.setString(1, bounty.getTarget().getUniqueId().toString());
				save.setString(2, bounty.hasCreator() ? bounty.getCreator().getUniqueId().toString() : "null");
				save.setDouble(3, bounty.getReward());
				save.setString(4, hunters.toString());
				save.setString(5, increased.toString());

				save.addBatch();
			}

			save.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
