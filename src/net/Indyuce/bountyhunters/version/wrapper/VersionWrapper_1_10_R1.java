package net.Indyuce.bountyhunters.version.wrapper;

import java.util.List;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bountyhunters.version.VersionMaterial;
import net.Indyuce.bountyhunters.version.wrapper.api.ItemTag;
import net.Indyuce.bountyhunters.version.wrapper.api.NBTItem;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction;

public class VersionWrapper_1_10_R1 implements VersionWrapper {

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

	@Override
	public void spawnParticle(Particle particle, Location loc, Player player, Color color) {
		player.spawnParticle(particle, loc, 0, (double) color.getRed() / 255, (double) color.getGreen() / 255, (double) color.getBlue() / 255, 0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getHead(OfflinePlayer player) {

		ItemStack item = VersionMaterial.PLAYER_HEAD.toItem();
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(player.getName());
		item.setItemMeta(meta);

		return item;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setOwner(SkullMeta meta, OfflinePlayer player) {
		meta.setOwner(player.getName());
	}

	@Override
	public NBTItem getNBTItem(org.bukkit.inventory.ItemStack item) {
		return new NBTItem_v1_10_R1(item);
	}

	public class NBTItem_v1_10_R1 extends NBTItem {
		private final net.minecraft.server.v1_10_R1.ItemStack nms;
		private final NBTTagCompound compound;

		public NBTItem_v1_10_R1(org.bukkit.inventory.ItemStack item) {
			super(item);

			nms = CraftItemStack.asNMSCopy(item);
			compound = nms.hasTag() ? nms.getTag() : new NBTTagCompound();
		}

		@Override
		public String getString(String path) {
			return compound.getString(path);
		}

		@Override
		public boolean hasTag(String path) {
			return compound.hasKey(path);
		}

		@Override
		public boolean getBoolean(String path) {
			return compound.getBoolean(path);
		}

		@Override
		public double getDouble(String path) {
			return compound.getDouble(path);
		}

		@Override
		public int getInteger(String path) {
			return compound.getInt(path);
		}

		@Override
		public NBTItem addTag(List<ItemTag> tags) {
			tags.forEach(tag -> {
				if (tag.getValue() instanceof Boolean)
					compound.setBoolean(tag.getPath(), (boolean) tag.getValue());
				else if (tag.getValue() instanceof Double)
					compound.setDouble(tag.getPath(), (double) tag.getValue());
				else if (tag.getValue() instanceof String)
					compound.setString(tag.getPath(), (String) tag.getValue());
				else if (tag.getValue() instanceof Integer)
					compound.setInt(tag.getPath(), (int) tag.getValue());
			});
			return this;
		}

		@Override
		public NBTItem removeTag(String... paths) {
			for (String path : paths)
				compound.remove(path);
			return this;
		}

		@Override
		public Set<String> getTags() {
			return compound.c();
		}

		@Override
		public org.bukkit.inventory.ItemStack toItem() {
			nms.setTag(compound);
			return CraftItemStack.asBukkitCopy(nms);
		}
	}
}
