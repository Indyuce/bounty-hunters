package net.Indyuce.bountyhunters.compat.interaction.external;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.TownyUniverse;
import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TownySupport implements InteractionRestriction {

	@Override
	public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
		return !inSameTown(claimer, target);
	}

	private boolean inSameTown(OfflinePlayer player, OfflinePlayer player1) {
		try {
			Resident resident = TownyUniverse.getInstance().getResident(player.getName());
			return resident.hasTown() && resident.getTown().hasResident(player1.getName());

			/*
			 * Player who's claiming the bounty has no town, therefore there is
			 * no towny restriction.
			 */
		} catch (NotRegisteredException exception) {
			return false;
		}
	}
}
