package net.Indyuce.bountyhunters.gui;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.collect.Ordering;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.PlayerData;

public class Leaderboard implements PluginInventory {
	private Player player;

	public Leaderboard(Player player) {
		this.player = player;
	}

	@Override
	public Inventory getInventory() {
		HashMap<PlayerData, Integer> map = new HashMap<PlayerData, Integer>();
		int[] slots = new int[] { 13, 21, 22, 23, 29, 30, 31, 32, 33, 37, 38, 39, 40, 41, 42, 43 };

		File f = new File(BountyHunters.plugin.getDataFolder(), "userdata");
		for (File f1 : f.listFiles()) {
			UUID uuid;
			try {
				uuid = UUID.fromString(f1.getName().replace(".yml", ""));
			} catch (Exception e) {
				continue;
			}

			OfflinePlayer t = Bukkit.getOfflinePlayer(uuid);
			if (t != null) {
				PlayerData playerData = PlayerData.get(t);
				map.put(playerData, playerData.getClaimedBounties());
			}
		}

		List<Integer> order = Ordering.natural().greatestOf(map.values(), 20);

		Inventory inv = Bukkit.createInventory(this, 54, Message.LEADERBOARD_GUI_NAME.getUpdated());
		int slot = 0;
		while (slot < slots.length && slot < order.size()) {
			PlayerData playerData = getKeyByValue(map, order.get(slot));
			if (playerData == null) {
				slot++;
				continue;
			}

			map.remove(playerData);
			if (order.get(slot) <= 0) {
				slot++;
				continue;
			}

			ItemStack i = CustomItem.LB_PLAYER_DATA.a();
			SkullMeta meta = (SkullMeta) i.getItemMeta();
			if (BountyHunters.plugin.getConfig().getBoolean("display-player-skulls"))
				meta.setOwner(playerData.getPlayerName());
			meta.setDisplayName(applyPlaceholders(meta.getDisplayName(), playerData, slot + 1));

			List<String> lore = meta.getLore();
			for (int j = 0; j < lore.size(); j++)
				lore.set(j, applyPlaceholders(lore.get(j), playerData, slot + 1));

			meta.setLore(lore);
			i.setItemMeta(meta);

			inv.setItem(slots[slot], i);
			slot++;
		}

		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		ItemMeta glassMeta = glass.getItemMeta();
		glassMeta.setDisplayName(Message.NO_PLAYER.getUpdated());
		glass.setItemMeta(glassMeta);

		for (int j = 0; j < slots.length; j++)
			if (inv.getItem(slots[j]) == null)
				inv.setItem(slots[j], glass);

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

	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet())
			if (value.equals(entry.getValue()))
				return entry.getKey();
		return null;
	}

	@Override
	public void whenClicked(ItemStack i, InventoryAction action, int slot) {
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