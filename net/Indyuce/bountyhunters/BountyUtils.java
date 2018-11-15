package net.Indyuce.bountyhunters;

import java.text.DecimalFormat;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BountyUtils {
	public static String format(double n) {
		if (!BountyHunters.plugin.getConfig().getBoolean("formatted-numbers"))
			return "" + n;

		String[] prefixes = new String[] { "M", "B", "Tril", "Quad", "Quin", "Sext", "Sept", "Octi", "Noni", "Deci" };
		for (int j = 9; j >= 0; j--) {
			double b = Math.pow(10, 6 + 3 * j);
			if (n > b)
				return new DecimalFormat("0.###").format(n / b) + prefixes[j];
		}
		return new DecimalFormat("0.#").format(n);
	}

	public static boolean isPluginItem(ItemStack item, boolean lore) {
		if (item != null && item.getType() != Material.AIR)
			if (item.getItemMeta() != null)
				if (item.getItemMeta().getDisplayName() != null)
					return !lore || item.getItemMeta().getLore() != null;
		return false;
	}

	public static double truncation(double x, int n) {
		double pow = Math.pow(10., n);
		return Math.floor(x * pow) / pow;
	}
}