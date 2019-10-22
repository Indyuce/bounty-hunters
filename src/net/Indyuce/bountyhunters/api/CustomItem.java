package net.Indyuce.bountyhunters.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.bountyhunters.BountyUtils;
import net.Indyuce.bountyhunters.version.VersionMaterial;

public enum CustomItem {
	NEXT_PAGE(new ItemStack(Material.ARROW), "Next"),
	PREVIOUS_PAGE(new ItemStack(Material.ARROW), "Previous"),
	PLAYER_HEAD(VersionMaterial.PLAYER_HEAD.toItem(), "{name}"),
	GUI_PLAYER_HEAD(VersionMaterial.PLAYER_HEAD.toItem(), "{target}", "", "{noCreator}&cThis player is a thug!", "{isCreator}&7You set this bounty.", "{extraCreator}&7Set by &f{creator}&7.",
			"&7" + AltChar.listDash + " The reward is &f${reward}&7.", "&7" + AltChar.listDash + " &f{contributors} &7player(s) have contributed.", "&7" + AltChar.listDash + " There are &f{hunters} &7player(s) tracking him.", "", "{isTarget}&cDon't let them kill you.",
			"{isCreator}&eRight click to take away your contribution.", "{isExtra}&eKill him to claim the bounty!", "{isHunter}&7" + AltChar.listDash + " Click to &euntarget &7him.",
			"{!isHunter}&7" + AltChar.listDash + " Click to &ctarget &7him."),
	LB_PLAYER_DATA(VersionMaterial.PLAYER_HEAD.toItem(), "[{rank}] {name}", "&8-----------------------------", "Claimed Bounties: &f{bounties}", "Head Collection: &f{successful_bounties}",
			"Current Title: &f{title}", "Level: &f{level}", "&8-----------------------------"),
	PROFILE(VersionMaterial.PLAYER_HEAD.toItem(), "[{level}] {name}", "&8--------------------------------", "Claimed Bounties: &f{claimed_bounties}", "Head Collection: &f{successful_bounties}",
			"Level: &f{level}", "Level Progress: {level_progress}", "", "Current Title: &f{current_title}", "", "Type /bounties titles to manage your title.",
			"Type /bounties quotes to manage your quote.", "&8--------------------------------"),
	SET_BOUNTY(new ItemStack(VersionMaterial.WRITABLE_BOOK.toMaterial()), "How to create a bounty?", "Use /bounty <player> <reward>", "to create a bounty on a player.", "",
			"&aHow to increase a bounty?", "Use /bounty <player> <amount>", "to increase a bounty.", "", "&aHow to remove a bounty?", "You can remove a bounty as the",
			"bounty creator by right clicking", "it in this menu."),
	BOUNTY_COMPASS(new ItemStack(Material.COMPASS), "Bounty Compass", "Allows you to see at which", "distance your target is."),

	;

	private ItemStack item;
	private String name;
	private List<String> lore;

	private static final String conditionPrefix = ChatColor.GRAY + "{";

	private CustomItem(ItemStack item, String name, String... lore) {
		this.item = item;
		this.name = name;
		this.lore = Arrays.asList(lore);
	}

	public String getName() {
		return name;
	}

	public List<String> getLore() {
		return lore;
	}

	public void update(ConfigurationSection config) {
		this.name = ChatColor.GREEN + ChatColor.translateAlternateColorCodes('&', config.getString("name"));
		this.lore = config.getStringList("lore");

		for (int n = 0; n < lore.size(); n++)
			lore.set(n, ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', lore.get(n)));

		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.addItemFlags(ItemFlag.values());
		if (lore != null)
			meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public ItemStack toItemStack() {
		return item.clone();
	}

	public boolean loreMatches(ItemStack item) {
		return BountyUtils.hasItemMeta(item, true) && item.getItemMeta().getLore().equals(lore);
	}

	public Builder newBuilder() {
		return new Builder();
	}

	/*
	 * allows to format the item lore based on boolean conditions, also applies
	 * placeholders
	 */
	public class Builder {
		private final Map<String, Boolean> conditions = new HashMap<>();
		private final Set<Placeholder> placeholders = new HashSet<>();

		private final ItemStack item = toItemStack();

		public Builder applyPlaceholders(String... placeholders) {
			for (int j = 0; j < placeholders.length - 1; j += 2)
				this.placeholders.add(new Placeholder(placeholders[j], placeholders[j + 1]));
			return this;
		}

		public Builder applyConditions(String[] conditions, boolean[] values) {
			for (int j = 0; j < conditions.length && j < values.length; j++)
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
			for (Iterator<String> iterator = lore.iterator(); iterator.hasNext();)
				if ((next = iterator.next()).startsWith(conditionPrefix)) {
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
			if (str.startsWith(conditionPrefix) && str.contains("}"))
				str = str.substring(str.indexOf("}") + 1);
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