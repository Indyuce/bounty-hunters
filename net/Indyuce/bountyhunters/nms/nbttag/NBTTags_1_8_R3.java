package net.Indyuce.bountyhunters.nms.nbttag;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class NBTTags_1_8_R3 implements NBTTags {
	@Override
	public ItemStack add(ItemStack i, ItemTag... tags) {
		net.minecraft.server.v1_8_R3.ItemStack nmsi = CraftItemStack.asNMSCopy(i);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();

		for (ItemTag tag : tags) {
			if (tag.getValue() instanceof Boolean) {
				compound.setBoolean(tag.getPath(), (boolean) tag.getValue());
				continue;
			}
			if (tag.getValue() instanceof Double) {
				compound.setDouble(tag.getPath(), (double) tag.getValue());
				continue;
			}
			compound.setString(tag.getPath(), (String) tag.getValue());
		}

		nmsi.setTag(compound);
		i = CraftItemStack.asBukkitCopy(nmsi);
		return i;
	}
	@Override
	public String getStringTag(ItemStack i, String path) {
		if (i == null || i.getType() == Material.AIR)
			return "";

		net.minecraft.server.v1_8_R3.ItemStack nmsi = CraftItemStack.asNMSCopy(i);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();
		return compound.getString(path);
	}
}
