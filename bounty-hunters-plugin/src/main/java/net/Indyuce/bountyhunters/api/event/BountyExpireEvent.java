package net.Indyuce.bountyhunters.api.event;

import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.language.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.Objects;

public class BountyExpireEvent extends BountyEvent {
    private final BountyExpireCause cause;
    private final Player player;
    private final double amount;
    private final boolean expiring;

    private static final HandlerList handlers = new HandlerList();

    /**
     * When a bounty expires due to an admin
     *
     * @param bounty Bounty expiring
     */
    public BountyExpireEvent(Bounty bounty) {
        this(bounty, null, bounty.getReward(), BountyExpireCause.ADMIN);
    }

    /**
     * Called when a player takes away his contribution from the bounty
     *
     * @param bounty Bounty expiring
     * @param player Player taking away his contribution
     */
    public BountyExpireEvent(Bounty bounty, Player player) {
        this(bounty, player, bounty.getContribution(player), BountyExpireCause.PLAYER);
    }

    public BountyExpireEvent(Bounty bounty, Player player, double amount, BountyExpireCause cause) {
        super(bounty);

        this.player = player;
        this.amount = amount;
        this.cause = cause;
        expiring = getBounty().getReward() <= amount;
    }

    public BountyExpireCause getCause() {
        return cause;
    }

    public double getAmountRemoved() {
        return amount;
    }

    /**
     * @return If the bounty should disappear after the event is called
     */
    public boolean isExpiring() {
        return expiring;
    }

    public boolean hasPlayer() {
        return player != null;
    }

    public Player getPlayer() {
        return Objects.requireNonNull(player, "No player caused that event");
    }

    public void sendAllert() {
        if (isExpiring())
            Message.BOUNTY_EXPIRED.format("target", getBounty().getTarget().getName()).send(Bukkit.getOnlinePlayers());
        else {
            double reward = getBounty().getReward();
            Message.BOUNTY_DECREASED.format(
                    "target", getBounty().getTarget().getName(),
                    "old", new NumberFormat().format(reward + amount),
                    "new", new NumberFormat().format(reward),
                    "player", player.getName(),
                    "amount", new NumberFormat().format(amount))
                    .send(Bukkit.getOnlinePlayers());
        }
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum BountyExpireCause {

        /**
         * When an admin uses an admin command to remove the bounty or when the
         * admin removes a player's contribution
         */
        ADMIN,

        /**
         * When the creator takes away his contribution or when the bounty
         * finally expires
         */
        PLAYER,

        /**
         * When a bounty is removed due to inactivity
         */
        INACTIVITY;
    }
}
