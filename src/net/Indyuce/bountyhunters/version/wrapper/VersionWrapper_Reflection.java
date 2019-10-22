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
import net.Indyuce.bountyhunters.version.wrapper.api.NBTItem;

public class VersionWrapper_Reflection implements VersionWrapper {

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

	// private Class<?> obc(String str) throws ClassNotFoundException {
	// return Class.forName("org.bukkit.craftbukkit." +
	// BountyHunters.getInstance().getVersion().toString() + "." + str);
	// }

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

	@Override
	public NBTItem getNBTItem(ItemStack item) {
		// TODO Auto-generated method stub
		return null;
	}
}
