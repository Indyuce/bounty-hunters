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

public class BountyCreateEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private Bounty bounty;
	private boolean cancelled = false;
	private BountyCause cause;

	/*
	 * this event is called whenever a player sets a bounty onto another player,
	 * or when the auto-bounty automatically sets a new bounty on a player since
	 * he killed someone illegaly
	 */
	public BountyCreateEvent(Bounty bounty, BountyCause cause) {
		this.bounty = bounty;
		this.cause = cause;
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

	public BountyCause getCause() {
		return cause;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public void sendAllert() {
		double reward = bounty.getReward();

		String toOnline = cause == BountyCause.PLAYER ? Message.NEW_BOUNTY_ON_PLAYER.formatRaw(ChatColor.YELLOW, "%creator%", bounty.getCreator().getName(), "%target%", bounty.getTarget().getName(), "%reward%", BountyUtils.format(reward)) : (cause == BountyCause.AUTO_BOUNTY ? Message.NEW_BOUNTY_ON_PLAYER_ILLEGAL.formatRaw(ChatColor.YELLOW, "%target%", bounty.getTarget().getName(), "%reward%", BountyUtils.format(reward)) : Message.NEW_BOUNTY_ON_PLAYER_UNDEFINED.formatRaw(ChatColor.YELLOW, "%target%", bounty.getTarget().getName(), "%reward%", BountyUtils.format(reward)));
		String toTarget = cause == BountyCause.PLAYER ? Message.NEW_BOUNTY_ON_YOU.formatRaw(ChatColor.RED, "%creator%", bounty.getCreator().getName(), "%reward%", BountyUtils.format(bounty.getReward())) : (cause == BountyCause.AUTO_BOUNTY ? Message.NEW_BOUNTY_ON_YOU_ILLEGAL.formatRaw(ChatColor.RED, "%reward%", BountyUtils.format(bounty.getReward())) : Message.NEW_BOUNTY_ON_YOU_UNDEFINED.formatRaw(ChatColor.RED, "%reward%", BountyUtils.format(bounty.getReward())));

		for (Player t : Bukkit.getOnlinePlayers()) {
			if (bounty.hasTarget(t)) {
				t.playSound(t.getLocation(), VersionSound.ENTITY_ENDERMAN_HURT.getSound(), 1, 0);
				t.sendMessage(toTarget);
				continue;
			}

			if (bounty.hasCreator(t)) {
				t.playSound(t.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
				Message.CHAT_BAR.format(ChatColor.YELLOW).send(t);
				Message.BOUNTY_CREATED.format(ChatColor.YELLOW, "%target%", bounty.getTarget().getName()).send(t);
				Message.BOUNTY_EXPLAIN.format(ChatColor.YELLOW, "%reward%", BountyUtils.format(reward)).send(t);
				continue;
			}

			t.playSound(t.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
			t.sendMessage(toOnline);
		}
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
