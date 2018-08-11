package net.Indyuce.bh.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.Indyuce.bh.api.Bounty;

// called when a player claims a bounty.

public class BountyClaimEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Player claimer;
	private Bounty bounty;
	private boolean cancelled;

	public BountyClaimEvent(Bounty bounty, Player claimer) {
		this.bounty = bounty;
		this.claimer = claimer;
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

	public Player getClaimer() {
		return claimer;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
