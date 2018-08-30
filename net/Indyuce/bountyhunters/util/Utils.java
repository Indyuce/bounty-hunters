package net.Indyuce.bountyhunters.util;

import java.text.DecimalFormat;

import org.bukkit.inventory.ItemStack;

import net.Indyuce.bountyhunters.BountyHunters;

public class Utils {
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

	public static boolean isPluginItem(ItemStack i, boolean lore) {
		if (i != null)
			if (i.getItemMeta() != null)
				if (i.getItemMeta().getDisplayName() != null)
					return !lore || i.getItemMeta().getLore() != null;
		return false;
	}

	public static double truncation(double x, int n) {
		double pow = Math.pow(10.0, n);
		return Math.floor(x * pow) / pow;
	}
}