package net.Indyuce.bountyhunters.api.event;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.language.Message;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class BountyClaimEvent extends BountyEvent {
	private final Player player;
	private final Player target;
	private final boolean headHunting;

	private static final HandlerList handlers = new HandlerList();

	public BountyClaimEvent(Bounty bounty, Player player) {
		this(bounty, player, false);
	}

	/**
	 * Called whenever a player claims a bounty by killing the bounty target or
	 * by giving the target's head to the bounty creator
	 * 
	 * @param bounty
	 *            Bounty claimed
	 * @param player
	 *            Player claiming the bounty
	 * @param headHunting
	 *            If the player is doing head hunting
	 */
	public BountyClaimEvent(Bounty bounty, Player player, boolean headHunting) {
		super(bounty);

		this.player = player;
		this.target = bounty.getTarget().getPlayer();
		this.headHunting = headHunting;
	}

	/*
	 * returns if the victim's head is given to the bounty creator
	 */
	public boolean isHeadHunting() {
		return headHunting;
	}

	public Player getTarget() {
		return target;
	}

	public Player getClaimer() {
		return player;
	}

	public void sendAllert() {

		// message to player
		String reward = new NumberFormat().format(getBounty().getReward());
		Message.BOUNTY_CLAIMED_BY_YOU.format("target", getBounty().getTarget().getName(), "reward", reward).send(player);

		// message to server
		PlayerData playerData = PlayerData.get(player);
		String title = playerData.hasTitle() ? ChatColor.LIGHT_PURPLE + "[" + playerData.getTitle().format() + ChatColor.LIGHT_PURPLE + "] " : "";
		for (Player online : Bukkit.getOnlinePlayers())
			if (online != player)
				Message.BOUNTY_CLAIMED.format("reward", new NumberFormat().format(getBounty().getReward()), "killer", title + player.getName(),
						"target", getBounty().getTarget().getName()).send(online);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
