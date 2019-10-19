package net.Indyuce.bountyhunters.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

public class HuntManager {
	private final Map<UUID, HunterData> hunting = new HashMap<>();

	public Set<UUID> getHuntingPlayers() {
		return hunting.keySet();
	}

	public Collection<HunterData> getHuntedPlayers() {
		return hunting.values();
	}

	public boolean isHunting(OfflinePlayer player) {
		return hunting.containsKey(player.getUniqueId());
	}

	public HunterData getData(OfflinePlayer hunter) {
		return hunting.get(hunter.getUniqueId());
	}

	public void setHunting(OfflinePlayer hunter, OfflinePlayer hunted) {
		hunting.put(hunter.getUniqueId(), new HunterData(hunted));
	}

	public void stopHunting(OfflinePlayer hunter) {
		hunting.remove(hunter.getUniqueId());
	}

	public Bounty getTargetBounty(OfflinePlayer hunter) {
		return BountyHunters.getInstance().getBountyManager().getBounty(getData(hunter).getHunted());
	}

	public class HunterData {
		private final OfflinePlayer tracked;

		private BukkitRunnable particles;
		private Player player;

		private ItemStack compass;

		public HunterData(OfflinePlayer tracked) {
			this.tracked = tracked;
		}

		public void showParticles(Player player) {
			hideParticles();
			this.player = player;

			(particles = new BukkitRunnable() {
				int ti = 0;
				boolean circle = BountyHunters.getInstance().getConfig().getBoolean("player-tracking.target-particles");

				public void run() {

					/*
					 * cancel runnable if any of the conditions is missing.
					 */
					if (!check()) {
						hideParticles();
						return;
					}

					// update compass display name based on distance
					ItemMeta meta = compass.getItemMeta();
					meta.setDisplayName(Language.COMPASS_FORMAT.format("blocks", new NumberFormat().thousands().format(tracked.getPlayer().getLocation().distance(player.getLocation()))));
					compass.setItemMeta(meta);

					// draw vector
					Location src = player.getLocation().add(0, 1.3, 0).add(player.getEyeLocation().getDirection().setY(0).normalize());
					Vector vec = tracked.getPlayer().getLocation().subtract(src.clone().add(0, -1.3, 0)).toVector().normalize().multiply(.2);
					for (int j = 0; j < 9; j++)
						BountyHunters.getInstance().getVersionWrapper().spawnParticle(Particle.REDSTONE, src.add(vec), player, Color.RED);

					// draw circle around target
					if (circle && (ti = (ti + 1) % 20) < 3) {
						Location loc = tracked.getPlayer().getLocation();
						for (double j = 0; j < Math.PI * 2; j += Math.PI / 16)
							BountyHunters.getInstance().getVersionWrapper().spawnParticle(Particle.REDSTONE, loc.clone().add(Math.cos(j) * .8, .15, Math.sin(j) * .8), player, Color.RED);
					}
				}
			}).runTaskTimer(BountyHunters.getInstance(), 0, 6);
		}

		public boolean isCompassActive() {
			return particles != null;
		}

		private boolean check() {
			if (player == null || tracked == null || !player.isOnline() || !tracked.isOnline() || !tracked.getPlayer().getWorld().equals(player.getWorld()))
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
			if (particles != null) {
				particles.cancel();
				particles = null;
			}
		}

		public OfflinePlayer getHunted() {
			return tracked;
		}
	}
}
