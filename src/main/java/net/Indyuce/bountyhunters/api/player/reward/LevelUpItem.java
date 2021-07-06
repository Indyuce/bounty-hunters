package net.Indyuce.bountyhunters.api.player.reward;

import net.Indyuce.bountyhunters.api.AltChar;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import org.apache.commons.lang.Validate;

public abstract class LevelUpItem {
    private final String id, format;
    private final int unlock;

    public LevelUpItem(String id, String format, int unlock) {
        Validate.notNull(id, "Item ID must not be null");
        Validate.notNull(format, "Item format must not be null");

        this.id = id;
        this.format = format;
        this.unlock = unlock;
    }

    public String getId() {
        return id;
    }

    public int getUnlockLevel() {
        return unlock;
    }

    public boolean hasUnlocked(PlayerData data) {
        return data.getLevel() > unlock;
    }

    public String format() {
        return AltChar.apply(format);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof LevelUpItem && ((LevelUpItem) obj).id.equals(id);
    }
}