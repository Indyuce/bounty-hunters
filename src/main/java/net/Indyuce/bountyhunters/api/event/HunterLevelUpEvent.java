package net.Indyuce.bountyhunters.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class HunterLevelUpEvent extends PlayerEvent {
	private final int newLevel;

	private static final HandlerList handlers = new HandlerList();

	/*
	 * this event is called when a player levels up (cannot be cancelled)
	 */
	public HunterLevelUpEvent(Player player, int newLevel) {
		super(player);

		this.newLevel = newLevel;
	}

	public int getNewLevel() {
		return newLevel;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
