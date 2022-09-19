package net.Indyuce.bountyhunters.compat.interaction.external;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PartiesSupport implements InteractionRestriction {
    private final PartiesAPI api = Parties.getApi();

    @Override
    public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
        Party party = api.getParty(claimer.getUniqueId());
        return party == null || !party.getMembers().contains(target.getUniqueId());
    }
}