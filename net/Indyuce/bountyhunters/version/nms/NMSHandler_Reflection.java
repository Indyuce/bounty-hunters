package net.Indyuce.bountyhunters.version.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.bountyhunters.api.ItemTag;

public class NMSHandler_Reflection implements NMSHandler {
	@Override
	public ItemStack addTag(ItemStack i, ItemTag... tags) {
		try {
			Object nmsStack = ReflectUtils.obc("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(ReflectUtils.obc("inventory.CraftItemStack"), i);
			Object compound = ((boolean) nmsStack.getClass().getDeclaredMethod("hasTag").invoke(nmsStack) ? nmsStack.getClass().getDeclaredMethod("getTag").invoke(nmsStack) : ReflectUtils.nms("NBTTagCompound").getDeclaredConstructor().newInstance());

			for (ItemTag tag : tags) {
				if (tag.getValue() instanceof Boolean) {
					compound.getClass().getDeclaredMethod("setBoolean", String.class, Boolean.TYPE).invoke(compound, tag.getPath(), (boolean) tag.getValue());
					continue;
				}
				if (tag.getValue() instanceof Double) {
					compound.getClass().getDeclaredMethod("setDouble", String.class, Double.TYPE).invoke(compound, tag.getPath(), (double) tag.getValue());
					continue;
				}
				compound.getClass().getDeclaredMethod("setString", String.class, String.class).invoke(compound, tag.getPath(), (String) tag.getValue());
			}

			nmsStack.getClass().getDeclaredMethod("setTag", compound.getClass()).invoke(nmsStack, compound);
			return (ItemStack) ReflectUtils.obc("inventory.CraftItemStack").getDeclaredMethod("asBukkitCopy", nmsStack.getClass()).invoke(ReflectUtils.obc("inventory.CraftItemStack"), nmsStack);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException e) {
			e.printStackTrace();
			return i;
		}
	}

	@Override
	public String getStringTag(ItemStack i, String path) {
		if (i == null)
			return "";
		if (i.getType() == Material.AIR)
			return "";

		try {
			Object nmsStack = ReflectUtils.obc("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(ReflectUtils.obc("inventory.CraftItemStack"), i);
			Object compound = ((boolean) nmsStack.getClass().getDeclaredMethod("hasTag").invoke(nmsStack) ? nmsStack.getClass().getDeclaredMethod("getTag").invoke(nmsStack) : ReflectUtils.nms("NBTTagCompound").getDeclaredConstructor().newInstance());
			return (String) compound.getClass().getDeclaredMethod("getString", String.class).invoke(compound, path);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int ticks, int fadeOut) {
		try {
			Object chatTitle = ReflectUtils.chatSerializer().getMethod("a", String.class).invoke(null, "{\"text\": \"" + title + "\"}");
			Object chatSubtitle = ReflectUtils.chatSerializer().getMethod("a", String.class).invoke(null, "{\"text\": \"" + subtitle + "\"}");

			Constructor<?> cons = ReflectUtils.nms("PacketPlayOutTitle").getConstructor(ReflectUtils.enumTitleAction(), ReflectUtils.nms("IChatBaseComponent"), int.class, int.class, int.class);
			Object titlePacket = cons.newInstance(ReflectUtils.enumTitleAction().getField("TITLE").get(null), chatTitle, fadeIn, ticks, fadeOut);
			Object subtitlePacket = cons.newInstance(ReflectUtils.enumTitleAction().getField("SUBTITLE").get(null), chatSubtitle, fadeIn, ticks, fadeOut);

			ReflectUtils.sendPacket(player, titlePacket);
			ReflectUtils.sendPacket(player, subtitlePacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendJson(Player player, String message) {
		try {
			Object chatMsg = ReflectUtils.chatSerializer().getMethod("a", String.class).invoke(null, message);
			Object titlePacket = ReflectUtils.nms("PacketPlayOutChat").getConstructor(ReflectUtils.nms("IChatBaseComponent")).newInstance(chatMsg);
			ReflectUtils.sendPacket(player, titlePacket);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | NoSuchMethodException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
