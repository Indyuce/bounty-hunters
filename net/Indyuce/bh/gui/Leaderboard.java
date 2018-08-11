package net.Indyuce.bh.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.collect.Ordering;

import net.Indyuce.bh.Main;
import net.Indyuce.bh.api.PlayerData;
import net.Indyuce.bh.util.Utils;

public class Leaderboard implements PluginInventory {
	private Player player;

	public Leaderboard(Player player) {
		this.player = player;
	}

	@Override
	@SuppressWarnings("deprecation")
	public Inventory getInventory() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int[] slots = new int[] { 13, 21, 22, 23, 29, 30, 31, 32, 33, 37, 38, 39, 40, 41, 42, 43 };

		File f = new File(Main.plugin.getDataFolder(), "userdata");
		for (File f1 : f.listFiles()) {
			UUID uuid;
			try {
				uuid = UUID.fromString(f1.getName().replace(".yml", ""));
			} catch (Exception e) {
				continue;
			}

			OfflinePlayer t = Bukkit.getOfflinePlayer(uuid);
			if (t != null)
				map.put(t.getName(), PlayerData.get(t).getInt("claimed-bounties"));
		}

		List<Integer> order = Ordering.natural().greatestOf(map.values(), 20);

		Inventory inv = Bukkit.createInventory(this, 54, Utils.msg("leaderboard-gui-name"));
		int slot = 0;
		while (slot < slots.length && slot < order.size()) {
			String s = getKeyByValue(map, order.get(slot));
			if (s == null) {
				slot++;
				continue;
			}

			map.remove(s);
			if (order.get(slot) <= 0) {
				slot++;
				continue;
			}

			PlayerData playerData = PlayerData.get(Bukkit.getOfflinePlayer(s));

			ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta meta = (SkullMeta) i.getItemMeta();
			meta.addItemFlags(ItemFlag.values());
			meta.setOwner(s);
			meta.setDisplayName(ChatColor.GREEN + "[" + (slot + 1) + "] " + s);
			List<String> lore = new ArrayList<String>();
			if (!playerData.getString("current-title").equals(""))
				lore.add(ChatColor.GRAY + Utils.msg("leaderboard-gui-title").replace("%title%", ChatColor.translateAlternateColorCodes('&', Utils.applySpecialChars(playerData.getString("current-title")))));
			lore.add(ChatColor.GRAY + Utils.msg("leaderboard-gui-completed-bounties").replace("%bounties%", "" + order.get(slot)));
			lore.add(ChatColor.GRAY + Utils.msg("leaderboard-gui-level").replace("%level%", "" + playerData.getInt("level")));
			meta.setLore(lore);
			i.setItemMeta(meta);

			inv.setItem(slots[slot], i);
			slot++;
		}

		ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		ItemMeta glassMeta = glass.getItemMeta();
		glassMeta.setDisplayName(Utils.msg("no-player"));
		glass.setItemMeta(glassMeta);

		for (int j = 0; j < slots.length; j++)
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
}