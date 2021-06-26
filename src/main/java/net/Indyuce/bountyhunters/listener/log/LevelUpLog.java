package net.Indyuce.bountyhunters.listener.log;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.event.HunterLevelUpEvent;

public class LevelUpLog implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void a(HunterLevelUpEvent event) {
		BountyHunters.getInstance().getLogger().log(Level.INFO, event.getPlayer() + " reached Lvl " + event.getNewLevel());
	}
}
