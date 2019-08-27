package net.Indyuce.bountyhunters.api.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.AltChar;
import net.Indyuce.bountyhunters.api.ConfigFile;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.event.HunterLevelUpEvent;
import net.Indyuce.bountyhunters.manager.LevelManager.DeathQuote;
import net.Indyuce.bountyhunters.manager.LevelManager.LevelUpItem;
import net.Indyuce.bountyhunters.manager.LevelManager.Title;

public class PlayerData implements PlayerDataInterface {

	private final OfflinePlayer offline;

	/*
	 * player data
	 */
	private int level, successful, claimed, illegalStreak, illegalKills;
	private DeathQuote quote;
	private Title title;

	/*
	 * temp stuff
	 */
	private long lastBounty, lastTarget, lastSelect;

	private static Map<UUID, PlayerData> map = new HashMap<>();

	private PlayerData(OfflinePlayer player) {
		this.offline = player;
		FileConfiguration config = new ConfigFile("/userdata", offline.getUniqueId().toString()).getConfig();

		level = config.getInt("level");
		successful = config.getInt("successful-bounties");
		claimed = config.getInt("claimed-bounties");
		illegalKills = config.getInt("illegal-kills");
		illegalStreak = config.getInt("illegal-kill-streak");

		try {
			quote = config.contains("current-quote") ? BountyHunters.getInstance().getLevelManager().getQuote(config.getString("current-quote")) : null;
		} catch (NullPointerException exception) {
			log("Could not load quote from " + offline.getUniqueId().toString() + " (" + offline.getName() + ")");
		}

		try {
			title = config.contains("current-title") ? BountyHunters.getInstance().getLevelManager().getTitle(config.getString("current-title")) : null;
		} catch (NullPointerException exception) {
			log("Could not load title from " + offline.getUniqueId().toString() + " (" + offline.getName() + ")");
		}
	}

