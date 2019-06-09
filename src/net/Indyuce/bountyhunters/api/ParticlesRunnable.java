package net.Indyuce.bountyhunters.api;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.bountyhunters.BountyHunters;

public class ParticlesRunnable extends BukkitRunnable {
	final String permNode = BountyHunters.plugin.getConfig().getString("target-particles.permission");
	final boolean permBool = permNode.equals("");

	@Override
	public void run() {
		for (Bounty bounty : BountyHunters.getBountyManager().getBounties()) {
			if (!bounty.getTarget().isOnline())
				continue;

			Player player = Bukkit.getPlayer(bounty.getTarget().getUniqueId());
			for (UUID hunterUuid : bounty.getHunters()) {
				Player hunter = Bukkit.getPlayer(hunterUuid);
				if (hunter != null)
					if (permBool || hunter.hasPermission(permNode))
						new BukkitRunnable() {
							int ti = 0;
							Location loc = player.getLocation().clone().add(0, .1, 0);

							public void run() {
								ti++;
								if (ti > 2)
									cancel();

								for (double j = 0; j < Math.PI * 2; j += Math.PI / 16)
									hunter.spawnParticle(Particle.REDSTONE, loc.clone().add(Math.cos(j) * .8, 0, Math.sin(j) * .8), 0, new Particle.DustOptions(Color.RED, 1));
							}
						}.runTaskTimer(BountyHunters.plugin, 0, 7);
			}
		}
	}
}
