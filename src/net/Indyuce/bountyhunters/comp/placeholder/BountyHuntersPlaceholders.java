package net.Indyuce.bountyhunters.comp.placeholder;

import java.util.Optional;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.player.LeaderboardProfile;
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
		return BountyHunters.getInstance().getDescription().getVersion();
	}

	@Override
	public String onRequest(OfflinePlayer player, String identifier) {

		// leaderboard placeholders
		if (identifier.startsWith("top_name_")) {
			int index = Integer.parseInt(identifier.substring("top_name_".length()));
			Optional<LeaderboardProfile> found = BountyHunters.getInstance().getHunterLeaderboard().getPosition(index);
			return found.isPresent() ? found.get().getName() : "-";
		}
		if (identifier.startsWith("top_bounties_")) {
			int index = Integer.parseInt(identifier.substring("top_bounties_".length()));
			Optional<LeaderboardProfile> found = BountyHunters.getInstance().getHunterLeaderboard().getPosition(index);
			return found.isPresent() ? "" + found.get().getClaimedBounties() : "-";
		}
		if (identifier.startsWith("top_heads_")) {
			int index = Integer.parseInt(identifier.substring("top_heads_".length()));
			Optional<LeaderboardProfile> found = BountyHunters.getInstance().getHunterLeaderboard().getPosition(index);
			return found.isPresent() ? "" + found.get().getSuccessfulBounties() : "-";
		}
		if (identifier.startsWith("top_level_")) {
			int index = Integer.parseInt(identifier.substring("top_level_".length()));
			Optional<LeaderboardProfile> found = BountyHunters.getInstance().getHunterLeaderboard().getPosition(index);
			return found.isPresent() ? "" + found.get().getLevel() : "-";
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