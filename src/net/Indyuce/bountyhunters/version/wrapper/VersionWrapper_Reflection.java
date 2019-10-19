package net.Indyuce.bountyhunters.version.wrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.CustomItem;

public class VersionWrapper_Reflection implements VersionWrapper {
	@Override
	public ItemStack addTag(ItemStack i, ItemTag... tags) {
		try {
			Object nmsStack = obc("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(obc("inventory.CraftItemStack"), i);
			Object compound = ((boolean) nmsStack.getClass().getDeclaredMethod("hasTag").invoke(nmsStack) ? nmsStack.getClass().getDeclaredMethod("getTag").invoke(nmsStack) : nms("NBTTagCompound").getDeclaredConstructor().newInstance());

			for (ItemTag tag : tags) {
				if (tag.getValue() instanceof Boolean)
					compound.getClass().getDeclaredMethod("setBoolean", String.class, Boolean.TYPE).invoke(compound, tag.getPath(), (boolean) tag.getValue());
				else if (tag.getValue() instanceof Double)
					compound.getClass().getDeclaredMethod("setDouble", String.class, Double.TYPE).invoke(compound, tag.getPath(), (double) tag.getValue());
				else if (tag.getValue() instanceof Integer)
					compound.getClass().getDeclaredMethod("setInt", String.class, int.class).invoke(compound, tag.getPath(), (Integer) tag.getValue());
				else if (tag.getValue() instanceof String)
					compound.getClass().getDeclaredMethod("setString", String.class, String.class).invoke(compound, tag.getPath(), (String) tag.getValue());

			}

			nmsStack.getClass().getDeclaredMethod("setTag", compound.getClass()).invoke(nmsStack, compound);
			return (ItemStack) obc("inventory.CraftItemStack").getDeclaredMethod("asBukkitCopy", nmsStack.getClass()).invoke(obc("inventory.CraftItemStack"), nmsStack);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException e) {
			e.printStackTrace();
			return i;
		}
	}

	@Override
	public String getStringTag(ItemStack i, String path) {
		if (i == null || i.getType() == Material.AIR)
			return "";

		try {
			Object nmsStack = obc("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(obc("inventory.CraftItemStack"), i);
			Object compound = ((boolean) nmsStack.getClass().getDeclaredMethod("hasTag").invoke(nmsStack) ? nmsStack.getClass().getDeclaredMethod("getTag").invoke(nmsStack) : nms("NBTTagCompound").getDeclaredConstructor().newInstance());
			return (String) compound.getClass().getDeclaredMethod("getString", String.class).invoke(compound, path);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int ticks, int fadeOut) {
		try {
			Object chatTitle = chatSerializer().getMethod("a", String.class).invoke(null, "{\"text\": \"" + title + "\"}");
			Object chatSubtitle = chatSerializer().getMethod("a", String.class).invoke(null, "{\"text\": \"" + subtitle + "\"}");

			Constructor<?> cons = nms("PacketPlayOutTitle").getConstructor(enumTitleAction(), nms("IChatBaseComponent"), int.class, int.class, int.class);
			Object titlePacket = cons.newInstance(enumTitleAction().getField("TITLE").get(null), chatTitle, fadeIn, ticks, fadeOut);
			Object subtitlePacket = cons.newInstance(enumTitleAction().getField("SUBTITLE").get(null), chatSubtitle, fadeIn, ticks, fadeOut);

			sendPacket(player, titlePacket);
			sendPacket(player, subtitlePacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendJson(Player player, String message) {
		try {
			Object chatMsg = chatSerializer().getMethod("a", String.class).invoke(null, message);
			Object titlePacket = nms("PacketPlayOutChat").getConstructor(nms("IChatBaseComponent")).newInstance(chatMsg);
			sendPacket(player, titlePacket);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | NoSuchMethodException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void sendPacket(Player p, Object packet) {
		try {
			Object handle = p.getClass().getMethod("getHandle").invoke(p);
			Object connection = handle.getClass().getDeclaredField("playerConnection").get(handle);
			connection.getClass().getMethod("sendPacket", nms("Packet")).invoke(connection, packet);
		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	private Class<?> nms(String str) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + BountyHunters.getInstance().getVersion().toString() + "." + str);
	}

	private Class<?> obc(String str) throws ClassNotFoundException {
		return Class.forName("org.bukkit.craftbukkit." + BountyHunters.getInstance().getVersion().toString() + "." + str);
	}

	private Class<?> enumTitleAction() throws SecurityException, ClassNotFoundException {
		return nms("PacketPlayOutTitle").getDeclaredClasses().length > 0 ? nms("PacketPlayOutTitle").getDeclaredClasses()[0] : nms("EnumTitleAction");
	}

	private Class<?> chatSerializer() throws SecurityException, ClassNotFoundException {
		return nms("IChatBaseComponent").getDeclaredClasses().length > 0 ? nms("IChatBaseComponent").getDeclaredClasses()[0] : nms("ChatSerializer");
	}

	@Override
	public ItemStack getHead(OfflinePlayer player) {

		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) CustomItem.PLAYER_HEAD.toItemStack().getItemMeta();
		meta.setDisplayName(meta.getDisplayName().replace("{name}", player.getName()));
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
}
