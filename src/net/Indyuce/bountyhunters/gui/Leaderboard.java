package net.Indyuce.bountyhunters.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.PlayerData;

public class Leaderboard extends PluginInventory {
	private static final int[] slots = { 13, 21, 22, 23, 29, 30, 31, 32, 33, 37, 38, 39, 40, 41, 42, 43 };

	public Leaderboard(Player player) {
		super(player);
	}

	@Override
	public Inventory getInventory() {

		/*
		 * instead of calculating each player data to check what players has the
		 * most claimed bounties, players will the highest bounties are cached
		 * in a config file (cache/leaderboard.yml) and the 20 best can be
		 * accessed directly using that file.
		 */
		Map<PlayerData, Integer> hunters = new HashMap<>();
		for (String key : BountyHunters.getInstance().getCachedLeaderboard().getKeys(false)) {
			PlayerData data = PlayerData.get(Bukkit.getOfflinePlayer(UUID.fromString(key)));
			hunters.put(data, data.getClaimedBounties());
		}

		/*
		 * sort players depending on kills
		 */
		hunters = sortByBounties(hunters);

		Inventory inv = Bukkit.createInventory(this, 54, Message.LEADERBOARD_GUI_NAME.getUpdated());

		int slot = 0;
		for (Entry<PlayerData, Integer> entry : hunters.entrySet()) {
			if (slot > slots.length)
				break;

			PlayerData data = entry.getKey();
			ItemStack skull = CustomItem.LB_PLAYER_DATA.toItemStack();
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			if (BountyHunters.getInstance().getConfig().getBoolean("display-player-skulls"))
				meta.setOwningPlayer(Bukkit.getOfflinePlayer(data.getUniqueId()));
			meta.setDisplayName(applyPlaceholders(meta.getDisplayName(), data, slot + 1));
			List<String> lore = meta.getLore();
			for (int j = 0; j < lore.size(); j++)
				lore.set(j, applyPlaceholders(lore.get(j), data, slot + 1));
			meta.setLore(lore);
			skull.setItemMeta(meta);

			inv.setItem(slots[slot++], skull);
		}

		ItemStack glass = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemMeta glassMeta = glass.getItemMeta();
		glassMeta.setDisplayName(Message.NO_PLAYER.getUpdated());
		glass.setItemMeta(glassMeta);

		while (slot < slots.length)
			inv.setItem(slots[slot++], glass);

		return inv;
	}

	private LinkedHashMap<PlayerData, Integer> sortByBounties(Map<PlayerData, Integer> map) {
		List<Entry<PlayerData, Integer>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());

		LinkedHashMap<PlayerData, Integer> result = new LinkedHashMap<>();
		for (Entry<PlayerData, Integer> entry : list)
			result.put(entry.getKey(), entry.getValue());

		return result;
	}

	@Override
	public void whenClicked(ItemStack item, InventoryAction action, int slot) {
	}

	private String applyPlaceholders(String s, PlayerData playerData, int rank) {
		String title = playerData.hasTitle() ? playerData.getTitle() : Message.NO_TITLE.getUpdated();

		s = s.replace("%level%", "" + playerData.getLevel());
		s = s.replace("%bounties%", "" + playerData.getClaimedBounties());
		s = s.replace("%successful-bounties%", "" + playerData.getSuccessfulBounties());
		s = s.replace("%title%", title);
		s = s.replace("%name%", playerData.getOfflinePlayer().getName());
		s = s.replace("%rank%", "" + rank);

		return s;
	}

	public static void updateCachedLeaderboard(UUID uuid, int bounties) {
		/*
		 * if the leaderboard already contains that player, just add one to the
		 * bounties counter
		 * 
		 */
		if (BountyHunters.getInstance().getCachedLeaderboard().getKeys(false).contains(uuid.toString())) {
			BountyHunters.getInstance().getCachedLeaderboard().set(uuid.toString(), bounties);
			return;
		}

		/*
		 * if there is still not at least 16 players in the cached leaderboard,
		 * just add it to the keys and that's all
		 */
		if (BountyHunters.getInstance().getCachedLeaderboard().getKeys(false).size() < 16) {
			BountyHunters.getInstance().getCachedLeaderboard().set(uuid.toString(), bounties);
			return;
		}

		/*
		 * if there is more than 16 players in the leaderboard, the plugin will
		 * have to remove the player that has the least bounties and will
		 * replace it by the newer one IF the newer one has more bounties
		 */
		String leastKey = "";
		int leastBounties = Integer.MAX_VALUE;

		for (String key : BountyHunters.getInstance().getCachedLeaderboard().getKeys(false)) {
			int playerBounties = BountyHunters.getInstance().getCachedLeaderboard().getInt(key);
			if (playerBounties < leastBounties) {
				leastBounties = playerBounties;
				leastKey = key;
			}
		}

		if (bounties >= leastBounties) {
			BountyHunters.getInstance().getCachedLeaderboard().set(leastKey, null);
			BountyHunters.getInstance().getCachedLeaderboard().set(uuid.toString(), bounties);
		}
	}
}