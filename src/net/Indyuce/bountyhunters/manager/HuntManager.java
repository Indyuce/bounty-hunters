package net.Indyuce.bountyhunters.manager;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.BountyUtils;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.Message;

// hunters are not saved when the server shuts down
public class HuntManager {
	private Map<UUID, UUID> hunting = new HashMap<>();
	private Set<UUID> compassTimeout = new HashSet<>();

	public HuntManager() {
		if (BountyHunters.plugin.getConfig().getBoolean("compass.enabled"))
			new BukkitRunnable() {
				public void run() {
					for (Player p : Bukkit.getOnlinePlayers())
						if (checkCompass(p)) {
							compassTimeout.remove(p.getUniqueId());
							p.setCompassTarget(p.getWorld().getSpawnLocation());
						}
				}
			}.runTaskTimer(BountyHunters.plugin, 0, 10);
	}

	public Set<UUID> getHuntingPlayers() {
		return hunting.keySet();
	}

	public Collection<UUID> getHuntedPlayers() {
		return hunting.values();
	}

	public boolean isHunting(OfflinePlayer player) {
		return hunting.containsKey(player.getUniqueId());
	}

	public OfflinePlayer getHunted(OfflinePlayer hunter) {
		return Bukkit.getOfflinePlayer(hunting.get(hunter.getUniqueId()));
	}

	public void setHunting(OfflinePlayer hunter, OfflinePlayer hunted) {
		hunting.put(hunter.getUniqueId(), hunted.getUniqueId());
	}

	public void stopHunting(OfflinePlayer hunter) {
		hunting.remove(hunter.getUniqueId());
	}

	public Bounty getTargetBounty(OfflinePlayer hunter) {
		return BountyHunters.getBountyManager().getBounty(getHunted(hunter));
	}

	// returns true to reset the compass location
	// (!!) compass location can be abused
	private boolean checkCompass(Player p) {
		if (!isHunting(p))
			return compassTimeout.contains(p.getUniqueId());

		compassTimeout.add(p.getUniqueId());
		OfflinePlayer offlineTarget = getHunted(p);
		if (!offlineTarget.isOnline())
			return true;

		Player target = (Player) offlineTarget;
		String format = Message.COMPASS_IN_ANOTHER_WORLD.getUpdated();
		if (p.getWorld().getName().equals(target.getWorld().getName())) {
			format = Message.COMPASS_BLOCKS.formatRaw("%blocks%", new DecimalFormat(BountyHunters.plugin.getConfig().getString("compass.format")).format(target.getLocation().distance(p.getLocation())));
			p.setCompassTarget(target.getLocation().clone().add(.5, 0, .5));
		}

		@SuppressWarnings("deprecation")
		ItemStack i = p.getInventory().getItemInHand();
		if (BountyUtils.isPluginItem(i, true) ? i.getItemMeta().getLore().equals(CustomItem.BOUNTY_COMPASS.a().getItemMeta().getLore()) : false) {
			ItemMeta meta = i.getItemMeta();
			meta.setDisplayName(Message.COMPASS_FORMAT.formatRaw("%format%", format));
			i.setItemMeta(meta);
		}
		return false;
	}
}
