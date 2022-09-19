package net.Indyuce.bountyhunters.api.event;

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.language.Message;
import net.Indyuce.bountyhunters.api.language.PlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BountyCreateEvent extends BountyEvent {
    private static final HandlerList handlers = new HandlerList();
    private final BountyCause cause;
    private final Player creator;

    /**
     * This event is called whenever a player sets a bounty onto another player,
     * or when the auto-bounty automatically sets a new bounty on a player since
     * they killed someone illegaly
     */
    public BountyCreateEvent(Bounty bounty, Player creator, BountyCause cause) {
        super(bounty);

        this.cause = cause;
        this.creator = creator;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public BountyCause getCause() {
        return cause;
    }

    public Player getCreator() {
        return creator;
    }

    public boolean hasCreator() {
        return creator != null;
    }

    public void sendAllert() {
        String reward = new NumberFormat().format(getBounty().getReward());

        PlayerMessage toOnline = (cause == BountyCause.PLAYER ? Message.NEW_BOUNTY_ON_PLAYER
                : cause == BountyCause.AUTO_BOUNTY ? Message.NEW_BOUNTY_ON_PLAYER_ILLEGAL : Message.NEW_BOUNTY_ON_PLAYER_UNDEFINED).format("creator",
                getBounty().hasCreator() ? getBounty().getCreator().getName() : "null", "target", getBounty().getTarget().getName(), "reward",
                reward);

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (getBounty().hasTarget(player))
                (cause == BountyCause.PLAYER ? Message.NEW_BOUNTY_ON_YOU
                        : cause == BountyCause.AUTO_BOUNTY ? Message.NEW_BOUNTY_ON_YOU_ILLEGAL : Message.NEW_BOUNTY_ON_YOU_UNDEFINED)
                        .format("creator", getBounty().hasCreator() ? getBounty().getCreator().getName() : "null", "target",
                                getBounty().getTarget().getName(), "reward", reward)
                        .send(player);

            else if (getBounty().hasCreator(player))
                Message.BOUNTY_CREATED.format("creator", getBounty().hasCreator() ? getBounty().getCreator().getName() : "null", "target",
                        getBounty().getTarget().getName(), "reward", reward).send(player);

            else
                toOnline.send(player);
        }
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public enum BountyCause {

        /**
         * When a player sets a bounty onto another player's head
         */
        PLAYER,

        /**
         * When a non-player entity (console/command block) sets a bounty on a
         * player's head
         */
        CONSOLE,

        /**
         * When the auto bounty sets a bounty on a player since he killed
         * someone illegaly (illegaly = the player did not have any bounty on
         * them, which makes it an illegal kill)
         */
        AUTO_BOUNTY,

        /**
         * Extra bounty cause that is not used in the vanilla BountyHunters but
         * that can be used by other plugins
         */
        PLUGIN
    }
}
