package net.Indyuce.bountyhunters.version.wrapper;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public interface VersionWrapper {
	ItemStack addTag(ItemStack item, ItemTag... tags);

	String getStringTag(ItemStack item, String path);

	void sendTitle(Player player, String title, String subtitle, int fadeIn, int ticks, int fadeOut);

	void sendJson(Player player, String message);

	void spawnParticle(Particle particle, Location loc, Player player, Color color);

	ItemStack getHead(OfflinePlayer player);
	
	void setOwner(SkullMeta meta, OfflinePlayer player);
}
