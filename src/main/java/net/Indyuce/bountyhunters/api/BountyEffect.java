package net.Indyuce.bountyhunters.api;

import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.bountyhunters.BountyHunters;

public class BountyEffect {
	private Material material;

	private static final Random random = new Random();

	public BountyEffect(ConfigurationSection section) {
		try {
			String format = section.getString("material");
			Validate.notNull(format, "Could not load material name");
			material = Material.valueOf(format.toUpperCase().replace(" ", "_").replace("-", "_"));
		} catch (IllegalArgumentException exception) {
			BountyHunters.getInstance().getLogger().log(Level.WARNING, "Could not load bounty item effect: " + exception.getMessage());
		}
	}

	public void play(Location loc) {
		if (material != null)
			for (int j = 0; j < 8; j++) {
				ItemStack stack = new ItemStack(material);
				ItemMeta stackMeta = stack.getItemMeta();
				stackMeta.setDisplayName(UUID.randomUUID().toString());
				stack.setItemMeta(stackMeta);

				Item item = loc.getWorld().dropItemNaturally(loc, stack);
				item.setPickupDelay(100000000);
				Bukkit.getScheduler().runTaskLater(BountyHunters.getInstance(), () -> item.remove(), 30 + random.nextInt(40));
			}
	}
}
