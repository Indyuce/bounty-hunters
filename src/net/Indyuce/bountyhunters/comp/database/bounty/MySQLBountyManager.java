package net.Indyuce.bountyhunters.comp.database.bounty;

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

	@Override
	public void loadBounties() {
		try {
			ResultSet result = provider.prepareStatement("SELECT * FROM bounties").executeQuery();

			while (result.next()) {

				try {

					UUID creator = UUID.fromString(result.getString("creator"));
					UUID target = UUID.fromString(result.getString("target"));
					double reward = result.getDouble("reward");

					Bounty bounty = new Bounty(Bukkit.getOfflinePlayer(creator), Bukkit.getOfflinePlayer(target),
							reward);

					JsonObject increased = (JsonObject) new JsonParser().parse(result.getString("increased"));
					increased.entrySet().forEach(entry -> bounty.setBountyIncrease(UUID.fromString(entry.getKey()),
							entry.getValue().getAsDouble()));

					JsonArray hunters = (JsonArray) new JsonParser().parse(result.getString("hunters"));
					hunters.forEach(
							key -> bounty.addHunter(Bukkit.getOfflinePlayer(UUID.fromString(key.getAsString()))));

				} catch (IllegalArgumentException exception) {
					BountyHunters.getInstance().getLogger().log(Level.WARNING,
							"Could not load bounty from database: " + exception.getMessage());
				}
			}

		} catch (SQLException e) {
			BountyHunters.getInstance().getLogger().log(Level.SEVERE, "Could not load bounty data from database.");
			e.printStackTrace();
		}
	}

	@Override
	public void saveBounties() {
		try {
			PreparedStatement statement = provider.prepareStatement("DELETE * FROM bounties");
			statement.addBatch("INSERT INTO bounties(target,creator,reward,hunters,increased)");
			statement.addBatch("VALUES");
			for (Bounty bounty : BountyHunters.getInstance().getBountyManager().getBounties()) {

				JsonArray hunters = new JsonArray();
				bounty.getHunters().forEach(uuid -> hunters.add(uuid.toString()));

				JsonObject increased = new JsonObject();
				bounty.getPlayersWhoIncreased()
						.forEach(key -> increased.addProperty(key.toString(), bounty.getIncreaseAmount(key)));

				statement.addBatch("  (" + bounty.getTarget().getUniqueId().toString() + ","
						+ bounty.getCreator().getUniqueId().toString() + "," + bounty.getReward() + ","
						+ hunters.toString() + "," + increased.toString() + ")");
			}

			statement.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
