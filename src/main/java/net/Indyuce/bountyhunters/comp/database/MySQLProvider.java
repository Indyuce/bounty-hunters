package net.Indyuce.bountyhunters.comp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

import net.Indyuce.bountyhunters.comp.database.mysql.MySQLBountyManager;
import net.Indyuce.bountyhunters.comp.database.mysql.MySQLPlayerDataManager;
import net.Indyuce.bountyhunters.manager.BountyManager;
import net.Indyuce.bountyhunters.manager.PlayerDataManager;

public class MySQLProvider implements DataProvider {
	private final Connection connection;

	private final String host, database, username, password, args;
	private final int port;

	public MySQLProvider(ConfigurationSection config) throws SQLException {
		Validate.notNull(host = config.getString("host"), "Could not load host");
		Validate.notNull(database = config.getString("database"), "Could not load database name");
		Validate.notNull(username = config.getString("username"), "Could not load username");
		Validate.notNull(password = config.getString("password"), "Could not load password");
		args = config.getString("extra-args");
		port = config.getInt("port");

		connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + args, username, password);
	}

	public String getDatabase() {
		return database;
	}

	public PreparedStatement prepareStatement(String query) throws SQLException {
		return connection.prepareStatement(query);
	}

	@Override
	public PlayerDataManager providePlayerData() {
		return new MySQLPlayerDataManager(this);
	}

	@Override
	public BountyManager provideBounties() {
		return new MySQLBountyManager(this);
	}
}
