package net.Indyuce.bh.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.bh.Main;
import net.Indyuce.bh.api.PlayerData;
import net.Indyuce.bh.util.Utils;

@SuppressWarnings("deprecation")
public class MainListener implements Listener {
	public MainListener() {
		if (Main.plugin.getConfig().getBoolean("compass.enabled"))
			new BukkitRunnable() {
				public void run() {
					for (Player p : Bukkit.getOnlinePlayers())
						Utils.playerLoop(p);
				}
			}.runTaskTimer(Main.plugin, 0, 10);
	}

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
		PlayerData.get(e.getPlayer()).setup();
	}
}
