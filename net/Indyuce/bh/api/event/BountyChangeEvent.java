package net.Indyuce.bh.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.Indyuce.bh.api.Bounty;

// this event is called when a bounty reward changes, for instance
// when a player performs /addbounty <player> <reward> when there's
// already a bounty on the player, or when the auto bounty system
// adds a specific amount to a player's bounty.

public class BountyChangeEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Bounty bounty;
	private boolean cancelled;

	public BountyChangeEvent(Bounty bounty) {
		this.bounty = bounty;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean bool) {
		cancelled = bool;
	}

	public Bounty getBounty() {
		return bounty;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
