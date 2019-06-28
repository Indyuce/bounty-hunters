package net.Indyuce.bountyhunters.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import net.Indyuce.bountyhunters.api.Bounty;

public abstract class BountyEvent extends Event implements Cancellable {
	private Bounty bounty;
	private boolean cancelled = false;

	/*
	 * every event involving a bounty is cancellable. this allows not to specify
	 * an empty handlers list, not to have the event class always implement the
	 * cancellable interface, and the getBounty() method in an inheritance
	 */
	public BountyEvent(Bounty bounty) {
		this.bounty = bounty;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean bool) {
		cancelled = bool;
	}

	public Bounty getBounty() {
		return bounty;
	}
}
