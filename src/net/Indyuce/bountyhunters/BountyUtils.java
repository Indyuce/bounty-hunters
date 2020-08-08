package net.Indyuce.bountyhunters;

import org.bukkit.inventory.ItemStack;

import net.Indyuce.bountyhunters.api.NumberFormat;

public class BountyUtils {

	@Deprecated
	public static String format(double d) {
		return new NumberFormat().format(d);
	}

	/*
	 * checks if an item either has a display name, or both a display name and
	 * lore.
	 */
	public static boolean hasItemMeta(ItemStack item, boolean lore) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && (!lore || item.getItemMeta().getLore() != null);
	}

	public static double truncate(double x, int n) {
		double pow = Math.pow(10.0, n);
		return Math.floor(x * pow) / pow;
	}
}