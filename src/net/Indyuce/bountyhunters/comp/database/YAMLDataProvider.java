package net.Indyuce.bountyhunters.comp.database;

import net.Indyuce.bountyhunters.comp.database.yaml.YAMLBountyManager;
import net.Indyuce.bountyhunters.comp.database.yaml.YAMLPlayerDataManager;
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
