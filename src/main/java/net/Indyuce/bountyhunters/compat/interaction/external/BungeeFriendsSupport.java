package net.Indyuce.bountyhunters.compat.interaction.external;

import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import net.simplyrin.bungeefriends.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BungeeFriendsSupport implements InteractionRestriction {
	private final Main plugin = (Main) Bukkit.getPluginManager().getPlugin("BungeeFriends");

	@Override
	public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
		return !plugin.getFriendManager().getPlayer(claimer.getUniqueId()).isFriend(target.getUniqueId());
	}
}
