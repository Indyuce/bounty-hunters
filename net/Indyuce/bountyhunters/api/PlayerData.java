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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.ConfigData;

public class PlayerData {
	private static Map<UUID, PlayerData> map = new HashMap<UUID, PlayerData>();

	private UUID uuid;
	private String playerName;
	private FileConfiguration config;

	private List<String> unlocked;

	private PlayerData(OfflinePlayer player) {
		this.uuid = player.getUniqueId();
		this.playerName = player.getName();
		this.config = ConfigData.getCD(BountyHunters.plugin, "/userdata", uuid.toString());

		for (Value value : Value.values())
			if (!config.contains(value.getPath()))
				config.set(value.getPath(), value.getDefault());

		this.unlocked = config.getStringList("unlocked");
	}

	public static Collection<PlayerData> getPlayerDatas() {
		return map.values();
	}

	public static PlayerData get(OfflinePlayer player) {
		return map.get(player.getUniqueId());
	}

	public static void setup(OfflinePlayer player) {
		if (!map.containsKey(player.getUniqueId()))
			map.put(player.getUniqueId(), new PlayerData(player));
	}

	public String getPlayerName() {
		return playerName;
	}

	public UUID getUUID() {
		return uuid;
	}

	public int getLevel() {
		return config.getInt("level");
	}

	public int getSuccessfulBounties() {
		return config.getInt("successful-bounties");
	}

	public int getClaimedBounties() {
		return config.getInt("claimed-bounties");
	}

	public String getQuote() {
		String format = BountyHunters.getLevelsConfigFile().getString("reward.quote." + config.getString("current-quote") + ".format");
		return SpecialChar.apply(format == null ? "" : format);
	}

	public boolean hasQuote() {
		return !getQuote().equals("");
	}

	public String getTitle() {
		String format = BountyHunters.getLevelsConfigFile().getString("reward.title." + config.getString("current-title") + ".format");
		return SpecialChar.apply(format == null ? "" : format);
	}

	public boolean hasTitle() {
		return !getTitle().equals("");
	}

	public void setLevel(int value) {
		config.set("level", value);
	}

	public void addLevels(int value) {
		setLevel(getLevel() + value);
	}

	public void setSuccessfulBounties(int value) {
		config.set("successful-bounties", value);
	}

	public void addSuccessfulBounties(int value) {
		setSuccessfulBounties(getSuccessfulBounties() + value);
	}

	public void setClaimedBounties(int value) {
		config.set("claimed-bounties", value);
	}

	public void addClaimedBounties(int value) {
		setClaimedBounties(getClaimedBounties() + value);
	}

	public void setCurrentQuote(String value) {
		config.set("current-quote", value);
	}

	public void setCurrentTitle(String value) {
		config.set("current-title", value);
	}

	public void setUnlocked(List<String> value) {
		config.set("unlocked", value);
	}

	public List<String> getUnlocked() {
		return unlocked;
	}

	public void addUnlocked(String value) {
		unlocked.add(value);
	}

	public double getValueDependingOnLevel(ConfigurationSection section, int level) {
		return section.getDouble("base") + section.getDouble("per-level") * level;
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof PlayerData ? ((PlayerData) object).getUUID().equals(getUUID()) : false;
	}

	public String getLevelAdvancementBar() {
		FileConfiguration levels = ConfigData.getCD(BountyHunters.plugin, "", "levels");
		String lvlAdvancement = "";
		int bountiesNeeded = (int) getValueDependingOnLevel(levels.getConfigurationSection("bounties-needed"), getLevel());
		for (int j = 0; j < bountiesNeeded; j++)
			lvlAdvancement += (getClaimedBounties() % bountiesNeeded > j ? ChatColor.GREEN : ChatColor.WHITE) + SpecialChar.square;
		return lvlAdvancement;
	}

