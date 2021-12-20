package net.Indyuce.bountyhunters.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class PluginInventory implements InventoryHolder {
	protected final Player player;

	public PluginInventory(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void open() {
		getPlayer().openInventory(getInventory());
	}

	public abstract void whenClicked(ItemStack item, InventoryAction action, int slot);
}
