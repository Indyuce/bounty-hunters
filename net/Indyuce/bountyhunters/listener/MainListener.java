package net.Indyuce.bountyhunters.listener;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import net.Indyuce.bountyhunters.api.PlayerData;

public class MainListener implements Listener {
	@EventHandler
	public void a(PlayerPickupItemEvent e) {
		Item i = e.getItem();
		if (i.hasMetadata("BOUNTYHUNTERS:no_pickup"))
			e.setCancelled(true);
	}

	@EventHandler
	public void b(InventoryPickupItemEvent e) {
		Item i = e.getItem();
		if (i.hasMetadata("BOUNTYHUNTERS:no_pickup"))
			e.setCancelled(true);
	}

	@EventHandler
	public void c(PlayerJoinEvent e) {
		PlayerData.setup(e.getPlayer());
	}
}
