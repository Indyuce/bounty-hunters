package net.Indyuce.bountyhunters.version.nms;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMSHandler {
	public ItemStack addTag(ItemStack item, ItemTag... tags);

	public String getStringTag(ItemStack item, String path);

	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int ticks, int fadeOut);

	public void sendJson(Player player, String message);
}
