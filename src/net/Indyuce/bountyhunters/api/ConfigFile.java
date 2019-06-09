package net.Indyuce.bountyhunters.api;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import net.Indyuce.bountyhunters.BountyHunters;

public class ConfigFile {
	private Plugin plugin;
	private String path, name;

	private FileConfiguration config;

	public ConfigFile(String name) {
		this(BountyHunters.plugin, "", name);
	}

	public ConfigFile(Plugin plugin, String name) {
		this(plugin, "", name);
	}

	public ConfigFile(String path, String name) {
		this(BountyHunters.plugin, path, name);
	}

	public ConfigFile(Plugin plugin, String path, String name) {
		this.plugin = plugin;
		this.path = path;
		this.name = name;

		config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + path, name + ".yml"));
	}

	public void save() {
		try {
			config.save(new File(plugin.getDataFolder() + path, name + ".yml"));
		} catch (IOException e2) {
			plugin.getLogger().log(Level.SEVERE, "Could not save " + name + ".yml");
		}
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void setup() {

		// mkdir folder first in case it does not exist
		if (!new File(plugin.getDataFolder() + path).exists())
			new File(plugin.getDataFolder() + path).mkdir();

		if (!new File(plugin.getDataFolder() + path, name + ".yml").exists())
			try {
				new File(plugin.getDataFolder() + path, name + ".yml").createNewFile();
			} catch (IOException e) {
				plugin.getLogger().log(Level.SEVERE, "Could not generate " + name + ".yml");
			}
	}
}