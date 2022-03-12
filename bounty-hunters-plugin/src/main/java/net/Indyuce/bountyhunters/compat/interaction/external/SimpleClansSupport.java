package net.Indyuce.bountyhunters.compat.interaction.external;

import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class SimpleClansSupport implements InteractionRestriction {

    @Override
    public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
        ClanPlayer clanPlayer = SimpleClans.getInstance().getClanManager().getClanPlayer(claimer);
        return clanPlayer == null || !clanPlayer.getClan().isMember(target.getUniqueId());
    }
}
