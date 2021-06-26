package net.Indyuce.bountyhunters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
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

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.ConfigFile;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.HunterLeaderboard;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.account.BankAccount;
import net.Indyuce.bountyhunters.api.account.PlayerBankAccount;
import net.Indyuce.bountyhunters.api.account.SimpleBankAccount;
import net.Indyuce.bountyhunters.api.language.Language;
import net.Indyuce.bountyhunters.api.language.Message;
import net.Indyuce.bountyhunters.command.AddBountyCommand;
import net.Indyuce.bountyhunters.command.BountiesCommand;
import net.Indyuce.bountyhunters.command.HuntersCommand;
import net.Indyuce.bountyhunters.command.RedeemBountyHeadsCommand;
import net.Indyuce.bountyhunters.command.completion.AddBountyCompletion;
import net.Indyuce.bountyhunters.command.completion.BountiesCompletion;
import net.Indyuce.bountyhunters.comp.Metrics;
import net.Indyuce.bountyhunters.comp.database.DataProvider;
import net.Indyuce.bountyhunters.comp.database.MySQLProvider;
import net.Indyuce.bountyhunters.comp.database.YAMLDataProvider;
import net.Indyuce.bountyhunters.comp.flags.ResidenceFlags;
import net.Indyuce.bountyhunters.comp.flags.WorldGuardFlags;
import net.Indyuce.bountyhunters.comp.placeholder.BountyHuntersPlaceholders;
import net.Indyuce.bountyhunters.comp.placeholder.DefaultParser;
import net.Indyuce.bountyhunters.comp.placeholder.PlaceholderAPIParser;
import net.Indyuce.bountyhunters.comp.placeholder.PlaceholderParser;
import net.Indyuce.bountyhunters.comp.social.BungeeFriendsSupport;
import net.Indyuce.bountyhunters.comp.social.PartyAndFriendsSupport;
import net.Indyuce.bountyhunters.comp.social.TownySupport;
import net.Indyuce.bountyhunters.gui.PluginInventory;
import net.Indyuce.bountyhunters.gui.listener.GuiListener;
import net.Indyuce.bountyhunters.listener.BountyClaim;
import net.Indyuce.bountyhunters.listener.HeadHunting;
import net.Indyuce.bountyhunters.listener.HuntListener;
import net.Indyuce.bountyhunters.listener.PlayerListener;
import net.Indyuce.bountyhunters.listener.RestrictionListener;
import net.Indyuce.bountyhunters.listener.log.ClaimLog;
import net.Indyuce.bountyhunters.listener.log.ExpireLog;
import net.Indyuce.bountyhunters.listener.log.LevelUpLog;
import net.Indyuce.bountyhunters.manager.BountyManager;
import net.Indyuce.bountyhunters.manager.LevelManager;
import net.Indyuce.bountyhunters.manager.PlayerDataManager;
import net.Indyuce.bountyhunters.version.PluginVersion;
import net.Indyuce.bountyhunters.version.SpigotPlugin;
import net.Indyuce.bountyhunters.version.wrapper.VersionWrapper;
import net.milkbowl.vault.economy.Economy;

public class BountyHunters extends JavaPlugin {
	private static BountyHunters plugin;

	private PluginVersion version;
	private VersionWrapper wrapper;
	private PlaceholderParser placeholderParser;
	private DataProvider dataProvider;
	private WorldGuardFlags wgFlags;

	private Economy economy;

	private BountyManager bountyManager;
	private LevelManager levelManager;
	private PlayerDataManager playerDataManager;

	private HunterLeaderboard leaderboard;
	private BankAccount taxBankAccount;
	public boolean formattedNumbers;

