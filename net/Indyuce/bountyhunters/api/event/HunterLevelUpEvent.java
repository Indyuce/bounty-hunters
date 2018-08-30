package net.Indyuce.bountyhunters.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.Indyuce.bountyhunters.api.Bounty;

// this event is called when a bounty reward changes, for instance
// when a player performs /addbounty <player> <reward> when there's
// already a bounty on the player, or when the auto bounty system
// adds a specific amount to a player's bounty.

public class HunterLevelUpEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Bounty bounty;

	public HunterLevelUpEvent(Player player, Bounty bounty) {
		this.player = player;
		this.bounty = bounty;
	}

	public Player getPlayer() {
		return player;
	}

	public Bounty getBounty() {
		return bounty;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
