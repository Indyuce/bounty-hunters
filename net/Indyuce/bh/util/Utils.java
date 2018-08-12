package net.Indyuce.bh.util;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.bh.ConfigData;
import net.Indyuce.bh.Main;
import net.Indyuce.bh.api.Bounty;
import net.Indyuce.bh.resource.CustomItem;
import net.Indyuce.bh.resource.SpecialChar;

public class Utils {
	public static boolean isCompass(ItemStack i) {
		return isPluginItem(i, true) ? i.getItemMeta().getLore().equals(CustomItem.BOUNTY_COMPASS.a().getItemMeta().getLore()) : false;
	}

	public static String applySpecialChars(String s) {
		return ChatColor.translateAlternateColorCodes('&', s.replace("%star%", SpecialChar.star).replace("%square%", SpecialChar.square));
	}

	public static String format(double n) {
		if (!Main.plugin.getConfig().getBoolean("formatted-numbers"))
			return "" + n;

		String[] prefixes = new String[] { "M", "B", "Tril", "Quad", "Quin", "Sext", "Sept", "Octi", "Noni", "Deci" };
		for (int j = 9; j >= 0; j--) {
			double b = Math.pow(10, 6 + 3 * j);
			if (n > b)
				return new DecimalFormat("#.###").format(n / b) + prefixes[j];
		}
		return new DecimalFormat("0.#").format(n);
	}

	public static void playerLoop(Player p) {
		ItemStack i = VersionUtils.getMainItem(p);
		if (!isCompass(i))
			return;

		Player compassTarget = null;
		for (Bounty bounty : Main.getBountyManager().getBounties())
			if (bounty.isHunting(p)) {
				compassTarget = Bukkit.getPlayer(bounty.getTarget().getUniqueId());
				break;
			}

		if (compassTarget == null)
			return;

		String format = msg("in-another-world");
		if (p.getWorld().getName().equals(compassTarget.getWorld().getName())) {
			format = (Main.plugin.getConfig().getBoolean("round-distance") ? (int) (compassTarget.getLocation().distance(p.getLocation())) : new DecimalFormat("#.###").format(compassTarget.getLocation().distance(p.getLocation()))) + " blocks";
			p.setCompassTarget(compassTarget.getLocation().clone().add(.5, 0, .5));
		}

		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "[ " + ChatColor.GOLD + "" + ChatColor.BOLD + format + " " + ChatColor.GRAY + "" + ChatColor.BOLD + "]");
		i.setItemMeta(meta);
	}

	public static boolean isPluginItem(ItemStack i, boolean lore) {
		if (i != null)
			if (i.getItemMeta() != null)
				if (i.getItemMeta().getDisplayName() != null)
					return !lore || i.getItemMeta().getLore() != null;
		return false;
	}

	public static String msg(String path) {
		FileConfiguration messages = ConfigData.getCD(Main.plugin, "/language", "messages");
		String msg = messages.getString(path);
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		return msg;
	}

	public static double truncation(double x, int n) {
		double pow = Math.pow(10.0, n);
		return Math.floor(x * pow) / pow;
	}
}