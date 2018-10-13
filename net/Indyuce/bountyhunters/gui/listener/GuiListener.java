package net.Indyuce.bountyhunters.gui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.bountyhunters.BountyUtils;
import net.Indyuce.bountyhunters.gui.PluginInventory;

public class GuiListener implements Listener {
	@EventHandler
	public void a(InventoryClickEvent e) {
		ItemStack i = e.getCurrentItem();
		if (e.getInventory().getHolder() instanceof PluginInventory) {
			e.setCancelled(true);
			if (e.getClickedInventory() != e.getInventory() || !BountyUtils.isPluginItem(i, false))
				return;

			((PluginInventory) e.getInventory().getHolder()).whenClicked(i, e.getAction(), e.getSlot());
		}
	}
}
