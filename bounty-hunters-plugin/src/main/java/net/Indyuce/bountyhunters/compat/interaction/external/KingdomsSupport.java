package net.Indyuce.bountyhunters.compat.interaction.external;

import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class KingdomsSupport implements InteractionRestriction {

    @Override
    public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
        throw new NotImplementedException();
    }
}
