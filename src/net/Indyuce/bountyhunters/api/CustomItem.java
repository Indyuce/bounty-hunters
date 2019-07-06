package net.Indyuce.bountyhunters.api;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.bountyhunters.BountyUtils;

public enum CustomItem {
	NEXT_PAGE(new ItemStack(Material.ARROW), "Next"),
	PREVIOUS_PAGE(new ItemStack(Material.ARROW), "Previous"),
	PLAYER_HEAD(new ItemStack(Material.PLAYER_HEAD), "%name%"),
	GUI_PLAYER_HEAD(new ItemStack(Material.PLAYER_HEAD), "%name%", "&8--------------------------------", "%bounty-creator%", "%bounty-reward%", "%bounty-hunters%", "", "%bounty-instruction%", "%compass-instruction%", "&8--------------------------------"),
	LB_PLAYER_DATA(new ItemStack(Material.PLAYER_HEAD), "[%rank%] %name%", "&8-----------------------------", "Claimed Bounties: &f%bounties%", "Head Collection: &f%successful-bounties%", "Current Title: &f%title%", "Level: &f%level%", "&8-----------------------------"),
	PROFILE(new ItemStack(Material.PLAYER_HEAD), "[%level%] %name%", "&8--------------------------------", "Claimed Bounties: &f%claimed-bounties%", "Head Collection: &f%successful-bounties%", "Level: &f%level%", "Level Progress: %lvl-progress%", "", "Current Title: &f%current-title%", "", "Type /bounties titles to manage your title.", "Type /bounties quotes to manage your quote.", "&8--------------------------------"),
	SET_BOUNTY(new ItemStack(Material.WRITABLE_BOOK), "How to create a bounty?", "Use /bounty <player> <reward>", "to create a bounty on a player.", "", "&aHow to increase a bounty?", "Use /bounty <player> <amount>", "to increase a bounty.", "", "&aHow to remove a bounty?", "You can remove a bounty as the", "bounty creator by right clicking", "it in this menu."),
	BOUNTY_COMPASS(new ItemStack(Material.COMPASS), "Bounty Compass", "Allows you to see at which", "distance your target is."),

	;

	private ItemStack item;
	private String name;
	private List<String> lore;

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
		return BountyUtils.isPluginItem(item, true) && item.getItemMeta().getLore().equals(lore);
	}
}