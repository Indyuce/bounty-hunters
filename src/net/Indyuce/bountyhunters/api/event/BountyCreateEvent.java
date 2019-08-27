package net.Indyuce.bountyhunters.api.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.version.VersionSound;

public class BountyCreateEvent extends BountyEvent {
	private BountyCause cause;

	private static final HandlerList handlers = new HandlerList();

	/*
	 * this event is called whenever a player sets a bounty onto another player,
	 * or when the auto-bounty automatically sets a new bounty on a player since
	 * he killed someone illegaly
	 */
	public BountyCreateEvent(Bounty bounty, BountyCause cause) {
		super(bounty);
		this.cause = cause;
	}

	public BountyCause getCause() {
		return cause;
	}

	public void sendAllert() {
		String reward = new NumberFormat().format(getBounty().getReward());
		String toOnline = cause == BountyCause.PLAYER ? Message.NEW_BOUNTY_ON_PLAYER.formatRaw(ChatColor.YELLOW, "%creator%", getBounty().getCreator().getName(), "%target%", getBounty().getTarget().getName(), "%reward%", reward) : (cause == BountyCause.AUTO_BOUNTY ? Message.NEW_BOUNTY_ON_PLAYER_ILLEGAL.formatRaw(ChatColor.YELLOW, "%target%", getBounty().getTarget().getName(), "%reward%", reward) : Message.NEW_BOUNTY_ON_PLAYER_UNDEFINED.formatRaw(ChatColor.YELLOW, "%target%", getBounty().getTarget().getName(), "%reward%", reward));
		String toTarget = cause == BountyCause.PLAYER ? Message.NEW_BOUNTY_ON_YOU.formatRaw(ChatColor.RED, "%creator%", getBounty().getCreator().getName(), "%reward%", reward) : (cause == BountyCause.AUTO_BOUNTY ? Message.NEW_BOUNTY_ON_YOU_ILLEGAL.formatRaw(ChatColor.RED, "%reward%", reward) : Message.NEW_BOUNTY_ON_YOU_UNDEFINED.formatRaw(ChatColor.RED, "%reward%", reward));

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (getBounty().hasTarget(player)) {
				player.playSound(player.getLocation(), VersionSound.ENTITY_ENDERMAN_HURT.toSound(), 1, 0);
				player.sendMessage(toTarget);
				continue;
			}

			if (getBounty().hasCreator(player)) {
				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
				Message.CHAT_BAR.format(ChatColor.YELLOW).send(player);
				Message.BOUNTY_CREATED.format(ChatColor.YELLOW, "%target%", getBounty().getTarget().getName()).send(player);
				Message.BOUNTY_EXPLAIN.format(ChatColor.YELLOW, "%reward%", reward).send(player);
				continue;
			}

			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
			player.sendMessage(toOnline);
		}
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public enum BountyCause {

		/*
		 * when a player sets a bounty onto another player's head
		 */
		PLAYER,

		/*
		 * when a non-player entity (console/command block) sets a bounty on a
		 * player's head
		 */
		CONSOLE,

		/*
		 * when the auto bounty sets a bounty on a player since he killed
		 * someone illegaly (illegaly = the player did not have any bounty on
		 * him, which makes it an illegal kill)
		 */
		AUTO_BOUNTY,

		/*
		 * extra bounty cause that is not used in the vanilla BountyHunters but
		 * that can be used by other plugins
		 */
		PLUGIN;
	}
}
