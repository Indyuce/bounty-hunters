package net.Indyuce.bountyhunters.compat.interaction.external;

import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DungeonsXLSupport implements InteractionRestriction {

    @Override
    public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
        PlayerGroup group = DungeonsXL.getInstance().getPlayerGroup(claimer);
        if (group != null)
            for (UUID uuid : group.getMembers().getUniqueIds())
                if (uuid.equals(target.getUniqueId()))
                    return false;

        return true;
    }
}
