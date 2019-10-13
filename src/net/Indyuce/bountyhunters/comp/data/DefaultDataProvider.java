package net.Indyuce.bountyhunters.comp.data;

import net.Indyuce.bountyhunters.comp.data.player.YAMLPlayerDataManager;
import net.Indyuce.bountyhunters.manager.BountyManager;
import net.Indyuce.bountyhunters.manager.PlayerDataManager;

public class DefaultDataProvider implements DataProvider {

	@Override
	public BountyManager provideBounties() {
		return null;
	}

	@Override
	public PlayerDataManager providePlayerData() {
		return new YAMLPlayerDataManager();
	}
}
