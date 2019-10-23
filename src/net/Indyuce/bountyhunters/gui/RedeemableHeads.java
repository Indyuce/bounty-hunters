package net.Indyuce.bountyhunters.gui;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.BountyUtils;
import net.Indyuce.bountyhunters.api.language.Language;
import net.Indyuce.bountyhunters.api.language.Message;
import net.Indyuce.bountyhunters.api.player.PlayerData;

public class RedeemableHeads extends PluginInventory {
	private final PlayerData playerData;

	private static final int[] slots = { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43 };

	public RedeemableHeads(Player player) {
		super(player);

		playerData = BountyHunters.getInstance().getPlayerDataManager().get(player);
	}

	@Override
	public Inventory getInventory() {
		Inventory inv = Bukkit.createInventory(this, 54, Language.REDEEMABLE_HEADS.format());

		int n = 0;
		for (UUID uuid : playerData.getRedeemableHeads())
			inv.setItem(slots[n++], BountyHunters.getInstance().getVersionWrapper().getHead(Bukkit.getOfflinePlayer(uuid)));

		return inv;
	}

	@Override
	public void whenClicked(ItemStack item, InventoryAction action, int slot) {

		if (!BountyUtils.hasItemMeta(item, false) || !(item.getItemMeta() instanceof SkullMeta))
			return;

		if (player.getInventory().firstEmpty() == -1) {
			Message.EMPTY_INV_FIRST.format().send(player);
			return;
		}

		/*
		 * safe check that should never really be useful. if it is not verified,
		 * open inventory back which should fix the display bug
		 */
		OfflinePlayer owner = ((SkullMeta) item.getItemMeta()).getOwningPlayer();
		if (!playerData.getRedeemableHeads().contains(owner.getUniqueId())) {
			open();
			return;
		}

		player.getInventory().addItem(item);
		playerData.removeRedeemableHead(owner.getUniqueId());
		Message.REDEEM_HEAD.format("target", owner.getName()).send(player);
		open();
	}
}