	public void saveFile() {
		ConfigFile config = new ConfigFile("/userdata", offline.getUniqueId().toString());

		config.getConfig().set("level", level);
		config.getConfig().set("successful-bounties", successful);
		config.getConfig().set("claimed-bounties", claimed);
		config.getConfig().set("illegal-kills", illegalKills);
		config.getConfig().set("illegal-kill-streak", illegalStreak);
		config.getConfig().set("current-title", hasTitle() ? title.getId() : null);
		config.getConfig().set("current-quote", hasQuote() ? quote.getId() : null);

		config.save();
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

	public boolean canSelectItem() {
		return lastSelect + 3000 < System.currentTimeMillis();
	}

	public UUID getUniqueId() {
		return offline.getUniqueId();
	}

	public int getLevel() {
		return level;
	}

	public int getSuccessfulBounties() {
		return successful;
	}

	public int getClaimedBounties() {
		return claimed;
	}

	public String getQuote() {
		return quote == null ? "" : quote.format();
	}

	public String getTitle() {
		return title == null ? "" : title.format();
	}

	public boolean hasUnlocked(LevelUpItem item) {
		return level >= item.getUnlockLevel();
	}

	public double getValueDependingOnLevel(ConfigurationSection section, int level) {
		return section.getDouble("base") + section.getDouble("per-level") * level;
	}

	public int getBountiesNeededToLevelUp() {
		int needed = BountyHunters.getInstance().getLevelManager().getBountiesPerLevel();
		return needed - (claimed % needed);
	}

	public String getLevelProgressBar() {
		String advancement = "";
		int needed = BountyHunters.getInstance().getLevelManager().getBountiesPerLevel();
		for (int j = 0; j < needed; j++)
			advancement += (getClaimedBounties() % needed > j ? ChatColor.GREEN : ChatColor.WHITE) + AltChar.square;
		return advancement;
	}

	public ItemStack getProfileItem() {
		ItemStack profile = CustomItem.PROFILE.toItemStack().clone();
		SkullMeta meta = (SkullMeta) profile.getItemMeta();
		meta.setDisplayName(meta.getDisplayName().replace("%name%", offline.getName()).replace("%level%", "" + getLevel()));
		BountyHunters.getInstance().getVersionWrapper().setOwner(meta, offline);
		List<String> profileLore = meta.getLore();

		String title = hasTitle() ? getTitle() : Message.NO_TITLE.getMessage();
		for (int j = 0; j < profileLore.size(); j++)
			profileLore.set(j, profileLore.get(j).replace("%lvl-progress%", getLevelProgressBar()).replace("%claimed-bounties%", "" + getClaimedBounties()).replace("%successful-bounties%", "" + getSuccessfulBounties()).replace("%current-title%", title).replace("%level%", "" + getLevel()));

		meta.setLore(profileLore);
		profile.setItemMeta(meta);
		return profile;
	}

	public int getIllegalKillStreak() {
		return illegalStreak;
	}

	public int getIllegalKills() {
		return illegalKills;
	}

	public void log(String... message) {
		for (String line : message)
			BountyHunters.getInstance().getLogger().log(Level.WARNING, "[Player Data] " + offline.getName() + ": " + line);
	}

	public void setLastBounty() {
		lastBounty = System.currentTimeMillis();
	}

	public void setLastTarget() {
		lastTarget = System.currentTimeMillis();
	}

	public void setLastSelect() {
		lastSelect = System.currentTimeMillis();
	}

	public boolean hasQuote() {
		return !getQuote().equals("");
	}

	public boolean hasTitle() {
		return !getTitle().equals("");
	}

	public void setLevel(int value) {
		level = Math.max(0, value);
	}

	public void setSuccessfulBounties(int value) {
		successful = Math.max(0, value);
	}

	public void setClaimedBounties(int value) {
		claimed = Math.max(0, value);
	}

	public void setIllegalKills(int value) {
		illegalKills = Math.max(0, value);
	}

	public void setIllegalKillStreak(int value) {
		illegalStreak = Math.max(0, value);
	}

	public void setCurrentQuote(DeathQuote quote) {
		this.quote = quote;
	}

	public void setCurrentTitle(Title title) {
		this.title = title;
	}

	public void addLevels(int value) {
		setLevel(level + value);
	}

	@Override
	public void addSuccessfulBounties(int value) {
		setSuccessfulBounties(successful + value);
	}

	public void addClaimedBounties(int value) {
		setClaimedBounties(claimed + value);
	}

	public void addIllegalKills(int value) {
		setIllegalKills(illegalKills + value);
		setIllegalKillStreak(illegalStreak + value);
	}

	public void resetStreaks() {
		illegalStreak = 0;
	}

	public void refreshLevel(Player player) {
		while (levelUp(player))
			;
	}

	private boolean levelUp(Player player) {
		int nextLevel = getLevel() + 1;
		int neededBounties = nextLevel * BountyHunters.getInstance().getLevelManager().getBountiesPerLevel();
		if (getClaimedBounties() < neededBounties)
			return false;

		Bukkit.getPluginManager().callEvent(new HunterLevelUpEvent(player, nextLevel));

		Message.CHAT_BAR.format(ChatColor.YELLOW).send(player);
		Message.LEVEL_UP.format(ChatColor.YELLOW, "%level%", "" + nextLevel).send(player);
		Message.LEVEL_UP_2.format(ChatColor.YELLOW, "%bounties%", "" + BountyHunters.getInstance().getLevelManager().getBountiesPerLevel()).send(player);

		List<String> chatDisplay = new ArrayList<>();

		// titles
		for (Title title : BountyHunters.getInstance().getLevelManager().getTitles())
			if (nextLevel == title.getUnlockLevel())
				chatDisplay.add(title.format());

		// death quotes
		for (DeathQuote quote : BountyHunters.getInstance().getLevelManager().getQuotes())
			if (nextLevel == quote.getUnlockLevel())
				chatDisplay.add(quote.format());

		// send commands
		if (BountyHunters.getInstance().getLevelManager().hasCommands(nextLevel))
			BountyHunters.getInstance().getLevelManager().getCommands(nextLevel).forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), BountyHunters.getInstance().getPlaceholderParser().parse(player, cmd)));

		// money
		double money = BountyHunters.getInstance().getLevelManager().calculateLevelMoney(nextLevel);
		BountyHunters.getInstance().getEconomy().depositPlayer(player, money);

		// send json list
		String jsonList = money > 0 ? "\n" + Message.LEVEL_UP_REWARD_MONEY.formatRaw(ChatColor.YELLOW, "%amount%", new NumberFormat().format(money)) : "";
		for (String s : chatDisplay)
			jsonList += "\n" + Message.LEVEL_UP_REWARD.formatRaw(ChatColor.YELLOW, "%reward%", AltChar.apply(s));
		BountyHunters.getInstance().getVersionWrapper().sendJson(player, "{\"text\":\"" + ChatColor.YELLOW + Message.LEVEL_UP_REWARDS.getMessage() + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + jsonList.substring(1) + "\"}}}");

		setLevel(nextLevel);
		return true;
	}

	@Override
	public boolean equals(Object object) {
		return object != null && object instanceof PlayerData && ((PlayerData) object).getUniqueId().equals(getUniqueId());
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

	public enum Value {
		CLAIMED_BOUNTIES(0),
		SUCCESSFUL_BOUNTIES(0),
		LEVEL(0),
		CURRENT_TITLE(""),
		CURRENT_QUOTE(""),
		UNLOCKED(new ArrayList<>());

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
