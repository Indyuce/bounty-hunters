package net.Indyuce.bountyhunters.api.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

// this event is called when a bounty reward changes, for instance
// when a player performs /addbounty <player> <reward> when there's
// already a bounty on the player, or when the auto bounty system
// adds a specific amount to a player's bounty.

public class HunterTargetEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private OfflinePlayer target;
	private boolean cancelled = false;

	public HunterTargetEvent(Player player, OfflinePlayer target) {
		this.player = player;
		this.target = target;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean bool) {
		cancelled = bool;
	}

	public Player getPlayer() {
		return player;
	}

	public OfflinePlayer getTarget() {
		return target;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
