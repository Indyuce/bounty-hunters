package net.Indyuce.bountyhunters.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.AltChar;
import net.Indyuce.bountyhunters.api.player.PlayerData;

public class LevelManager {
	private final Map<String, DeathQuote> quotes = new HashMap<>();
	private final Map<String, Title> titles = new HashMap<>();
	private final Map<Integer, List<String>> commands = new HashMap<>();

	private int bountiesPerLevel;
	private double moneyBase, moneyPerLevel;

	private boolean enabled;

	public LevelManager(FileConfiguration config) {
		reload(config);
	}

	public void reload(FileConfiguration config) {
		quotes.clear();
		titles.clear();
		commands.clear();

		enabled = BountyHunters.getInstance().getConfig().getBoolean("enable-level-rewards");
		if (!isEnabled())
			return;

		bountiesPerLevel = config.getInt("bounties-per-level");
		moneyBase = config.getDouble("reward.money.base");
		moneyPerLevel = config.getDouble("reward.money.per-level");

		for (String key : config.getConfigurationSection("reward.title").getKeys(false))
			try {
				Title title = new Title(config.getConfigurationSection("reward.title." + key));
				titles.put(title.getId(), title);
			} catch (IllegalArgumentException exception) {
				BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not load title '" + key + "': " + exception.getMessage());
			}

		for (String key : config.getConfigurationSection("reward.quote").getKeys(false))
			try {
				DeathQuote quote = new DeathQuote(config.getConfigurationSection("reward.quote." + key));
				quotes.put(quote.getId(), quote);
			} catch (IllegalArgumentException exception) {
				BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not load quote '" + key + "': " + exception.getMessage());
			}

		for (String key : config.getConfigurationSection("reward.commands").getKeys(false))
			try {
				int level = Integer.parseInt(key);
				List<String> commands = config.getStringList("reward.commands." + key);

				Validate.notNull(commands, "Could not find command list");
				this.commands.put(level, commands);
			} catch (IllegalArgumentException exception) {
				BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not load command list '" + key + "': " + exception.getMessage());
			}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public DeathQuote getQuote(String id) {
		return quotes.get(id);
	}

	public Title getTitle(String id) {
		return titles.get(id);
	}

	public List<String> getCommands(int level) {
		return commands.get(level);
	}

	public boolean hasQuote(String id) {
		return quotes.containsKey(id);
	}

	public boolean hasTitle(String id) {
		return titles.containsKey(id);
	}

	public boolean hasCommands(int level) {
		return commands.containsKey(level);
	}

	public Collection<DeathQuote> getQuotes() {
		return quotes.values();
	}

	public Collection<Title> getTitles() {
		return titles.values();
	}

	public int getBountiesPerLevel() {
		return bountiesPerLevel;
	}

	public double calculateLevelMoney(int level) {
		return moneyBase + level * moneyPerLevel;
	}

	public abstract class LevelUpItem {
		private final String id, format;
		private final int unlock;

		private LevelUpItem(String id, String format, int unlock) {
			Validate.notNull(id, "Item ID must not be null");
			Validate.notNull(format, "Item format must not be null");

			this.id = id;
			this.format = format;
			this.unlock = unlock;
		}

		public String getId() {
			return id;
		}

		public int getUnlockLevel() {
			return unlock;
		}

		public boolean hasUnlocked(PlayerData data) {
			return data.getLevel() > unlock;
		}

		public String format() {
			return AltChar.apply(format);
		}

		@Override
		public boolean equals(Object obj) {
			return obj != null && obj instanceof LevelUpItem && ((LevelUpItem) obj).id.equals(id);
		}
	}

	public class Title extends LevelUpItem {
		private Title(ConfigurationSection config) {
			this(config.getName().toUpperCase().replace("-", "_"), config.getString("format"), config.getInt("unlock"));
		}

		private Title(String id, String format, int unlock) {
			super(id, format, unlock);
		}
	}

	public class DeathQuote extends LevelUpItem {
		private DeathQuote(ConfigurationSection config) {
			this(config.getName().toUpperCase().replace("-", "_"), config.getString("format"), config.getInt("unlock"));
		}

		private DeathQuote(String id, String format, int unlock) {
			super(id, format, unlock);
		}
	}
}
