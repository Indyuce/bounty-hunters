package net.Indyuce.bountyhunters.manager;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.BountyInactivityRemoval;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import net.Indyuce.bountyhunters.compat.interaction.BedSpawnPoint;
import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import net.Indyuce.bountyhunters.gui.BountyEditor;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BountyManager {

    /**
     * Warning: bounty ID does NOT correspond to the target UUID! Bounty ID is used
     * as key to store bounties however bounty target uuid is stored inside the
     * bounty class instance
     */
    private final LinkedHashMap<UUID, Bounty> bounties = new LinkedHashMap<>();

    /**
     * List of bounty restrictions that must be verified when a bounty is
     * claimed. Makes implementing plugin compatibility and extra options much
     * easier
     */
    private final Set<InteractionRestriction> restrictions = new HashSet<>();

    public BountyManager() {

        if (BountyHunters.getInstance().getConfig().getBoolean("claim-restrictions.bed-spawn-point.enabled"))
            registerClaimRestriction(
                    new BedSpawnPoint(BountyHunters.getInstance().getConfig().getConfigurationSection("claim-restrictions.bed-spawn-point")));

        /*
         * checks for inactive bounties every 2min
         */
        if (BountyHunters.getInstance().getConfig().getBoolean("inactive-bounty-removal.enabled"))
            new BountyInactivityRemoval().runTaskTimer(BountyHunters.getInstance(), 20 * 5, 20 * 60 * 2);
    }

    /**
     * Unregisters a bounty. It removes the bounty from the map, stops player
     * trackings and closes bounty editors
     *
     * @param remove If the bounty should be removed from the bounty map
     * @param bounty Bounty to unregister
     */
    public void unregisterBounty(Bounty bounty, boolean remove) {
        if (remove)
            bounties.remove(bounty.getId());
        bounty.getHunters().forEach(hunter -> {
            PlayerData data = BountyHunters.getInstance().getPlayerDataManager().get(hunter);
            if (data.isHunting())
                data.stopHunting();
        });

        /*
         * checks for online admins who opened the bounty editor for that
         * specific bounty and close GUIs
         */
        for (Player online : Bukkit.getOnlinePlayers())
            if (online.getOpenInventory() != null && online.getOpenInventory().getTopInventory().getHolder() instanceof BountyEditor)
                if (((BountyEditor) online.getOpenInventory().getTopInventory().getHolder()).getBounty().equals(bounty))
                    online.closeInventory();
    }

    /**
     * Registers a bounty. Throws an IAE if there is already a bounty with the
     * same bounty identifier
     *
     * @param bounty Bounty to register
     */
    public void registerBounty(Bounty bounty) {
        Validate.isTrue(!bounties.containsKey(bounty.getId()), "Attempted to register bounty with duplicate ID '" + bounty.getId() + "'");

        bounties.put(bounty.getId(), bounty);
    }

    public Set<InteractionRestriction> getClaimRestrictions() {
        return restrictions;
    }

    /**
     * Registers a bounty restriction. Bounty restrictions apply when
     * creating/increasing/claiming a bounty. If some restriction says two
     * player's can't interact because they are in the same town/whatever
     * reason, the bounty event is cancelled
     *
     * @param restriction Restriction to register
     */
    public void registerClaimRestriction(InteractionRestriction restriction) {
        restrictions.add(restriction);
    }

    /**
     * @return Currently active player bounties
     */
    public Collection<Bounty> getBounties() {
        return bounties.values();
    }

    /**
     * @param player Given player
     * @return Find all the bounties the given player contributed in
     */
    public List<Bounty> getContributions(Player player) {
        return bounties.values().stream().filter(bounty -> bounty.hasContributed(player)).collect(Collectors.toList());
    }

    /**
     * Checks for a bounty on a player
     *
     * @param player The bounty target
     * @return If the given player has a bounty on his head
     * @deprecated Use getBounty() to retrieve the bounty and check for its
     *         existence at the same time
     */
    @Deprecated
    public boolean hasBounty(OfflinePlayer player) {
        return getBounty(player).isPresent();
    }

    /**
     * Checks for a bounty using a bounty ID
     *
     * @param bountyId The bounty identifier
     * @return If there is a bounty wouch
     */
    public boolean hasBounty(UUID bountyId) {
        return bounties.containsKey(bountyId);
    }

    /**
     * Find a player's bounty
     *
     * @param target Bounty target
     * @return The player bounty
     */
    public Optional<Bounty> getBounty(OfflinePlayer target) {
        for (Bounty bounty : bounties.values())
            if (bounty.hasTarget(target))
                return Optional.of(bounty);
        return Optional.empty();
    }

    /**
     * Find a bounty by bounty ID
     *
     * @param bountyId The bounty unique identifier
     * @return The corresponding bounty
     */
    public Bounty getBounty(UUID bountyId) {
        return bounties.get(bountyId);
    }

    /**
     * Find a bounty by player name
     *
     * @param name The target player's name
     * @return Bounty found or none
     */
    public Optional<Bounty> findByName(String name) {
        for (Bounty bounty : bounties.values())
            if (bounty.getTarget().getName().equalsIgnoreCase(name))
                return Optional.of(bounty);
        return Optional.empty();
    }

    public abstract void saveBounties();
}
