package net.Indyuce.bountyhunters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.Indyuce.bountyhunters.api.BountyManager;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.HuntManager;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.PlayerData;
import net.Indyuce.bountyhunters.command.AddBountyCommand;
import net.Indyuce.bountyhunters.command.BountiesCommand;
import net.Indyuce.bountyhunters.command.HuntersCommand;
import net.Indyuce.bountyhunters.command.completion.AddBountyCompletion;
import net.Indyuce.bountyhunters.command.completion.BountiesCompletion;
import net.Indyuce.bountyhunters.comp.BountyHuntersPlaceholders;
import net.Indyuce.bountyhunters.comp.Metrics;
import net.Indyuce.bountyhunters.gui.PluginInventory;
import net.Indyuce.bountyhunters.gui.listener.GuiListener;
import net.Indyuce.bountyhunters.listener.BountyClaim;
import net.Indyuce.bountyhunters.listener.MainListener;
import net.Indyuce.bountyhunters.listener.UpdateNotify;
import net.Indyuce.bountyhunters.version.PluginVersion;
import net.Indyuce.bountyhunters.version.SpigotPlugin;
import net.Indyuce.bountyhunters.version.nms.NMSHandler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class BountyHunters extends JavaPlugin {

	// plugin
	public static BountyHunters plugin;
	private static PluginVersion version;
	private static SpigotPlugin spigotPlugin;

	// systems
	private static BountyManager bountyManager;
	private static HuntManager huntManager;
	private static Economy economy;
	private static Permission permission;

	// no reflection nms
	public static NMSHandler nms;

	// cached config files
	private static FileConfiguration levels;

	public void onEnable() {

		// check for latest version
		spigotPlugin = new SpigotPlugin(this, 40610);
		if (spigotPlugin.isOutOfDate())
			for (String s : spigotPlugin.getOutOfDateMessage())
				getLogger().log(Level.INFO, "\u001B[32m" + s + "\u001B[37m");

		// load first the plugin, then hunters and
		// last bounties (bounties need hunters setup)
		plugin = this;
		huntManager = new HuntManager();
		bountyManager = new BountyManager();

		// listeners
		Bukkit.getServer().getPluginManager().registerEvents(new BountyClaim(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new MainListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new GuiListener(), this);

		saveDefaultConfig();

		if (getConfig().getBoolean("update-notify"))
			Bukkit.getServer().getPluginManager().registerEvents(new UpdateNotify(), this);

		try {
			version = new PluginVersion(Bukkit.getServer().getClass());
			getLogger().log(Level.INFO, "Detected Server Version: " + version.toString());

			// no reflection nms, each class
			// corresponds to a server version
			nms = (NMSHandler) Class.forName("net.Indyuce.bountyhunters.version.nms.NMSHandler_" + version.toString().substring(1)).newInstance();
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Your server version is not compatible.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		new Metrics(this);

		// placeholderpi compatibility
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new BountyHuntersPlaceholders().register();
			getLogger().log(Level.INFO, "Hooked onto PlaceholderAPI");
		}

		// vault compatibility
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		RegisteredServiceProvider<Permission> permProvider = getServer().getServicesManager().getRegistration(Permission.class);
		if (economyProvider != null && permProvider != null) {
			economy = economyProvider.getProvider();
			permission = permProvider.getProvider();
		} else {
			getLogger().log(Level.SEVERE, "Couldn't load Vault. Disabling...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		try {
			File file = new File(getDataFolder(), "levels.yml");
			if (!file.exists())
				Files.copy(BountyHunters.plugin.getResource("default/levels.yml"), file.getAbsoluteFile().toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		FileConfiguration messages = ConfigData.getCD(this, "/language", "messages");
		for (Message pa : Message.values()) {
			String path = pa.name().toLowerCase().replace("_", "-");
			if (!messages.contains(path))
				messages.set(path, pa.getDefault());
		}
		ConfigData.saveCD(this, messages, "/language", "messages");

		FileConfiguration items = ConfigData.getCD(this, "/language", "items");
		for (CustomItem i : CustomItem.values()) {
			if (!items.contains(i.name())) {
				items.set(i.name() + ".name", i.getName());
				items.set(i.name() + ".lore", i.getLore());
			}
			i.update(items);
		}
		ConfigData.saveCD(this, items, "/language", "items");

		File userdataFolder = new File(getDataFolder() + "/userdata");
		if (!userdataFolder.exists())
			userdataFolder.mkdir();

		ConfigData.setupCD(this, "", "data");
		for (Player p : Bukkit.getOnlinePlayers())
			PlayerData.setup(p);

		// after levels.yml was loaded only
		// else it can't load the file
		reloadConfigFiles();

		// commands
		getCommand("addbounty").setExecutor(new AddBountyCommand());
		getCommand("bounties").setExecutor(new BountiesCommand());
		getCommand("hunters").setExecutor(new HuntersCommand());

		getCommand("addbounty").setTabCompleter(new AddBountyCompletion());
		getCommand("bounties").setTabCompleter(new BountiesCompletion());
	}

	public void onDisable() {
		bountyManager.saveBounties();

		for (PlayerData playerData : PlayerData.getPlayerDatas())
			playerData.saveFile();

		for (Player t : Bukkit.getOnlinePlayers())
			if (t.getOpenInventory() != null)
				if (t.getOpenInventory().getTopInventory().getHolder() instanceof PluginInventory)
					t.closeInventory();
	}

	public static NMSHandler getNMS() {
		return nms;
	}

	public static Economy getEconomy() {
		return economy;
	}

	public static Permission getPermission() {
		return permission;
	}

	public static BountyManager getBountyManager() {
		return bountyManager;
	}

	public static HuntManager getHuntManager() {
		return huntManager;
	}

	public static FileConfiguration getLevelsConfigFile() {
		return levels;
	}

	public static PluginVersion getVersion() {
		return version;
	}

	public static SpigotPlugin getSpigotPlugin() {
		return spigotPlugin;
	}

	public void reloadConfigFiles() {
		levels = ConfigData.getCD(BountyHunters.plugin, "", "levels");
	}
}