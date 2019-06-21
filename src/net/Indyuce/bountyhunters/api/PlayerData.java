package net.Indyuce.bountyhunters.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.event.HunterLevelUpEvent;

public class PlayerData {
	private static Map<UUID, PlayerData> map = new HashMap<>();

	private final OfflinePlayer offline;
	private final ConfigFile config;

	// caches the list of unlocked stuff
	private List<String> unlocked;

	// last time the player created a bounty
	private long lastBounty, lastTarget;

	private PlayerData(OfflinePlayer player) {
		this.offline = player;
		this.config = new ConfigFile("/userdata", offline.getUniqueId().toString());

		for (Value value : Value.values())
			if (!config.getConfig().contains(value.getPath()))
				config.getConfig().set(value.getPath(), value.getDefault());

		this.unlocked = config.getConfig().getStringList("unlocked");
	}

	public static Collection<PlayerData> getLoaded() {
		return map.values();
	}

	public static PlayerData get(OfflinePlayer player) {
		return map.get(player.getUniqueId());
	}

	public static void load(OfflinePlayer player) {
		if (!map.containsKey(player.getUniqueId()))
			map.put(player.getUniqueId(), new PlayerData(player));
	}

	public static boolean isLoaded(UUID uuid) {
		return map.containsKey(uuid);
	}

	public OfflinePlayer getOfflinePlayer() {
		return offline;
	}

	/*
	 * CAREFUL! this method does NOT save any of the player data. you MUST save
	 * the player data using saveFile() before unloading the player data from
	 * the map!
	 */
	public void unload() {
		map.remove(offline.getUniqueId());
	}

	public long getLastBounty() {
		return lastBounty;
	}

	public long getLastTarget() {
		return lastTarget;
	}

	public UUID getUniqueId() {
		return offline.getUniqueId();
	}

	public int getLevel() {
		return config.getConfig().getInt("level");
	}

	public int getSuccessfulBounties() {
		return config.getConfig().getInt("successful-bounties");
	}

	public int getClaimedBounties() {
		return config.getConfig().getInt("claimed-bounties");
	}

	public List<String> getUnlocked() {
		return unlocked;
	}

	public String getQuote() {
		String format = BountyHunters.getLevelsConfigFile().getString("reward.quote." + config.getConfig().getString("current-quote") + ".format");
		return AltChar.apply(format == null ? "" : format);
	}

	public String getTitle() {
		String format = BountyHunters.getLevelsConfigFile().getString("reward.title." + config.getConfig().getString("current-title") + ".format");
		return AltChar.apply(format == null ? "" : format);
	}

	public double getValueDependingOnLevel(ConfigurationSection section, int level) {
		return section.getDouble("base") + section.getDouble("per-level") * level;
	}

	public int getBountiesNeededToLevelUp() {
		int bountiesNeeded = BountyHunters.getLevelsConfigFile().getInt("bounties-per-level");
		return bountiesNeeded - (getClaimedBounties() % bountiesNeeded);
	}

	public String getLevelProgressBar() {
		String lvlAdvancement = "";
		int bountiesNeeded = BountyHunters.getLevelsConfigFile().getInt("bounties-per-level");
		for (int j = 0; j < bountiesNeeded; j++)
			lvlAdvancement += (getClaimedBounties() % bountiesNeeded > j ? ChatColor.GREEN : ChatColor.WHITE) + AltChar.square;
		return lvlAdvancement;
	}

	public ItemStack getProfileItem() {
		ItemStack profile = CustomItem.PROFILE.a().clone();
		SkullMeta profileMeta = (SkullMeta) profile.getItemMeta();
		profileMeta.setDisplayName(profileMeta.getDisplayName().replace("%name%", offline.getName()).replace("%level%", "" + getLevel()));
		profileMeta.setOwningPlayer(Bukkit.getOfflinePlayer(offline.getUniqueId()));
		List<String> profileLore = profileMeta.getLore();

		String title = hasTitle() ? getTitle() : Message.NO_TITLE.getUpdated();
		for (int j = 0; j < profileLore.size(); j++)
			profileLore.set(j, profileLore.get(j).replace("%lvl-progress%", getLevelProgressBar()).replace("%claimed-bounties%", "" + getClaimedBounties()).replace("%successful-bounties%", "" + getSuccessfulBounties()).replace("%current-title%", title).replace("%level%", "" + getLevel()));

		profileMeta.setLore(profileLore);
		profile.setItemMeta(profileMeta);
		return profile;
	}

	public void setLastBounty() {
		lastBounty = System.currentTimeMillis();
	}

	public void setLastTarget() {
		lastTarget = System.currentTimeMillis();
	}

