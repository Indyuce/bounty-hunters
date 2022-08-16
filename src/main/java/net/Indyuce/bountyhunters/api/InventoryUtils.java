package net.Indyuce.bountyhunters.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
	private final Player player;

	public InventoryUtils(Player player) {
		this.player = player;
	}

	public void giveItems(ItemStack... items) {
		for (ItemStack drop : player.getInventory().addItem(items).values())
			player.getWorld().dropItem(player.getLocation(), drop);
	}

	@Deprecated
	public void setHandItem(EquipmentSlot slot, ItemStack item) {
		if (slot == EquipmentSlot.HAND)
			player.getInventory().setItemInMainHand(item);
		else
			player.getInventory().setItemInOffHand(item);
	}
}