	public ItemStack getLevelAdvancementItem() {
		ItemStack item = CustomItem.LVL_ADVANCEMENT.a().clone();
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(itemMeta.getDisplayName().replace("%name%", playerName).replace("%level%", "" + getLevel()));

		List<String> itemLore = itemMeta.getLore();
		for (int j = 0; j < itemLore.size(); j++) {
			String s = itemLore.get(j);
			s = s.replace("%level%", "" + getLevel()).replace("%lvl-advancement%", getLevelAdvancementBar());
			itemLore.set(j, s);
		}
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		return item;
	}

	public ItemStack getProfileItem() {
		ItemStack profile = CustomItem.PROFILE.a().clone();
		SkullMeta profileMeta = (SkullMeta) profile.getItemMeta();
		profileMeta.setDisplayName(profileMeta.getDisplayName().replace("%name%", playerName));
		profileMeta.setOwner(playerName);
		List<String> profileLore = profileMeta.getLore();

		String title = hasTitle() ? getTitle() : Message.NO_TITLE.getUpdated();
		for (int j = 0; j < profileLore.size(); j++)
			profileLore.set(j, profileLore.get(j).replace("%claimed-bounties%", "" + getClaimedBounties()).replace("%successful-bounties%", "" + getSuccessfulBounties()).replace("%current-title%", title).replace("%level%", "" + getLevel()));

		profileMeta.setLore(profileLore);
		profile.setItemMeta(profileMeta);
		return profile;
	}

	public void updateLevel(Player player) {
		FileConfiguration levels = ConfigData.getCD(BountyHunters.plugin, "", "levels");
		for (int j = 50; j > 0; j--) {
			int bountiesNeeded = (int) getValueDependingOnLevel(levels.getConfigurationSection("bounties-needed"), j);
			if (getClaimedBounties() < bountiesNeeded || getLevel() >= j)
				continue;

			Message.CHAT_BAR.format(ChatColor.YELLOW).send(player);
			Message.LEVEL_UP.format(ChatColor.YELLOW, "%level%", "" + j).send(player);
			Message.LEVEL_UP_2.format(ChatColor.YELLOW, "%bounties%", "" + levels.getInt("bounties-per-level")).send(player);

			List<String> chatDisplay = new ArrayList<String>();

			// titles
			for (String titleId : levels.getConfigurationSection("reward.title").getKeys(false))
				if (j >= levels.getInt("reward.title." + titleId + ".unlock") && !unlocked.contains(titleId)) {
					addUnlocked(titleId);
					chatDisplay.add(levels.getString("reward.title." + titleId + ".format"));
				}

			// death quotes
			for (String quoteId : levels.getConfigurationSection("reward.quote").getKeys(false))
				if (j >= levels.getInt("reward.quote." + quoteId + ".unlock") && !unlocked.contains(quoteId)) {
					addUnlocked(quoteId);
					chatDisplay.add(levels.getString("reward.quote." + quoteId + ".format"));
				}

			// send commands
			for (String s : levels.getStringList("reward.commands." + j))
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", player.getName()));

			// money
			double money = levels.getDouble("reward.money.base") + (j * levels.getDouble("reward.money.per-level"));
			BountyHunters.getEconomy().depositPlayer(player, money);

			// send json list
			String jsonList = money > 0 ? "\n" + Message.LEVEL_UP_REWARD.formatRaw(ChatColor.YELLOW, "%reward%", "$" + money) : "";
			for (String s : chatDisplay)
				jsonList += "\n" + Message.LEVEL_UP_REWARD.formatRaw(ChatColor.YELLOW, "%reward%", SpecialChar.apply(s));
			BountyHunters.json.message(player, "{\"text\":\"" + ChatColor.YELLOW + Message.LEVEL_UP_REWARDS.getUpdated() + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + jsonList.substring(1) + "\"}}}");

			setLevel(j);
			setUnlocked(unlocked);
			break;
		}
	}

	public void saveFile() {
		config.set("unlocked", unlocked);
		ConfigData.saveCD(BountyHunters.plugin, config, "/userdata", uuid.toString());
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
