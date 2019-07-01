package net.Indyuce.bountyhunters.listener;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.manager.HuntManager.HunterData;

public class HuntListener implements Listener {
	@EventHandler
	public void a(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK || !BountyHunters.getInstance().getHuntManager().isHunting(player) || !CustomItem.BOUNTY_COMPASS.loreMatches(event.getItem()))
			return;

		HunterData data = BountyHunters.getInstance().getHuntManager().getData(player);
		Player hunted = data.getHunted().getPlayer();
		if (!data.getHunted().isOnline() || !hunted.getWorld().equals(player.getWorld())) {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			return;
		}

		if (!player.hasPermission(BountyHunters.getInstance().getConfig().getString("player-tracking.permission"))) {
			Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(player);
			return;
		}

		if (!data.isCompassActive()) {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
			data.showParticles(player);
		}
	}
}
