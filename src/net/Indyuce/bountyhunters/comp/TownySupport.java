package net.Indyuce.bountyhunters.comp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;

public class TownySupport implements Listener {
	@EventHandler
	public void a(BountyClaimEvent event) {
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(event.getClaimer().getName());
			if (resident.hasTown() && resident.getTown().hasResident(event.getBounty().getCreator().getName()))
				event.setCancelled(true);
		} catch (NotRegisteredException hasNoTownException) {
			/*
			 * player who's claiming the bounty has no town, therefore there is
			 * no towny restriction.
			 */
		}
	}
}
