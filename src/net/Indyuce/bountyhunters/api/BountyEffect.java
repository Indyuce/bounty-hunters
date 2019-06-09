package net.Indyuce.bountyhunters.api;

import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.bountyhunters.BountyHunters;

public class BountyEffect {
	private Material material = null;

	private static final Random random = new Random();

	public BountyEffect(ConfigurationSection section) {
		String format = section.getString("material");
		try {
			material = Material.valueOf(format.toUpperCase().replace(" ", "_").replace("-", "_"));
		} catch (Exception e) {
			BountyHunters.plugin.getLogger().log(Level.WARNING, "Bounty Effect: " + format + " is not a valid material name.");
		}
	}

	public void play(Location loc) {
		if (material == null)
			return;

		for (int j = 0; j < 8; j++) {
			ItemStack stack = new ItemStack(material);
			ItemMeta stackMeta = stack.getItemMeta();
			stackMeta.setDisplayName(UUID.randomUUID().toString());
			stack.setItemMeta(stackMeta);

			Item item = loc.getWorld().dropItemNaturally(loc, stack);
			item.setMetadata("BOUNTYHUNTERS:no_pickup", new FixedMetadataValue(BountyHunters.plugin, true));
			new BukkitRunnable() {
				public void run() {
					item.remove();
				}
			}.runTaskLater(BountyHunters.plugin, 30 + random.nextInt(40));
		}
	}
}
