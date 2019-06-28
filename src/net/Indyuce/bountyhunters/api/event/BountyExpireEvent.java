package net.Indyuce.bountyhunters.api.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.Message;

public class BountyExpireEvent extends BountyEvent {
	private BountyExpireCause cause;
	
	private static final HandlerList handlers = new HandlerList();

	/*
	 * this event is called when the bounty creator cancels the bounty in the
	 * bounty menu by clicking the corresponding bounty item or when an admin
	 * removes the bounty using an admin command
	 */
	public BountyExpireEvent(Bounty bounty, BountyExpireCause cause) {
		super(bounty);
		this.cause = cause;
	}

	public BountyExpireCause getCause() {
		return cause;
	}

	public void sendAllert() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 2);
			Message.BOUNTY_EXPIRED.format(ChatColor.YELLOW, "%target%", getBounty().getTarget().getName()).send(player);
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
		 * when an admin uses an admin command to remove the bounty
		 */
		ADMIN,

		/*
		 * when the creator removes the bounty from the list using the menu
		 */
		CREATOR;
	}
}
