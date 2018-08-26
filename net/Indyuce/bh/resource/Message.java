package net.Indyuce.bh.resource;

public enum Message {
	SET_BY("Set by &f%creator%&7."),
	SET_BY_YOURSELF("You set this bounty."),
	REWARD_IS("The reward is &f$%reward%&7."),
	KILL_HIM_CLAIM_BOUNTY("Kill him to claim the bounty!"),
	DONT_LET_THEM_KILL_U("Don't let them kill you."),
	THUG_PLAYER("This player is a thug!"),
	RIGHT_CLICK_REMOVE_BOUNTY("Right click to remove this bounty."),
	CLICK_BUY_COMPASS("Click to buy the compass for $%price%."),
	CLICK_TARGET("Click to target him."),
	CLICK_UNTARGET("Click to untarget him."),
	CLICK_UP_BOUNTY("<Shift Click> to increase the bounty."),
	CURRENT_HUNTERS("There are &f%hunters% &7players tracking him."),
	NO_TITLE("&cNo Title"),
	NO_PLAYER("&c- No Player -"),

	// gui
	GUI_NAME("&nBounties (%page%/%max-page%)"),
	LEADERBOARD_GUI_NAME("&nHunter Leaderboard"),
	CHAT_BAR("&e&m-----------------------------------------------------"),

	// bounty creation
	BOUNTY_CREATED("You succesfully set a bounty on &f%target%&e."),
	BOUNTY_EXPLAIN("The first to kill this player will receive &f$%reward%&e."),
	NEW_BOUNTY_ON_YOU("&f%creator% &ejust set a bounty on you!"),
	NEW_BOUNTY_ON_YOU_ILLEGAL("You killed a man illegally! A bounty was set on you!"),
	NEW_BOUNTY_ON_YOU_UNDEFINED("Someone just set a bounty on you!"),
	NEW_BOUNTY_ON_PLAYER("&f%creator% &eset a bounty on &f%target%&e! Kill him to get &f$%reward%&e!"),
	NEW_BOUNTY_ON_PLAYER_ILLEGAL("&f%target%&e killed a player illegally: a bounty was set on him! Kill him to get &f$%reward%&e!"),
	NEW_BOUNTY_ON_PLAYER_UNDEFINED("Someone set a bounty on &f%target%&e! Kill him to get &f$%reward%&e!"),
	BOUNTY_CHANGE("&f%player%&e's bounty is now &f$%reward%&e."),

	// bounty claim
	BOUNTY_CLAIMED("&f%killer% &eclaimed &f%target%&e's bounty: &f$%reward%&e!"),
	BOUNTY_CLAIMED_BY_YOU("You succesfully claimed &f%target%&e's bounty: &f$%reward%&e!"),

	ERROR_PLAYER("Couldn't find the player called %arg%."),
	NOT_VALID_NUMBER("&f%arg% &eis not a valid number!"),
	NEW_HUNTER_ALERT("%hunter% now targets you."),
	EMPTY_INV_FIRST("Please empty your inventory first."),
	IN_ANOTHER_WORLD("IN ANOTHER WORLD"),
	NOT_ENOUGH_PERMS("You don't have enough permissions."),
	COMMAND_USAGE("Usage: %command%"),
	BOUNTY_IMUN("You can't set a bounty on this player."),
	TRACK_IMUN("You can't track this player."),
	TARGET_COOLDOWN("Please wait another %remain% second%s% to target a player."),
	CANT_SET_BOUNTY_ON_YOURSELF("You can't set a bounty on yourself!"),
	BOUNTY_EXPIRED("The bounty on &f%target% &ehas expired."),
	WRONG_REWARD("Reward must be between &f%min% &cand &f%max%&c!"),
	NOT_ENOUGH_MONEY("You don't have enough money."),
	BOUNTY_SET_RESTRICTION("You must wait %left% more second%s% before creating a bounty."),
	TARGET_SET("Target set."),
	TARGET_REMOVED("Target removed."),
	BOUGHT_COMPASS("You Succesfully bought a &ftracking compass&e."),
	TAX_EXPLAIN("&f%percent%&e% of the reward (&f$%price%&e) were taken as tax."),
	PLAYER_MUST_BE_CONNECTED("The player must be connected in order to be tracked."),

	// level up
	LEVEL_UP("&lWell done!&e You're level &f%level% &enow!"),
	LEVEL_UP_2("Claim &f%bounties% &emore bounties to level up again!"),
	LEVEL_UP_REWARDS("Put your cursor over this message to see your rewards!"),
	LEVEL_UP_REWARD("- &f"),

	CLICK_SELECT("Click to select."),
	SUCCESSFULLY_SELECTED("You successfully selected &f%item%&e."),
	UNLOCKED_TITLES("Unlocked titles:"),
	UNLOCKED_QUOTES("Unlocked quotes:"),
	TRACKING_COMPASS_RESET("Compass reset."),
	ADDING_MONEY_TO_BOUNTY("Please type in the amount you want to add to the bounty."),
	CONSOLE("console");

	public Object value;

	private Message(Object value) {
		this.value = value;
	}
	
	
}