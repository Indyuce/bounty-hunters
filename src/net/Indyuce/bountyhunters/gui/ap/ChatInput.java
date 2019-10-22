package net.Indyuce.bountyhunters.gui.ap;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.gui.PluginInventory;

public class ChatInput implements Listener {
	private final Consumer<String> action;
	private final PluginInventory inv;

	public ChatInput(PluginInventory inv, Consumer<String> action) {
		this.action = action;
		this.inv = inv;
		
		inv.getPlayer().closeInventory();

		Bukkit.getPluginManager().registerEvents(this, BountyHunters.getInstance());
	}

	public void close() {
		AsyncPlayerChatEvent.getHandlerList().unregister(this);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void a(AsyncPlayerChatEvent event) {
		if (!event.getMessage().equalsIgnoreCase("cancel"))
			action.accept(event.getMessage());
		close();
		inv.open();
	}
}
