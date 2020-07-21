package net.Indyuce.bountyhunters.comp.placeholder;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.player.PlayerData;

public class BountyHuntersPlaceholders extends PlaceholderExpansion {
	@Override
	public String getAuthor() {
		return "Indyuce";
	}

	@Override
	public String getIdentifier() {
		return "bountyhunters";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	public String onPlaceholderRequest(Player player, String identifier) {

		if (identifier.startsWith("top_")) {
			int index = Integer.parseInt(identifier.substring(4));
			UUID found = BountyHunters.getInstance().getHunterLeaderboard().getPosition(index);
			return found == null ? "-" : Bukkit.getOfflinePlayer(found).getName();
		}
		if (identifier.startsWith("topb_")) {
			int index = Integer.parseInt(identifier.substring(5));
			UUID found = BountyHunters.getInstance().getHunterLeaderboard().getPosition(index);
			return found == null ? "0" : "" + BountyHunters.getInstance().getHunterLeaderboard().getScore(found);
		}

		PlayerData playerData = BountyHunters.getInstance().getPlayerDataManager().get(player);
		switch (identifier) {
		case "level":
			return "" + playerData.getLevel();
		case "successful_bounties":
			return "" + playerData.getSuccessfulBounties();
		case "claimed_bounties":
			return "" + playerData.getClaimedBounties();
		case "illegal_streak":
			return "" + playerData.getIllegalKillStreak();
		case "illegal_kills":
			return "" + playerData.getIllegalKills();
		case "current_bounty": {
			Optional<Bounty> bounty = BountyHunters.getInstance().getBountyManager().getBounty(player);
			return bounty.isPresent() ? new NumberFormat().format(bounty.get().getReward()) : "0";
		}
		case "quote":
			return playerData.hasQuote() ? playerData.getQuote().format() : "";
		case "title":
			return playerData.hasTitle() ? playerData.getTitle().format() : "";
		case "progress":
			return playerData.getLevelProgressBar();
		case "before_level_up":
			return "" + playerData.getBountiesNeededToLevelUp();
		}
		return null;
	}
}
