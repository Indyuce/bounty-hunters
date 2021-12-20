package net.Indyuce.bountyhunters.api.event;

import net.Indyuce.bountyhunters.api.language.Message;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class HunterTargetEvent extends PlayerEvent implements Cancellable {
	private final OfflinePlayer target;

	private boolean cancelled = false;

	private static final HandlerList handlers = new HandlerList();

	/**
	 * This event is called when a bounty reward changes, for instance when a
	 * player performs /addbounty {player} {reward} when there's already a
	 * bounty on the player, or when the auto bounty system adds a specific
	 * amount to a player's bounty.
	 * 
	 * @param player
	 *            Player who hunts
	 * @param target
	 *            Player who is being hunted down
	 */
	public HunterTargetEvent(Player player, OfflinePlayer target) {
		super(player);

		this.target = target;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean bool) {
		cancelled = bool;
	}

	public OfflinePlayer getTarget() {
		return target;
	}

	public void sendAllert(Player target) {
		Message.NEW_HUNTER_ALERT.format("hunter", getPlayer().getName()).send(target);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
