package net.Indyuce.bountyhunters.listener;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.BountyCommands;
import net.Indyuce.bountyhunters.api.BountyEffect;
import net.Indyuce.bountyhunters.api.PhysicalRewards;
import net.Indyuce.bountyhunters.api.PlayerData;
import net.Indyuce.bountyhunters.api.PlayerHead;
import net.Indyuce.bountyhunters.api.event.BountyChangeEvent;
import net.Indyuce.bountyhunters.api.event.BountyChangeEvent.BountyChangeCause;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent.BountyCause;
import net.Indyuce.bountyhunters.gui.Leaderboard;
import net.Indyuce.bountyhunters.manager.BountyManager;

public class BountyClaim implements Listener {
	@EventHandler
	public void a(PlayerDeathEvent event) {
		Player target = event.getEntity();
		if (target.getKiller() == null || !(target.getKiller() instanceof Player) || target == target.getKiller())
			return;

		/*
		 * check if the player world is in the world blacklist (the plugin is
		 * totally disabled in these worlds)
		 */
		if (BountyHunters.plugin.getConfig().getStringList("world-blacklist").contains(target.getWorld().getName()))
			return;

		Random random = new Random();
		BountyManager bountyManager = BountyHunters.getBountyManager();
		Player killer = target.getKiller();

		/*
		 * auto bounty: killing a player on whom there was no bounty makes the
		 * kill illegal. When a kill is illegal, the killer has a chance to have
		 * a bounty drop onto him
		 */
		if (!bountyManager.hasBounty(target)) {
			if (BountyHunters.plugin.getConfig().getBoolean("auto-bounty.enabled") && random.nextDouble() <= BountyHunters.plugin.getConfig().getDouble("auto-bounty.chance") / 100) {

				/*
				 * removes the death message
				 */
				if (BountyHunters.plugin.getConfig().getBoolean("disable-death-message.auto-bounty"))
					event.setDeathMessage(null);

				/*
				 * send auto-bounty commands
				 */
				new BountyCommands("auto-bounty.target", target, killer).send(target);
				new BountyCommands("auto-bounty.killer", target, killer).send(killer);

				/*
				 * create a new bounty using the auto bounty
				 */
				if (!bountyManager.hasBounty(killer)) {
					Bounty bounty = new Bounty(null, killer, BountyHunters.plugin.getConfig().getDouble("auto-bounty.reward"));

					BountyCreateEvent bountyEvent = new BountyCreateEvent(bounty, BountyCause.AUTO_BOUNTY);
					Bukkit.getPluginManager().callEvent(bountyEvent);
					if (bountyEvent.isCancelled())
						return;

					bounty.register();
					bountyEvent.sendAllert();
					return;
				}

				/*
				 * increase the existing bounty amount
				 */
				Bounty bounty = bountyManager.getBounty(killer);

				BountyChangeEvent bountyEvent = new BountyChangeEvent(bounty, BountyChangeCause.AUTO_BOUNTY);
				Bukkit.getPluginManager().callEvent(bountyEvent);
				if (bountyEvent.isCancelled())
					return;

				bounty.setReward(bounty.getReward() + BountyHunters.plugin.getConfig().getDouble("auto-bounty.reward"));
				bountyEvent.sendAllert();
			}
			return;
		}

		if (!killer.hasPermission("bountyhunters.claim"))
			return;

		Bounty bounty = bountyManager.getBounty(target);

		/*
		 * prevents the player from claiming the bounty if he is the bounty
		 * creator & if the corresponding option is disabled
		 */
		if (bounty.hasCreator())
			if (!BountyHunters.plugin.getConfig().getBoolean("own-bounty-claiming") && bounty.hasCreator(killer))
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
		if (BountyHunters.plugin.getConfig().getBoolean("disable-death-message.bounty-claim"))
			event.setDeathMessage(null);

		/*
		 * drops items at the target's location, best look with CHEST, REDSTONE
		 * or GOLD_NUGGET. these items can't be picked up and only act as
		 * cosmetics
		 */
		if (BountyHunters.plugin.getConfig().getBoolean("bounty-effect.enabled"))
			new BountyEffect(BountyHunters.plugin.getConfig().getConfigurationSection("bounty-effect")).play(target.getLocation());

		/*
		 * read physical rewards from the config file and drop them at the
		 * target's location. error messages are displayed if the items can't be
		 * read TODO add support for external plugin items?
		 */
		if (BountyHunters.plugin.getConfig().getBoolean("physical-rewards.enabled"))
			for (ItemStack drop : new PhysicalRewards(BountyHunters.plugin.getConfig().getConfigurationSection("physical-rewards.list")).readItems())
				target.getWorld().dropItem(target.getLocation(), drop);

		/*
		 * send bounty commands TODO improve command tables
		 */
		new BountyCommands("claim.target", target, killer).send(target);
		new BountyCommands("claim.killer", target, killer).send(killer);

		/*
		 * drops the killed player's head
		 */
		if (BountyHunters.plugin.getConfig().getBoolean("drop-head.enabled") && random.nextDouble() <= BountyHunters.plugin.getConfig().getDouble("drop-head.chance") / 100)
			target.getWorld().dropItem(target.getLocation(), new PlayerHead(target));

		/*
		 * give the money to the player who claimed the bounty
		 */
		BountyHunters.getEconomy().depositPlayer(killer, bounty.getReward());

		/*
		 * adds 1 to the claimer's claimed bounties stat and checks for a level
		 * up ; also checks if the player can join the hunter leaderboard
		 */
		PlayerData playerData = PlayerData.get(killer);
		playerData.addClaimedBounties(1);
		if (BountyHunters.plugin.getConfig().getBoolean("enable-quotes-levels-titles"))
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
		if (BountyHunters.plugin.getConfig().getBoolean("enable-quotes-levels-titles")) {
			String deathQuote = playerData.getQuote();
			if (!deathQuote.equals("")) {
				boolean bool = BountyHunters.plugin.getConfig().getBoolean("display-death-quote-on-title");
				for (Player t2 : Bukkit.getOnlinePlayers()) {
					t2.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + killer.getName() + "> " + deathQuote);
					if (bool)
						BountyHunters.getNMS().sendTitle(t2, ChatColor.GOLD + "" + ChatColor.BOLD + killer.getName().toUpperCase(), ChatColor.ITALIC + deathQuote, 10, 60, 10);
				}
			}
		}

		// finally, unregister the bounty
		bounty.unregister();
	}

	@EventHandler
	public void b(PlayerPickupItemEvent event) {
		Item item = event.getItem();
		if (item.hasMetadata("BOUNTYHUNTERS:no_pickup"))
			event.setCancelled(true);
	}

	@EventHandler
	public void c(InventoryPickupItemEvent event) {
		Item item = event.getItem();
		if (item.hasMetadata("BOUNTYHUNTERS:no_pickup"))
			event.setCancelled(true);
	}
}