package net.Indyuce.bountyhunters.version.nms;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.bountyhunters.api.ItemTag;

public interface NMSHandler {
	public ItemStack addTag(ItemStack i, ItemTag... tags);

	public String getStringTag(ItemStack i, String path);

	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int ticks, int fadeOut);

	public void sendJson(Player player, String message);
}
