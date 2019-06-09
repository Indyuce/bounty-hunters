package net.Indyuce.bountyhunters.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface PluginInventory extends InventoryHolder {
	public int getPage();

	public Player getPlayer();

	public void whenClicked(ItemStack i, InventoryAction action, int slot);

	public default void open() {
		getPlayer().openInventory(getInventory());
	}
}
