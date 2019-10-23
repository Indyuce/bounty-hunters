package net.Indyuce.bountyhunters.api.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.language.Message;

public class BountyChangeEvent extends BountyEvent {
	private double added;

	private final BountyChangeCause cause;

	private static final HandlerList handlers = new HandlerList();

	/*
	 * this event is when a bounty reward changes, either when the auto bounty
	 * increases the bounty reward since he killed someone illegaly, or when a
	 * player increases manually a player bounty by using the /bounty command
	 */
	public BountyChangeEvent(Bounty bounty, double newAmount, BountyChangeCause cause) {
		super(bounty);

		this.cause = cause;
		this.added = newAmount;
	}

	public double getAdded() {
		return added;
	}

	public void setAdded(double added) {
		this.added = added;
	}

	public BountyChangeCause getCause() {
		return cause;
	}

	public void sendAllert() {
		for (Player player : Bukkit.getOnlinePlayers())
			Message.BOUNTY_CHANGE.format("player", getBounty().getTarget().getName(), "reward", new NumberFormat().format(getBounty().getReward())).send(player);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public enum BountyChangeCause {

		/*
		 * when a player adds money to a bounty using /bounty
		 */
		PLAYER,

		/*
		 * when the server adds money to a bounty using /bounty
		 */
		CONSOLE,

		/*
		 * when the auto bounty increases a player's bounty since the killer has
		 * killed a player illegaly
		 */
		AUTO_BOUNTY,

		/*
		 * extra cause which can be used by other plugins/addons
		 */
		PLUGIN;
	}
}
