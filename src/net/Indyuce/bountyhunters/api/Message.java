package net.Indyuce.bountyhunters.api;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum Message {

	// gui
	CLICK_BUY_COMPASS("Click to buy the compass for $%price%."),
	NO_TITLE("&cNo Title"),
	NO_PLAYER("&c- No Player -"),
	GUI_NAME("&nBounties (%page%/%max-page%)"),
	LEADERBOARD_GUI_NAME("&nHunter Leaderboard"),
	CHAT_BAR("-----------------------------------------------------"),

	// bounty creation
	BOUNTY_CREATED("You succesfully set a bounty on &6%target%&e."),
	BOUNTY_EXPLAIN("The first to kill this player will receive &6$%reward%&e."),
	NEW_BOUNTY_ON_YOU("&6%creator% &ejust set a &6$%reward%&e bounty on you!"),
	NEW_BOUNTY_ON_YOU_ILLEGAL("You killed a man illegally! A $%reward% bounty was set on you!"),
	NEW_BOUNTY_ON_YOU_UNDEFINED("Someone just set a $%reward% bounty on you!"),
	NEW_BOUNTY_ON_PLAYER("&6%creator% &eset a bounty on &6%target%&e! Kill him to get &6$%reward%&e!"),
	NEW_BOUNTY_ON_PLAYER_ILLEGAL("&6%target%&e killed a player illegally: a bounty was set on him! Kill him to get &6$%reward%&e!"),
	NEW_BOUNTY_ON_PLAYER_UNDEFINED("Someone set a bounty on &6%target%&e! Kill him to get &6$%reward%&e!"),
	BOUNTY_CHANGE("&6%player%&e's bounty is now &6$%reward%&e."),

	// bounty claim
	BOUNTY_CLAIMED("&6%killer% &eclaimed &6%target%&e's bounty: &6$%reward%&e!"),
	BOUNTY_CLAIMED_BY_YOU("You successfully claimed &6%target%&e's bounty: &6$%reward%&e!"),

	ERROR_PLAYER("Couldn't find the player called %arg%."),
	NOT_VALID_NUMBER("%arg% is not a valid number."),
	NEW_HUNTER_ALERT("%hunter% now targets you."),
	EMPTY_INV_FIRST("Please empty your inventory first."),
	NOT_ENOUGH_PERMS("You don't have enough permissions."),
	COMMAND_USAGE("Usage: %command%"),
	BOUNTY_IMUN("You can't set a bounty on this player."),
	TRACK_IMUN("You can't track this player."),
	CANT_TRACK_CREATOR("You created this bounty, you can't target him."),
	TARGET_COOLDOWN("Please wait another %remain% second%s% to target a player."),
	CANT_SET_BOUNTY_ON_YOURSELF("You can't set a bounty on yourself!"),
	BOUNTY_EXPIRED("The bounty on &6%target% &ehas expired."),
	REWARD_MUST_BE_HIGHER("Reward must be higher than $%min%!"),
	REWARD_MUST_BE_LOWER("Reward must be lower than $%max%!"),
	NOT_ENOUGH_MONEY("You don't have enough money."),
	BOUNTY_SET_RESTRICTION("You must wait %left% more second%s% before creating a bounty."),
	TARGET_SET("Target set."),
	TARGET_REMOVED("Target removed."),
	BOUGHT_COMPASS("You Succesfully bought a &6tracking compass&e."),
	TAX_EXPLAIN("&6%percent%&e% of the reward (&6$%price%&e) were taken as tax."),
	PLAYER_MUST_BE_CONNECTED("The player must be connected in order to be tracked."),

	// level up
	LEVEL_UP("&lWell done!&e You're level &6%level% &enow!"),
	LEVEL_UP_2("Claim &6%bounties% &emore bounties to level up again!"),
	LEVEL_UP_REWARDS("Hover this message to see your rewards!"),
	LEVEL_UP_REWARD("- &f%reward%"),
	LEVEL_UP_REWARD_MONEY("- &f$%amount%"),

	CLICK_SELECT("Click to select."),
	SUCCESSFULLY_SELECTED("You successfully selected &6%item%&e."),
	UNLOCKED_TITLES("Unlocked titles:"),
	UNLOCKED_QUOTES("Unlocked quotes:"),
	TRACKING_COMPASS_RESET("Compass reset."),
	CONSOLE("console"),
	// COMPASS_IN_ANOTHER_WORLD("In another world"),
	COMPASS_FORMAT("&7&l[ &6&l%blocks% blocks &7&l]");

	private String message;

	private Message(String message) {
		this.message = message;
	}

	public String getUncolored() {
		return message;
	}

	public String getMessage() {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public String getPath() {
		return name().toLowerCase().replace("_", "-");
	}

	public void update(String string) {
		message = string;
	}

	public String formatRaw(ChatColor prefix, String... toReplace) {
		String message = prefix + getMessage();
		for (int j = 0; j < toReplace.length; j += 2)
			message = message.replace(toReplace[j], toReplace[j + 1]);
		return message;
	}

	// toReplace length must be even
	public String formatRaw(String... toReplace) {
		String message = getMessage();
		for (int j = 0; j < toReplace.length; j += 2)
			message = message.replace(toReplace[j], toReplace[j + 1]);
		return message;
	}

	public PlayerMessage format(ChatColor prefix, String... toReplace) {
		return new PlayerMessage(formatRaw(prefix, toReplace));
	}

	public class PlayerMessage {
		private final String message;

		public PlayerMessage(String message) {
			this.message = message;
		}

		// send if message is not empty
		public void send(CommandSender sender) {
			if (!ChatColor.stripColor(message).equals(""))
				sender.sendMessage(message);
		}
	}

}