package net.Indyuce.bh;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigData {
	public static void setupCD(Plugin plugin, String path, String name) {
		if (!new File(plugin.getDataFolder() + path).exists())
			new File(plugin.getDataFolder() + path).mkdir();

		File file = new File(plugin.getDataFolder() + path, name + ".yml");
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				Bukkit.getServer().getLogger().severe("§4Could not create " + name + ".yml!");
			}
	}

	public static FileConfiguration getCD(Plugin plugin, String path, String name) {
		return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + path, name + ".yml"));
	}

	public static void saveCD(Plugin plugin, FileConfiguration config, String path, String name) {
		try {
			config.save(new File(plugin.getDataFolder() + path, name + ".yml"));
		} catch (IOException e2) {
			Bukkit.getServer().getLogger().severe("§4Could not save " + name + ".yml!");
		}
	}
}