package net.Indyuce.bountyhunters.version.wrapper;

import net.Indyuce.bountyhunters.version.wrapper.api.NBTItem;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public interface VersionWrapper {

	/**
	 * @param item Item which NBT we want to manipulate
	 * @return See {@link NBTItem}
	 */
	NBTItem getNBTItem(ItemStack item);

	void sendTitle(Player player, String title, String subtitle, int fadeIn, int ticks, int fadeOut);

	void sendJson(Player player, String message);

	void spawnParticle(Particle particle, Location loc, Player player, Color color);

	ItemStack getHead(OfflinePlayer player);

	void setOwner(SkullMeta meta, OfflinePlayer player);

	/**
	 * @return If the two materials match. This only checks for the item materials,
	 * and does take into account pre 1.14 item data.
	 */
	boolean matchesMaterial(ItemStack item, ItemStack item1);
}
