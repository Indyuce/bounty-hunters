package net.Indyuce.bountyhunters.comp.placeholder;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.bountyhunters.BountyHunters;
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
		case "current_bounty":
			return BountyHunters.getInstance().getBountyManager().hasBounty(player) ? new NumberFormat().format(BountyHunters.getInstance().getBountyManager().getBounty(player).getReward()) : "0";
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
