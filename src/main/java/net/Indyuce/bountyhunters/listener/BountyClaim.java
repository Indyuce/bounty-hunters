package net.Indyuce.bountyhunters.listener;

import java.util.Optional;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.BountyCommands;
import net.Indyuce.bountyhunters.api.BountyEffect;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent.BountyCause;
import net.Indyuce.bountyhunters.api.event.BountyEvent;
import net.Indyuce.bountyhunters.api.event.BountyIncreaseEvent;
import net.Indyuce.bountyhunters.api.event.BountyIncreaseEvent.BountyChangeCause;
import net.Indyuce.bountyhunters.api.player.PlayerData;

public class BountyClaim implements Listener {
	private static final Random random = new Random();

	@EventHandler
	public void a(PlayerDeathEvent event) {
		Player target = event.getEntity();
		if (target.getKiller() == null || !(target.getKiller() instanceof Player) || target.equals(target.getKiller()))
			return;

		Player killer = target.getKiller();

		/*
		 * auto bounty: killing a player on whom there was no bounty makes the
		 * kill illegal. When a kill is illegal, the killer has a chance to have
		 * a bounty drop onto him
		 */
		Optional<Bounty> hasBounty = BountyHunters.getInstance().getBountyManager().getBounty(target);
		if (!hasBounty.isPresent()) {
			if (BountyHunters.getInstance().getConfig().getBoolean("auto-bounty.enabled") && random.nextDouble() <= BountyHunters.getInstance().getConfig().getDouble("auto-bounty.chance") / 100) {

				/*
				 * check for auto bounty increment
				 */
				Optional<Bounty> killerBounty = BountyHunters.getInstance().getBountyManager().getBounty(killer);
				if (killerBounty.isPresent() && !BountyHunters.getInstance().getConfig().getBoolean("auto-bounty.increment"))
					return;

				BountyEvent bountyEvent = killerBounty.isPresent() ? new BountyIncreaseEvent(killerBounty.get(), null, BountyHunters.getInstance().getConfig().getDouble("auto-bounty.reward"), BountyChangeCause.AUTO_BOUNTY) : new BountyCreateEvent(new Bounty(killer, BountyHunters.getInstance().getConfig().getDouble("auto-bounty.reward")), null, BountyCause.AUTO_BOUNTY);
				Bounty bounty = bountyEvent.getBounty();
				Bukkit.getPluginManager().callEvent(bountyEvent);
				if (bountyEvent.isCancelled())
					return;

				BountyHunters.getInstance().getPlayerDataManager().get(killer).addIllegalKills(1);

				/*
				 * removes the death message
				 */
				if (BountyHunters.getInstance().getConfig().getBoolean("disable-death-message.auto-bounty"))
					event.setDeathMessage(null);

				/*
				 * create a new bounty using the auto bounty
				 */
				if (bountyEvent instanceof BountyCreateEvent) {
					BountyHunters.getInstance().getBountyManager().registerBounty(bounty);
					new BountyCommands("place.auto-bounty", bounty, killer).send();
				}

				/*
				 * increase the existing bounty amount
				 */
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

		/*
		 * removes the death message
		 */
		if (BountyHunters.getInstance().getConfig().getBoolean("disable-death-message.bounty-claim"))
			event.setDeathMessage(null);

		/*
		 * bukkit event check
		 */
		BountyClaimEvent bountyEvent = new BountyClaimEvent(bounty, killer);
		Bukkit.getPluginManager().callEvent(bountyEvent);
		if (bountyEvent.isCancelled())
			return;

		bountyEvent.sendAllert();
		setClaimed(bounty, killer, target);
	}

	public void setClaimed(Bounty bounty, Player killer, Player target) {

		/*
		 * drops items at the target's location, best look with CHEST, REDSTONE
		 * or GOLD_NUGGET. these items can't be picked up and only act as
		 * cosmetics
		 */
		if (target != null && BountyHunters.getInstance().getConfig().getBoolean("bounty-effect.enabled"))
			new BountyEffect(BountyHunters.getInstance().getConfig().getConfigurationSection("bounty-effect")).play(((Player) target).getLocation());

		/*
		 * send bounty commands
		 */
		new BountyCommands("claim", bounty, killer).send();

		/*
		 * drops the killed player's head
		 */
		if (target != null && BountyHunters.getInstance().getConfig().getBoolean("drop-head.killer.enabled") && random.nextDouble() <= BountyHunters.getInstance().getConfig().getDouble("drop-head.killer.chance") / 100)
			target.getWorld().dropItem(target.getLocation(), BountyHunters.getInstance().getVersionWrapper().getHead(target));
		if (BountyHunters.getInstance().getConfig().getBoolean("drop-head.creator.enabled") && random.nextDouble() <= BountyHunters.getInstance().getConfig().getDouble("drop-head.creator.chance") / 100)
			BountyHunters.getInstance().getPlayerDataManager().getOfflineData(bounty.getCreator()).givePlayerHead(target);

		/*
		 * give the money to the player who claimed the bounty
		 */
		BountyHunters.getInstance().getEconomy().depositPlayer(killer, bounty.getReward());

		/*
		 * adds 1 to the claimer's claimed bounties stat and checks for a level
		 * up ; also checks if the player can join the hunter leaderboard
		 */
		PlayerData playerData = BountyHunters.getInstance().getPlayerDataManager().get(killer);
		playerData.addClaimedBounties(1);
		if (BountyHunters.getInstance().getLevelManager().isEnabled())
			playerData.refreshLevel(killer);
		BountyHunters.getInstance().getHunterLeaderboard().update(playerData);

		/*
		 * adds 1 to the bounty creator's successful-bounties stat
		 */
		if (bounty.hasCreator())
			BountyHunters.getInstance().getPlayerDataManager().getOfflineData(bounty.getCreator()).addSuccessfulBounties(1);

		/*
		 * displays the claimer's death title
		 */
		if (BountyHunters.getInstance().getLevelManager().isEnabled()) {
			String deathQuote = playerData.hasQuote() ? playerData.getQuote().format() : "";
			if (!deathQuote.equals("")) {
				boolean bool = BountyHunters.getInstance().getConfig().getBoolean("display-death-quote-on-title");
				for (Player online : Bukkit.getOnlinePlayers()) {
					online.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + killer.getName() + "> " + deathQuote);
					if (bool)
						BountyHunters.getInstance().getVersionWrapper().sendTitle(online, ChatColor.GOLD + "" + ChatColor.BOLD + killer.getName().toUpperCase(), ChatColor.ITALIC + deathQuote, 10, 60, 10);
				}
			}
		}

		// finally, unregister the bounty
		BountyHunters.getInstance().getBountyManager().unregisterBounty(bounty, true);
	}
}