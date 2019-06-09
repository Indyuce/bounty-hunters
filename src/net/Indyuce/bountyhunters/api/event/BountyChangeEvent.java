package net.Indyuce.bountyhunters.api.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.BountyUtils;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.version.VersionSound;

public class BountyChangeEvent extends BountyEvent {
	private BountyChangeCause cause;

	/*
	 * this event is when a bounty reward changes, either when the auto bounty
	 * increases the bounty reward since he killed someone illegaly, or when a
	 * player increases manually a player bounty by using the /bounty command
	 */
	public BountyChangeEvent(Bounty bounty, BountyChangeCause cause) {
		super(bounty);
		this.cause = cause;
	}

	public BountyChangeCause getCause() {
		return cause;
	}

	public void sendAllert() {
		for (Player player : Bukkit.getOnlinePlayers())
			Message.BOUNTY_CHANGE.format(ChatColor.YELLOW, "%player%", getBounty().getTarget().getName(), "%reward%", BountyUtils.format(getBounty().getReward())).send(player);
		if (getBounty().getTarget().isOnline()) {
			Player t = getBounty().getTarget().getPlayer();
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
