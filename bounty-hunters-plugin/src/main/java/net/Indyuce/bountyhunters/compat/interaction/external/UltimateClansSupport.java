package net.Indyuce.bountyhunters.compat.interaction.external;

import me.ulrich.clans.Clans;
import me.ulrich.clans.data.ClanData;
import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class UltimateClansSupport implements InteractionRestriction {
    private final Clans clans;

    public UltimateClansSupport() {
        this.clans = (Clans) Bukkit.getPluginManager().getPlugin("Clans");
    }

    @Override
    public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
        ClanData clan = clans.getPlayerAPI().getPlayerClan(claimer.getUniqueId());
        return clan == null || !clan.getMembers().contains(target.getUniqueId());
    }
}
