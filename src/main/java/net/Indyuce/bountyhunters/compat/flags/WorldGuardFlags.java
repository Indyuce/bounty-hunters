package net.Indyuce.bountyhunters.compat.flags;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class WorldGuardFlags implements Listener {
    private final WorldGuard worldguard;
    private final WorldGuardPlugin worldguardPlugin;

    /**
     * Leaves space for extra WG flags if needed.
     */
    private final Map<String, StateFlag> registeredFlags = new HashMap<>();

    public WorldGuardFlags() {
        this.worldguard = WorldGuard.getInstance();
        this.worldguardPlugin = ((WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard"));

        FlagRegistry registry = worldguard.getFlagRegistry();
        for (CustomFlag customFlag : CustomFlag.values())
            try {
                StateFlag flag = new StateFlag(customFlag.getPath(), true);
                registry.register(flag);
                registeredFlags.put(customFlag.name(), flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void a(BountyClaimEvent event) {
        if (!isAllowed(event.getBounty().getTarget().getPlayer(), CustomFlag.CLAIM_BOUNTIES))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void b(BountyCreateEvent event) {
        CustomFlag checked = event.getCause() == BountyCreateEvent.BountyCause.AUTO_BOUNTY ? CustomFlag.AUTO_BOUNTY : CustomFlag.CREATE_BOUNTIES;
        if (event.hasCreator() && !isAllowed(event.getCreator(), checked))
            event.setCancelled(true);
    }

    public boolean isAllowed(Player player, CustomFlag customFlag) {
        return getApplicableRegion(player.getLocation()).queryValue(worldguardPlugin.wrapPlayer(player), registeredFlags.get(customFlag.name())) != StateFlag.State.DENY;
    }

    private ApplicableRegionSet getApplicableRegion(Location loc) {
        return worldguard.getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
    }
}
