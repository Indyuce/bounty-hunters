package net.Indyuce.bountyhunters.gui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.bountyhunters.api.AltChar;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.gui.api.ChatInput;
import net.Indyuce.bountyhunters.version.VersionMaterial;
import net.Indyuce.bountyhunters.version.wrapper.api.ItemTag;
import net.Indyuce.bountyhunters.version.wrapper.api.NBTItem;

public class BountyEditor extends PluginInventory {
	private final Bounty bounty;

	private int offset;

	private static final DecimalFormat digit = new DecimalFormat("0.#");
	private static final int[] slots = { 20, 21, 22, 23, 24 };

	public BountyEditor(Player player, Bounty bounty) {
		super(player);

		this.bounty = bounty;
	}

	@Override
	public Inventory getInventory() {
		Inventory inv = Bukkit.createInventory(this, 54, "Bounty Editor: " + bounty.getTarget().getName());
		double reward = bounty.getReward();

		ItemStack book = new ItemStack(Material.BOOK);
		ItemMeta bookMeta = book.getItemMeta();
		bookMeta.setDisplayName(ChatColor.GOLD + genetive(bounty.getTarget().getName()) + "Bounty");
		List<String> bookLore = new ArrayList<>();
		bookLore.add("");
		bookLore.add(ChatColor.GRAY + "Reward: " + ChatColor.GOLD + "$" + new NumberFormat().format(reward));
		bookLore.add(ChatColor.GRAY + "  - From Players: " + ChatColor.GOLD + "$" + new NumberFormat().format(reward - bounty.getExtra()));
		bookLore.add(ChatColor.GRAY + "  - Extra: " + ChatColor.GOLD + "$" + new NumberFormat().format(bounty.getExtra()));
		bookLore.add("");
		bookLore.add(ChatColor.GRAY + "Hunters: " + ChatColor.RED + bounty.getHunters().size());
		bookLore.add(ChatColor.GRAY + "Contributors: " + ChatColor.RED + bounty.getContributors().size());
		bookMeta.setLore(bookLore);
		book.setItemMeta(bookMeta);
		inv.setItem(4, book);

		ItemStack chest = new ItemStack(Material.CHEST);
		ItemMeta chestMeta = chest.getItemMeta();
		chestMeta.setDisplayName(ChatColor.GREEN + "Extra Reward");
		List<String> chestLore = new ArrayList<>();
		chestLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Extra reward due to");
		chestLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "the auto-bounty system.");
		chestLore.add("");
		chestLore.add(ChatColor.GRAY + "Amount: " + ChatColor.GOLD + "$" + new NumberFormat().format(bounty.getExtra()));
		chestLore.add("");
		chestLore.add(ChatColor.YELLOW + AltChar.listDash + " Click to change it.");
		chestMeta.setLore(chestLore);
		chest.setItemMeta(chestMeta);
		inv.setItem(40, chest);

		if (bounty.getContributors().size() == 0) {

			ItemStack noContrib = new ItemStack(VersionMaterial.RED_STAINED_GLASS_PANE.toMaterial());
			ItemMeta noContribMeta = noContrib.getItemMeta();
			noContribMeta.setDisplayName(ChatColor.RED + "- No contributors -");
			noContrib.setItemMeta(noContribMeta);
			inv.setItem(22, noContrib);

		} else {

			ItemStack previous = new ItemStack(Material.ARROW);
			ItemMeta previousMeta = previous.getItemMeta();
			previousMeta.setDisplayName(ChatColor.GREEN + "Previous");
			previous.setItemMeta(previousMeta);
			inv.setItem(19, previous);

			ItemStack next = new ItemStack(Material.ARROW);
			ItemMeta nextMeta = next.getItemMeta();
			nextMeta.setDisplayName(ChatColor.GREEN + "Next");
			next.setItemMeta(nextMeta);
			inv.setItem(25, next);

			List<OfflinePlayer> contributors = new ArrayList<>(bounty.getContributors());
			for (int j = 0; j < slots.length; j++) {
				int index = (offset + j) % contributors.size();
				OfflinePlayer contributor = contributors.get(index);
				double contribution = bounty.getContribution(contributor);

				ItemStack item = new ItemStack(Material.EMERALD, index + 1);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD + contributor.getName());
				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Bounty Contributor");
				lore.add("");
				lore.add(ChatColor.GRAY + "Amount: " + ChatColor.GOLD + "$" + new NumberFormat().format(contribution));
				lore.add(ChatColor.GRAY + "Proportion: " + ChatColor.GOLD + digit.format(contribution / reward * 100) + "%");
				lore.add("");
				lore.add(ChatColor.YELLOW + AltChar.listDash + " Click to change this.");
				lore.add(ChatColor.YELLOW + AltChar.listDash + " Right click to remove.");
				meta.setLore(lore);
				item.setItemMeta(meta);

				inv.setItem(slots[j], NBTItem.get(item).addTag(new ItemTag("contributor", contributor.getUniqueId().toString())).toItem());
			}
		}

		return inv;
	}

	public Bounty getBounty() {
		return bounty;
	}

	/*
	 * literally useless
	 */
	private String genetive(String str) {
		return str + (str.endsWith("s") || str.endsWith("z") || str.endsWith("x") ? "' " : "'s ");
	}

	@Override
	public void whenClicked(ItemStack item, InventoryAction action, int slot) {

		if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous")) {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 2);
			offset = (offset - 1) % slots.length;
			open();
		}

		else if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Next")) {
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 2);
			offset = (offset + 1) % slots.length;
			open();
		}

		else if (item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Extra Reward")) {
			player.sendMessage(ChatColor.GREEN + "> Write in the chat the new amount you'd like.");
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
			new ChatInput(this, input -> {
				try {
					double d = Double.parseDouble(input);
					Validate.isTrue(d >= 0);

					player.sendMessage(ChatColor.GREEN + "> Extra reward successfully set to $" + new NumberFormat().format(d) + ".");
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
					bounty.setExtra(d);
					return true;
				} catch (IllegalArgumentException exception) {
					player.sendMessage(ChatColor.RED + "> " + input + " is not a valid number.");
					return false;
				}
			});
		}

		String tag = NBTItem.get(item).getString("contributor");
		if (tag.equals(""))
			return;

		if (action == InventoryAction.PICKUP_ALL) {
			player.sendMessage(ChatColor.GREEN + "> Write in the chat the new amount you'd like.");
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
			new ChatInput(this, input -> {
				try {
					double d = Double.parseDouble(input);
					Validate.isTrue(d > 0);

					OfflinePlayer contributor = Bukkit.getOfflinePlayer(UUID.fromString(tag));
					player.sendMessage(ChatColor.GREEN + "> Contribution of " + contributor.getName() + " successfully set to $" + new NumberFormat().format(d) + ".");
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
					bounty.setContribution(contributor, d);
					return true;
				} catch (IllegalArgumentException exception) {
					player.sendMessage(ChatColor.RED + "> " + input + " is not a valid number.");
					return false;
				}
			});
		}

		if (action == InventoryAction.PICKUP_HALF) {
			OfflinePlayer contributor = Bukkit.getOfflinePlayer(UUID.fromString(tag));
			player.sendMessage(ChatColor.GREEN + "> Contribution of " + contributor.getName() + " successfully removed.");
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
			bounty.removeContribution(contributor);
			open();
		}
	}
}
