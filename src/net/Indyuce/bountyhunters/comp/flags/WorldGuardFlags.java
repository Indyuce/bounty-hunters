package net.Indyuce.bountyhunters.comp.flags;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent.BountyCause;

public class WorldGuardFlags implements Listener {
	private final WorldGuard worldguard;
	private final WorldGuardPlugin worldguardPlugin;

	/*
	 * leaves space for extra WG flags if needed.
	 */
	private final Map<String, StateFlag> flags = new HashMap<>();

	public WorldGuardFlags() {
		this.worldguard = WorldGuard.getInstance();
		this.worldguardPlugin = ((WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard"));

		FlagRegistry registry = worldguard.getFlagRegistry();
		for (CustomFlag customFlag : CustomFlag.values())
			try {
				StateFlag flag = new StateFlag(customFlag.getPath(), true);
				registry.register(flag);
				flags.put(customFlag.getPath(), flag);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	@EventHandler
	public void a(BountyClaimEvent event) {
		if (!isFlagAllowed(event.getBounty().getTarget().getPlayer(), CustomFlag.CLAIM_BOUNTIES))
			event.setCancelled(true);
	}

	@EventHandler
	public void b(BountyCreateEvent event) {
		CustomFlag checked = event.getCause() == BountyCause.AUTO_BOUNTY ? CustomFlag.AUTO_BOUNTY : CustomFlag.CREATE_BOUNTIES;
		if (event.hasCreator() && !isFlagAllowed(event.getCreator(), checked))
			event.setCancelled(true);
	}

	public boolean isFlagAllowed(Player player, CustomFlag customFlag) {
		return getApplicableRegion(player.getLocation()).queryValue(worldguardPlugin.wrapPlayer(player), flags.get(customFlag.getPath())) != StateFlag.State.DENY;
	}

	private ApplicableRegionSet getApplicableRegion(Location loc) {
		return worldguard.getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
	}
}
