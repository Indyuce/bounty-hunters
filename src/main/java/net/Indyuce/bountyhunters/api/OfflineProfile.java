package net.Indyuce.bountyhunters.api;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Not used yet but here if BH needs to stop using OfflinePlayers and migrate to
 * Name/UUID duos to store player data
 * 
 * @author cympe
 *
 */
public class OfflineProfile {

	private final UUID uuid;

	private final String name;
	private final OfflinePlayer offline;

	/**
	 * Solves the problem of trying to retrieve OfflinePlayers when loading
	 * bounties. If the offline player is found then we can access its name,
	 * otherwise we can't.
	 * 
	 * Displaying player skulls require the OfflinePlayer instance.
	 * 
	 * Added in 2.3.15
	 * 
	 * @param uuid The player UUID
	 */
	public OfflineProfile(OfflinePlayer player) {
		this.uuid = player.getUniqueId();
		this.name = player.getName();
		this.offline = player;
	}

	/**
	 * @param obj Either a string of a configuration section to support both new
	 *            and old bounty data formats (2.3.15 or earlier)
	 */
	public OfflineProfile(Object obj) {

		if (obj instanceof String) {
			uuid = UUID.fromString((String) obj);
			offline = Bukkit.getOfflinePlayer(uuid);
			name = isFound() ? getOfflinePlayer().getName() : "Name?";
			return;
		}

		if (obj instanceof ConfigurationSection) {
			ConfigurationSection config = (ConfigurationSection) obj;
			uuid = UUID.fromString(config.getString("uuid"));
			name = config.getString("name");
			offline = Bukkit.getOfflinePlayer(uuid);
			return;
		}

		throw new IllegalArgumentException("Provide either a string or a config section");
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public OfflinePlayer getOfflinePlayer() {
		return offline;
	}

	public void displaySkull(ItemStack item, ItemMeta meta) {
		if (offline == null)
			return;

	}

	/**
	 * @return Checks if the OfflinePlayer was found
	 */
	public boolean isFound() {
		return offline != null;
	}
}
