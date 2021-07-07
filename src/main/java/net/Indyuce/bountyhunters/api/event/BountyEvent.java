package net.Indyuce.bountyhunters.api.event;

import net.Indyuce.bountyhunters.api.Bounty;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class BountyEvent extends Event implements Cancellable {
	private final Bounty bounty;

	private boolean cancelled = false;

	/**
	 * Every event involving a bounty is cancellable. Saves some methods
	 * like the ones inherited from the Cancellable interface as
	 * well as {@link #getBounty()}.
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

	public abstract void sendAllert();
}
