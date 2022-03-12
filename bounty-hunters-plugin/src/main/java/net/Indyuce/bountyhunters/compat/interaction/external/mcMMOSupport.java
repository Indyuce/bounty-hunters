package net.Indyuce.bountyhunters.compat.interaction.external;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class mcMMOSupport implements InteractionRestriction {

    @Override
    public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
        McMMOPlayer extPlayerData = UserManager.getPlayer(claimer);
        if (extPlayerData == null)
            return true;

        Party party = extPlayerData.getParty();
        return party == null || !party.getMembers().containsKey(target.getUniqueId());
    }
}
