package net.Indyuce.bountyhunters.api.player.reward;

import net.Indyuce.bountyhunters.api.player.reward.particle.ParticleEffect;
import net.Indyuce.bountyhunters.api.player.reward.particle.ParticleEffectType;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class BountyAnimation extends LevelUpItem {
    private final ParticleEffect effect;

    public BountyAnimation(ConfigurationSection config) {
        super(config.getName().toUpperCase().replace("-", "_"), config.getString("format"), config.getInt("unlock"));

        effect = (config.contains("effect") ? ParticleEffectType.valueOf(config.getString("effect")
                .toUpperCase().replace("-", "_").replace(" ", "_")) : ParticleEffectType.NONE).getHandler();
    }

    public BountyAnimation(String id, String format, int unlock, ParticleEffect effect) {
        super(id, format, unlock);

        Validate.notNull(this.effect = effect, "Effect cannot be null, use NONE for none");
    }

    public ParticleEffect getEffect() {
        return effect;
    }
}