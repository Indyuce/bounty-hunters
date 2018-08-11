package net.Indyuce.bh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.Indyuce.bh.api.BountyManager;
import net.Indyuce.bh.api.PlayerData;
import net.Indyuce.bh.command.AddBountyCommand;
import net.Indyuce.bh.command.BountiesCommand;
import net.Indyuce.bh.command.completion.AddBountyCompletion;
import net.Indyuce.bh.command.completion.BountiesCompletion;
import net.Indyuce.bh.comp.BountyHuntersPlaceholders;
import net.Indyuce.bh.gui.GuiListener;
import net.Indyuce.bh.gui.PluginInventory;
import net.Indyuce.bh.listener.BountyClaim;
import net.Indyuce.bh.listener.MainListener;
import net.Indyuce.bh.nms.json.Json;
import net.Indyuce.bh.nms.title.Title;
import net.Indyuce.bh.resource.CustomItem;
import net.Indyuce.bh.resource.Language_Message;
import net.Indyuce.bh.resource.QuoteReward;
import net.Indyuce.bh.resource.TitleReward;
import net.Indyuce.bh.util.VersionUtils;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	// test push
	
	// external
	public static Main plugin;
	private static BountyManager bountyManager;
	private static Economy economy;

	// no reflection nms
	public static Title title;
	public static Json json;

	// systems
	public HashMap<UUID, Long> lastBounty = new HashMap<UUID, Long>();

	public static Economy getEconomy() {
		return economy;
	}

	public static BountyManager getBountyManager() {
		return bountyManager;
	}

	public void onDisable() {
		for (Player t : Bukkit.getOnlinePlayers())
			if (t.getInventory() != null)
				if (t.getInventory().getHolder() instanceof PluginInventory)
					t.closeInventory();

		bountyManager.saveBounties();
	}

	public void onEnable() {
		plugin = this;
		bountyManager = new BountyManager();

		// listeners
		Bukkit.getServer().getPluginManager().registerEvents(new BountyClaim(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new MainListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new GuiListener(), this);

		// version compatibility
		try {
			VersionUtils.version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
			Bukkit.getConsoleSender().sendMessage("[BountyHunters] " + ChatColor.DARK_GRAY + "Detected Server Version: " + VersionUtils.version);

			// no reflection nms
			// each class corresponds to a server version
			title = (Title) Class.forName("net.Indyuce.bh.nms.title.Title_" + VersionUtils.version.substring(1)).newInstance();
			json = (Json) Class.forName("net.Indyuce.bh.nms.json.Json_" + VersionUtils.version.substring(1)).newInstance();
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage("[BountyHunters] " + ChatColor.RED + "Your server version is not compatible.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// placeholderpi compatibility
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
			Bukkit.getConsoleSender().sendMessage("[BountyHunters] " + ChatColor.DARK_GRAY + "Detected PlaceholderAPI, loading placeholders...");
			new BountyHuntersPlaceholders().register();
		}

		// vault compatibility
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		else {
			Bukkit.getConsoleSender().sendMessage("[BountyHunters] " + ChatColor.RED + "Couldn't load Vault. Disabling...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// config files
		saveDefaultConfig();

		// level data
		FileConfiguration levels = ConfigData.getCD(this, "", "levels");
		if (levels.getKeys(false).isEmpty()) {
			for (TitleReward title : TitleReward.values())
				levels.set("reward.title." + title.level, title.title);
			for (QuoteReward quote : QuoteReward.values())
				levels.set("reward.quote." + quote.level, quote.quote);
			levels.set("bounties-needed-to-lvl-up", 5);
			levels.set("reward.money.base", 50);
			levels.set("reward.money.per-lvl", 6);
		}
		ConfigData.saveCD(this, levels, "", "levels");

		// messages
		FileConfiguration messages = ConfigData.getCD(this, "/language", "messages");
		for (Language_Message pa : Language_Message.values()) {
			String path = pa.name().toLowerCase().replace("_", "-");
			if (!messages.contains(path))
				messages.set(path, pa.value);
		}
		ConfigData.saveCD(this, messages, "/language", "messages");

		// items
		FileConfiguration items = ConfigData.getCD(this, "/language", "items");
		for (CustomItem i : CustomItem.values()) {
			if (!items.contains(i.name())) {
				items.set(i.name() + ".name", i.name);
				items.set(i.name() + ".lore", i.lore == null ? new ArrayList<String>() : Arrays.asList(i.lore));
			}
			i.update(items);
		}
		ConfigData.saveCD(this, items, "/language", "items");

		for (Player p : Bukkit.getOnlinePlayers())
			PlayerData.get(p).setup();

		// commands
		getCommand("addbounty").setExecutor(new AddBountyCommand());
		getCommand("bounties").setExecutor(new BountiesCommand());

		getCommand("addbounty").setTabCompleter(new AddBountyCompletion());
		getCommand("bounties").setTabCompleter(new BountiesCompletion());
	}

	public boolean checkPl(CommandSender sender, boolean msg) {
		boolean b = sender instanceof Player;
		if (!b && msg)
			sender.sendMessage(ChatColor.RED + "This command is for players only.");
		return b;
	}
}