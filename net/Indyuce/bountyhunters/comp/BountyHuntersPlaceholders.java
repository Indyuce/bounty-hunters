package net.Indyuce.bountyhunters.comp;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.bountyhunters.api.PlayerData;

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
		PlayerData playerData = PlayerData.get(player);

		switch (identifier) {
		case "level":
			return "" + playerData.getLevel();
		case "successful_bounties":
			return "" + playerData.getSuccessfulBounties();
		case "claimed_bounties":
			return "" + playerData.getClaimedBounties();
		case "quote":
			return playerData.getQuote();
		case "title":
			return playerData.getTitle();
		case "progress":
			return playerData.getLevelProgressBar();
		case "before_level_up":
			return "" + playerData.getBountiesNeededToLevelUp();
		// case "target_distance":
		// return "" + playerData.getBountiesNeededToLevelUp();
		}
		return null;
	}
}
