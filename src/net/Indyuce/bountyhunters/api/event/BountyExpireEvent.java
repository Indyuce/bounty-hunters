package net.Indyuce.bountyhunters.api.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.language.Message;

public class BountyExpireEvent extends BountyEvent {
	private final BountyExpireCause cause;
	private final Player player;
	private final double amount;

	/*
	 * if the bounty expires after this event is called.
	 */
	private final boolean expiring;

	private static final HandlerList handlers = new HandlerList();

	/*
	 * called when a bounty disappears
	 */
	public BountyExpireEvent(Bounty bounty) {
		this(bounty, bounty.getReward());
	}

	/*
	 * called when a player takes away his contribution from the bounty
	 */
	public BountyExpireEvent(Bounty bounty, Player player) {
		this(bounty, player, bounty.getContribution(player), BountyExpireCause.PLAYER);
	}

	/*
	 * called when an admin removes some extra bounty reward
	 */
	public BountyExpireEvent(Bounty bounty, double amount) {
		this(bounty, null, amount, BountyExpireCause.ADMIN);
	}

	public BountyExpireEvent(Bounty bounty, Player player, double amount, BountyExpireCause cause) {
		super(bounty);

		this.player = player;
		this.amount = amount;
		this.cause = cause;
		expiring = getBounty().getReward() <= amount;
	}

	public BountyExpireCause getCause() {
		return cause;
	}

	public double getAmountRemoved() {
		return amount;
	}

	/*
	 * if bounty disappears
	 */
	public boolean isExpiring() {
		return expiring;
	}

	public boolean hasPlayer() {
		return player != null;
	}

	public Player getPlayer() {
		return player;
	}

	public void sendAllert() {
		if (isExpiring())
			Message.BOUNTY_EXPIRED.format("target", getBounty().getTarget().getName()).send(Bukkit.getOnlinePlayers());
		else {
			double reward = getBounty().getReward();
			Message.BOUNTY_DECREASED.format("target", getBounty().getTarget().getName(), "old",
					new NumberFormat().format(reward), "new", new NumberFormat().format(reward - amount), "player",
					player.getName(), "amount", new NumberFormat().format(amount)).send(Bukkit.getOnlinePlayers());
		}
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public enum BountyExpireCause {

		/*
		 * when an admin uses an admin command to remove the bounty or when the
		 * admin removes a player's contribution
		 */
		ADMIN,

		/*
		 * when the creator takes away his contribution or when the bounty
		 * finally expires
		 */
		PLAYER,

		/*
		 * when a bounty is removed due to inactivity
		 */
		INACTIVITY;
	}
}
