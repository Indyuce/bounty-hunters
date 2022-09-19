package net.Indyuce.bountyhunters.compat.database;

import net.Indyuce.bountyhunters.compat.database.mysql.MySQLBountyManager;
import net.Indyuce.bountyhunters.compat.database.mysql.MySQLPlayerDataManager;
import net.Indyuce.bountyhunters.manager.BountyManager;
import net.Indyuce.bountyhunters.manager.PlayerDataManager;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLProvider implements DataProvider {
    private final Connection connection;

    private final String host, database, username, password, args, playerDataTable, bountyDataTable;
    private final int port;

    public MySQLProvider(ConfigurationSection config) throws SQLException {
        Validate.notNull(host = config.getString("host"), "Could not load host");
        Validate.notNull(database = config.getString("database"), "Could not load database name");
        Validate.notNull(username = config.getString("username"), "Could not load username");
        Validate.notNull(password = config.getString("password"), "Could not load password");
        args = config.getString("extra-args");
        port = config.getInt("port");
        playerDataTable = config.getString("table-name.player-data", "playerData");
        bountyDataTable = config.getString("table-name.bounty-data", "bounties");

        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + args, username, password);
    }

    public String getDatabase() {
        return database;
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    public String getPlayerDataTable() {
        return playerDataTable;
    }

    public String getBountyDataTable() {
        return bountyDataTable;
    }

    @Override
    public PlayerDataManager providePlayerDatas() {
        return new MySQLPlayerDataManager(this);
    }

    @Override
    public BountyManager provideBounties() {
        return new MySQLBountyManager(this);
    }
}
