package net.Indyuce.bountyhunters.api.player;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.language.Language;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerHunting {
    private final Bounty bounty;

    @Nullable
    private BukkitRunnable compassRunnable;
    @Nullable
    private Player player;
    @Nullable
    private ItemStack compass;

    public PlayerHunting(Bounty bounty) {
        this.bounty = bounty;
    }

    /**
     * @param player The hunter
     */
    public void enableCompass(Player player) {
        Validate.isTrue(!isCompassActive(), "Compass is already active");

        this.player = player;

        (compassRunnable = new BukkitRunnable() {
            int ti = 0;
            final boolean circle = BountyHunters.getInstance().getConfig().getBoolean("player-tracking.target-particles");

            public void run() {

                // Cancel runnable if any of the conditions is missing.
                if (!check()) {
                    hideParticles();
                    return;
                }

                // Update compass display name based on distance
                ItemMeta meta = compass.getItemMeta();
                meta.setDisplayName(Language.COMPASS_FORMAT.format("blocks",
                        new NumberFormat(true).format(bounty.getTarget().getPlayer().getLocation().distance(player.getLocation()))));
                compass.setItemMeta(meta);

                // Draw vector
                Location src = player.getLocation().add(0, 1.3, 0).add(player.getEyeLocation().getDirection().setY(0).normalize());
                Vector vec = bounty.getTarget().getPlayer().getLocation().subtract(src.clone().add(0, -1.3, 0)).toVector().normalize().multiply(.2);
                for (int j = 0; j < 9; j++)
                    src.getWorld().spawnParticle(Particle.REDSTONE, src.add(vec), 0, new Particle.DustOptions(Color.RED, 1));

                // Draw circle around target
                if (circle && (ti = (ti + 1) % 20) < 3) {
                    Location loc = bounty.getTarget().getPlayer().getLocation();
                    for (double j = 0; j < Math.PI * 2; j += Math.PI / 16)
                        loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(Math.cos(j) * .8, .15, Math.sin(j) * .8), 0, new Particle.DustOptions(Color.RED, 1));
                }
            }
        }).runTaskTimer(BountyHunters.getInstance(), 0, 6);
    }

    @Deprecated
    public void showParticles(Player player) {
        enableCompass(player);
    }

    public boolean isCompassActive() {
        return compassRunnable != null;
    }

    private boolean check() {
        if (!player.isOnline() || !bounty.getTarget().isOnline() || !bounty.getTarget().getPlayer().getWorld().equals(player.getWorld()))
            return false;

        if (CustomItem.BOUNTY_COMPASS.matches(player.getInventory().getItemInMainHand())) {
            compass = player.getInventory().getItemInMainHand();
            return true;
        }

        if (CustomItem.BOUNTY_COMPASS.matches(player.getInventory().getItemInOffHand())) {
            compass = player.getInventory().getItemInOffHand();
            return true;
        }

        return false;
    }

    public void disableCompass() {
        Validate.notNull(compassRunnable, "Player is not hunting");

        // Close runnable and collect garbage
        compassRunnable.cancel();
        compassRunnable = null;
        player = null;
        compass = null;
    }

    @Deprecated
    public void hideParticles() {
        disableCompass();
    }

    @NotNull
    public Bounty getBounty() {
        return bounty;
    }

    @NotNull
    public OfflinePlayer getHunted() {
        return getBounty().getTarget();
    }
}
