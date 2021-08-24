package net.Indyuce.bountyhunters.compat.interaction;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * This interface is used to check
 */
public interface InteractionRestriction {

    /**
     * @param interaction The interaction type (bounty creation/increase/claim)
     * @param claimer     The player interacting with the bounty
     * @param target      The bounty target
     * @return If the player can claim, increase or
     *         create a bounty with given target
     */
     boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target);

     enum InteractionType {

        /**
         * Bounty creation
         */
        CREATE,

        /**
         * Bounty claiming
         */
        CLAIM,

        /**
         * Bounty contribution
         */
        INCREASE;
    }
}
