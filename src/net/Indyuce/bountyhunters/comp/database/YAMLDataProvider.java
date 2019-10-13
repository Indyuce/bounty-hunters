package net.Indyuce.bountyhunters.comp.database;

import net.Indyuce.bountyhunters.comp.database.bounty.YAMLBountyManager;
import net.Indyuce.bountyhunters.comp.database.player.YAMLPlayerDataManager;
import net.Indyuce.bountyhunters.manager.BountyManager;
import net.Indyuce.bountyhunters.manager.PlayerDataManager;

public class YAMLDataProvider implements DataProvider {

	@Override
	public BountyManager provideBounties() {
		return new YAMLBountyManager();
	}

	@Override
	public PlayerDataManager providePlayerData() {
		return new YAMLPlayerDataManager();
	}
}
