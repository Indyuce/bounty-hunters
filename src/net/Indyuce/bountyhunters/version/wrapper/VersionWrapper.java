package net.Indyuce.bountyhunters.version.wrapper;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bountyhunters.version.wrapper.api.NBTItem;

public interface VersionWrapper {
	NBTItem getNBTItem(ItemStack item);

	void sendTitle(Player player, String title, String subtitle, int fadeIn, int ticks, int fadeOut);

	void sendJson(Player player, String message);

	void spawnParticle(Particle particle, Location loc, Player player, Color color);

	ItemStack getHead(OfflinePlayer player);

	void setOwner(SkullMeta meta, OfflinePlayer player);
	
	boolean matchesMaterial(ItemStack item, ItemStack item1);
}
