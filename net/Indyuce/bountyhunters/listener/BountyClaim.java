package net.Indyuce.bountyhunters.listener;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.Eff;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.BountyCause;
import net.Indyuce.bountyhunters.api.BountyManager;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.PlayerData;
import net.Indyuce.bountyhunters.api.event.BountyClaimEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;

public class BountyClaim implements Listener {
	public BountyClaim() {

		// target particles
		if (BountyHunters.plugin.getConfig().getBoolean("target-particles.enabled"))
			new BukkitRunnable() {
				final String permNode = BountyHunters.plugin.getConfig().getString("target-particles.permission");
				final boolean permBool = permNode.equals("");

				public void run() {
					for (Bounty bounty : BountyHunters.getBountyManager().getBounties()) {
						if (!bounty.getTarget().isOnline())
							continue;

						Player p = Bukkit.getPlayer(bounty.getTarget().getUniqueId());
						for (UUID hunterUuid : bounty.getHunters()) {
							Player hunter = Bukkit.getPlayer(hunterUuid);
							if (hunter != null)
								if (permBool || hunter.hasPermission(permNode))
									new BukkitRunnable() {
										int ti = 0;
										Location loc = p.getLocation().clone().add(0, .1, 0);

										public void run() {
											ti++;
											if (ti > 2)
												cancel();

											for (double j = 0; j < Math.PI * 2; j += Math.PI / 16)
												Eff.REDSTONE.display(0, 0, 0, 0, 1, loc.clone().add(Math.cos(j) * .8, 0, Math.sin(j) * .8), hunter);
										}
									}.runTaskTimer(BountyHunters.plugin, 0, 7);
						}
					}
				}
			}.runTaskTimer(BountyHunters.plugin, 0, 100);
	}

	@EventHandler
	public void a(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if (p.getKiller() == null || !(p.getKiller() instanceof Player) || p == p.getKiller())
			return;

		// world blacklist
		if (BountyHunters.plugin.getConfig().getStringList("world-blacklist").contains(p.getWorld().getName()))
			return;

		Random random = new Random();
		BountyManager bountyManager = BountyHunters.getBountyManager();
		Player t = p.getKiller();

		// auto bounty
		if (!bountyManager.hasBounty(p)) {
			if (BountyHunters.plugin.getConfig().getBoolean("auto-bounty.enabled") && random.nextDouble() <= BountyHunters.plugin.getConfig().getDouble("auto-bounty.chance") / 100) {
				// create a new bounty
				if (!bountyManager.hasBounty(t)) {
					Bounty bounty = new Bounty(null, t, BountyHunters.plugin.getConfig().getDouble("auto-bounty.reward"));

					// check API event
					BountyCreateEvent e1 = new BountyCreateEvent(bounty, BountyCause.AUTO_BOUNTY);
					Bukkit.getPluginManager().callEvent(e1);
					if (e1.isCancelled())
						return;

					bounty.register();
					Alerts.newBounty(e1);
					return;
				}

				Bounty bounty = bountyManager.getBounty(t);

				// check API event
				BountyCreateEvent e1 = new BountyCreateEvent(bounty, BountyCause.AUTO_BOUNTY);
				Bukkit.getPluginManager().callEvent(e1);
				if (e1.isCancelled())
					return;

				bounty.setReward(bounty.getReward() + BountyHunters.plugin.getConfig().getDouble("auto-bounty.reward"));
				Alerts.bountyChange(e1);
			}
			return;
		}

		if (!t.hasPermission("bountyhunters.claim"))
			return;

		Bounty bounty = bountyManager.getBounty(p);

		// own bounty claiming option
		if (bounty.hasCreator())
			if (!BountyHunters.plugin.getConfig().getBoolean("own-bounty-claiming") && bounty.hasCreator(t))
				return;

		// API
		BountyClaimEvent bountyEvent = new BountyClaimEvent(bounty, t);
		Bukkit.getPluginManager().callEvent(bountyEvent);
		if (bountyEvent.isCancelled())
			return;

		// physical drops
		// bounty effects
		// drop player head
		dropOptions(random, p);

		// give money
		BountyHunters.getEconomy().depositPlayer(t, bounty.getReward());

		Alerts.claimBounty(p.getKiller(), bounty);

		// add 1 to claimed bounties, update level
		PlayerData playerData = PlayerData.get(t);
		playerData.addClaimedBounties(1);
		if (BountyHunters.plugin.getConfig().getBoolean("enable-quotes-levels-titles"))
			playerData.checkForLevelUp(t);

		// add 1 to successful bounties
		if (bounty.hasCreator()) {
			PlayerData playerData1 = PlayerData.get(bounty.getCreator());
			playerData1.addSuccessfulBounties(1);
		}

		// display death quote
		if (BountyHunters.plugin.getConfig().getBoolean("enable-quotes-levels-titles")) {
			String deathQuote = playerData.getQuote();
			if (!deathQuote.equals("")) {
				boolean bool = BountyHunters.plugin.getConfig().getBoolean("display-death-quote-on-title");
				for (Player t2 : Bukkit.getOnlinePlayers()) {
					t2.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + t.getName() + "> " + deathQuote);
					if (bool)
						BountyHunters.title.title(t2, ChatColor.GOLD + "" + ChatColor.BOLD + t.getName().toUpperCase(), ChatColor.ITALIC + deathQuote, 10, 60, 10);
				}
			}
		}

