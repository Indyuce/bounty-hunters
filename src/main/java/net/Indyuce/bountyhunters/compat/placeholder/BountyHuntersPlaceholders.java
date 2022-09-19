package net.Indyuce.bountyhunters.compat.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import net.Indyuce.bountyhunters.leaderboard.profile.BountyProfile;
import net.Indyuce.bountyhunters.leaderboard.profile.HunterProfile;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BountyHuntersPlaceholders extends PlaceholderExpansion {

    @Override
    public @NotNull String getAuthor() {
        return "Indyuce";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bountyhunters";
    }

    @Override
    public @NotNull String getVersion() {
        return BountyHunters.getInstance().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {

        // Hunter leaderboard placeholders
        if (identifier.startsWith("top_name_")) {
            int index = Integer.parseInt(identifier.substring("top_name_".length()));
            Optional<HunterProfile> found = BountyHunters.getInstance().getHunterLeaderboard().getPosition(index);
            return found.isPresent() ? found.get().getName() : "---";
        }
        if (identifier.startsWith("top_bounties_")) {
            int index = Integer.parseInt(identifier.substring("top_bounties_".length()));
            Optional<HunterProfile> found = BountyHunters.getInstance().getHunterLeaderboard().getPosition(index);
            return found.map(hunterProfile -> String.valueOf(hunterProfile.getClaimedBounties())).orElse("---");
        }
        if (identifier.startsWith("top_heads_")) {
            int index = Integer.parseInt(identifier.substring("top_heads_".length()));
            Optional<HunterProfile> found = BountyHunters.getInstance().getHunterLeaderboard().getPosition(index);
            return found.map(hunterProfile -> String.valueOf(hunterProfile.getSuccessfulBounties())).orElse("---");
        }
        if (identifier.startsWith("top_level_")) {
            int index = Integer.parseInt(identifier.substring("top_level_".length()));
            Optional<HunterProfile> found = BountyHunters.getInstance().getHunterLeaderboard().getPosition(index);
            return found.map(hunterProfile -> String.valueOf(hunterProfile.getLevel())).orElse("---");
        }

        // Highest bounties
        if (identifier.startsWith("topb_name_")) {
            int index = Integer.parseInt(identifier.substring("topb_name_".length()));
            Optional<BountyProfile> found = BountyHunters.getInstance().getBountyLeaderboard().getPosition(index);
            return found.isPresent() ? found.get().getName() : "---";
        }
        if (identifier.startsWith("topb_bounty_")) {
            int index = Integer.parseInt(identifier.substring("topb_bounty_".length()));
            Optional<BountyProfile> found = BountyHunters.getInstance().getBountyLeaderboard().getPosition(index);
            return found.map(bountyProfile -> String.valueOf(bountyProfile.getCurrentBounty())).orElse("---");
        }

        PlayerData playerData = PlayerData.get(player);
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
		/*case "quote":
			return playerData.hasQuote() ? playerData.getQuote().format() : "";*/
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