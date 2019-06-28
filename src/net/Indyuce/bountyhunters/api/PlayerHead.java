package net.Indyuce.bountyhunters.api;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerHead extends ItemStack {
	public PlayerHead(OfflinePlayer player) {
		super(Material.PLAYER_HEAD);

		SkullMeta headMeta = (SkullMeta) CustomItem.PLAYER_HEAD.toItemStack().getItemMeta();
		headMeta.setDisplayName(headMeta.getDisplayName().replace("%name%", player.getName()));
		headMeta.setOwningPlayer(player);
		setItemMeta(headMeta);
	}
}
