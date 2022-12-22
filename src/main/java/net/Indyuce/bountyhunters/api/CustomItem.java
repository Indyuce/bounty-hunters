package net.Indyuce.bountyhunters.api;

import net.Indyuce.bountyhunters.BountyHunters;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.*;

public enum CustomItem {
    NEXT_PAGE(Material.ARROW, "Next"),
    PREVIOUS_PAGE(Material.ARROW, "Previous"),
    GUI_PLAYER_HEAD(Material.PLAYER_HEAD, "{target}",
            "",
            "{noCreator}&cThis player is a criminal!",
            "{isCreator}&7You set this bounty.",
            "{extraCreator}&7Set by &f{creator}&7.",
            "&7" + AltChar.listDash + " The reward is &f${reward}&7.",
            "&7" + AltChar.listDash + " &f{contributors} &7player(s) have contributed.",
            "&7" + AltChar.listDash + " &f{hunters} &7player(s) are tracking them.",
            "",
            "{isTarget}&cDon't let them kill you.",
            "{isCreator}&eRight click to take away your contribution.",
            "{isExtra}&eKill them to claim the bounty!",
            "{isHunter}&7" + AltChar.listDash + " Click to &euntarget &7them.",
            "{!isHunter}&7" + AltChar.listDash + " Click to &ctarget &7them for ${target_tax}."),
    LB_PLAYER_DATA(Material.PLAYER_HEAD, "[{rank}] {name}",
            "&8-----------------------------",
            "Claimed Bounties: &f{bounties}",
            "Head Collection: &f{successful_bounties}",
            "Current Title: &f{title}",
            "Level: &f{level}",
            "&8-----------------------------"),
    PROFILE(Material.PLAYER_HEAD, "[{level}] {name}",
            "&8--------------------------------",
            "Claimed Bounties: &f{claimed_bounties}",
            "Head Collection: &f{successful_bounties}",
            "Level: &f{level}",
            "Level Progress: {level_progress}", "",
            "Current Title: &f{current_title}",
            "",
            "Type /bounties titles to manage your title.",
            "Type /bounties quotes to manage your quote.",
            "&8--------------------------------"),
    SET_BOUNTY(Material.WRITABLE_BOOK, "How to create a bounty?",
            "Use /bounty <player> <reward>",
            "to create a bounty on a player.",
            "",
            "&aHow to increase a bounty?",
            "Use /bounty <player> <amount>",
            "to increase a bounty.",
            "",
            "&aHow to remove a bounty?",
            "Take off your contribution by right",
            "clicking the bounty item in this menu."),
    BOUNTY_COMPASS(Material.COMPASS, "Bounty Compass",
            "Allows you to see at which",
            "distance your target is."),
    ;

    private ItemStack item;
    private String name;
    private List<String> lore;
    private int customModelData;

    private CustomItem(Material mat, String name, String... lore) {
        this.item = new ItemStack(mat);
        this.name = name;
        this.lore = Arrays.asList(lore);
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    private static final NamespacedKey ITEM_ID_KEY = new NamespacedKey(BountyHunters.getInstance(), "BountyHuntersItemId");

    public void update(ConfigurationSection config) {
        this.name = ChatColor.GREEN + ChatColor.translateAlternateColorCodes('&', config.getString("name"));
        this.lore = config.getStringList("lore");
        this.customModelData = config.getInt("custom-model-data");

        for (int n = 0; n < lore.size(); n++)
            lore.set(n, ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', lore.get(n)));

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setCustomModelData(customModelData);
        meta.addItemFlags(ItemFlag.values());
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, name());
        if (lore != null)
            meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public ItemStack toItemStack() {
        return item.clone();
    }

    /**
     * Checks for lore and NBTTags.
     *
     * @return If the item matches the config item
     */
    public boolean matches(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta())
            return false;

        final @Nullable String tag = item.getItemMeta().getPersistentDataContainer().get(ITEM_ID_KEY, PersistentDataType.STRING);
        return tag != null && tag.equals(name());
    }

    public Builder newBuilder() {
        return new Builder();
    }

    /**
     * Used to format the item lore based on boolean
     * conditions and easily apply lore placeholders
     *
     * @author indyuce
     */
    public class Builder {
        private final Map<String, Boolean> conditions = new HashMap<>();
        private final Set<Placeholder> placeholders = new HashSet<>();

        private final ItemStack item = toItemStack();

        public Builder applyPlaceholders(Object... placeholders) {
            for (int j = 0; j < placeholders.length - 1; j += 2)
                this.placeholders.add(new Placeholder(placeholders[j].toString(), placeholders[j + 1].toString()));
            return this;
        }

        public Builder applyConditions(String[] conditions, boolean[] values) {
            for (int j = 0; j < Math.min(conditions.length, values.length); j++)
                this.conditions.put(conditions[j], values[j]);
            return this;
        }

        public ItemStack build() {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();

            /*
             * check for conditions.
             */
            String next;
            for (Iterator<String> iterator = lore.iterator(); iterator.hasNext(); )
                if ((next = iterator.next()).startsWith(ChatColor.GRAY + "{")) {
                    String condition = next.substring(3).split("\\}")[0];
                    if (conditions.containsKey(condition) && !conditions.get(condition))
                        iterator.remove();
                }

            for (int j = 0; j < lore.size(); j++)
                lore.set(j, format(lore.get(j)));
            meta.setDisplayName(format(meta.getDisplayName()));

            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;
        }

        private String format(String str) {

            // remove condition
            if (str.startsWith(ChatColor.GRAY + "{") && str.contains("}"))
                str = str.substring(str.indexOf("}") + 1);

            // apply placeholders
            for (Placeholder placeholder : placeholders)
                str = str.replace("{" + placeholder.placeholder + "}", placeholder.replacement);

            return str;
        }

        public class Placeholder {
            private final String placeholder, replacement;

            public Placeholder(String placeholder, String replacement) {
                this.placeholder = placeholder;
                this.replacement = replacement;
            }
        }
    }
}
