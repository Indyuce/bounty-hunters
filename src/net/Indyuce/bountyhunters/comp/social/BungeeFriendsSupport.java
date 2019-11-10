package net.Indyuce.bountyhunters.comp.social;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.restriction.BountyRestriction;
import net.simplyrin.bungeefriends.Main;

public class BungeeFriendsSupport implements BountyRestriction {
	private final Main plugin = (Main) Bukkit.getPluginManager().getPlugin("BungeeFriends");

	@Override
	public boolean canClaimBounty(Player claimer, Bounty bounty) {
		return !plugin.getFriendManager().getPlayer(claimer.getUniqueId()).isFriend(bounty.getTarget().getUniqueId());
	}
}
