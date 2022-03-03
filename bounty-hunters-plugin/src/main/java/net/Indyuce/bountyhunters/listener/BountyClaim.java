package net.Indyuce.bountyhunters.listener;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.BountyCommands;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent.BountyCause;
import net.Indyuce.bountyhunters.api.event.BountyEvent;
import net.Indyuce.bountyhunters.api.event.BountyIncreaseEvent;
import net.Indyuce.bountyhunters.api.event.BountyIncreaseEvent.BountyChangeCause;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import net.Indyuce.bountyhunters.leaderboard.profile.HunterProfile;
import org.antlr.v4.runtime.misc.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class BountyClaim implements Listener {
	private static final Random random = new Random();

	@EventHandler
	public void a(PlayerDeathEvent event) {
		Player target = event.getEntity();
		if (target.getKiller() == null || !(target.getKiller() instanceof Player) || target.equals(target.getKiller()))
			return;

		Player killer = target.getKiller();

		/*
		 * Auto bounty: killing a player on whom there was no bounty makes the
		 * kill illegal. When a kill is illegal, the killer has a chance to have
		 * a bounty drop onto them
		 */
		Optional<Bounty> hasBounty = BountyHunters.getInstance().getBountyManager().getBounty(target);
		if (!hasBounty.isPresent()) {
			if (BountyHunters.getInstance().getConfig().getBoolean("auto-bounty.enabled") && random.nextDouble() <= BountyHunters.getInstance().getConfig().getDouble("auto-bounty.chance") / 100) {

				// Check for auto bounty increment
				Optional<Bounty> killerBounty = BountyHunters.getInstance().getBountyManager().getBounty(killer);
				if (killerBounty.isPresent() && !BountyHunters.getInstance().getConfig().getBoolean("auto-bounty.increment"))
					return;

				BountyEvent bountyEvent = killerBounty.isPresent() ? new BountyIncreaseEvent(killerBounty.get(), null, BountyHunters.getInstance().getConfig().getDouble("auto-bounty.reward"), BountyChangeCause.AUTO_BOUNTY) : new BountyCreateEvent(new Bounty(killer, BountyHunters.getInstance().getConfig().getDouble("auto-bounty.reward")), null, BountyCause.AUTO_BOUNTY);
				Bounty bounty = bountyEvent.getBounty();
				Bukkit.getPluginManager().callEvent(bountyEvent);
				if (bountyEvent.isCancelled())
					return;

				BountyHunters.getInstance().getPlayerDataManager().get(killer).addIllegalKills(1);

				// Removes the death message
				if (BountyHunters.getInstance().getConfig().getBoolean("disable-death-message.auto-bounty"))
					event.setDeathMessage(null);

				// Create a new bounty using the auto bounty
				if (bountyEvent instanceof BountyCreateEvent) {
					BountyHunters.getInstance().getBountyManager().registerBounty(bounty);
					new BountyCommands("place.auto-bounty", bounty, killer).send();
				}

				// Increase the existing bounty amount
				else {
					bounty.addReward(((BountyIncreaseEvent) bountyEvent).getAdded());
					new BountyCommands("increase.auto-bounty", bounty, killer).send();
				}

				bountyEvent.sendAllert();
			}
			return;
		}

		if (!killer.hasPermission("bountyhunters.claim"))
			return;

		Bounty bounty = hasBounty.get();
		if (BountyHunters.getInstance().getConfig().getBoolean("claim-restrictions.targets-only") && !bounty.hasHunter(killer))
			return;

		if (BountyHunters.getInstance().getConfig().getBoolean("claim-restrictions.own-bounties") && bounty.hasCreator(killer))
			return;

		// Removes the death message
		if (BountyHunters.getInstance().getConfig().getBoolean("disable-death-message.bounty-claim"))
			event.setDeathMessage(null);

		// Bukkit event check
		BountyClaimEvent bountyEvent = new BountyClaimEvent(bounty, killer);
		Bukkit.getPluginManager().callEvent(bountyEvent);
		if (bountyEvent.isCancelled())
			return;

		bountyEvent.sendAllert();
		handleBountyClaim(bounty, killer, target, null);
	}

    /**
     * @param bounty Bounty being claimed
     * @param killer Player claiming the bounty
     * @param target Bounty target
     * @param author Bounty author (used for Head Hunting)
     */
    public void handleBountyClaim(@NotNull Bounty bounty, @NotNull Player killer, @Nullable Player target, @Nullable Player author) {

        // Send bounty commands
		new BountyCommands("claim", bounty, killer).send();

        // Drops the killed player's head
        if (target != null && BountyHunters.getInstance().getConfig().getBoolean("drop-head.killer.enabled") && random.nextDouble() <= BountyHunters.getInstance().getConfig().getDouble("drop-head.killer.chance") / 100)
            target.getWorld().dropItem(target.getLocation(), BountyHunters.getInstance().getVersionWrapper().getHead(target));
        if (BountyHunters.getInstance().getConfig().getBoolean("drop-head.creator.enabled") && random.nextDouble() <= BountyHunters.getInstance().getConfig().getDouble("drop-head.creator.chance") / 100)
            BountyHunters.getInstance().getPlayerDataManager().getOfflineData(Objects.requireNonNullElse(author, bounty.getCreator())).givePlayerHead(target);

		// Give the money to the player who claimed the bounty
		BountyHunters.getInstance().getEconomy().depositPlayer(killer, bounty.getReward());

		/*
		 * Adds 1 to the claimer's claimed bounties stat and checks for a level
		 * up ; also checks if the player can join the hunter leaderboard
		 */
		PlayerData playerData = BountyHunters.getInstance().getPlayerDataManager().get(killer);
		playerData.addClaimedBounties(1);
		if (BountyHunters.getInstance().getLevelManager().isEnabled())
			playerData.refreshLevel(killer);

		// Adds 1 to the bounty creator's successful-bounties stat
		if (bounty.hasCreator())
			BountyHunters.getInstance().getPlayerDataManager().getOfflineData(bounty.getCreator()).addSuccessfulBounties(1);

		// Displays the claimer's death quote
		if (BountyHunters.getInstance().getLevelManager().isEnabled() && playerData.hasAnimation()) {
			playerData.getAnimation().getEffect().playParticleEffect(target.getLocation().add(0, 1, 0), target);
			String deathQuote = playerData.getAnimation().format();
			for (Player online : Bukkit.getOnlinePlayers())
				online.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + killer.getName() + "> " + deathQuote);
		}

		// Finally, unregister the bounty
		BountyHunters.getInstance().getBountyManager().unregisterBounty(bounty, true);

		// Update leaderboards after the bounty was unregistered
		BountyHunters.getInstance().getHunterLeaderboard().update(new HunterProfile(playerData));
	}
}
