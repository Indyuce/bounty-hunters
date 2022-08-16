package net.Indyuce.bountyhunters.compat.interaction.external;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class LandsSupport implements InteractionRestriction {
    private final LandsIntegration inte;

    public LandsSupport() {
        this.inte = new LandsIntegration(BountyHunters.getInstance());
    }

    @Override
    public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
        LandPlayer landPlayer = inte.getLandPlayer(claimer.getUniqueId());

        // Check if not in the same land
        for (Land land : landPlayer.getLands())
            if (land.getTrustedPlayers().contains(target.getUniqueId()))
                return false;

        /*for (Nation nation : getNations(landPlayer))
            for (Land land : nation.getLands())
                if (land.getTrustedPlayers().contains(target.getUniqueId()))
                    return false;*/

        return true;
    }

    /*private Set<Nation> getNations(LandPlayer player) {
        Set<Nation> nations = new HashSet<>();

        for (Land land : player.getLands())
            nations.add(land.getNation());

        return nations;
    }*/
}
