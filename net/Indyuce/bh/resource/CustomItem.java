package net.Indyuce.bh.resource;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum CustomItem {
	NEXT_PAGE(new ItemStack(Material.ARROW), "Next"),
	PREVIOUS_PAGE(new ItemStack(Material.ARROW), "Previous"),
	PLAYER_HEAD(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "%name%"),
	GUI_PLAYER_HEAD(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "%name%", "&8&m--------------------------------", "%bounty-creator%", "%bounty-reward%", "%bounty-hunters%", "", "%bounty-instruction%", "%compass-instruction%", "&8&m--------------------------------"),
	LB_PLAYER_DATA(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "[%rank%] %name%", "&8&m-----------------------------", "Current Title: &f%title%", "Level: &f%level%", "Claimed Bounties: &f%bounties%", "&8&m-----------------------------"),
	PROFILE(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "%name%", "&8&m--------------------------------", "Claimed Bounties: &f%claimed-bounties%", "Successful Bounties: &f%successful-bounties%", "Level: &f%level%", "", "Current Title: &f%current-title%", "", "Type /bounties titles to manage your title.", "Type /bounties quotes to manage your quote.", "&8&m--------------------------------"),
	LVL_ADVANCEMENT(new ItemStack(Material.EMERALD), "[%level%] %name%", "&8&m--------------------------------", "Level: &f%level%", "Level Progress: %lvl-advancement%", "&8&m--------------------------------"),
	SET_BOUNTY(new ItemStack(Material.BOOK_AND_QUILL), "How to create a bounty?", "Use /bounty <player> <reward>", "to create a bounty on a player.", "", "&aHow to increase a bounty?", "Use /bounty <player> <amount>", "to increase a bounty.", "", "&aHow to remove a bounty?", "You can remove a bounty as the", "bounty creator by right clicking", "it in this menu."),
	BOUNTY_COMPASS(new ItemStack(Material.COMPASS), "Bounty Compass", "Allows you to see at which", "distance your target is."),;

	ItemStack item;
	public String name;
	public String[] lore;

	private CustomItem(ItemStack item, String name, String... lore) {
		this.item = item;
		this.name = name;
		this.lore = lore;
	}

	public void update(FileConfiguration config) {
		this.name = config.getString(name() + ".name");
		this.lore = config.getStringList(name() + ".lore").toArray(new String[0]);
	}

	public ItemStack a() {
		ItemStack i = item.clone();
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + ChatColor.translateAlternateColorCodes('&', name));
		meta.addItemFlags(ItemFlag.values());
		if (lore != null) {
			ArrayList<String> lore = new ArrayList<String>();
			for (String s : this.lore)
				lore.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', s));
			meta.setLore(lore);
		}
		i.setItemMeta(meta);
		return i;
	}
}