package net.Indyuce.bountyhunters.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.BountyCause;

// called whenever a bounty is registered by a plugin or created
// either by a player or by the auto-bounty feature

public class BountyCreateEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Bounty bounty;
	private boolean cancelled;
	private BountyCause cause;

	public BountyCreateEvent(Bounty bounty, BountyCause cause) {
		this.bounty = bounty;
		this.cause = cause;
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

	public BountyCause getCause() {
		return cause;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
