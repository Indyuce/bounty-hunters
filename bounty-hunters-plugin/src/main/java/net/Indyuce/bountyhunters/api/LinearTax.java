package net.Indyuce.bountyhunters.api;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Tax that is composed of a flat component and a scaling
 * component.
 * <p>
 * Example: $100 + 3% of the amount involved
 * - flat component is 100
 * - scale component is 3%
 */
public class LinearTax {
    private final double flat, scale;

    public LinearTax(double flat, double scale) {
        this.flat = flat;
        this.scale = scale;

        Validate.isTrue(scale >= 0 && scale <= 100, "Scale must be between 0 and 100");
    }

    public LinearTax(ConfigurationSection config) {
        Validate.notNull(config, "Config cannot be null");

        this.flat = config.getDouble("flat");
        this.scale = config.getDouble("scale");

        Validate.isTrue(scale >= 0 && scale <= 100, "Scale must be between 0 and 100");
    }

    public double getTax(double money) {
        return flat + scale * money / 100d;
    }
}
