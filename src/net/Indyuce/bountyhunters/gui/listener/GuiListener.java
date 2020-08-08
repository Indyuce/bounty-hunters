package net.Indyuce.bountyhunters.gui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.bountyhunters.gui.PluginInventory;

public class GuiListener implements Listener {
	@EventHandler
	public void a(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if (event.getInventory().getHolder() instanceof PluginInventory) {
			event.setCancelled(true);
			if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getInventory()))
				((PluginInventory) event.getInventory().getHolder()).whenClicked(item, event.getAction(), event.getSlot());
		}
	}
}
