package net.Indyuce.bountyhunters.api.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.Indyuce.bountyhunters.BountyUtils;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.version.VersionSound;

public class BountyChangeEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private BountyChangeCause cause;
	private Bounty bounty;
	private boolean cancelled = false;

	/*
	 * this event is when a bounty reward changes, either when the auto bounty
	 * increases the bounty reward since he killed someone illegaly, or when a
	 * player increases manually a player bounty by using the /bounty command
	 */
	public BountyChangeEvent(Bounty bounty, BountyChangeCause cause) {
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

	public BountyChangeCause getCause() {
		return cause;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public void sendAllert() {
		for (Player player : Bukkit.getOnlinePlayers())
			Message.BOUNTY_CHANGE.format(ChatColor.YELLOW, "%player%", bounty.getTarget().getName(), "%reward%", BountyUtils.format(bounty.getReward())).send(player);
		if (bounty.getTarget().isOnline()) {
			Player t = bounty.getTarget().getPlayer();
			t.playSound(t.getLocation(), VersionSound.ENTITY_ENDERMAN_HURT.getSound(), 1, 0);
		}
	}

	public enum BountyChangeCause {

		/*
		 * when a player adds money to a bounty using /bounty
		 */
		PLAYER,

		/*
		 * when the auto bounty increases a player's bounty since the killer has
		 * killed a player illegaly
		 */
		AUTO_BOUNTY;
	}
}