	@SuppressWarnings("deprecation")
	public void onLoad() {
		plugin = this;

		try {
			version = new PluginVersion(Bukkit.getServer().getClass());
			wrapper = (VersionWrapper) Class.forName("net.Indyuce.bountyhunters.version.wrapper.VersionWrapper_" + version.toString().substring(1))
					.newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException exception) {
			getLogger().log(Level.INFO, "Your server version is not supported.");
//			wrapper = new VersionWrapper_Reflection();
			
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		try {
			if (getServer().getPluginManager().getPlugin("WorldGuard") != null && version.isStrictlyHigher(1, 12)) {
				wgFlags = new WorldGuardFlags();
				getLogger().log(Level.INFO, "Hooked onto WorldGuard");
			}

			/*
			 * bad exception handling.. cannot really tell what is going to
			 * happen with different versions of WG API anyways
			 */
		} catch (Exception exception) {
			getLogger().log(Level.WARNING, "Could not initialize support with WorldGuard 7: " + exception.getMessage());
		}
	}

	public void onEnable() {
		getLogger().log(Level.INFO, "Detected Server Version: " + version.toString());

		// vault compatibility
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		} else {
			getLogger().log(Level.SEVERE, "Couldn't load Vault. Disabling...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		new SpigotPlugin(72142, this).checkForUpdate();

		/*
		 * determines using a MySQL database or default YAML
		 */
		saveDefaultConfig();
		try {
			dataProvider = getConfig().getBoolean("my-sql.enabled") ? new MySQLProvider(getConfig().getConfigurationSection("my-sql"))
					: new YAMLDataProvider();
		} catch (SQLException | IllegalArgumentException exception) {
			getLogger().log(Level.SEVERE, "Database error: " + exception.getMessage());
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		/*
		 * Bounties must be loaded after player datas as registering hunters
		 * requires PlayerDatas to be initiliazed
		 */
		playerDataManager = dataProvider.providePlayerData();
		Bukkit.getOnlinePlayers().forEach(player -> playerDataManager.load(player));
		bountyManager = dataProvider.provideBounties();

		// listeners
		Bukkit.getPluginManager().registerEvents(new BountyClaim(), this);
		Bukkit.getPluginManager().registerEvents(new GuiListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new HuntListener(), this);
		Bukkit.getPluginManager().registerEvents(new RestrictionListener(), this);

		if (getConfig().getBoolean("logging.bounty-claim"))
			Bukkit.getPluginManager().registerEvents(new ClaimLog(), this);
		if (getConfig().getBoolean("logging.bounty-expire"))
			Bukkit.getPluginManager().registerEvents(new ExpireLog(), this);
		if (getConfig().getBoolean("logging.level-up"))
			Bukkit.getPluginManager().registerEvents(new LevelUpLog(), this);
		if (getConfig().getBoolean("head-hunting.enabled"))
			Bukkit.getPluginManager().registerEvents(new HeadHunting(), this);

		if (getConfig().getBoolean("target-login-message.enabled"))
			Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
				private final String message = ChatColor.translateAlternateColorCodes('&',
						plugin.getConfig().getString("target-login-message.format"));

				@EventHandler(priority = EventPriority.HIGH)
				public void a(PlayerJoinEvent event) {
					Player player = event.getPlayer();
					Optional<Bounty> bounty = bountyManager.getBounty(player);
					if (bounty.isPresent())
						event.setJoinMessage(message.replace("{player}", player.getName()).replace("{bounty}",
								new NumberFormat().format(bounty.get().getReward())));
				}
			}, this);

		new Metrics(this);

		if (getServer().getPluginManager().getPlugin("Towny") != null && getConfig().getBoolean("claim-restrictions.town-members")) {
			bountyManager.registerClaimRestriction(new TownySupport());
			getLogger().log(Level.INFO, "Hooked onto Towny");
		}

		if (getConfig().getBoolean("claim-restrictions.friends")) {

			if (Bukkit.getPluginManager().getPlugin("PartyAndFriends") != null) {
				bountyManager.registerClaimRestriction(new PartyAndFriendsSupport());
				getLogger().log(Level.INFO, "Hooked onto PartyAndFriends");

			} else if (Bukkit.getPluginManager().getPlugin("BungeeFriends") != null) {
				bountyManager.registerClaimRestriction(new BungeeFriendsSupport());
				getLogger().log(Level.INFO, "Hooked onto BungeeFriends");
			}
		}

		placeholderParser = getServer().getPluginManager().getPlugin("PlaceholderAPI") != null ? new PlaceholderAPIParser() : new DefaultParser();
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new BountyHuntersPlaceholders().register();
			getLogger().log(Level.INFO, "Hooked onto PlaceholderAPI");
		}

		if (wgFlags != null)
			Bukkit.getPluginManager().registerEvents(wgFlags, this);

		if (Bukkit.getPluginManager().getPlugin("Residence") != null) {
			new ResidenceFlags();
			getLogger().log(Level.INFO, "Hooked onto Residence");
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
			String path = key.getPath();
			if (!messages.getConfig().contains(path)) {
				messages.getConfig().set(path + ".format", key.getDefault());
				if (key.hasSound()) {
					messages.getConfig().set(path + ".sound.name", key.getSound().getSound().name());
					messages.getConfig().set(path + ".sound.vol", key.getSound().getVolume());
					messages.getConfig().set(path + ".sound.pitch", key.getSound().getPitch());
				}
			}

			key.update(messages.getConfig().getConfigurationSection(path));
		}
		messages.save();

		ConfigFile language = new ConfigFile("/language", "language");
		for (Language key : Language.values()) {
			String path = key.getPath();
			if (!language.getConfig().contains(path))
				language.getConfig().set(path, key.getDefault());

			key.update(language.getConfig().getString(path));
		}
		language.save();

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
		leaderboard = new HunterLeaderboard(new ConfigFile("/cache", "leaderboard"));

		/*
		 * only reload config files after levels.yml is loaded or else it can't
		 * load the file
		 */
		reloadConfigFiles();

		// commands
		getCommand("addbounty").setExecutor(new AddBountyCommand());
		getCommand("bounties").setExecutor(new BountiesCommand());
		getCommand("hunters").setExecutor(new HuntersCommand());
		getCommand("redeembountyheads").setExecutor(new RedeemBountyHeadsCommand());

		getCommand("addbounty").setTabCompleter(new AddBountyCompletion());
		getCommand("bounties").setTabCompleter(new BountiesCompletion());
	}

