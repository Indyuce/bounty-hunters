package net.Indyuce.bountyhunters.gui;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.language.Language;
import net.Indyuce.bountyhunters.leaderboard.profile.HunterProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class Leaderboard extends PluginInventory {
    private static final int[] slots = {13, 21, 22, 23, 29, 30, 31, 32, 33, 37, 38, 39, 40, 41, 42, 43};

    public Leaderboard(Player player) {
        super(player);
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, Language.LEADERBOARD_GUI_NAME.format());

        int slot = 0;
        for (UUID uuid : BountyHunters.getInstance().getHunterLeaderboard().getCached()) {
            if (slot > slots.length)
                break;

            HunterProfile profile = BountyHunters.getInstance().getHunterLeaderboard().getData(uuid);
            ItemStack skull = CustomItem.LB_PLAYER_DATA.toItemStack();
            SkullMeta meta = (SkullMeta) skull.getItemMeta();

            final int slot1 = slot;
            Bukkit.getScheduler().runTaskAsynchronously(BountyHunters.getInstance(), () -> {
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(profile.getUniqueId()));
                inv.getItem(slots[slot1]).setItemMeta(meta);
            });

            meta.setDisplayName(applyPlaceholders(meta.getDisplayName(), profile, slot + 1));
            List<String> lore = meta.getLore();
            for (int j = 0; j < lore.size(); j++)
                lore.set(j, applyPlaceholders(lore.get(j), profile, slot + 1));
            meta.setLore(lore);
            skull.setItemMeta(meta);

            inv.setItem(slots[slot++], skull);
        }

        ItemStack glass = new ItemStack(Material.RED_STAINED_GLASS_PANE);
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

    private String applyPlaceholders(String str, HunterProfile profile, int rank) {
        str = str.replace("{level}", "" + profile.getLevel());
        str = str.replace("{bounties}", "" + profile.getClaimedBounties());
        str = str.replace("{successful_bounties}", "" + profile.getSuccessfulBounties());
        str = str.replace("{title}", profile.getTitle());
        str = str.replace("{name}", profile.getName());
        str = str.replace("{rank}", "" + rank);
        return str;
    }
}