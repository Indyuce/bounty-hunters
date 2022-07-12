package net.Indyuce.bountyhunters.version.wrapper;

import net.Indyuce.bountyhunters.version.wrapper.api.ItemTag;
import net.Indyuce.bountyhunters.version.wrapper.api.NBTItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Set;

public class VersionWrapper_1_19_R1 implements VersionWrapper {

    @Override
    public boolean matchesMaterial(ItemStack item, ItemStack item1) {
        return item.getType() == item1.getType();
    }

    @Override
    public void sendJson(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int ticks, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, ticks, fadeOut);
    }

    @Override
    public ItemStack getHead(OfflinePlayer player) {

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(player);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public void spawnParticle(Particle particle, Location loc, Player player, Color color) {
        player.spawnParticle(particle, loc, 0, new Particle.DustOptions(color, 1));
    }

    @Override
    public void setOwner(SkullMeta meta, OfflinePlayer player) {
        meta.setOwningPlayer(player);
    }

    @Override
    public NBTItem getNBTItem(ItemStack item) {
        return new NBTItem_v1_17_R1(item);
    }

    public class NBTItem_v1_17_R1 extends NBTItem {
        private final net.minecraft.world.item.ItemStack nms;
        private final CompoundTag compound;

        public NBTItem_v1_17_R1(ItemStack item) {
            super(item);

            nms = CraftItemStack.asNMSCopy(item);
            compound = nms.hasTag() ? nms.getTag() : new CompoundTag();
        }

        @Override
        public String getString(String path) {
            return compound.getString(path);
        }

        @Override
        public boolean hasTag(String path) {
            return compound.contains(path);
        }

        @Override
        public boolean getBoolean(String path) {
            return compound.getBoolean(path);
        }

        @Override
        public double getDouble(String path) {
            return compound.getDouble(path);
        }

        @Override
        public int getInteger(String path) {
            return compound.getInt(path);
        }

        @Override
        public NBTItem addTag(List<ItemTag> tags) {
            tags.forEach(tag -> {
                if (tag.getValue() instanceof Boolean)
                    compound.putBoolean(tag.getPath(), (boolean) tag.getValue());
                else if (tag.getValue() instanceof Double)
                    compound.putDouble(tag.getPath(), (double) tag.getValue());
                else if (tag.getValue() instanceof String)
                    compound.putString(tag.getPath(), (String) tag.getValue());
                else if (tag.getValue() instanceof Integer)
                    compound.putInt(tag.getPath(), (int) tag.getValue());
            });
            return this;
        }

        @Override
        public NBTItem removeTag(String... paths) {
            for (String path : paths)
                compound.remove(path);
            return this;
        }

        @Override
        public Set<String> getTags() {
            return compound.getAllKeys();
        }

        @Override
        public ItemStack toItem() {
            nms.setTag(compound);
            return CraftItemStack.asBukkitCopy(nms);
        }
    }
}
