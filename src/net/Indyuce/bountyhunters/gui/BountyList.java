package net.Indyuce.bountyhunters.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
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
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.PlayerData;
import net.Indyuce.bountyhunters.api.event.BountyExpireEvent;
import net.Indyuce.bountyhunters.api.event.BountyExpireEvent.BountyExpireCause;
import net.Indyuce.bountyhunters.api.event.HunterTargetEvent;
import net.Indyuce.bountyhunters.version.VersionMaterial;
import net.Indyuce.bountyhunters.version.wrapper.ItemTag;

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

		Inventory inv = Bukkit.createInventory(this, 54, Message.GUI_NAME.formatRaw("%page%", "" + page, "%max-page%", "" + maxPage));
		int min = (page - 1) * 21;
		int max = page * 21;

		for (int j = min; j < max && j < bounties.size(); j++) {
			Bounty bounty = bounties.get(j);
			Builder builder = CustomItem.GUI_PLAYER_HEAD.newBuilder();
			boolean isTarget = bounty.hasTarget(player), isCreator = bounty.hasCreator(player), isHunter = bounty.hasHunter(player), noCreator = !bounty.hasCreator();
			builder.applyConditions(new String[] { "noCreator", "isCreator", "extraCreator", "isExtra", "isTarget", "isHunter", "!isHunter" }, new boolean[] { !bounty.hasCreator(), isCreator, !noCreator && !isCreator, !isTarget && !isCreator, isTarget, !isTarget && isHunter, !isTarget && !isHunter });
			builder.applyPlaceholders("target", bounty.getTarget().getName(), "creator", bounty.hasCreator() ? bounty.getCreator().getName() : "Server", "reward", "" + new NumberFormat().format(bounty.getReward()), "hunters", "" + bounty.getHunters().size());
			ItemStack item = builder.build();

			SkullMeta meta = (SkullMeta) item.getItemMeta();
			if (BountyHunters.getInstance().getConfig().getBoolean("display-player-skulls"))
				BountyHunters.getInstance().getVersionWrapper().setOwner(meta, bounty.getTarget());
			item.setItemMeta(meta);
			inv.setItem(slots[j - min], BountyHunters.getInstance().getVersionWrapper().addTag(item, new ItemTag("playerUuid", bounty.getTarget().getUniqueId().toString())));
		}

		if (BountyHunters.getInstance().getConfig().getBoolean("player-tracking.enabled")) {
			ItemStack compass = CustomItem.BOUNTY_COMPASS.toItemStack().clone();
			ItemMeta compassMeta = compass.getItemMeta();
			List<String> compassLore = compassMeta.getLore();
			compassLore.add("");
			compassLore.add(Message.CLICK_BUY_COMPASS.formatRaw(ChatColor.YELLOW, "%price%", new NumberFormat().format(BountyHunters.getInstance().getConfig().getDouble("player-tracking.price"))));
			compassMeta.setLore(compassLore);
			compass.setItemMeta(compassMeta);

			inv.setItem(51, compass);
		}

		inv.setItem(26, CustomItem.NEXT_PAGE.toItemStack());
		inv.setItem(18, CustomItem.PREVIOUS_PAGE.toItemStack());

		inv.setItem(47, data.getProfileItem());
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
				Message.EMPTY_INV_FIRST.format(ChatColor.RED).send(player);
				return;
			}

			if (!player.hasPermission(BountyHunters.getInstance().getConfig().getString("player-tracking.permission"))) {
				Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(player);
				return;
			}

			double price = BountyHunters.getInstance().getConfig().getDouble("player-tracking.price");
			if (BountyHunters.getInstance().getEconomy().getBalance(player) < price) {
				Message.NOT_ENOUGH_MONEY.format(ChatColor.RED).send(player);
				return;
			}

			BountyHunters.getInstance().getEconomy().withdrawPlayer(player, price);
			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
			player.getInventory().addItem(CustomItem.BOUNTY_COMPASS.toItemStack());
			return;
		}

		// target someone
		if (action == InventoryAction.PICKUP_ALL && BountyHunters.getInstance().getConfig().getBoolean("player-tracking.enabled"))
			if (slot < 35 && item.getType() == VersionMaterial.PLAYER_HEAD.toMaterial()) {
				String tag = BountyHunters.getInstance().getVersionWrapper().getStringTag(item, "playerUuid");
				if (tag == null || tag.equals(""))
					return;

				OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(tag));
				Bounty bounty = BountyHunters.getInstance().getBountyManager().getBounty(target);

				if (bounty.hasHunter(player)) {
					bounty.removeHunter(player);
					Message.TARGET_REMOVED.format(ChatColor.YELLOW).send(player);
				} else {

					// permission check
					if (BountyHunters.getInstance().getPermission().playerHas(null, target, "bountyhunters.untargetable") && !player.hasPermission("bountyhunters.untargetable.bypass")) {
						Message.TRACK_IMUN.format(ChatColor.RED).send(player);
						return;
					}

					/*
					 * check the player who wants to hunt the bounty target has
					 * not created the bounty.
					 */
					if (bounty.hasCreator(player) && !BountyHunters.getInstance().getConfig().getBoolean("own-bounty-claiming")) {
						Message.CANT_TRACK_CREATOR.format(ChatColor.RED).send(player);
						return;
					}

					// player can't track himself
					if (bounty.hasTarget(player))
						return;

					// check for target cooldown
					long remain = (long) (data.getLastTarget() + BountyHunters.getInstance().getConfig().getDouble("player-tracking.cooldown") * 1000 - System.currentTimeMillis()) / 1000;
					if (remain > 0) {
						Message.TARGET_COOLDOWN.format(ChatColor.RED, "%remain%", "" + remain, "%s%", remain >= 2 ? "s" : "").send(player);
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
					Message.TARGET_SET.format(ChatColor.YELLOW).send(player);
				}

				open();
			}

		// remove bounty
		if (action == InventoryAction.PICKUP_HALF)
			if (slot < 35 && item.getType() == VersionMaterial.PLAYER_HEAD.toMaterial()) {
				String tag = BountyHunters.getInstance().getVersionWrapper().getStringTag(item, "playerUuid");
				if (tag == null || tag.equals(""))
					return;

				OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(tag));
				if (t == null)
					return;

				// check for creator
				Bounty bounty = BountyHunters.getInstance().getBountyManager().getBounty(t);
				if (!bounty.hasCreator())
					return;

				if (!bounty.hasCreator(player))
					return;

				BountyExpireEvent bountyEvent = new BountyExpireEvent(bounty, BountyExpireCause.CREATOR);
				Bukkit.getPluginManager().callEvent(bountyEvent);

				double cashback = bounty.getReward();

				// gives the players the money back if upped the bounty
				for (UUID up : bounty.getPlayersWhoIncreased()) {
					OfflinePlayer offline = Bukkit.getOfflinePlayer(up);
					double given = bounty.getIncreaseAmount(offline);
					BountyHunters.getInstance().getEconomy().depositPlayer(offline, given);
					cashback -= given;
				}

				BountyHunters.getInstance().getEconomy().depositPlayer(player, cashback);
				bountyEvent.sendAllert();
				BountyHunters.getInstance().getBountyManager().unregisterBounty(bounty);

				open();
			}
	}
}