package net.Indyuce.bountyhunters.api;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerHead extends ItemStack {
	public PlayerHead(OfflinePlayer player) {
		super(Material.SKULL_ITEM, 1, (short) 3);

		SkullMeta headMeta = (SkullMeta) CustomItem.PLAYER_HEAD.a().getItemMeta();
		headMeta.setDisplayName(headMeta.getDisplayName().replace("%name%", player.getName()));
		headMeta.setOwner(player.getName());
		setItemMeta(headMeta);
	}
}
