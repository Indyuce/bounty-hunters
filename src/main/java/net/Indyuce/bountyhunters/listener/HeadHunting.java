package net.Indyuce.bountyhunters.listener;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.InventoryUtils;
import net.Indyuce.bountyhunters.api.Utils;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.language.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.UUID;

public class HeadHunting implements Listener {
    private static final NamespacedKey BOUNTY_TAG_PATH = new NamespacedKey(BountyHunters.getInstance(), "BountyId");

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void a(BountyClaimEvent event) {

        // Head will be given to the bounty creator if he claims the bounty.
        if (event.isHeadHunting() || !event.getBounty().hasCreator() || event.getBounty().hasCreator(event.getClaimer()))
            return;

        event.setCancelled(true);
        Message.HEAD_DROPPED.format("victim", event.getBounty().getTarget().getName(), "creator", event.getBounty().getCreator().getName()).send(event.getClaimer());

        // Generate head with NBT
        ItemStack head = Utils.getHead(event.getBounty().getTarget());
        ItemMeta meta = head.getItemMeta();
        meta.getPersistentDataContainer().set(BOUNTY_TAG_PATH, PersistentDataType.STRING, event.getBounty().getId().toString());
        head.setItemMeta(meta);

        // Give head
        new InventoryUtils(event.getClaimer()).giveItems(head);
    }

    @EventHandler(ignoreCancelled = true)
    public void b(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player))
            return;

        // Check for item in hand
        Player player = event.getPlayer();
        ItemStack item = event.getHand() == EquipmentSlot.HAND ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
        if (item.getType() == Material.AIR || item.getType() != Material.PLAYER_HEAD || !item.hasItemMeta())
            return;

        final @Nullable String tag = item.getItemMeta().getPersistentDataContainer().get(BOUNTY_TAG_PATH, PersistentDataType.STRING);
        if (tag == null || tag.isEmpty())
            return;

        final UUID id = UUID.fromString(tag);
        if (!BountyHunters.getInstance().getBountyManager().hasBounty(id))
            return;

        final Bounty bounty = BountyHunters.getInstance().getBountyManager().getBounty(id);
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
        final ItemStack item;
        if (event.hasItem() && (item = event.getItem()).getType() == Material.PLAYER_HEAD && item.hasItemMeta())
            if (item.getItemMeta().getPersistentDataContainer().has(BOUNTY_TAG_PATH, PersistentDataType.STRING))
                event.setCancelled(true);
    }
}
