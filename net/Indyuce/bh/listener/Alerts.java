package net.Indyuce.bh.listener;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.Indyuce.bh.api.Bounty;
import net.Indyuce.bh.api.PlayerData;
import net.Indyuce.bh.api.event.BountyCreateEvent;
import net.Indyuce.bh.resource.BountyCause;
import net.Indyuce.bh.util.Utils;
import net.Indyuce.bh.util.VersionUtils;

public class Alerts {
	public static void claimBounty(Player p, Bounty bounty) {

		// message to claimer
		p.sendMessage(Utils.msg("chat-bar"));
		p.sendMessage(ChatColor.YELLOW + Utils.msg("bounty-claimed-by-you").replace("%target%", bounty.getTarget().getName()).replace("%reward%", Utils.format(bounty.getReward())));

		// compass
		for (OfflinePlayer hunter : bounty.getHuntingPlayers()) {
			if (!hunter.isOnline())
				continue;

			Player h = (Player) hunter;
			h.setCompassTarget(h.getWorld().getSpawnLocation());
		}

		// message to server
		PlayerData playerData = PlayerData.get(p);
		String title = playerData.getString("current-title").equals("") ? "" : ChatColor.LIGHT_PURPLE + "[" + Utils.applySpecialChars(playerData.getString("current-title")) + "] ";
		for (Player t1 : Bukkit.getOnlinePlayers()) {
			VersionUtils.sound(t1, "ENTITY_PLAYER_LEVELUP", 1, 2);
			if (t1 != p)
				t1.sendMessage(ChatColor.YELLOW + Utils.msg("bounty-claimed").replace("%reward%", Utils.format(bounty.getReward())).replace("%killer%", title + ChatColor.getLastColors(Utils.msg("bounty-claimed").split(Pattern.quote("%killer%"))[0]) + p.getName()).replace("%target%", bounty.getTarget().getName()));
		}
	}

	public static void newBounty(BountyCreateEvent e) {
		Bounty b = e.getBounty();
		double reward = e.getBounty().getReward();

		String toOnline = ChatColor.YELLOW + (e.getCause() == BountyCause.PLAYER ? Utils.msg("new-bounty-on-player").replace("%creator%", b.getCreator().getName()).replace("%target%", b.getTarget().getName()).replace("%reward%", Utils.format(reward)) : (e.getCause() == BountyCause.AUTO_BOUNTY ? Utils.msg("new-bounty-on-player-illegal").replace("%target%", b.getTarget().getName()).replace("%reward%", Utils.format(reward)) : Utils.msg("new-bounty-on-player-undefined").replace("%target%", b.getTarget().getName()).replace("%reward%", Utils.format(reward))));
		String toTarget = ChatColor.RED + (e.getCause() == BountyCause.PLAYER ? Utils.msg("new-bounty-on-you").replace("%creator%", b.getCreator().getName()) : (e.getCause() == BountyCause.AUTO_BOUNTY ? Utils.msg("new-bounty-on-you-illegal") : Utils.msg("new-bounty-on-you-undefined")));

		for (Player t : Bukkit.getOnlinePlayers()) {
			if (b.getTarget() == t) {
				VersionUtils.sound(t, "ENTITY_ENDERMEN_HURT", 1, 0);
				t.sendMessage(toTarget);
				continue;
			}

			if (b.getCreator() == t) {
				VersionUtils.sound(t, "ENTITY_PLAYER_LEVELUP", 1, 2);
				t.sendMessage(Utils.msg("chat-bar"));
				t.sendMessage(ChatColor.YELLOW + Utils.msg("bounty-created").replace("%target%", b.getTarget().getName()));
				t.sendMessage(ChatColor.YELLOW + Utils.msg("bounty-explain").replace("%reward%", Utils.format(reward)));
				continue;
			}

			VersionUtils.sound(t, "ENTITY_PLAYER_LEVELUP", 1, 2);
			t.sendMessage(toOnline);
		}
	}

	public static void newHunter(Player t, Player h) {
		t.sendMessage(ChatColor.RED + Utils.msg("new-hunter-alert").replace("%hunter%", h.getName()));
		VersionUtils.sound(t.getLocation(), "ENTITY_ENDERMEN_HURT", 1, 1);
	}

	public static void upBounty(String name, double newReward) {
		for (Player t : Bukkit.getOnlinePlayers())
			t.sendMessage(ChatColor.YELLOW + Utils.msg("upped-bounty").replace("%player%", name).replace("%reward%", Utils.format(newReward)));
	}

	public static void bountyExpired(Bounty bounty) {
		for (Player t1 : Bukkit.getOnlinePlayers()) {
			VersionUtils.sound(t1, "ENTITY_VILLAGER_NO", 1, 2);
			t1.sendMessage(ChatColor.YELLOW + Utils.msg("bounty-expired").replace("%target%", bounty.getTarget().getName()));
		}
	}

	public static void bountyChange(BountyCreateEvent e) {
		Bounty b = e.getBounty();
		for (Player t1 : Bukkit.getOnlinePlayers())
			t1.sendMessage(ChatColor.YELLOW + Utils.msg("bounty-change").replace("%player%", b.getTarget().getName()).replace("%reward%", Utils.format(b.getReward())));
		if (b.getTarget().isOnline())
			VersionUtils.sound((Player) b.getTarget(), "ENTITY_ENDERMEN_HURT", 1, 1);
	}
}
