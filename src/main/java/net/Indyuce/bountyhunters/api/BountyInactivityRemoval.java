package net.Indyuce.bountyhunters.api;

import java.util.Iterator;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.event.BountyExpireEvent;
import net.Indyuce.bountyhunters.api.event.BountyExpireEvent.BountyExpireCause;
import net.Indyuce.bountyhunters.api.language.Message;

public class BountyInactivityRemoval extends BukkitRunnable {
	private final long timeout = (long) (1000d * 3600d
			* Math.max(1, BountyHunters.getInstance().getConfig().getDouble("inactive-bounty-removal.time")));

	@Override
	public void run() {

		Iterator<Bounty> iterator = BountyHunters.getInstance().getBountyManager().getBounties().iterator();
		while (iterator.hasNext()) {
			Bounty bounty = iterator.next();

			/*
			 * check if bounty is inactive
			 */
			if (System.currentTimeMillis() - bounty.getLastModified() > timeout) {
				Message.BOUNTY_EXPIRED_INACTIVITY.format("target", bounty.getTarget().getName()).send(Bukkit.getOnlinePlayers());
				for (OfflinePlayer contributor : bounty.getContributors())

					/*
					 * use try-catch to make sure the bounty is removed even
					 * though there is an error trying to transfer bounty money
					 * back.
					 */
					try {
						BountyHunters.getInstance().getEconomy().depositPlayer(contributor, bounty.getContribution(contributor));
					} catch (Exception exception) {
						BountyHunters.getInstance().getLogger().log(Level.WARNING, "An error occured while attempting to transfer "
								+ bounty.getContribution(contributor) + "$ to " + contributor.getName() + " during bounty inactivity removal.");
					}

				Bukkit.getPluginManager().callEvent(new BountyExpireEvent(bounty, null, bounty.getReward(), BountyExpireCause.INACTIVITY));

				/*
				 * only one bounty can be removed every 2min in order to reduce
				 * potential chat hard spam. should not cause inactive problem
				 * because check frequency is 2m which is much smaller than
				 * inactivity time
				 */
				iterator.remove();

				/*
				 * unregister bounty after removing it from the map so no
				 * iterator error is thrown
				 */
				BountyHunters.getInstance().getBountyManager().unregisterBounty(bounty, false);
				break;
			}
		}
	}
}
