package net.Indyuce.bountyhunters.gui;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.language.Language;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import net.Indyuce.bountyhunters.version.VersionMaterial;

public class Leaderboard extends PluginInventory {
	private static final int[] slots = { 13, 21, 22, 23, 29, 30, 31, 32, 33, 37, 38, 39, 40, 41, 42, 43 };

	public Leaderboard(Player player) {
		super(player);
	}

	@Override
	public Inventory getInventory() {
		Inventory inv = Bukkit.createInventory(this, 54, Language.LEADERBOARD_GUI_NAME.format());

		int slot = 0;
		for (UUID uuid : BountyHunters.getInstance().getHunterLeaderboard().getCachedLeaderboard()) {
			if (slot > slots.length)
				break;

			PlayerData data = BountyHunters.getInstance().getPlayerDataManager().get(uuid);
			ItemStack skull = CustomItem.LB_PLAYER_DATA.toItemStack();
			SkullMeta meta = (SkullMeta) skull.getItemMeta();

			final int slot1 = slot;
			Bukkit.getScheduler().runTaskAsynchronously(BountyHunters.getInstance(), () -> {
				BountyHunters.getInstance().getVersionWrapper().setOwner(meta, Bukkit.getOfflinePlayer(data.getUniqueId()));
				inv.getItem(slots[slot1]).setItemMeta(meta);
			});

			meta.setDisplayName(applyPlaceholders(meta.getDisplayName(), data, slot + 1));
			List<String> lore = meta.getLore();
			for (int j = 0; j < lore.size(); j++)
				lore.set(j, applyPlaceholders(lore.get(j), data, slot + 1));
			meta.setLore(lore);
			skull.setItemMeta(meta);

			inv.setItem(slots[slot++], skull);
		}

		ItemStack glass = VersionMaterial.RED_STAINED_GLASS_PANE.toItem();
		ItemMeta glassMeta = glass.getItemMeta();
		glassMeta.setDisplayName(Language.NO_PLAYER.format());
		glass.setItemMeta(glassMeta);

		while (slot < slots.length)
			inv.setItem(slots[slot++], glass);

		return inv;
	}

	@Override
	public void whenClicked(ItemStack item, InventoryAction action, int slot) {
	}

	private String applyPlaceholders(String str, PlayerData playerData, int rank) {
		String title = playerData.hasTitle() ? playerData.getTitle().format() : Language.NO_TITLE.format();

		str = str.replace("{level}", "" + playerData.getLevel());
		str = str.replace("{bounties}", "" + playerData.getClaimedBounties());
		str = str.replace("{successful_bounties}", "" + playerData.getSuccessfulBounties());
		str = str.replace("{title}", title);
		str = str.replace("{name}", playerData.getOfflinePlayer().getName());
		str = str.replace("{rank}", "" + rank);

		return str;
	}
}