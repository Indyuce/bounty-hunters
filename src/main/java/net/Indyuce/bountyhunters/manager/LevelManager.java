package net.Indyuce.bountyhunters.manager;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.player.reward.BountyAnimation;
import net.Indyuce.bountyhunters.api.player.reward.HunterTitle;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class LevelManager {
    private final Map<String, BountyAnimation> animations = new HashMap<>();
    private final Map<String, HunterTitle> titles = new HashMap<>();
    private final Map<Integer, List<String>> commands = new HashMap<>();

    private int bountiesPerLevel;
    private double moneyBase, moneyPerLevel;

    private boolean enabled;

    public LevelManager(FileConfiguration config) {
        reload(config);
    }

    public void reload(FileConfiguration config) {
        animations.clear();
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
                HunterTitle title = new HunterTitle(config.getConfigurationSection("reward.title." + key));
                titles.put(title.getId(), title);
            } catch (IllegalArgumentException exception) {
                BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not load title '" + key + "': " + exception.getMessage());
            }

        for (String key : config.getConfigurationSection("reward.animation").getKeys(false))
            try {
                BountyAnimation anim = new BountyAnimation(config.getConfigurationSection("reward.animation." + key));
                animations.put(anim.getId(), anim);
            } catch (IllegalArgumentException exception) {
                BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not load animation '" + key + "': " + exception.getMessage());
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

    public BountyAnimation getAnimation(String id) {
        return animations.get(id);
    }

    public HunterTitle getTitle(String id) {
        return titles.get(id);
    }

    public List<String> getCommands(int level) {
        return commands.get(level);
    }

    public boolean hasAnimation(String id) {
        return animations.containsKey(id);
    }

    public boolean hasTitle(String id) {
        return titles.containsKey(id);
    }

    public boolean hasCommands(int level) {
        return commands.containsKey(level);
    }

    public Collection<BountyAnimation> getAnimations() {
        return animations.values();
    }

    public Collection<HunterTitle> getTitles() {
        return titles.values();
    }

    public int getBountiesPerLevel() {
        return bountiesPerLevel;
    }

    public double calculateLevelMoney(int level) {
        return moneyBase + level * moneyPerLevel;
    }
}
