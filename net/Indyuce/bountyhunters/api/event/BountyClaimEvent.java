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
import net.Indyuce.bountyhunters.api.PlayerData;
import net.Indyuce.bountyhunters.version.VersionSound;

public class BountyClaimEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Bounty bounty;
	private boolean cancelled = false;

	/*
	 * this event is called whenever a player claims a bounty by killing the
	 * bounty target
	 */
	public BountyClaimEvent(Bounty bounty, Player player) {
		this.bounty = bounty;
		this.player = player;
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

	public Player getClaimer() {
		return player;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public void sendAllert() {

		// message to player
		Message.CHAT_BAR.format(ChatColor.YELLOW).send(player);
		Message.BOUNTY_CLAIMED_BY_YOU.format(ChatColor.YELLOW, "%target%", bounty.getTarget().getName(), "%reward%", BountyUtils.format(bounty.getReward())).send(player);

		// message to server
		PlayerData playerData = PlayerData.get(player);
		String title = playerData.hasTitle() ? ChatColor.LIGHT_PURPLE + "[" + playerData.getTitle() + ChatColor.LIGHT_PURPLE + "] " : "";
		for (Player t : Bukkit.getOnlinePlayers()) {
			t.playSound(t.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
			if (t != player)
				Message.BOUNTY_CLAIMED.format(ChatColor.YELLOW, "%reward%", BountyUtils.format(bounty.getReward()), "%killer%", title + player.getName(), "%target%", bounty.getTarget().getName()).send(t);
		}
	}
}
