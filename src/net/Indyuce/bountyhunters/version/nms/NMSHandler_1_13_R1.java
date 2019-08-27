package net.Indyuce.bountyhunters.version.nms;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import net.minecraft.server.v1_13_R1.PacketPlayOutChat;
import net.minecraft.server.v1_13_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_13_R1.PacketPlayOutTitle.EnumTitleAction;

public class NMSHandler_1_13_R1 implements NMSHandler {
	@Override
	public ItemStack addTag(ItemStack item, ItemTag... tags) {
		net.minecraft.server.v1_13_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();

		for (ItemTag tag : tags)
			if (tag.getValue() instanceof String)
				compound.setString(tag.getPath(), (String) tag.getValue());
			else if (tag.getValue() instanceof Boolean)
				compound.setBoolean(tag.getPath(), (boolean) tag.getValue());
			else if (tag.getValue() instanceof Double)
				compound.setDouble(tag.getPath(), (double) tag.getValue());
			else if (tag.getValue() instanceof Integer)
				compound.setInt(tag.getPath(), (Integer) tag.getValue());

		nmsi.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsi);
	}

	@Override
	public String getStringTag(ItemStack item, String path) {
		if (item == null || item.getType() == Material.AIR)
			return "";

		net.minecraft.server.v1_13_R1.ItemStack nmsi = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsi.hasTag() ? nmsi.getTag() : new NBTTagCompound();
		return compound.getString(path);
	}

	@Override
	public void sendJson(Player player, String message) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(message)));
	}

	@Override
	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int ticks, int fadeOut) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\": \"" + title + "\"}")));
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\": \"" + subtitle + "\"}")));
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fadeIn, ticks, fadeOut));
	}
}
