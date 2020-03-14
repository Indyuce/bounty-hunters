package net.Indyuce.bountyhunters.api.restriction;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.Indyuce.bountyhunters.api.Bounty;

public class TeamRestriction implements BountyRestriction {
	@Override
	public boolean canInteractWith(Player claimer, Bounty bounty) {
		return !sameTeam(claimer, bounty.getTarget());
	}

	private boolean sameTeam(OfflinePlayer player1, OfflinePlayer player2) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team1 = scoreboard.getEntryTeam(player1.getName());
		
		Bukkit.broadcastMessage(team1 +" " + scoreboard.getEntryTeam(player2.getName()));
		
		return team1 != null && team1.equals(scoreboard.getEntryTeam(player2.getName()));
	}
}
