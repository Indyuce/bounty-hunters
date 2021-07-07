package net.Indyuce.bountyhunters.api.player;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.language.Language;

public class PlayerHunting {
	private final Bounty bounty;

	private BukkitRunnable compassRunnable;
	private Player player;
	private ItemStack compass;

	public PlayerHunting(Bounty bounty) {
		this.bounty = bounty;
	}

	public void showParticles(Player player) {
		hideParticles();
		this.player = player;

		(compassRunnable = new BukkitRunnable() {
			int ti = 0;
			boolean circle = BountyHunters.getInstance().getConfig().getBoolean("player-tracking.target-compassRunnable");

			public void run() {

				// Cancel runnable if any of the conditions is missing.
				if (!check()) {
					hideParticles();
					return;
				}

				// Update compass display name based on distance
				ItemMeta meta = compass.getItemMeta();
				meta.setDisplayName(Language.COMPASS_FORMAT.format("blocks",
						new NumberFormat(true).format(bounty.getTarget().getPlayer().getLocation().distance(player.getLocation()))));
				compass.setItemMeta(meta);

				// Draw vector
				Location src = player.getLocation().add(0, 1.3, 0).add(player.getEyeLocation().getDirection().setY(0).normalize());
				Vector vec = bounty.getTarget().getPlayer().getLocation().subtract(src.clone().add(0, -1.3, 0)).toVector().normalize().multiply(.2);
				for (int j = 0; j < 9; j++)
					BountyHunters.getInstance().getVersionWrapper().spawnParticle(Particle.REDSTONE, src.add(vec), player, Color.RED);

				// Draw circle around target
				if (circle && (ti = (ti + 1) % 20) < 3) {
					Location loc = bounty.getTarget().getPlayer().getLocation();
					for (double j = 0; j < Math.PI * 2; j += Math.PI / 16)
						BountyHunters.getInstance().getVersionWrapper().spawnParticle(Particle.REDSTONE,
								loc.clone().add(Math.cos(j) * .8, .15, Math.sin(j) * .8), player, Color.RED);
				}
			}
		}).runTaskTimer(BountyHunters.getInstance(), 0, 6);
	}

	public boolean isCompassActive() {
		return compassRunnable != null;
	}

	private boolean check() {
		if (!player.isOnline() || !bounty.getTarget().isOnline() || !bounty.getTarget().getPlayer().getWorld().equals(player.getWorld()))
			return false;

		if (CustomItem.BOUNTY_COMPASS.loreMatches(player.getInventory().getItemInMainHand())) {
			compass = player.getInventory().getItemInMainHand();
			return true;
		}

		if (CustomItem.BOUNTY_COMPASS.loreMatches(player.getInventory().getItemInOffHand())) {
			compass = player.getInventory().getItemInOffHand();
			return true;
		}

		return false;
	}

	public void hideParticles() {
		if (!isCompassActive())
			return;

		compassRunnable.cancel();
		compassRunnable = null;
	}

	public Bounty getBounty() {
		return bounty;
	}

	public OfflinePlayer getHunted() {
		return bounty.getTarget();
	}
}
