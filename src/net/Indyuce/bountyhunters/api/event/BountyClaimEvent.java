package net.Indyuce.bountyhunters.api.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.BountyUtils;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.PlayerData;
import net.Indyuce.bountyhunters.version.VersionSound;

public class BountyClaimEvent extends BountyEvent {
	private Player player;

	/*
	 * this event is called whenever a player claims a bounty by killing the
	 * bounty target
	 */
	public BountyClaimEvent(Bounty bounty, Player player) {
		super(bounty);
		this.player = player;
	}

	public Player getClaimer() {
		return player;
	}

	public void sendAllert() {

		// message to player
		Message.CHAT_BAR.format(ChatColor.YELLOW).send(player);
		Message.BOUNTY_CLAIMED_BY_YOU.format(ChatColor.YELLOW, "%target%", getBounty().getTarget().getName(), "%reward%", BountyUtils.format(getBounty().getReward())).send(player);

		// message to server
		PlayerData playerData = PlayerData.get(player);
		String title = playerData.hasTitle() ? ChatColor.LIGHT_PURPLE + "[" + playerData.getTitle() + ChatColor.LIGHT_PURPLE + "] " : "";
		for (Player t : Bukkit.getOnlinePlayers()) {
			t.playSound(t.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
			if (t != player)
				Message.BOUNTY_CLAIMED.format(ChatColor.YELLOW, "%reward%", BountyUtils.format(getBounty().getReward()), "%killer%", title + player.getName(), "%target%", getBounty().getTarget().getName()).send(t);
		}
	}
}
