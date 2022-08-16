package net.Indyuce.bountyhunters.compat.interaction.external;

import cc.javajobs.factionsbridge.FactionsBridge;
import cc.javajobs.factionsbridge.bridge.infrastructure.struct.Faction;
import cc.javajobs.factionsbridge.bridge.infrastructure.struct.FactionsAPI;
import cc.javajobs.factionsbridge.bridge.infrastructure.struct.Relationship;
import net.Indyuce.bountyhunters.compat.interaction.InteractionRestriction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FactionsBridgeSupport implements InteractionRestriction {

    @Override
    public boolean canInteractWith(InteractionType interaction, Player claimer, OfflinePlayer target) {

        FactionsAPI api = FactionsBridge.getFactionsAPI();
        Faction faction = api.getFaction(claimer);
        if (faction == null)
            return true;

        Relationship relation = faction.getRelationshipTo(api.getFPlayer(target));
        return relation != Relationship.ENEMY;
    }
}
