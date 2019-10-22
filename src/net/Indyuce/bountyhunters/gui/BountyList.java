package net.Indyuce.bountyhunters.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.CustomItem.Builder;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.event.BountyExpireEvent;
import net.Indyuce.bountyhunters.api.event.HunterTargetEvent;
import net.Indyuce.bountyhunters.api.language.Language;
import net.Indyuce.bountyhunters.api.language.Message;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import net.Indyuce.bountyhunters.version.VersionMaterial;
import net.Indyuce.bountyhunters.version.wrapper.api.ItemTag;
import net.Indyuce.bountyhunters.version.wrapper.api.NBTItem;

public class BountyList extends PluginInventory {
	private final PlayerData data;

	private int page = 1;

	private static final int[] slots = { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34 };

	public BountyList(Player player) {
		super(player);

		data = BountyHunters.getInstance().getPlayerDataManager().get(player);
	}

	@Override
	public Inventory getInventory() {
		List<Bounty> bounties = new ArrayList<>(BountyHunters.getInstance().getBountyManager().getBounties());
		int maxPage = getMaxPage();

		Inventory inv = Bukkit.createInventory(this, 54, Language.GUI_NAME.format("page", "" + page, "max_page", "" + maxPage));
		int min = (page - 1) * 21;
		int max = page * 21;

		for (int j = min; j < max && j < bounties.size(); j++) {
			final int index = j - min;

			Bounty bounty = bounties.get(j);
			Builder builder = CustomItem.GUI_PLAYER_HEAD.newBuilder();
			boolean isTarget = bounty.hasTarget(player), isCreator = bounty.hasCreator(player), isHunter = bounty.hasHunter(player), noCreator = !bounty.hasCreator();
			builder.applyConditions(new String[] { "noCreator", "isCreator", "extraCreator", "isExtra", "isTarget", "isHunter", "!isHunter" }, new boolean[] { !bounty.hasCreator(), isCreator, !noCreator && !isCreator, !isTarget && !isCreator, isTarget, !isTarget && isHunter, !isTarget && !isHunter });
			builder.applyPlaceholders("target", bounty.getTarget().getName(), "creator", bounty.hasCreator() ? bounty.getCreator().getName() : "Server", "reward", "" + new NumberFormat().format(bounty.getReward()), "contributors", "" + bounty.getContributors().size(), "hunters", "" + bounty.getHunters().size());
			ItemStack item = NBTItem.get(builder.build()).addTag(new ItemTag("playerUuid", bounty.getTarget().getUniqueId().toString())).toItem();

			SkullMeta meta = (SkullMeta) item.getItemMeta();
			Bukkit.getScheduler().runTaskAsynchronously(BountyHunters.getInstance(), () -> {
				BountyHunters.getInstance().getVersionWrapper().setOwner(meta, bounty.getTarget());
				inv.getItem(slots[index]).setItemMeta(meta);
			});
			item.setItemMeta(meta);
			inv.setItem(slots[index], item);
		}

		if (BountyHunters.getInstance().getConfig().getBoolean("player-tracking.enabled")) {
			ItemStack compass = CustomItem.BOUNTY_COMPASS.toItemStack().clone();
			ItemMeta compassMeta = compass.getItemMeta();
			List<String> compassLore = compassMeta.getLore();
			compassLore.add("");
			compassLore.add(Language.CLICK_BUY_COMPASS.format("price", new NumberFormat().format(BountyHunters.getInstance().getConfig().getDouble("player-tracking.price"))));
			compassMeta.setLore(compassLore);
			compass.setItemMeta(compassMeta);

			inv.setItem(51, compass);
		}

		inv.setItem(26, CustomItem.NEXT_PAGE.toItemStack());
		inv.setItem(18, CustomItem.PREVIOUS_PAGE.toItemStack());

		ItemStack profile = data.getProfileItem();
		Bukkit.getScheduler().runTaskAsynchronously(BountyHunters.getInstance(), () -> {
			SkullMeta meta = (SkullMeta) profile.getItemMeta();
			BountyHunters.getInstance().getVersionWrapper().setOwner(meta, player);
			inv.getItem(47).setItemMeta(meta);
		});

		inv.setItem(47, profile);
		inv.setItem(49, CustomItem.SET_BOUNTY.toItemStack());

		return inv;
	}

	public int getMaxPage() {
		return Math.max(1, (int) Math.ceil(((double) BountyHunters.getInstance().getBountyManager().getBounties().size()) / 21d));
	}

