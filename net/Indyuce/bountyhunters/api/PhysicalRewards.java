package net.Indyuce.bountyhunters.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.bountyhunters.BountyHunters;

public class PhysicalRewards {
	private ConfigurationSection config;
	List<ItemStack> items = null;

	public PhysicalRewards(ConfigurationSection config) {
		this.config = config;
	}

	public List<ItemStack> readItems() {
		if (items != null)
			return items;

		List<ItemStack> items = new ArrayList<ItemStack>();

		for (String key : config.getKeys(false)) {
			ItemStack item = readItem(key, config.getString(key));
			if (item != null)
				items.add(item);
		}

		return this.items = items;
	}

	/*
	 * item format: "MATERIAL: <amount> <durability>"
	 */
	private ItemStack readItem(String key, String value) {
		Material material = null;
		String materialFormat = key.toUpperCase().replace(" ", "_").replace("-", "_");
		try {
			material = Material.valueOf(materialFormat);
		} catch (Exception e) {
			BountyHunters.plugin.getLogger().log(Level.WARNING, "Physical Rewards: " + materialFormat + " is not a valid material name.");
			return null;
		}

		String[] split = value.split("\\ ");
		int amount = 0;
		try {
			amount = Integer.valueOf(split[0]);
		} catch (Exception e) {
			BountyHunters.plugin.getLogger().log(Level.WARNING, "Physical Rewards: " + split[0] + " is not a valid number.");
			return null;
		}

		short durability = 0;
		if (split.length > 0)
			try {
				durability = (short) (int) Integer.valueOf(split[1]);
			} catch (Exception e) {
				BountyHunters.plugin.getLogger().log(Level.WARNING, "Physical Rewards: " + split[1] + " is not a valid number.");
				return null;
			}

		return new ItemStack(material, amount, durability);
	}
}
