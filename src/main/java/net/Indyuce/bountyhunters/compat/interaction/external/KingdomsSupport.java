package net.Indyuce.bountyhunters.compat.interaction.external;

import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.main.Kingdoms;

/**
 * Make sure the two players are NOT in the same kingdom
 */
public class KingdomsSupport implements InteractionRestriction {

    @Override
    public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
        KingdomPlayer player = Kingdoms.get().getDataHandlers().getKingdomPlayerManager().getData(claimer.getUniqueId());  // Should be loaded
        return player.getKingdom() == null || !player.getKingdom().isMember(target);
    }
}
