package net.Indyuce.bountyhunters.comp.database;

import net.Indyuce.bountyhunters.manager.BountyManager;
import net.Indyuce.bountyhunters.manager.PlayerDataManager;

public interface DataProvider {
	PlayerDataManager providePlayerData();

	BountyManager provideBounties();
}
