package net.Indyuce.bountyhunters.listener;

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
import net.Indyuce.bountyhunters.api.PlayerData;
import net.Indyuce.bountyhunters.api.PlayerHead;
import net.Indyuce.bountyhunters.api.event.BountyChangeEvent;
import net.Indyuce.bountyhunters.api.event.BountyChangeEvent.BountyChangeCause;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent.BountyCause;
import net.Indyuce.bountyhunters.api.event.BountyEvent;
import net.Indyuce.bountyhunters.gui.Leaderboard;

public class BountyClaim implements Listener {
	private static final Random random = new Random();

	@EventHandler
	public void a(PlayerDeathEvent event) {
		Player target = event.getEntity();
		if (target.getKiller() == null || !(target.getKiller() instanceof Player) || target.equals(target.getKiller()))
			return;

		/*
		 * check if the player world is in the world blacklist (the plugin is
		 * totally disabled in these worlds)
		 */
		if (BountyHunters.getInstance().getConfig().getStringList("world-blacklist").contains(target.getWorld().getName()))
			return;

		Player killer = target.getKiller();

		/*
		 * auto bounty: killing a player on whom there was no bounty makes the
		 * kill illegal. When a kill is illegal, the killer has a chance to have
		 * a bounty drop onto him
		 */
		if (!BountyHunters.getInstance().getBountyManager().hasBounty(target)) {
			if (BountyHunters.getInstance().getConfig().getBoolean("auto-bounty.enabled") && random.nextDouble() <= BountyHunters.getInstance().getConfig().getDouble("auto-bounty.chance") / 100) {

				BountyEvent bountyEvent = BountyHunters.getInstance().getBountyManager().hasBounty(killer) ? new BountyChangeEvent(BountyHunters.getInstance().getBountyManager().getBounty(killer), BountyChangeCause.AUTO_BOUNTY) : new BountyCreateEvent(new Bounty(null, killer, BountyHunters.getInstance().getConfig().getDouble("auto-bounty.reward")), BountyCause.AUTO_BOUNTY);
				Bounty bounty = bountyEvent.getBounty();
				Bukkit.getPluginManager().callEvent(bountyEvent);
				if (bountyEvent.isCancelled())
					return;

				/*
				 * removes the death message
				 */
				if (BountyHunters.getInstance().getConfig().getBoolean("disable-death-message.auto-bounty"))
					event.setDeathMessage(null);

				/*
				 * send auto-bounty commands
				 */
				new BountyCommands("auto-bounty.target", bounty, killer).send(target);
				new BountyCommands("auto-bounty.killer", bounty, killer).send(killer);
				if (bounty.hasCreator())
					new BountyCommands("auto-bounty.creator", bounty, killer).send(bounty.getCreator());

				/*
				 * create a new bounty using the auto bounty
				 */
				if (!BountyHunters.getInstance().getBountyManager().hasBounty(killer))
					BountyHunters.getInstance().getBountyManager().registerBounty(bounty);

				/*
				 * increase the existing bounty amount
				 */
				else
					bounty.setReward(bounty.getReward() + BountyHunters.getInstance().getConfig().getDouble("auto-bounty.reward"));

				bountyEvent.sendAllert();
			}
			return;
		}

		if (!killer.hasPermission("bountyhunters.claim"))
			return;

		/*
		 * prevents the player from claiming the bounty if he is the bounty
		 * creator & if the corresponding option is disabled
		 */
		Bounty bounty = BountyHunters.getInstance().getBountyManager().getBounty(target);
		if (bounty.hasCreator())
			if (!BountyHunters.getInstance().getConfig().getBoolean("own-bounty-claiming") && bounty.hasCreator(killer))
				return;

		/*
		 * create an event instance, call it and check if it is cancelled. if it
		 * is not cancelled, send the corresponding allert
		 */
		BountyClaimEvent bountyEvent = new BountyClaimEvent(bounty, killer);
		Bukkit.getPluginManager().callEvent(bountyEvent);
		if (bountyEvent.isCancelled())
			return;
		bountyEvent.sendAllert();

		/*
		 * removes the death message
		 */
		if (BountyHunters.getInstance().getConfig().getBoolean("disable-death-message.bounty-claim"))
			event.setDeathMessage(null);

		/*
		 * drops items at the target's location, best look with CHEST, REDSTONE
		 * or GOLD_NUGGET. these items can't be picked up and only act as
		 * cosmetics
		 */
		if (BountyHunters.getInstance().getConfig().getBoolean("bounty-effect.enabled"))
			new BountyEffect(BountyHunters.getInstance().getConfig().getConfigurationSection("bounty-effect")).play(target.getLocation());

		/*
		 * send bounty commands TODO improve command tables
		 */
		new BountyCommands("claim.target", bounty, killer).send(target);
		new BountyCommands("claim.killer", bounty, killer).send(killer);
		if (bounty.hasCreator())
			new BountyCommands("claim.creator", bounty, killer).send(bounty.getCreator());

		/*
		 * drops the killed player's head
		 */
		if (BountyHunters.getInstance().getConfig().getBoolean("drop-head.enabled") && random.nextDouble() <= BountyHunters.getInstance().getConfig().getDouble("drop-head.chance") / 100)
			target.getWorld().dropItem(target.getLocation(), new PlayerHead(target));

		/*
		 * give the money to the player who claimed the bounty
		 */
		BountyHunters.getInstance().getEconomy().depositPlayer(killer, bounty.getReward());

		/*
		 * adds 1 to the claimer's claimed bounties stat and checks for a level
		 * up ; also checks if the player can join the hunter leaderboard
		 */
		PlayerData playerData = PlayerData.get(killer);
		playerData.addClaimedBounties(1);
		if (BountyHunters.getInstance().getConfig().getBoolean("enable-quotes-levels-titles"))
			playerData.checkForLevelUp(killer);
		Leaderboard.updateCachedLeaderboard(killer.getUniqueId(), playerData.getClaimedBounties());

		/*
		 * adds 1 to the bounty creator's successful-bounties stat
		 */
		if (bounty.hasCreator()) {
			PlayerData playerData1 = PlayerData.get(bounty.getCreator());
			playerData1.addSuccessfulBounties(1);
		}

		/*
		 * displays the claimer's death title
		 */
		if (BountyHunters.getInstance().getConfig().getBoolean("enable-quotes-levels-titles")) {
			String deathQuote = playerData.getQuote();
			if (!deathQuote.equals("")) {
				boolean bool = BountyHunters.getInstance().getConfig().getBoolean("display-death-quote-on-title");
				for (Player online : Bukkit.getOnlinePlayers()) {
					online.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + killer.getName() + "> " + deathQuote);
					if (bool)
						BountyHunters.getInstance().getNMS().sendTitle(online, ChatColor.GOLD + "" + ChatColor.BOLD + killer.getName().toUpperCase(), ChatColor.ITALIC + deathQuote, 10, 60, 10);
				}
			}
		}

		// finally, unregister the bounty
		BountyHunters.getInstance().getBountyManager().unregisterBounty(bounty);
	}
}