	@Override
	public void whenClicked(ItemStack item, InventoryAction action, int slot) {

		// next page
		if (item.getItemMeta().getDisplayName().equals(CustomItem.NEXT_PAGE.toItemStack().getItemMeta().getDisplayName()))
			if (page < getMaxPage()) {
				page++;
				open();
			}

		// prev page
		if (item.getItemMeta().getDisplayName().equals(CustomItem.PREVIOUS_PAGE.toItemStack().getItemMeta().getDisplayName()))
			if (page > 1) {
				page--;
				open();
			}

		// buy bounty compass
		if (item.getItemMeta().getDisplayName().equals(CustomItem.BOUNTY_COMPASS.toItemStack().getItemMeta().getDisplayName())) {
			if (player.getInventory().firstEmpty() <= -1) {
				Message.EMPTY_INV_FIRST.format().send(player);
				return;
			}

			if (!player.hasPermission(BountyHunters.getInstance().getConfig().getString("player-tracking.permission"))) {
				Message.NOT_ENOUGH_PERMS.format().send(player);
				return;
			}

			double price = BountyHunters.getInstance().getConfig().getDouble("player-tracking.price");
			if (BountyHunters.getInstance().getEconomy().getBalance(player) < price) {
				Message.NOT_ENOUGH_MONEY.format().send(player);
				return;
			}

			BountyHunters.getInstance().getEconomy().withdrawPlayer(player, price);
			Message.BOUGHT_COMPASS.format().send(player);
			player.getInventory().addItem(CustomItem.BOUNTY_COMPASS.toItemStack());
			return;
		}

		// target someone
		if (action == InventoryAction.PICKUP_ALL && BountyHunters.getInstance().getConfig().getBoolean("player-tracking.enabled"))
			if (slot < 35 && item.getType() == VersionMaterial.PLAYER_HEAD.toMaterial()) {
				String tag = NBTItem.get(item).getString("playerUuid");
				if (tag == null || tag.equals(""))
					return;

				OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(tag));
				Bounty bounty = BountyHunters.getInstance().getBountyManager().getBounty(target);

				if (bounty.hasHunter(player)) {
					bounty.removeHunter(player);
					Message.TARGET_REMOVED.format().send(player);
				} else {

					// permission check
					if (BountyHunters.getInstance().getPermission().playerHas(null, target, "bountyhunters.untargetable") && !player.hasPermission("bountyhunters.untargetable.bypass")) {
						Message.TRACK_IMUN.format().send(player);
						return;
					}

					/*
					 * check the player who wants to hunt the bounty target has
					 * not created the bounty.
					 */
					if (bounty.hasCreator(player) && !BountyHunters.getInstance().getConfig().getBoolean("own-bounty-claiming")) {
						Message.CANT_TRACK_CREATOR.format().send(player);
						return;
					}

					// player can't track himself
					if (bounty.hasTarget(player))
						return;

					// check for target cooldown
					long remain = (long) (data.getLastTarget() + BountyHunters.getInstance().getConfig().getDouble("player-tracking.cooldown") * 1000 - System.currentTimeMillis()) / 1000;
					if (remain > 0) {
						Message.TARGET_COOLDOWN.format("remain", "" + remain, "s", remain >= 2 ? "s" : "").send(player);
						return;
					}

					// event check
					HunterTargetEvent hunterEvent = new HunterTargetEvent(player, target);
					Bukkit.getPluginManager().callEvent(hunterEvent);
					if (hunterEvent.isCancelled())
						return;

					data.setLastTarget();

					bounty.addHunter(player);
					if (target.isOnline())
						hunterEvent.sendAllert(target.getPlayer());
					Message.TARGET_SET.format().send(player);
				}

				open();
			}

		// remove bounty
		if (action == InventoryAction.PICKUP_HALF)
			if (slot < 35 && item.getType() == VersionMaterial.PLAYER_HEAD.toMaterial()) {
				String tag = NBTItem.get(item).getString("playerUuid");
				if (tag == null || tag.equals(""))
					return;

				Bounty bounty = BountyHunters.getInstance().getBountyManager().getBounty(UUID.fromString(tag));
				if (!bounty.hasContributed(player) || !player.hasPermission("bountyhunters.remove"))
					return;

				BountyExpireEvent bountyEvent = new BountyExpireEvent(bounty, player);
				Bukkit.getPluginManager().callEvent(bountyEvent);
				if (bountyEvent.isCancelled())
					return;

				double tax = Math.max(0, Math.min(1, BountyHunters.getInstance().getConfig().getDouble("bounty-tax.bounty-removal") / 100));
				BountyHunters.getInstance().getEconomy().depositPlayer(player, bountyEvent.getAmountRemoved() * (1 - tax));
				
				if (bountyEvent.isExpiring())
					BountyHunters.getInstance().getBountyManager().unregisterBounty(bounty);
				bounty.removeContribution(player);
				bountyEvent.sendAllert();

				open();
			}
	}
}