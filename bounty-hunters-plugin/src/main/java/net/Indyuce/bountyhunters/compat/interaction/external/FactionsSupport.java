package net.Indyuce.bountyhunters.compat.interaction.external;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Relation;
import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FactionsSupport implements InteractionRestriction {

    @Override
    public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(claimer);
        FPlayer fTarget = FPlayers.getInstance().getByOfflinePlayer(target);

        // Safe check
        if (fPlayer == null || fTarget == null)
            return true;

        Relation relation = fTarget.getRelationTo(fPlayer);
        return relation == Relation.ALLY || relation == Relation.MEMBER;
    }
}
