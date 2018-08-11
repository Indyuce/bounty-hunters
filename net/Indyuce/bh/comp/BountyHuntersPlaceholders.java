package net.Indyuce.bh.comp;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.bh.ConfigData;
import net.Indyuce.bh.Main;
import net.Indyuce.bh.api.PlayerData;

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

	public String onPlaceholderRequest(Player p, String identifier) {
		if (identifier.equals("level") || identifier.equals("successful_bounties") || identifier.equals("claimed_bounties"))
			return "" + PlayerData.get(p).getInt(identifier.replace("_", "-"));

		if (identifier.equals("quote") || identifier.equals("title"))
			return PlayerData.get(p).getString(identifier);

		if (identifier.equals("progress")) {
			return PlayerData.get(p).getLevelAdvancementBar();
		}

		if (identifier.equals("before_level_up")) {
			int levelUp = ConfigData.getCD(Main.plugin, "", "levels").getInt("bounties-needed-to-lvl-up");
			int claimedBounties = PlayerData.get(p).getInt("claimed-bounties");
			return "" + (levelUp - (claimedBounties % levelUp));
		}
		return null;
	}
}
