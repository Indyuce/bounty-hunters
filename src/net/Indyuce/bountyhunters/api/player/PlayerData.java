package net.Indyuce.bountyhunters.api.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.AltChar;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.event.HunterLevelUpEvent;
import net.Indyuce.bountyhunters.manager.LevelManager.DeathQuote;
import net.Indyuce.bountyhunters.manager.LevelManager.LevelUpItem;
import net.Indyuce.bountyhunters.manager.LevelManager.Title;

public class PlayerData implements OfflinePlayerData {

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

	public PlayerData(OfflinePlayer player) {
		this.offline = player;
	}

	@Deprecated
	public static boolean isLoaded(UUID uuid) {
		return BountyHunters.getInstance().getPlayerDataManager().isLoaded(uuid);
	}

	public OfflinePlayer getOfflinePlayer() {
		return offline;
	}

	/*
	 * CAREFUL! this method does NOT save any of the player data. you MUST save
	 * the player data using saveFile() before unloading the player data from
	 * the map!
	 */
	@Deprecated
	public void unload() {
		BountyHunters.getInstance().getPlayerDataManager().unload(getUniqueId());
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
		return level;
	}

	public int getSuccessfulBounties() {
		return successful;
	}

	public int getClaimedBounties() {
		return claimed;
	}

	public DeathQuote getQuote() {
		return quote;
	}

	public Title getTitle() {
		return title;
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

		String title = hasTitle() ? getTitle().format() : Message.NO_TITLE.getMessage();
		for (int j = 0; j < profileLore.size(); j++)
			profileLore.set(j, profileLore.get(j).replace("%lvl-progress%", getLevelProgressBar()).replace("%claimed-bounties%", "" + getClaimedBounties())
					.replace("%successful-bounties%", "" + getSuccessfulBounties()).replace("%current-title%", title).replace("%level%", "" + getLevel()));

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

	public boolean hasUnlocked(LevelUpItem item) {
		return level >= item.getUnlockLevel();
	}

	public boolean hasQuote() {
		return quote != null;
	}

	public boolean hasTitle() {
		return title != null;
	}

	public boolean canSelectItem() {
		return lastSelect + 3000 < System.currentTimeMillis();
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

	public void setQuote(DeathQuote quote) {
		this.quote = quote;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public void addLevels(int value) {
		setLevel(level + value);
	}

	@Override
	public void addSuccessfulBounties(int value) {
		setSuccessfulBounties(getSuccessfulBounties() + value);
	}

	public void addClaimedBounties(int value) {
		setClaimedBounties(claimed + value);
	}

	public void addIllegalKills(int value) {
		setIllegalKills(illegalKills + value);
		setIllegalKillStreak(illegalStreak + value);
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
			BountyHunters.getInstance().getLevelManager().getCommands(nextLevel)
					.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), BountyHunters.getInstance().getPlaceholderParser().parse(player, cmd)));

		// money
		double money = BountyHunters.getInstance().getLevelManager().calculateLevelMoney(nextLevel);
		BountyHunters.getInstance().getEconomy().depositPlayer(player, money);

		// send json list
		String jsonList = money > 0 ? "\n" + Message.LEVEL_UP_REWARD_MONEY.formatRaw(ChatColor.YELLOW, "%amount%", new NumberFormat().format(money)) : "";
		for (String s : chatDisplay)
			jsonList += "\n" + Message.LEVEL_UP_REWARD.formatRaw(ChatColor.YELLOW, "%reward%", AltChar.apply(s));
		BountyHunters.getInstance().getVersionWrapper().sendJson(player,
				"{\"text\":\"" + ChatColor.YELLOW + Message.LEVEL_UP_REWARDS.getMessage() + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + jsonList.substring(1) + "\"}}}");

		setLevel(nextLevel);
		return true;
	}

	@Override
	public boolean equals(Object object) {
		return object != null && object instanceof PlayerData && ((PlayerData) object).getUniqueId().equals(getUniqueId());
	}

	@Override
	public String toString() {
		return "{Level=" + level + ", ClaimedBounties=" + claimed + ", SuccessfulBounties=" + successful + ", IllegalKills=" + illegalKills + ", IllegalKillStreak=" + illegalStreak
				+ (hasTitle() ? ", Title=" + title.getId() : "") + (hasQuote() ? ", Quote=" + quote.getId() : "") + "}";
	}

	@Deprecated
	public static Collection<PlayerData> getLoaded() {
		return BountyHunters.getInstance().getPlayerDataManager().getLoaded();
	}

	@Deprecated
	public static PlayerData get(OfflinePlayer player) {
		return BountyHunters.getInstance().getPlayerDataManager().get(player.getUniqueId());
	}

	@Deprecated
	public static void load(OfflinePlayer player) {
		BountyHunters.getInstance().getPlayerDataManager().load(player);
	}
}
