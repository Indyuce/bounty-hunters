package net.Indyuce.bountyhunters.version.wrapper;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.version.wrapper.api.ItemTag;
import net.Indyuce.bountyhunters.version.wrapper.api.NBTItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VersionWrapper_Reflection implements VersionWrapper {

	@Override
	public boolean matchesMaterial(ItemStack item, ItemStack item1) {
		return item.getType() == item1.getType();
	}

	@Override
	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int ticks, int fadeOut) {
		player.sendTitle(title, subtitle, fadeIn, ticks, fadeOut);
	}

	/*
	 * cannot use spigot() methods because of other bukkit forks? this method
	 * works anyways
	 */
	@Override
	public void sendJson(Player player, String message) {
		getConnection(player).sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(message), ChatMessageType.a, UUID.randomUUID()));
	}

	/**
	 * @return Object required to send packets
	 */
	private PlayerConnection getConnection(Player player) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			return (PlayerConnection) handle.getClass().getDeclaredField("b").get(handle);

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException exception) {
			throw new RuntimeException("Reflection issue: " + exception.getMessage());
		}
	}

	private Class<?> obc(String str) throws ClassNotFoundException {
		return Class.forName("org.bukkit.craftbukkit." + BountyHunters.getInstance().getVersion().toString() + "." + str);
	}

	@Override
	public ItemStack getHead(OfflinePlayer player) {

		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(player);
		item.setItemMeta(meta);

		return item;
	}

	@Override
	public void spawnParticle(Particle particle, Location loc, Player player, Color color) {
		player.spawnParticle(particle, loc, 0, new Particle.DustOptions(color, 1));
	}

	@Override
	public void setOwner(SkullMeta meta, OfflinePlayer player) {
		meta.setOwningPlayer(player);
	}

	@Override
	public NBTItem getNBTItem(org.bukkit.inventory.ItemStack item) {
		return new NBTItem_Reflection(item);
	}

	public class NBTItem_Reflection extends NBTItem {
		private final net.minecraft.world.item.ItemStack nms;
		private final NBTTagCompound compound;

		public NBTItem_Reflection(ItemStack item) {
			super(item);

			try {
				nms = (net.minecraft.world.item.ItemStack) obc("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException
					| ClassNotFoundException exception) {
				throw new RuntimeException("Reflection issue: " + exception.getMessage());
			}

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
			return compound.getKeys();
		}

		@Override
		public ItemStack toItem() {
			nms.setTag(compound);

			try {
				return (ItemStack) obc("inventory.CraftItemStack").getMethod("asBukkitCopy", nms.getClass()).invoke(null, nms);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException exception) {
				throw new RuntimeException("Reflection issue: " + exception.getMessage());
			}
		}
	}
}
