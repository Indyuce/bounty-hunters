package net.Indyuce.bountyhunters.compat.interaction;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.simonsator.partyandfriends.spigot.api.pafplayers.PAFPlayerManager;

public class PartyAndFriendsSupport implements InteractionRestriction {
	private final PAFPlayerManager manager = PAFPlayerManager.getInstance();

	@Override
	public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
		return !manager.getPlayer(claimer.getUniqueId()).isAFriendOf(manager.getPlayer(target.getUniqueId()));
	}
}
