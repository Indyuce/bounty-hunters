package net.Indyuce.bountyhunters.manager;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.player.OfflinePlayerData;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PlayerDataManager {
    private static Map<UUID, PlayerData> dataMap = new HashMap<>();

    public boolean isLoaded(OfflinePlayer player) {
        return isLoaded(player.getUniqueId());
    }

    public boolean isLoaded(UUID uuid) {
        return dataMap.containsKey(uuid);
    }

    @Nullable
    public PlayerData get(OfflinePlayer player) {
        return get(player.getUniqueId());
    }

    @Nullable
    public PlayerData get(UUID uuid) {
        return dataMap.get(uuid);
    }

    @NotNull
    public OfflinePlayerData getOfflineData(OfflinePlayer player) {
        final @Nullable PlayerData found = dataMap.get(player.getUniqueId());
        return found == null ? loadOfflineData(player) : found;
    }

    public void unload(UUID uuid) {
        dataMap.remove(uuid);
    }

    public void unload(OfflinePlayer player) {
        unload(player.getUniqueId());
    }

    @NotNull
    public Collection<PlayerData> getLoaded() {
        return dataMap.values();
    }

    public void load(OfflinePlayer player) {
        if (!dataMap.containsKey(player.getUniqueId())) {

            final PlayerData data = new PlayerData(player);

            /*
             * Directly maps the player data into the HashMap however data is not loaded yet.
             * Better for extra plugins not to glitch out and so they can cache the player
             * data instance if needed for later calculations
             */
            dataMap.put(player.getUniqueId(), data);

            // Load data asynchronously for either SQL or YAML not to freeze main server thread
            Bukkit.getScheduler().runTaskAsynchronously(BountyHunters.getInstance(), () -> loadData(data));
        }
    }

    @NotNull
    public abstract OfflinePlayerData loadOfflineData(OfflinePlayer player);

    public abstract void loadData(PlayerData data);

    public abstract void saveData(PlayerData data);
}
