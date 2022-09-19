package net.Indyuce.bountyhunters.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class HunterLevelUpEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final int newLevel;

    /**
     * Called when a player levels up which cannot be cancelled
     *
     * @param player   Hunter leveling up
     * @param newLevel Level reached
     */
    public HunterLevelUpEvent(Player player, int newLevel) {
        super(player);

        this.newLevel = newLevel;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
