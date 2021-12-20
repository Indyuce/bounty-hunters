package net.Indyuce.bountyhunters.api.event;

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.language.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

public class BountyIncreaseEvent extends BountyEvent {
	private final Player player;
	private final BountyChangeCause cause;

	private double added;

	private static final HandlerList handlers = new HandlerList();

	/**
	 * Called when the total bounty reward changes either when the auto bounty
	 * increases the reward since the player killed another player illegaly; or
	 * when a player manually increases an existing bounty using /addbounty
	 * 
	 * @param bounty
	 *            Bounty being increased
	 * @param player
	 *            Player increasing the bounty
	 * @param added
	 *            Amount of cash being added in
	 * @param cause
	 *            Reason why the bounty reward changes
	 */
	public BountyIncreaseEvent(Bounty bounty, @Nullable Player player, double added, BountyChangeCause cause) {
		super(bounty);

		this.player = player;
		this.cause = cause;
		this.added = added;
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

	public Player getPlayer() {
		return player;
	}

	public boolean hasPlayer() {
		return player != null;
	}

	public void sendAllert() {
		for (Player player : Bukkit.getOnlinePlayers())
			Message.BOUNTY_CHANGE.format("player", getBounty().getTarget().getName(), "reward", new NumberFormat().format(getBounty().getReward()))
					.send(player);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public enum BountyChangeCause {

		/**
		 * When a player adds money to a bounty using /bounty
		 */
		PLAYER,

		/**
		 * When the server adds money to a bounty using /bounty
		 */
		CONSOLE,

		/**
		 * When the auto bounty increases a player's bounty since the killer has
		 * killed a player illegaly
		 */
		AUTO_BOUNTY,

		/**
		 * Extra cause which can be used by other plugins/addons
		 */
		PLUGIN;
	}
}
