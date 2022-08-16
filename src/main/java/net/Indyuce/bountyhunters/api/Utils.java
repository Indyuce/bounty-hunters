package net.Indyuce.bountyhunters.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class Utils {
    public static final Random random = new Random();

    /**
     * @param d Number to format
     * @return Formatted numbers with suffixes like k, M...
     * @deprecated Use {@link NumberFormat} instead
     */
    @Deprecated
    public static String format(double d) {
        return new NumberFormat().format(d);
    }

    @NotNull
    public static ItemStack getHead(OfflinePlayer player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(player);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * @return If an item either has a display name, or both a display name and lore.
     */
    public static boolean hasItemMeta(ItemStack item, boolean checkLore) {
        return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && (!checkLore || item.getItemMeta().hasLore());
    }

    private final static char[] DELAY_CHARACTERS = {'m', 'h', 'd', 'm', 'y'};
    private final static long[] DELAY_AMOUNTS = {60, 60 * 60, 60 * 60 * 24, 60 * 60 * 24 * 30, 60 * 60 * 24 * 30 * 365};

    public static String formatDelay(long millis) {

        if (millis < 1000 * 60)
            return "1m";

        String format = "";
        for (int j = DELAY_CHARACTERS.length - 1; j >= 0; j--) {
            long divisor = DELAY_AMOUNTS[j] * 1000;
            if (millis < divisor)
                continue;

            format = (millis / divisor) + DELAY_CHARACTERS[j] + " " + format;
            millis = millis % divisor;
        }

        return format;
    }

    /**
     * @param x Number to truncate
     * @param n Amount of  decimal places kept
     * @return Truncated number
     */
    public static double truncate(double x, int n) {
        double pow = Math.pow(10.0, n);
        return Math.floor(x * pow) / pow;
    }

    /**
     * @param v   Vector to rotate
     * @param loc The position is not actually being used here, only the pitch and yaw
     * @return Vector facing direction given by location
     */
    public static Vector rotateFunc(Vector v, Location loc) {
        double yaw = loc.getYaw() / 180 * Math.PI;
        double pitch = loc.getPitch() / 180 * Math.PI;
        v = rotAxisX(v, pitch);
        v = rotAxisY(v, -yaw);
        return v;
    }

    private static Vector rotAxisX(Vector v, double a) {
        double y = v.getY() * Math.cos(a) - v.getZ() * Math.sin(a);
        double z = v.getY() * Math.sin(a) + v.getZ() * Math.cos(a);
        return v.setY(y).setZ(z);
    }

    private static Vector rotAxisY(Vector v, double b) {
        double x = v.getX() * Math.cos(b) + v.getZ() * Math.sin(b);
        double z = v.getX() * -Math.sin(b) + v.getZ() * Math.cos(b);
        return v.setX(x).setZ(z);
    }

    private static Vector rotAxisZ(Vector v, double c) {
        double x = v.getX() * Math.cos(c) - v.getY() * Math.sin(c);
        double y = v.getX() * Math.sin(c) + v.getY() * Math.cos(c);
        return v.setX(x).setY(y);
    }
}