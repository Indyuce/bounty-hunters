package net.Indyuce.bountyhunters.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.BountyCause;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.PlayerData;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.util.Utils;
import net.Indyuce.bountyhunters.version.VersionSound;

public class Alerts {
	public static void claimBounty(Player p, Bounty bounty) {

		// message to claimer
		Message.CHAT_BAR.format(ChatColor.YELLOW).send(p);
		Message.BOUNTY_CLAIMED_BY_YOU.format(ChatColor.YELLOW, "%target%", bounty.getTarget().getName(), "%reward%", Utils.format(bounty.getReward())).send(p);

		// message to server
		PlayerData playerData = PlayerData.get(p);
		String title = playerData.hasTitle() ? ChatColor.LIGHT_PURPLE + "[" + playerData.getTitle() + ChatColor.LIGHT_PURPLE + "] " : "";
		for (Player t : Bukkit.getOnlinePlayers()) {
			t.playSound(t.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
			if (t != p)
				Message.BOUNTY_CLAIMED.format(ChatColor.YELLOW, "%reward%", Utils.format(bounty.getReward()), "%killer%", title + p.getName(), "%target%", bounty.getTarget().getName()).send(t);
		}
	}

	public static void newBounty(BountyCreateEvent e) {
		Bounty b = e.getBounty();
		double reward = e.getBounty().getReward();

		String toOnline = e.getCause() == BountyCause.PLAYER ? Message.NEW_BOUNTY_ON_PLAYER.formatRaw(ChatColor.YELLOW, "%creator%", b.getCreator().getName(), "%target%", b.getTarget().getName(), "%reward%", Utils.format(reward)) : (e.getCause() == BountyCause.AUTO_BOUNTY ? Message.NEW_BOUNTY_ON_PLAYER_ILLEGAL.formatRaw(ChatColor.YELLOW, "%target%", b.getTarget().getName(), "%reward%", Utils.format(reward)) : Message.NEW_BOUNTY_ON_PLAYER_UNDEFINED.formatRaw(ChatColor.YELLOW, "%target%", b.getTarget().getName(), "%reward%", Utils.format(reward)));
		String toTarget = e.getCause() == BountyCause.PLAYER ? Message.NEW_BOUNTY_ON_YOU.formatRaw(ChatColor.RED, "%creator%", b.getCreator().getName(), "%reward%", Utils.format(b.getReward())) : (e.getCause() == BountyCause.AUTO_BOUNTY ? Message.NEW_BOUNTY_ON_YOU_ILLEGAL.formatRaw(ChatColor.RED, "%reward%", Utils.format(b.getReward())) : Message.NEW_BOUNTY_ON_YOU_UNDEFINED.formatRaw(ChatColor.RED, "%reward%", Utils.format(b.getReward())));

		for (Player t : Bukkit.getOnlinePlayers()) {
			if (b.hasTarget(t)) {
				t.playSound(t.getLocation(), VersionSound.ENTITY_ENDERMAN_HURT.getSound(), 1, 0);
				t.sendMessage(toTarget);
				continue;
			}

			if (b.hasCreator(t)) {
				t.playSound(t.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
				Message.CHAT_BAR.format(ChatColor.YELLOW).send(t);
				Message.BOUNTY_CREATED.format(ChatColor.YELLOW, "%target%", b.getTarget().getName()).send(t);
				Message.BOUNTY_EXPLAIN.format(ChatColor.YELLOW, "%reward%", Utils.format(reward)).send(t);
				continue;
			}

			t.playSound(t.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
			t.sendMessage(toOnline);
		}
	}

	public static void newHunter(Player t, Player h) {
		Message.NEW_HUNTER_ALERT.format(ChatColor.RED, "%hunter%", h.getName()).send(t);
		t.playSound(t.getLocation(), VersionSound.ENTITY_ENDERMAN_HURT.getSound(), 1, 0);
	}

	public static void bountyExpired(Bounty bounty) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.playSound(p.getLocation(), VersionSound.ENTITY_VILLAGER_NO.getSound(), 1, 2);
			Message.BOUNTY_EXPIRED.format(ChatColor.YELLOW, "%target%", bounty.getTarget().getName()).send(p);
		}
	}

	public static void bountyChange(BountyCreateEvent e) {
		Bounty b = e.getBounty();
		for (Player p : Bukkit.getOnlinePlayers())
			Message.BOUNTY_CHANGE.format(ChatColor.YELLOW, "%player%", b.getTarget().getName(), "%reward%", Utils.format(b.getReward())).send(p);
		if (b.getTarget().isOnline()) {
			Player t = b.getTarget().getPlayer();
			t.playSound(t.getLocation(), VersionSound.ENTITY_ENDERMAN_HURT.getSound(), 1, 0);
		}
	}
}
