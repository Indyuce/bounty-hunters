package net.Indyuce.bountyhunters.api.language;

import org.bukkit.ChatColor;

public enum Language {

	/*
	 * menu
	 */
	CLICK_BUY_COMPASS("&eClick to buy the compass for ${price}."),
	NO_TITLE("&cNo Title"),
	NO_PLAYER("&c- No Player -"),
	GUI_NAME("&nBounties ({page}/{max_page})"),
	LEADERBOARD_GUI_NAME("&nHunter Leaderboard"),
	REDEEMABLE_HEADS("Redeem your player heads"),
	CLICK_SELECT("&eClick to select."),

	/*
	 * misc
	 */
	COMPASS_FORMAT("&7&l[ &6&l{blocks} blocks &7&l]"),
	CONSOLE("console"),

	/*
	 * level up
	 */
	LEVEL_UP_REWARDS("&eHover this message to see your rewards!"),
	LEVEL_UP_REWARD("&e- &f{reward}"),
	LEVEL_UP_REWARD_MONEY("&e- &f${amount}"),

	;

	private String format;

	private Language(String format) {
		this.format = format;
	}

	public String getPath() {
		return name().toLowerCase().replace("_", "-");
	}

	public String getDefault() {
		return format;
	}

	public String format(String... placeholders) {
		String str = new String(format);

		for (int k = 0; k < placeholders.length; k += 2)
			str = str.replace("{" + placeholders[k] + "}", placeholders[k + 1]);

		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public void update(String str) {
		format = str;
	}
}
