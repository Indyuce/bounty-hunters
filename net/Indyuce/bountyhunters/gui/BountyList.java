package net.Indyuce.bountyhunters.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.BountyUtils;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.BountyManager;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.Message;
import net.Indyuce.bountyhunters.api.PlayerData;
import net.Indyuce.bountyhunters.api.event.HunterTargetEvent;
import net.Indyuce.bountyhunters.listener.Alerts;
import net.Indyuce.bountyhunters.version.VersionSound;
import net.Indyuce.bountyhunters.version.nms.ItemTag;

public class BountyList implements PluginInventory {
	private static HashMap<UUID, Long> lastTarget = new HashMap<UUID, Long>();

	private Player player;
	private int page;

	public BountyList(Player player, int page) {
		this.player = player;
		this.page = page;
	}

	@Override
	public Inventory getInventory() {
		BountyManager bountyManager = BountyHunters.getBountyManager();
		List<Bounty> bounties = new ArrayList<Bounty>(bountyManager.getBounties());
		int[] slots = new int[] { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34 };
		int maxPage = getMaxPage();

		Inventory inv = Bukkit.createInventory(this, 54, Message.GUI_NAME.formatRaw("%page%", "" + page, "%max-page%", "" + maxPage));
		int min = (page - 1) * 21;
		int max = page * 21;

		for (int j = min; j < max && j < bounties.size(); j++) {
			Bounty bounty = bounties.get(j);
			ItemStack i = CustomItem.GUI_PLAYER_HEAD.a();
			SkullMeta meta = (SkullMeta) i.getItemMeta();
			if (BountyHunters.plugin.getConfig().getBoolean("display-player-skulls"))
				meta.setOwner(bounty.getTarget().getName());
			meta.setDisplayName(meta.getDisplayName().replace("%name%", bounty.getTarget().getName()));
			List<String> lore = meta.getLore();

			String creatorString = !bounty.hasCreator() ? Message.THUG_PLAYER.formatRaw(ChatColor.RED) : (bounty.hasCreator(player) ? Message.SET_BY_YOURSELF.formatRaw(ChatColor.GRAY) : Message.SET_BY.formatRaw(ChatColor.GRAY, "%creator%", bounty.getCreator().getName()));
			insertInLore(lore, "bounty-creator", creatorString);

			String rewardString = Message.REWARD_IS.formatRaw(ChatColor.GRAY, "%reward%", BountyUtils.format(bounty.getReward()));
			insertInLore(lore, "bounty-reward", rewardString);

			String huntersString = Message.CURRENT_HUNTERS.formatRaw(ChatColor.GRAY, "%hunters%", "" + bounty.getHunters().size());
			insertInLore(lore, "bounty-hunters", huntersString);

			String statusString = bounty.getTarget().getName().equals(player.getName()) ? Message.DONT_LET_THEM_KILL_U.formatRaw(ChatColor.RED) : (!bounty.hasCreator() ? Message.KILL_HIM_CLAIM_BOUNTY.formatRaw(ChatColor.YELLOW) : (bounty.hasCreator(player) ? Message.RIGHT_CLICK_REMOVE_BOUNTY.formatRaw(ChatColor.YELLOW) : Message.KILL_HIM_CLAIM_BOUNTY.formatRaw(ChatColor.YELLOW)));
			insertInLore(lore, "bounty-instruction", statusString);

			String compassString = bounty.hasHunter(player) ? Message.CLICK_UNTARGET.formatRaw(ChatColor.RED) : Message.CLICK_TARGET.formatRaw(ChatColor.YELLOW);
			if (!BountyHunters.plugin.getConfig().getBoolean("compass.enabled") || bounty.hasTarget(player))
				insertInLore(lore, "compass-instruction");
			else
				insertInLore(lore, "compass-instruction", compassString);

			meta.setLore(lore);
			i.setItemMeta(meta);

			i = BountyHunters.getNMS().addTag(i, new ItemTag("playerUuid", bounty.getTarget().getUniqueId().toString()));

			inv.setItem(slots[j - min], i);
		}

		ItemStack compass = CustomItem.BOUNTY_COMPASS.a().clone();
		ItemMeta compassMeta = compass.getItemMeta();
		List<String> compassLore = compassMeta.getLore();
		compassLore.add("");
		compassLore.add(Message.CLICK_BUY_COMPASS.formatRaw(ChatColor.YELLOW, "%price%", BountyUtils.format(BountyHunters.plugin.getConfig().getDouble("compass.price"))));
		compassMeta.setLore(compassLore);
		compass.setItemMeta(compassMeta);

		inv.setItem(26, CustomItem.NEXT_PAGE.a());
		inv.setItem(18, CustomItem.PREVIOUS_PAGE.a());

		PlayerData playerData = PlayerData.get(player);
		inv.setItem(47, playerData.getProfileItem());
		inv.setItem(49, CustomItem.SET_BOUNTY.a());

		if (BountyHunters.plugin.getConfig().getBoolean("compass.enabled"))
			inv.setItem(51, compass);

		return inv;
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void whenClicked(ItemStack i, InventoryAction action, int slot) {

		// next page
		if (i.getItemMeta().getDisplayName().equals(CustomItem.NEXT_PAGE.a().getItemMeta().getDisplayName()))
			if (page < getMaxPage())
				new BountyList(player, page + 1).open();

		// prev page
		if (i.getItemMeta().getDisplayName().equals(CustomItem.PREVIOUS_PAGE.a().getItemMeta().getDisplayName()))
			if (page > 1)
				new BountyList(player, page - 1).open();

		// buy bounty compass
		if (i.getItemMeta().getDisplayName().equals(CustomItem.BOUNTY_COMPASS.a().getItemMeta().getDisplayName())) {
			if (player.getInventory().firstEmpty() <= -1) {
				Message.EMPTY_INV_FIRST.format(ChatColor.RED).send(player);
				return;
			}

			double price = BountyHunters.plugin.getConfig().getDouble("compass.price");
			if (BountyHunters.getEconomy().getBalance(player) < price) {
				Message.NOT_ENOUGH_MONEY.format(ChatColor.RED).send(player);
				return;
			}

			BountyHunters.getEconomy().withdrawPlayer(player, price);
			player.playSound(player.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
			player.getInventory().addItem(CustomItem.BOUNTY_COMPASS.a());
			return;
		}

		// target someone
		BountyManager bountyManager = BountyHunters.getBountyManager();
		if (action == InventoryAction.PICKUP_ALL && BountyHunters.plugin.getConfig().getBoolean("compass.enabled"))
			if (slot < 35 && i.getType() == Material.SKULL_ITEM && !i.getItemMeta().getDisplayName().equals(CustomItem.PLAYER_HEAD.a().getItemMeta().getDisplayName().replace("%name%", player.getName()))) {
				String tag = BountyHunters.getNMS().getStringTag(i, "playerUuid");
				if (tag == null || tag.equals(""))
					return;

				OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(tag));

				Bounty bounty = bountyManager.getBounty(t);

				if (bounty.hasHunter(player)) {
					bounty.removeHunter(player);
					Message.TARGET_REMOVED.format(ChatColor.YELLOW).send(player);
				} else {

					// permission check
					if (BountyHunters.getPermission().playerHas(null, t, "bountyhunters.untargetable") && !player.hasPermission("bountyhunters.untargetable.bypass")) {
						Message.TRACK_IMUN.format(ChatColor.YELLOW).send(player);
						return;
					}

					// event check
					HunterTargetEvent event = new HunterTargetEvent(player, t);
					Bukkit.getPluginManager().callEvent(event);
					if (event.isCancelled())
						return;

					// check for target cooldown
					long lastTarget = BountyList.lastTarget.containsKey(player.getUniqueId()) ? BountyList.lastTarget.get(player.getUniqueId()) : 0;
					long remain = (long) (lastTarget + BountyHunters.plugin.getConfig().getDouble("compass.target-cooldown") * 1000 - System.currentTimeMillis()) / 1000;
					if (remain > 0) {
						Message.TARGET_COOLDOWN.format(ChatColor.RED, "%remain%", "" + remain, "%s%", remain >= 2 ? "s" : "").send(player);
						return;
					}

					BountyList.lastTarget.put(player.getUniqueId(), System.currentTimeMillis());

					// remove older hunter
					if (BountyHunters.getHuntManager().isHunting(player))
						BountyHunters.getHuntManager().getTargetBounty(player).removeHunter(player);

					bounty.addHunter(player);
					if (t.isOnline())
						Alerts.newHunter(t.getPlayer(), player);
					Message.TARGET_SET.format(ChatColor.YELLOW).send(player);
				}

				new BountyList(player, page).open();
			}

		// remove bounty
		if (action == InventoryAction.PICKUP_HALF)
			if (slot < 35 && i.getType() == Material.SKULL_ITEM) {
				String tag = BountyHunters.getNMS().getStringTag(i, "playerUuid");
				if (tag == null || tag.equals(""))
					return;

				OfflinePlayer t = Bukkit.getOfflinePlayer(UUID.fromString(tag));
				if (t == null)
					return;

				// check for creator
				Bounty bounty = bountyManager.getBounty(t);
				if (!bounty.hasCreator())
					return;

				if (!bounty.hasCreator(player))
					return;

				double cashback = bounty.getReward();

				// gives the players the money back if upped the bounty
				for (UUID up : bounty.getPlayersWhoIncreased()) {
					OfflinePlayer offline = Bukkit.getOfflinePlayer(up);
					double given = bounty.getIncreaseAmount(offline);
					BountyHunters.getEconomy().depositPlayer(offline, given);
					cashback -= given;
				}

				BountyHunters.getEconomy().depositPlayer(player, cashback);
				Alerts.bountyExpired(bounty);
				bounty.unregister();

				new BountyList(player, page).open();
			}
	}

	public int getMaxPage() {
		return Math.max(1, (int) Math.ceil(((double) BountyHunters.getBountyManager().getBounties().size()) / 21d));
	}

	private void insertInLore(List<String> lore, String path, String... add) {
		if (!lore.contains(ChatColor.GRAY + "%" + path + "%"))
			return;

		int index = lore.indexOf(ChatColor.GRAY + "%" + path + "%");
		for (String add1 : add)
			lore.add(index + 1, add1);
		lore.remove(index);
	}
}