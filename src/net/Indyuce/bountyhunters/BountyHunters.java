package net.Indyuce.bountyhunters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.Indyuce.bountyhunters.api.ConfigFile;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import net.Indyuce.bountyhunters.command.AddBountyCommand;
import net.Indyuce.bountyhunters.command.BountiesCommand;
import net.Indyuce.bountyhunters.command.HuntersCommand;
import net.Indyuce.bountyhunters.command.completion.AddBountyCompletion;
import net.Indyuce.bountyhunters.command.completion.BountiesCompletion;
import net.Indyuce.bountyhunters.comp.BountyHuntersPlaceholders;
import net.Indyuce.bountyhunters.comp.Metrics;
import net.Indyuce.bountyhunters.comp.TownySupport;
import net.Indyuce.bountyhunters.comp.placeholder.DefaultParser;
import net.Indyuce.bountyhunters.comp.placeholder.PlaceholderAPIParser;
import net.Indyuce.bountyhunters.comp.placeholder.PlaceholderParser;
import net.Indyuce.bountyhunters.gui.PluginInventory;
import net.Indyuce.bountyhunters.gui.listener.GuiListener;
import net.Indyuce.bountyhunters.listener.BountyClaim;
import net.Indyuce.bountyhunters.listener.HuntListener;
import net.Indyuce.bountyhunters.listener.PlayerListener;
import net.Indyuce.bountyhunters.manager.BountyManager;
import net.Indyuce.bountyhunters.manager.HuntManager;
import net.Indyuce.bountyhunters.manager.LevelManager;
import net.Indyuce.bountyhunters.version.PluginVersion;
import net.Indyuce.bountyhunters.version.SpigotPlugin;
import net.Indyuce.bountyhunters.version.nms.NMSHandler;
import net.Indyuce.bountyhunters.version.nms.NMSHandler_Reflection;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class BountyHunters extends JavaPlugin {
	private static BountyHunters plugin;

	private PluginVersion version;
	private NMSHandler nms;
	private PlaceholderParser placeholderParser;

	private Economy economy;
	private Permission permission;

	private BountyManager bountyManager;
	private HuntManager huntManager;
	private LevelManager levelManager;

	private FileConfiguration leaderboard, messages;
	public boolean formattedNumbers;

	public void onEnable() {
		new SpigotPlugin(40610, this).checkForUpdate();

		try {
			version = new PluginVersion(Bukkit.getServer().getClass());
			getLogger().log(Level.INFO, "Detected Server Version: " + version.toString());

			// no reflection nms, each class
			// corresponds to a server version
			nms = (NMSHandler) Class.forName("net.Indyuce.bountyhunters.version.nms.NMSHandler_" + version.toString().substring(1)).newInstance();
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Your server version is not handled with NMS.");
			nms = new NMSHandler_Reflection();
		}

		// load first the plugin, then hunters and
		// last bounties (bounties need hunters setup)
		plugin = this;
		huntManager = new HuntManager();
		bountyManager = new BountyManager();

		saveDefaultConfig();

		// listeners
		Bukkit.getServer().getPluginManager().registerEvents(new BountyClaim(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new GuiListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new HuntListener(), this);

		if (getConfig().getBoolean("target-login-message.enabled"))
			Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
				private final String message = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("target-login-message.format"));

				@EventHandler(priority = EventPriority.HIGH)
				public void a(PlayerJoinEvent event) {
					Player player = event.getPlayer();
					if (bountyManager.hasBounty(player))
						event.setJoinMessage(message.replace("%player%", player.getName()).replace("%bounty%", new NumberFormat().format(bountyManager.getBounty(player).getReward())));
				}
			}, this);

		new Metrics(this);

		placeholderParser = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null ? new PlaceholderAPIParser() : new DefaultParser();
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new BountyHuntersPlaceholders().register();
			getLogger().log(Level.INFO, "Hooked onto PlaceholderAPI");
		}

		if (getServer().getPluginManager().getPlugin("Towny") != null && getConfig().getBoolean("plugin-compatibility.towny-bounty-friendly-fire")) {
			Bukkit.getPluginManager().registerEvents(new TownySupport(), this);
			getLogger().log(Level.INFO, "Hooked onto Towny");
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
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		levelManager = new LevelManager(new ConfigFile("levels").getConfig());

		ConfigFile messages = new ConfigFile("/language", "messages");
		for (Message key : Message.values()) {
			String path = key.name().toLowerCase().replace("_", "-");
			if (!messages.getConfig().contains(path))
				messages.getConfig().set(path, key.getDefault());
		}
		messages.save();

		ConfigFile items = new ConfigFile("/language", "items");
		for (CustomItem item : CustomItem.values()) {
			if (!items.getConfig().contains(item.name())) {
				items.getConfig().set(item.name() + ".name", item.getName());
				items.getConfig().set(item.name() + ".lore", item.getLore());
			}
			item.update(items.getConfig().getConfigurationSection(item.name()));
		}
		items.save();

		File userdataFolder = new File(getDataFolder() + "/userdata");
		if (!userdataFolder.exists())
			userdataFolder.mkdir();

		new ConfigFile("data").setup();
		leaderboard = new ConfigFile("/cache", "leaderboard").getConfig();

		/*
		 * load player data from all online players in case of /reload
		 */
		Bukkit.getOnlinePlayers().forEach(player -> PlayerData.load(player));

		/*
		 * only reload config files after levels.yml is loaded or else it can't
		 * load the file
		 */
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

		PlayerData.getLoaded().forEach(data -> data.saveFile());

		for (Player online : Bukkit.getOnlinePlayers())
			if (online.getOpenInventory() != null && online.getOpenInventory().getTopInventory().getHolder() instanceof PluginInventory)
				online.closeInventory();
	}

	public static BountyHunters getInstance() {
		return plugin;
	}

	public NMSHandler getNMS() {
		return nms;
	}

	public Economy getEconomy() {
		return economy;
	}

	public Permission getPermission() {
		return permission;
	}

	public BountyManager getBountyManager() {
		return bountyManager;
	}

	public HuntManager getHuntManager() {
		return huntManager;
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

	public PluginVersion getVersion() {
		return version;
	}

	public FileConfiguration getCachedLeaderboard() {
		return leaderboard;
	}

	public FileConfiguration getMessages() {
		return messages;
	}

	public PlaceholderParser getPlaceholderParser() {
		return placeholderParser;
	}

	public void reloadConfigFiles() {
		messages = new ConfigFile("/language", "messages").getConfig();

		formattedNumbers = getConfig().getBoolean("formatted-numbers");
	}
}