package net.Indyuce.bountyhunters.gui.api;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.gui.PluginInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.function.Function;

public class ChatInput implements Listener {
	private final Function<String, Boolean> action;
	private final PluginInventory inv;

	public ChatInput(PluginInventory inv, Function<String, Boolean> action) {
		this.action = action;
		this.inv = inv;

		inv.getPlayer().closeInventory();
		inv.getPlayer().sendMessage(ChatColor.GREEN + "> Type 'cancel' to cancel.");

		Bukkit.getPluginManager().registerEvents(this, BountyHunters.getInstance());
	}

	public void close() {
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
		PlayerMoveEvent.getHandlerList().unregister(this);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void a(AsyncPlayerChatEvent event) {
		if (!event.getPlayer().equals(inv.getPlayer()))
			return;

		event.setCancelled(true);
		if (event.getMessage().equalsIgnoreCase("cancel") || action.apply(event.getMessage())) {
			close();
			Bukkit.getScheduler().runTask(BountyHunters.getInstance(), () -> inv.open());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void b(PlayerMoveEvent event) {
		if (event.getPlayer().equals(inv.getPlayer()) && (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ())) {
			inv.getPlayer().sendMessage(ChatColor.GREEN + "> Chat input cancelled.");
			close();
		}
	}
}
