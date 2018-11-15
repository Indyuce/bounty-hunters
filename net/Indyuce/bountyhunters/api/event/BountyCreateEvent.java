package net.Indyuce.bountyhunters.api.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.BountyUtils;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.version.VersionSound;

public class BountyCreateEvent extends BountyEvent {
	private BountyCause cause;

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
		double reward = getBounty().getReward();

		String toOnline = cause == BountyCause.PLAYER ? Message.NEW_BOUNTY_ON_PLAYER.formatRaw(ChatColor.YELLOW, "%creator%", getBounty().getCreator().getName(), "%target%", getBounty().getTarget().getName(), "%reward%", BountyUtils.format(reward)) : (cause == BountyCause.AUTO_BOUNTY ? Message.NEW_BOUNTY_ON_PLAYER_ILLEGAL.formatRaw(ChatColor.YELLOW, "%target%", getBounty().getTarget().getName(), "%reward%", BountyUtils.format(reward)) : Message.NEW_BOUNTY_ON_PLAYER_UNDEFINED.formatRaw(ChatColor.YELLOW, "%target%", getBounty().getTarget().getName(), "%reward%", BountyUtils.format(reward)));
		String toTarget = cause == BountyCause.PLAYER ? Message.NEW_BOUNTY_ON_YOU.formatRaw(ChatColor.RED, "%creator%", getBounty().getCreator().getName(), "%reward%", BountyUtils.format(getBounty().getReward())) : (cause == BountyCause.AUTO_BOUNTY ? Message.NEW_BOUNTY_ON_YOU_ILLEGAL.formatRaw(ChatColor.RED, "%reward%", BountyUtils.format(getBounty().getReward())) : Message.NEW_BOUNTY_ON_YOU_UNDEFINED.formatRaw(ChatColor.RED, "%reward%", BountyUtils.format(getBounty().getReward())));

		for (Player t : Bukkit.getOnlinePlayers()) {
			if (getBounty().hasTarget(t)) {
				t.playSound(t.getLocation(), VersionSound.ENTITY_ENDERMAN_HURT.getSound(), 1, 0);
				t.sendMessage(toTarget);
				continue;
			}

			if (getBounty().hasCreator(t)) {
				t.playSound(t.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
				Message.CHAT_BAR.format(ChatColor.YELLOW).send(t);
				Message.BOUNTY_CREATED.format(ChatColor.YELLOW, "%target%", getBounty().getTarget().getName()).send(t);
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