	public boolean hasQuote() {
		return !getQuote().equals("");
	}

	public boolean hasTitle() {
		return !getTitle().equals("");
	}

	public boolean hasUnlocked(String path) {
		return unlocked.contains(path);
	}

	public void setLevel(int value) {
		config.getConfig().set("level", value);
	}

	public void setSuccessfulBounties(int value) {
		config.getConfig().set("successful-bounties", value);
	}

	public void setCurrentQuote(String value) {
		config.getConfig().set("current-quote", value);
	}

	public void setCurrentTitle(String value) {
		config.getConfig().set("current-title", value);
	}

	public void setClaimedBounties(int value) {
		config.getConfig().set("claimed-bounties", value);
	}

	public void setUnlocked(List<String> value) {
		config.getConfig().set("unlocked", value);
	}

	public void addLevels(int value) {
		setLevel(getLevel() + value);
	}

	public void addSuccessfulBounties(int value) {
		setSuccessfulBounties(getSuccessfulBounties() + value);
	}

	public void addClaimedBounties(int value) {
		setClaimedBounties(getClaimedBounties() + value);
	}

	public void addUnlocked(String value) {
		unlocked.add(value);
	}

	public void checkForLevelUp(Player player) {
		FileConfiguration levels = BountyHunters.getLevelsConfigFile();
		while (levelUp(levels, player)) {
		}
	}

	private boolean levelUp(FileConfiguration levels, Player player) {
		int nextLevel = getLevel() + 1;
		int neededBounties = nextLevel * levels.getInt("bounties-per-level");
		if (getClaimedBounties() < neededBounties)
			return false;

		HunterLevelUpEvent event = new HunterLevelUpEvent(player, nextLevel);
		Bukkit.getPluginManager().callEvent(event);

		Message.CHAT_BAR.format(ChatColor.YELLOW).send(player);
		Message.LEVEL_UP.format(ChatColor.YELLOW, "%level%", "" + nextLevel).send(player);
		Message.LEVEL_UP_2.format(ChatColor.YELLOW, "%bounties%", "" + levels.getInt("bounties-per-level")).send(player);

		List<String> chatDisplay = new ArrayList<String>();

		// titles
		for (String titleId : levels.getConfigurationSection("reward.title").getKeys(false))
			if (nextLevel >= levels.getInt("reward.title." + titleId + ".unlock") && !unlocked.contains(titleId)) {
				addUnlocked(titleId);
				chatDisplay.add(levels.getString("reward.title." + titleId + ".format"));
			}

		// death quotes
		for (String quoteId : levels.getConfigurationSection("reward.quote").getKeys(false))
			if (nextLevel >= levels.getInt("reward.quote." + quoteId + ".unlock") && !unlocked.contains(quoteId)) {
				addUnlocked(quoteId);
				chatDisplay.add(levels.getString("reward.quote." + quoteId + ".format"));
			}

		// send commands
		for (String s : levels.getStringList("reward.commands." + nextLevel))
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", player.getName()));

		// money
		double money = levels.getDouble("reward.money.base") + (nextLevel * levels.getDouble("reward.money.per-level"));
		BountyHunters.getEconomy().depositPlayer(player, money);

		// send json list
		String jsonList = money > 0 ? "\n" + Message.LEVEL_UP_REWARD.formatRaw(ChatColor.YELLOW, "%reward%", "$" + money) : "";
		for (String s : chatDisplay)
			jsonList += "\n" + Message.LEVEL_UP_REWARD.formatRaw(ChatColor.YELLOW, "%reward%", AltChar.apply(s));
		BountyHunters.getNMS().sendJson(player, "{\"text\":\"" + ChatColor.YELLOW + Message.LEVEL_UP_REWARDS.getUpdated() + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + jsonList.substring(1) + "\"}}}");

		setLevel(nextLevel);
		setUnlocked(unlocked);
		return true;
	}

	@Override
	public boolean equals(Object object) {
		return object != null && object instanceof PlayerData && ((PlayerData) object).getUniqueId().equals(getUniqueId());
	}

	public void saveFile() {
		config.getConfig().set("unlocked", unlocked);
		config.save();
	}

	public enum Value {
		CLAIMED_BOUNTIES(0),
		SUCCESSFUL_BOUNTIES(0),
		LEVEL(0),
		CURRENT_TITLE(""),
		CURRENT_QUOTE(""),
		UNLOCKED(new ArrayList<String>());

		private Object def;

		private Value(Object def) {
			this.def = def;
		}

		public String getPath() {
			return name().toLowerCase().replace("_", "-");
		}

		public Object getDefault() {
			return def;
		}
	}
}
