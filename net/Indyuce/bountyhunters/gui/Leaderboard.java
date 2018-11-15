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

public class Leaderboard implements PluginInventory {
	private Player player;

	private static final int[] slots = { 13, 21, 22, 23, 29, 30, 31, 32, 33, 37, 38, 39, 40, 41, 42, 43 };

	public Leaderboard(Player player) {
		this.player = player;
	}

	@Override
	public Inventory getInventory() {

		/*
		 * instead of calculating each player data to check what players has the
		 * most claimed bounties, players will the highest bounties are cached
		 * in a config file (cache/leaderboard.yml) and the 20 best can be
		 * accessed directly using that file.
		 */
		Map<PlayerData, Integer> hunters = new HashMap<PlayerData, Integer>();
		for (String key : BountyHunters.getCachedLeaderboard().getKeys(false)) {
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
			ItemStack skull = CustomItem.LB_PLAYER_DATA.a();
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			if (BountyHunters.plugin.getConfig().getBoolean("display-player-skulls"))
				meta.setOwner(data.getPlayerName());
			meta.setDisplayName(applyPlaceholders(meta.getDisplayName(), data, slot + 1));
			List<String> lore = meta.getLore();
			for (int j = 0; j < lore.size(); j++)
				lore.set(j, applyPlaceholders(lore.get(j), data, slot + 1));
			meta.setLore(lore);
			skull.setItemMeta(meta);

			inv.setItem(slots[slot++], skull);
		}

		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		ItemMeta glassMeta = glass.getItemMeta();
		glassMeta.setDisplayName(Message.NO_PLAYER.getUpdated());
		glass.setItemMeta(glassMeta);

		while (slot < slots.length)
			inv.setItem(slots[slot++], glass);

		return inv;
	}

	@Override
	public int getPage() {
		return 0;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	private LinkedHashMap<PlayerData, Integer> sortByBounties(Map<PlayerData, Integer> map) {
		List<Entry<PlayerData, Integer>> list = new ArrayList<>(map.entrySet());
		list.sort(Entry.comparingByValue());

		LinkedHashMap<PlayerData, Integer> result = new LinkedHashMap<PlayerData, Integer>();
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
		s = s.replace("%name%", playerData.getPlayerName());
		s = s.replace("%rank%", "" + rank);

		return s;
	}
}