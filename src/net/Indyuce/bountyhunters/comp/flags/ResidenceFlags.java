package net.Indyuce.bountyhunters.comp.flags;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;

public class ResidenceFlags implements Listener {
	public ResidenceFlags() {
		for (CustomFlag flag : CustomFlag.values())
			FlagPermissions.addFlag(flag.getPath());

		Bukkit.getPluginManager().registerEvents(this, BountyHunters.getInstance());
	}

	@EventHandler
	public void a(BountyClaimEvent event) {
		if (!isFlagAllowed(event.getBounty().getTarget().getPlayer(), CustomFlag.CLAIM_BOUNTIES))
			event.setCancelled(true);
	}

	@EventHandler
	public void b(BountyCreateEvent event) {
		if (event.hasCreator() && !isFlagAllowed(event.getCreator(), CustomFlag.CREATE_BOUNTIES))
			event.setCancelled(true);
	}

	@SuppressWarnings("deprecation")
	public boolean isFlagAllowed(Player player, CustomFlag flag) {
		ClaimedResidence res = Residence.getInstance().getResidenceManager().getByLoc(player);
		return res == null || res.getPermissions().playerHas(player, flag.getPath(), true);
	}
}