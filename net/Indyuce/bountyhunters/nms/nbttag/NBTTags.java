package net.Indyuce.bountyhunters.nms.nbttag;

import org.bukkit.inventory.ItemStack;

public interface NBTTags {
	public ItemStack add(ItemStack i, ItemTag... tags);

	public String getStringTag(ItemStack i, String path);
}
