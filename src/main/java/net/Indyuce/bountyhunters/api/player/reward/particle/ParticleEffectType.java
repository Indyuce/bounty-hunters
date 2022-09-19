package net.Indyuce.bountyhunters.api.player.reward.particle;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Utils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public enum ParticleEffectType {

    /**
     * Summons two interwoven bright jets of particles
     */
    TOTEM((loc, target) -> new BukkitRunnable() {
        double a = 0;

        private static final double radius = 1.7;

        public void run() {
            if (a > Math.PI * 2 * 3)
                cancel();

            a += Math.PI / 32;

            for (int k = 0; k < 2; k++) {

                Vector dir = new Vector(Math.cos(1.5 * a + k * Math.PI), Math.sin(a + k * Math.PI - .8) / 2.1 * radius, Math.sin(1.5 * a + k * Math.PI));
                loc.getWorld().spawnParticle(Particle.TOTEM, loc, 0, dir.getX(), dir.getY(), dir.getZ(), 1f);

                dir = new Vector(Math.cos(1.5 * a + k * Math.PI), Math.sin(a + k * Math.PI - .8 + Math.PI / 2) / 2.1 * radius, Math.sin(1.5 * a + k * Math.PI));
                loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 0, dir.getX(), dir.getY(), dir.getZ(), .2f);

            }
        }
    }.runTaskTimer(BountyHunters.getInstance(), 0, 1)),

    /**
     * Summons two interwoven flame jets
     */
    FLAME_VORTEX((loc, target) -> new BukkitRunnable() {
        double a = 0;

        private static final double radius = 1.7;

        public void run() {
            if (a > Math.PI * 2 * 3)
                cancel();

            a += Math.PI / 32;


            for (int k = 0; k < 2; k++)
                for (int j = 0; j < 2; j++) {

                    Vector dir = new Vector(Math.cos(1.5 * a + j * Math.PI), Math.sin(a + j * Math.PI - .8) / 2.1 * radius, Math.sin(1.5 * a + j * Math.PI));
                    loc.getWorld().spawnParticle(Particle.FLAME, loc, 0, dir.getX(), dir.getY(), dir.getZ(), .2f);

                    Location loc1 = loc.clone().add(Math.cos(a + j * Math.PI) * radius, 1 + Math.sin(a) / 2 * radius, Math.sin(a + j * Math.PI) * radius);
                    loc.getWorld().spawnParticle(Particle.FLAME, loc1, 0);
                    if (Utils.random.nextDouble() < .05)
                        loc.getWorld().spawnParticle(Particle.LAVA, loc1, 0);
                }
        }
    }.runTaskTimer(BountyHunters.getInstance(), 0, 1)),

    /**
     * Spawns many gold nuggets flying out of the player's corpse
     */
    GOLD((loc, target) -> {

        for (int j = 0; j < 16; j++) {
            ItemStack stack = new ItemStack(Material.GOLD_NUGGET);
            ItemMeta stackMeta = stack.getItemMeta();
            stackMeta.setDisplayName(UUID.randomUUID().toString());
            stack.setItemMeta(stackMeta);

            Item item = loc.getWorld().dropItem(loc, stack);
            item.setPickupDelay(100000000);

            double r = .7 + Utils.random.nextDouble() * .3;
            double a = Utils.random.nextDouble() * Math.PI * 2;
            Vector vec = new Vector(Math.cos(a) * r, 2, Math.sin(a) * r);
            item.setVelocity(vec.multiply(.15));

            Bukkit.getScheduler().runTaskLater(BountyHunters.getInstance(), item::remove, 50 + Utils.random.nextInt(50));
        }

    }),

    /**
     * Summons a sphere of smoke around target player
     */
    SMOKE((loc, target) -> new BukkitRunnable() {
        double a = 0;

        private static final double r = 1.3;

        public void run() {
            if (a > Math.PI * 2 * 3)
                cancel();

            a += Math.PI / 30;

            for (int j = 0; j < 6; j++) {
                Location loc1 = loc.clone().add(Math.cos(a + j * Math.PI / 3) * Math.cos(a) * r, Math.sin(a), Math.sin(a + j * Math.PI / 3) * Math.cos(a) * r);
                loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc1, 0);
                if (Utils.random.nextDouble() < .07)
                    loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc1, 0);
            }
        }
    }.runTaskTimer(BountyHunters.getInstance(), 0, 1)),

    /**
     * Summons blood marks on the ground
     */
    BLOODBATH((loc, target) -> {

        // Almost on the ground
        loc.add(0, -.55, 0);

        new BukkitRunnable() {
            double a = 0;

            public void run() {
                if (a > Math.PI * 2 * 3) {
                    cancel();
                    return;
                }

                a += Math.PI / 32;

                double rem = a % Math.PI * 2;
                double r = ((rem < Math.PI) ? rem : Math.PI * 2 - rem) * 1.3;

                for (int j = 0; j < 8; j++)
                    loc.getWorld().spawnParticle(Particle.REDSTONE,
                            loc.clone().add(
                                    Math.cos(a / 7 + j * Math.PI / 4) * r,
                                    Math.sin(a) * .4,
                                    Math.sin(a / 7 + j * Math.PI / 4) * r),
                            0, new Particle.DustOptions(Color.RED, 1));
            }
        }.runTaskTimer(BountyHunters.getInstance(), 0, 1);

    }),

    /**
     * Summons three waves of dark energy spreading across the ground
     */
    WITCHCRAFT((loc, target) -> new BukkitRunnable() {
        double a = 0;

        public void run() {
            if (a > 15 - Math.PI / 12)
                cancel();

            a += Math.PI / 24;
            double rem = a % 5;

            for (int k = 0; k < 15; k++) {
                Vector dir = new Vector(Math.cos(k * Math.PI * 2 / 15) * rem, 4.5 * Math.sin(rem / 1.5) * Math.exp(-rem / 1.5) - .9, Math.sin(k * Math.PI * 2 / 15) * rem);
                loc.getWorld().spawnParticle(Particle.REDSTONE, loc.clone().add(dir), 1, new Particle.DustOptions(Color.PURPLE, 1f));
                if (Utils.random.nextDouble() < .2)
                    loc.getWorld().spawnParticle(Particle.SPELL_WITCH, loc.clone().add(dir), 1);
            }
        }
    }.runTaskTimer(BountyHunters.getInstance(), 0, 1)),

    /**
     * Summons a baby sheep with the player's name
     */
    METAMORPHOSE((loc, target) -> new BukkitRunnable() {
        int ti = 0;

        public void run() {
            if (ti++ > 20) {

                Sheep sheep = (Sheep) loc.getWorld().spawnEntity(loc, EntityType.SHEEP);
                sheep.setBaby();
                sheep.setCustomName(target.getName());
                sheep.setHealth(1);

                loc.getWorld().spawnParticle(Particle.TOTEM, loc, 32, 0, 0, 0, .5f);
                loc.getWorld().playSound(loc, Sound.ENTITY_SHEEP_DEATH, 1, 1);

                cancel();
            } else
                loc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc, 0);
        }
    }.runTaskTimer(BountyHunters.getInstance(), 0, 1)),

    /**
     * Summons three meteors dashing at the target location
     */
    METEOR((loc, target) -> {

        // Display on the ground
        loc.add(0, -.9, 0);

        for (int k = 0; k < 3; k++)
            new BukkitRunnable() {
                int ti = 0;
                final Location source = loc.clone().add(5 * Math.cos(Utils.random.nextDouble() * 2 * Math.PI), 20, 5 * Math.sin(Utils.random.nextDouble() * 2 * Math.PI));
                final Vector vec = loc.clone().subtract(source).toVector().multiply(1. / 30.);

                public void run() {
                    if (ti == 0)
                        source.setDirection(vec);

                    for (int k = 0; k < 2; k++) {
                        ti++;
                        source.add(vec);
                        for (double i = 0; i < Math.PI * 2; i += Math.PI / 6) {
                            Vector vec = Utils.rotateFunc(new Vector(Math.cos(i), Math.sin(i), 0), source);
                            source.getWorld().spawnParticle(Particle.SMOKE_LARGE, source, 0, vec.getX(), vec.getY(), vec.getZ(), .1);
                        }
                    }

                    if (ti >= 30) {
                        source.getWorld().playSound(source, Sound.ENTITY_GENERIC_EXPLODE, 3, 1);
                        source.getWorld().spawnParticle(Particle.FLAME, source, 64, 0, 0, 0, .25);
                        source.getWorld().spawnParticle(Particle.LAVA, source, 32);
                        for (double j = 0; j < Math.PI * 2; j += Math.PI / 24)
                            source.getWorld().spawnParticle(Particle.SMOKE_LARGE, source, 0, Math.cos(j), 0, Math.sin(j), .5);

                        cancel();
                    }
                }
            }.runTaskTimer(BountyHunters.getInstance(), k * 20, 1);

    }),

    /**
     * Empty particle effect
     */
    NONE((loc, target) -> {
    });

    private final ParticleEffect handler;

    ParticleEffectType(ParticleEffect handler) {
        this.handler = handler;
    }

    public ParticleEffect getHandler() {
        return handler;
    }
}