		bounty.unregister();
	}

	private void dropOptions(Random random, Player p) {

		// drop head
		if (BountyHunters.plugin.getConfig().getBoolean("drop-head.enabled") && random.nextDouble() <= BountyHunters.plugin.getConfig().getDouble("drop-head.chance") / 100) {
			ItemStack head = CustomItem.PLAYER_HEAD.a().clone();
			SkullMeta headMeta = (SkullMeta) head.getItemMeta();
			headMeta.setDisplayName(headMeta.getDisplayName().replace("%name%", p.getName()));
			headMeta.setOwner(p.getName());
			head.setItemMeta(headMeta);

			p.getWorld().dropItemNaturally(p.getLocation(), head);
		}

		// effect
		if (BountyHunters.plugin.getConfig().getBoolean("bounty-effect.enabled")) {
			String format = BountyHunters.plugin.getConfig().getString("bounty-effect.material").toUpperCase().replace("-", "_").replace(" ", "_");
			Material m = null;
			try {
				m = Material.valueOf(format);
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[Bounty Hunters] No such material found: " + format + ".");
			}
			for (int j = 0; j < 8; j++) {
				ItemStack stack = new ItemStack(m);
				ItemMeta stack_meta = stack.getItemMeta();
				stack_meta.setDisplayName("BOUNTYHUNTERS:chest " + p.getUniqueId().toString() + " " + j);
				stack.setItemMeta(stack_meta);

				Item item = p.getWorld().dropItemNaturally(p.getLocation(), stack);
				item.setMetadata("BOUNTYHUNTERS:no_pickup", new FixedMetadataValue(BountyHunters.plugin, true));
				Bukkit.getScheduler().scheduleSyncDelayedTask(BountyHunters.plugin, new Runnable() {
					public void run() {
						item.remove();
					}
				}, 40 + new Random().nextInt(30));
			}
		}

		// physical rewards
		if (BountyHunters.plugin.getConfig().getBoolean("physical-rewards.enabled"))
			for (String s : BountyHunters.plugin.getConfig().getConfigurationSection("physical-rewards.list").getKeys(false)) {
				try {
					String[] split = BountyHunters.plugin.getConfig().getString("physical-rewards.list." + s).split(Pattern.quote(" "));
					ItemStack i = new ItemStack(Material.valueOf(s.toUpperCase().replace("-", "_").replace(" ", "_")), (int) Double.parseDouble(split[0]), (split.length > 1 ? (short) Double.parseDouble(split[1]) : (short) 0));
					p.getWorld().dropItem(p.getLocation(), i);
				} catch (Exception e) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[Bounty Hunters] Wrong item format: " + s + ":" + BountyHunters.plugin.getConfig().getString("physical-rewards.list." + s));
				}
			}
	}
}