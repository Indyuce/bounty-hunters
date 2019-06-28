package net.Indyuce.bountyhunters;

import java.text.DecimalFormat;

import org.bukkit.inventory.ItemStack;

public class BountyUtils {
	private static final DecimalFormat digit1 = new DecimalFormat("0.#"), digit3 = new DecimalFormat("0.###");

	public static String format(double n) {
		if (!BountyHunters.plugin.getConfig().getBoolean("formatted-numbers"))
			return "" + n;

		String[] prefixes = new String[] { "M", "B", "Tril", "Quad", "Quin", "Sext", "Sept", "Octi", "Noni", "Deci" };
		for (int j = 9; j >= 0; j--) {
			double b = Math.pow(10, 6 + 3 * j);
			if (n > b)
				return digit3.format(n / b) + prefixes[j];
		}
		return digit1.format(n);
	}

	/*
	 * checks if an item either has a display name, or both a display name and
	 * lore.
	 */
	public static boolean isPluginItem(ItemStack item, boolean lore) {
		return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && (!lore || item.getItemMeta().getLore() != null);
	}

	public static double truncation(double x, int n) {
		double pow = Math.pow(10.0, n);
		return Math.floor(x * pow) / pow;
	}
}