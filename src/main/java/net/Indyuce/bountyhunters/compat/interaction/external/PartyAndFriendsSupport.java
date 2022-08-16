package net.Indyuce.bountyhunters.compat.interaction.external;

import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PartyAndFriendsSupport implements InteractionRestriction {
	private final PAFPlayerManager manager = PAFPlayerManager.getInstance();

	@Override
	public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
		return !manager.getPlayer(claimer.getUniqueId()).isAFriendOf(manager.getPlayer(target.getUniqueId()));
	}
}
