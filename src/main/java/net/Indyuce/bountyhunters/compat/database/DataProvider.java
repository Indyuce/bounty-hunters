package net.Indyuce.bountyhunters.compat.database;

import net.Indyuce.bountyhunters.manager.BountyManager;
import net.Indyuce.bountyhunters.manager.PlayerDataManager;

public interface DataProvider {
	PlayerDataManager providePlayerData();

	BountyManager provideBounties();
}
