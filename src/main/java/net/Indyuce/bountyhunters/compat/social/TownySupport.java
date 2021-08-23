package net.Indyuce.bountyhunters.compat.social;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import net.Indyuce.bountyhunters.api.restriction.BountyRestriction;

public class TownySupport implements BountyRestriction {

	@Override
	public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
		return !inSameTown(claimer, target);
	}

	private boolean inSameTown(OfflinePlayer player, OfflinePlayer player1) {
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
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
