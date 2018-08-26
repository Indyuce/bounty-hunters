package net.Indyuce.bh.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bh.ConfigData;
import net.Indyuce.bh.Main;
import net.Indyuce.bh.resource.CustomItem;
import net.Indyuce.bh.resource.Message;
import net.Indyuce.bh.resource.SpecialChar;
import net.Indyuce.bh.util.Utils;

public class PlayerData {
	private UUID uuid;
	private String playerName;
	private FileConfiguration config;

	private List<String> unlocked;

	public PlayerData(OfflinePlayer player) {
		this(player.getUniqueId(), player.getName());
	}

	public PlayerData(UUID uuid, String playerName) {
		this.uuid = uuid;
		this.playerName = playerName;
		this.config = ConfigData.getCD(Main.plugin, "/userdata", uuid.toString());
		this.unlocked = config.getStringList("unlocked");
	}

	public static PlayerData get(OfflinePlayer player) {
		return new PlayerData(player.getUniqueId(), player.getName());
	}

	public static PlayerData get(UUID uuid, String playerName) {
		return new PlayerData(uuid, playerName);
	}

	public String getPlayerName() {
		return playerName;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getInt(String path) {
		return config.getInt(path);
	}

	public void add(String path, int value) {
		config.set(path, config.getInt(path) + value);
	}

	public String getString(String path) {
		return config.getString(path);
	}

	public void set(String path, Object value) {
		config.set(path, value);
	}

	public List<String> getUnlocked() {
		return unlocked;
	}

	public void addUnlocked(String value) {
		unlocked.add(value);
	}

	public String getLevelAdvancementBar() {
		FileConfiguration levels = ConfigData.getCD(Main.plugin, "", "levels");
		String lvlAdvancement = "";
		int ntlu = levels.getInt("bounties-needed-to-lvl-up");
		for (int j = 0; j < ntlu; j++)
			lvlAdvancement += (getInt("claimed-bounties") % ntlu > j ? ChatColor.GREEN : ChatColor.WHITE) + SpecialChar.square;
		return lvlAdvancement;
	}

	public ItemStack getLevelAdvancementItem() {
		ItemStack item = CustomItem.LVL_ADVANCEMENT.a().clone();
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(itemMeta.getDisplayName().replace("%name%", playerName).replace("%level%", "" + getInt("level")));

		List<String> itemLore = itemMeta.getLore();
		for (int j = 0; j < itemLore.size(); j++) {
			String s = itemLore.get(j);
			s = s.replace("%level%", "" + getInt("level")).replace("%lvl-advancement%", getLevelAdvancementBar());
			itemLore.set(j, s);
		}
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);
		return item;
	}

	@SuppressWarnings("deprecation")
	public ItemStack getProfileItem() {
		ItemStack profile = CustomItem.PROFILE.a().clone();
		SkullMeta profileMeta = (SkullMeta) profile.getItemMeta();
		profileMeta.setDisplayName(profileMeta.getDisplayName().replace("%name%", playerName));
		profileMeta.setOwner(playerName);
		List<String> profileLore = profileMeta.getLore();

		String title = getString("current-title").equals("") ? Message.NO_TITLE.getUpdated() : Utils.applySpecialChars(getString("current-title"));
		for (int j = 0; j < profileLore.size(); j++)
			profileLore.set(j, profileLore.get(j).replace("%claimed-bounties%", "" + getInt("claimed-bounties")).replace("%successful-bounties%", "" + getInt("successful-bounties")).replace("%current-title%", title).replace("%level%", "" + getInt("level")));

		profileMeta.setLore(profileLore);
		profile.setItemMeta(profileMeta);
		return profile;
	}

	public void updateLevel(Player p) {
		FileConfiguration levels = ConfigData.getCD(Main.plugin, "", "levels");
		for (int j = 30; j > 0; j--) {
			int ntlu = j * levels.getInt("bounties-needed-to-lvl-up");
			if (getInt("claimed-bounties") < ntlu || getInt("level") >= j)
				continue;

			Message.CHAT_BAR.format(ChatColor.YELLOW).send(p);
			Message.LEVEL_UP.format(ChatColor.YELLOW, "%level%", "" + j).send(p);
			Message.LEVEL_UP_2.format(ChatColor.YELLOW, "%bounties%", "" + levels.getInt("bounties-needed-to-lvl-up")).send(p);

			List<String> unlocked = getUnlocked();
			List<String> rewards = new ArrayList<String>();

			// title rewards
			for (String title : levels.getConfigurationSection("reward.title").getKeys(false)) {
				int rewardLevel = 0;
				try {
					rewardLevel = Integer.parseInt(title);
				} catch (Exception e) {
					continue;
				}
				String reward = levels.getString("reward.title." + title);
				if (j >= rewardLevel && !unlocked.contains(reward))
					rewards.add(reward);
			}

			// quote rewards
			for (String quote : levels.getConfigurationSection("reward.quote").getKeys(false)) {
				int rewardLevel = 0;
				try {
					rewardLevel = Integer.parseInt(quote);
				} catch (Exception e) {
					continue;
				}
				String reward = levels.getString("reward.quote." + quote);
				if (j >= rewardLevel && !unlocked.contains(reward))
					rewards.add(reward);
			}

			unlocked.addAll(rewards);

			double money = levels.getDouble("reward.money.base") + (j * levels.getDouble("reward.money.per-lvl"));
			String rewardsFormat = "";
			if (money > 0)
				rewardsFormat += "\n" + ChatColor.YELLOW + Message.LEVEL_UP_REWARD.getUpdated() + money + ChatColor.YELLOW + " money";
			for (String s : rewards)
				rewardsFormat += "\n" + ChatColor.YELLOW + Message.LEVEL_UP_REWARD.getUpdated() + Utils.applySpecialChars(s);
			rewardsFormat = rewardsFormat.substring(1);

			Main.json.message(p, "{\"text\":\"" + ChatColor.YELLOW + Message.LEVEL_UP_REWARDS.getUpdated() + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"" + rewardsFormat + "\"}}}");

			Main.getEconomy().depositPlayer(p, money);
			set("level", j);
			set("unlocked", unlocked);
			
			// send commands
			for (String s : levels.getStringList("reward.commands." + j))
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", p.getName()));
			
			break;
		}
	}

	public void save() {
		config.set("unlocked", unlocked);
		ConfigData.saveCD(Main.plugin, config, "/userdata", uuid.toString());
	}

	public void setup() {
		for (ParamEnum pa : ParamEnum.values()) {
			String path = pa.name().toLowerCase().replace("_", "-");
			if (!config.getKeys(false).contains(path))
				config.set(path, pa.defaultValue);
		}
		save();
	}

	public enum ParamEnum {
		CLAIMED_BOUNTIES(0),
		SUCCESSFUL_BOUNTIES(0),
		LEVEL(0),
		CURRENT_TITLE(""),
		CURRENT_QUOTE(""),
		UNLOCKED(new ArrayList<String>());

		public Object defaultValue;

		private ParamEnum(Object defaultValue) {
			this.defaultValue = defaultValue;
		}
	}
}
