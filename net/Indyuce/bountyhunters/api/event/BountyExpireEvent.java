package net.Indyuce.bountyhunters.api.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.version.VersionSound;

// called when the bounty creator cancels the bounty
// in the bounty menu by clicking the item or when an
// admin removes the bounty using an admin command

public class BountyExpireEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Bounty bounty;
	private BountyExpireCause cause;
	private boolean cancelled = false;

	/*
	 * this event is called when the bounty creator cancels the bounty in the
	 * bounty menu by clicking the corresponding bounty item or when an admin
	 * removes the bounty using an admin command
	 */
	public BountyExpireEvent(Bounty bounty, BountyExpireCause cause) {
		this.bounty = bounty;
		this.cause = cause;
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

	public BountyExpireCause getCause() {
		return cause;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public void sendAllert() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(player.getLocation(), VersionSound.ENTITY_VILLAGER_NO.getSound(), 1, 2);
			Message.BOUNTY_EXPIRED.format(ChatColor.YELLOW, "%target%", bounty.getTarget().getName()).send(player);
		}
	}

	public enum BountyExpireCause {

		/*
		 * when an admin uses an admin command to remove the bounty
		 */
		ADMIN,

		/*
		 * when the creator removes the bounty from the list using the menu
		 */
		CREATOR;
	}
}
