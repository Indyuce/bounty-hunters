package net.Indyuce.bountyhunters.compat.database;

import net.Indyuce.bountyhunters.compat.database.yaml.YAMLBountyManager;
import net.Indyuce.bountyhunters.compat.database.yaml.YAMLPlayerDataManager;
import net.Indyuce.bountyhunters.manager.BountyManager;
import net.Indyuce.bountyhunters.manager.PlayerDataManager;

public class YAMLDataProvider implements DataProvider {

    @Override
    public BountyManager provideBounties() {
        return new YAMLBountyManager();
    }

    @Override
    public PlayerDataManager providePlayerDatas() {
        return new YAMLPlayerDataManager();
    }
}
