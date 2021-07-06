package net.Indyuce.bountyhunters.manager;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.player.reward.DeathQuote;
import net.Indyuce.bountyhunters.api.player.reward.Title;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

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
}