	public void onDisable() {

		/*
		 * must not be performed when the plugin disables after a startup error
		 * occurs otherwise an additional error will prompt
		 */
		if (bountyManager == null)
			return;

		bountyManager.saveBounties();
		playerDataManager.getLoaded().forEach(data -> playerDataManager.saveData(data));

		leaderboard.save();

		for (Player online : Bukkit.getOnlinePlayers())
			if (online.getOpenInventory() != null && online.getOpenInventory().getTopInventory().getHolder() instanceof PluginInventory)
				online.closeInventory();
	}

	public static BountyHunters getInstance() {
		return plugin;
	}

	public VersionWrapper getVersionWrapper() {
		return wrapper;
	}

	public Economy getEconomy() {
		return economy;
	}

	public BountyManager getBountyManager() {
		return bountyManager;
	}

	public PlayerDataManager getPlayerDataManager() {
		return playerDataManager;
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

	public PluginVersion getVersion() {
		return version;
	}

	public BankAccount getTaxBankAccount() {
		return taxBankAccount;
	}

	public HunterLeaderboard getHunterLeaderboard() {
		return leaderboard;
	}

	public PlaceholderParser getPlaceholderParser() {
		return placeholderParser;
	}

	public void reloadConfigFiles() {
		formattedNumbers = getConfig().getBoolean("formatted-numbers");

		if (!getConfig().getString("tax-bank-account.name").isEmpty())
			try {
				String type = getConfig().getString("tax-bank-account.type"), name = getConfig().getString("tax-bank-account.name");
				taxBankAccount = type.equalsIgnoreCase("player") ? new PlayerBankAccount(name)
						: type.equalsIgnoreCase("account") ? new SimpleBankAccount(name) : null;
				Validate.notNull(taxBankAccount, "Account type must be either 'player' or 'account'");
			} catch (IllegalArgumentException exception) {
				getLogger().log(Level.WARNING, "Could not load tax bank account: " + exception.getMessage());
			}

		FileConfiguration messages = new ConfigFile("/language", "messages").getConfig();
		for (Message message : Message.values())
			message.update(messages.getConfigurationSection(message.getPath()));

		FileConfiguration language = new ConfigFile("/language", "language").getConfig();
		for (Language key : Language.values())
			key.update(language.getString(key.getPath()));
	}
}