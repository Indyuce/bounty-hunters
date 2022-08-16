package net.Indyuce.bountyhunters.compat.database.mysql;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.compat.database.MySQLProvider;
import net.Indyuce.bountyhunters.compat.database.yaml.YAMLBountyManager;
import net.Indyuce.bountyhunters.manager.BountyManager;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class MySQLBountyManager extends BountyManager {
    private final MySQLProvider provider;

    public MySQLBountyManager(MySQLProvider provider) {
        this.provider = provider;
    }

    @Override
    public void loadBounties() {

        try {

            // Create table if non existent
            if (!provider.prepareStatement("SELECT * FROM information_schema.tables WHERE TABLE_NAME = '" + provider.getBountyDataTable() + "'").executeQuery().next())
                provider.prepareStatement(
                        "CREATE TABLE " + provider.getBountyDataTable() + "(id VARCHAR(36), target VARCHAR(36), extra DECIMAL, last_updated BIGINT, hunters TEXT, increased TEXT)")
                        .execute();

            // Check if database has the 'last_updated' column added in 2.3.6
            if (!provider.prepareStatement("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '" + provider.getDatabase()
                    + "' AND TABLE_NAME = '" + provider.getBountyDataTable() + "' AND COLUMN_NAME = 'last_updated'").executeQuery().next())
                provider.prepareStatement("ALTER TABLE " + provider.getBountyDataTable() + " ADD COLUMN last_updated BIGINT").execute();

            // Load ALL active bounties unlike player datas
            final ResultSet result = provider.prepareStatement("SELECT * FROM " + provider.getBountyDataTable()).executeQuery();
            while (result.next())
                try {

                    final Bounty bounty = new Bounty(UUID.fromString(result.getString("id")),
                            Bukkit.getOfflinePlayer(UUID.fromString(result.getString("target"))), result.getDouble("extra"));
                    final long lastUpdated = result.getLong("last_updated");
                    bounty.setLastModified(lastUpdated == 0 ? System.currentTimeMillis() : lastUpdated);

                    JsonObject increased = (JsonObject) new JsonParser().parse(result.getString("increased"));
                    increased.entrySet().forEach(entry -> bounty.addContribution(Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey())),
                            entry.getValue().getAsDouble()));

                    // JsonArray hunters = (JsonArray) new
                    // JsonParser().parse(result.getString("hunters"));
                    // hunters.forEach(key ->
                    // bounty.addHunter(Bukkit.getOfflinePlayer(UUID.fromString(key.getAsString()))));

                    registerBounty(bounty);
                } catch (IllegalArgumentException exception) {
                    BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not load bounty from database: " + exception.getMessage());
                }

        } catch (SQLException exception) {
            BountyHunters.getInstance().getLogger().log(Level.SEVERE, "Could not load bounty data from database: " + exception.getMessage());
        }
    }

    @Override
    public void saveBounties() {
        try {
            provider.prepareStatement("TRUNCATE TABLE " + provider.getBountyDataTable()).execute();

            PreparedStatement save = provider.prepareStatement("INSERT INTO " + provider.getBountyDataTable() + " VALUES (?,?,?,?,?,?)");
            for (Bounty bounty : BountyHunters.getInstance().getBountyManager().getBounties()) {

                JsonArray hunters = new JsonArray();
                // bounty.getHunters().forEach(uuid ->
                // hunters.add(uuid.toString()));

                JsonObject increased = new JsonObject();
                bounty.getContributors().forEach(key -> increased.addProperty(key.getUniqueId().toString(), bounty.getContribution(key)));

                save.setString(1, bounty.getId().toString());
                save.setString(2, bounty.getTarget().getUniqueId().toString());
                save.setDouble(3, bounty.getExtra());
                save.setLong(4, bounty.getLastModified());
                save.setString(5, hunters.toString());
                save.setString(6, increased.toString());

                save.addBatch();
            }

            save.executeBatch();

            /*
             * In case MySQL can't save bounty data, everything
             * is saved in a backup file instead
             */
        } catch (SQLException exception) {
            final YAMLBountyManager temp = new YAMLBountyManager("backup-data");
            temp.saveBounties();
            BountyHunters.getInstance().getLogger().log(Level.SEVERE,
                    "Could not save bounty data using MySQL. Temporarily saving data to 'backup-data.yml'! Do copy this file and save it somewhere else not to lose your data.");
            exception.printStackTrace();
        }
    }
}
