package net.Indyuce.bountyhunters.comp.social;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import net.Indyuce.bountyhunters.api.event.BountyChangeEvent;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;

public class TownySupport implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void a(BountyClaimEvent event) {
		if (event.getBounty().hasCreator() && inSameTown(event.getClaimer(), event.getBounty().getCreator()))
			event.setCancelled(true);
	}

	/*
	 * cancel bounty changes when players are in the same town
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void b(BountyChangeEvent event) {
		if (event.getBounty().hasCreator() && inSameTown(event.getPlayer(), event.getBounty().getCreator()))
			event.setCancelled(true);
	}

	private boolean inSameTown(OfflinePlayer player, OfflinePlayer player1) {
		try {
			Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
			return resident.hasTown() && resident.getTown().hasResident(player1.getName());

		} catch (NotRegisteredException exception) {
			/*
			 * player who's claiming the bounty has no town, therefore there is
			 * no towny restriction.
			 */
			return false;
		}
	}
}
