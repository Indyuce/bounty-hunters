package net.Indyuce.bountyhunters.listener;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.language.Message;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import net.Indyuce.bountyhunters.api.player.PlayerHunting;
import net.Indyuce.bountyhunters.version.VersionSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class HuntListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void a(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK
				|| !CustomItem.BOUNTY_COMPASS.loreMatches(event.getItem()))
			return;

		// heavy checks afterwards
		PlayerData data = BountyHunters.getInstance().getPlayerDataManager().get(player);
		if (!data.isHunting())
			return;

		PlayerHunting hunt = data.getHunting();
		if (hunt.isCompassActive())
			return;

		if (!hunt.getHunted().isOnline() || !hunt.getHunted().getPlayer().getWorld().equals(player.getWorld())) {
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
			return;
		}

		if (!player.hasPermission("bountyhunters.compass")) {
			Message.NOT_ENOUGH_PERMS.format().send(player);
			return;
		}

		player.playSound(player.getLocation(), VersionSound.BLOCK_NOTE_BLOCK_HAT.toSound(), 1, 1);
		hunt.showParticles(player);
	}
}
