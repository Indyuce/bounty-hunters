package net.Indyuce.bountyhunters.compat.interaction.external;

import me.glaremasters.guilds.Guilds;
import me.glaremasters.guilds.guild.Guild;
import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class GuildsSupport implements InteractionRestriction {

    @Override
    public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {
        Guild guild = Guilds.getApi().getGuild(claimer.getUniqueId());
        return guild == null || guild.getMember(target.getUniqueId()) == null;
    }
}
