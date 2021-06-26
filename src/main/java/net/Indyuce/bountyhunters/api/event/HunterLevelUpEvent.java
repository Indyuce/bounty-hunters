package net.Indyuce.bountyhunters.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class HunterLevelUpEvent extends PlayerEvent {
	private final int newLevel;

	private static final HandlerList handlers = new HandlerList();

	/**
	 * Called when a player levels up which cannot be cancelled
	 * 
	 * @param player
	 *            Hunter leveling up
	 * @param newLevel
	 *            Level reached
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
