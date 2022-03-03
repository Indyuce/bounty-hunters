package net.Indyuce.bountyhunters.listener;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.InventoryUtils;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.language.Message;
import net.Indyuce.bountyhunters.version.VersionMaterial;
import net.Indyuce.bountyhunters.version.wrapper.api.ItemTag;
import net.Indyuce.bountyhunters.version.wrapper.api.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HeadHunting implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void a(BountyClaimEvent event) {

		// Head will be given to the bounty creator if he claims the bounty.
		if (event.isHeadHunting() || !event.getBounty().hasCreator() || event.getBounty().hasCreator(event.getClaimer()))
			return;

		event.setCancelled(true);

		Message.HEAD_DROPPED.format("victim", event.getBounty().getTarget().getName(), "creator", event.getBounty().getCreator().getName()).send(event.getClaimer());

		ItemStack head = BountyHunters.getInstance().getVersionWrapper().getHead(event.getBounty().getTarget());
		head = NBTItem.get(head).addTag(new ItemTag("BountyId", event.getBounty().getId().toString())).toItem();
		new InventoryUtils(event.getClaimer()).giveItems(head);
	}

	@EventHandler(ignoreCancelled = true)
	public void b(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Player))
			return;

		// Check for item in hand
		Player player = event.getPlayer();
		ItemStack item = event.getHand() == EquipmentSlot.HAND ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
		if (item == null || !VersionMaterial.PLAYER_HEAD.matches(item))
			return;

		NBTItem nbtItem = BountyHunters.getInstance().getVersionWrapper().getNBTItem(item);
		String tag = nbtItem.getString("BountyId");
		if (tag.equals(""))
			return;

		UUID id = UUID.fromString(tag);
		if (!BountyHunters.getInstance().getBountyManager().hasBounty(id))
			return;

		Bounty bounty = BountyHunters.getInstance().getBountyManager().getBounty(id);
		if (!bounty.hasCreator((Player) event.getRightClicked()) || bounty.hasTarget(event.getPlayer()))
			return;

		// Cast Bukkit event
		BountyClaimEvent bountyEvent = new BountyClaimEvent(bounty, player, true);
		Bukkit.getPluginManager().callEvent(bountyEvent);
		if (bountyEvent.isCancelled())
			return;

		// Take head away from bounty claimer
		item.setAmount(item.getAmount() - 1);

		bountyEvent.sendAllert();
		new BountyClaim().handleBountyClaim(bounty, player, null, (Player) event.getRightClicked());
	}

	@EventHandler(ignoreCancelled = true)
	public void c(PlayerInteractEvent event) {
		if (event.hasItem() && VersionMaterial.PLAYER_HEAD.matches(event.getItem()))
			if (BountyHunters.getInstance().getVersionWrapper().getNBTItem(event.getItem()).hasTag("BountyId"))
				event.setCancelled(true);
	}
}
