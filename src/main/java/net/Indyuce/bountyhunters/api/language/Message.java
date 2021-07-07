package net.Indyuce.bountyhunters.api.language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import net.Indyuce.bountyhunters.version.VersionSound;

public enum Message {

	/*
	 * bounty creation, auto-bounties and bounty updates
	 */
	BOUNTY_CREATED(new SoundReader(Sound.ENTITY_PLAYER_LEVELUP, 1, 2), "&eYou successfully set a bounty on &6{target}&e.", "&eThe first to kill this player will receive &6${reward}&e."),
	NEW_BOUNTY_ON_YOU(new SoundReader(VersionSound.ENTITY_ENDERMAN_HURT.toSound(), 1, 0), "&6{creator} &ejust set a &6${reward}&e bounty on you!"),
	NEW_BOUNTY_ON_YOU_ILLEGAL(new SoundReader(VersionSound.ENTITY_ENDERMAN_HURT.toSound(), 1, 0), "&eYou killed a man illegally! A ${reward} bounty was set on you!"),
	NEW_BOUNTY_ON_YOU_UNDEFINED(new SoundReader(VersionSound.ENTITY_ENDERMAN_HURT.toSound(), 1, 0), "&eSomeone just set a ${reward} bounty on you!"),
	NEW_BOUNTY_ON_PLAYER(new SoundReader(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1), "&6{creator} &eset a bounty on &6{target}&e! Kill him to get &6${reward}&e!"),
	NEW_BOUNTY_ON_PLAYER_ILLEGAL(new SoundReader(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1), "&6{target}&e killed a player illegally: a bounty was set on him! Kill him to get &6${reward}&e!"),
	NEW_BOUNTY_ON_PLAYER_UNDEFINED(new SoundReader(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1), "&eSomeone set a bounty on &6{target}&e! Kill him to get &6${reward}&e!"),
	BOUNTY_CHANGE(new SoundReader(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1), "&6{player}&e's bounty is now &6${reward}&e."),

	/*
	 * bounty claim
	 */
	BOUNTY_CLAIMED(new SoundReader(Sound.ENTITY_PLAYER_LEVELUP, 1, 1), "&6{killer} &eclaimed &6{target}&e's bounty: &6${reward}&e!"),
	BOUNTY_CLAIMED_BY_YOU(new SoundReader(Sound.ENTITY_PLAYER_LEVELUP, 1, 1), "&eYou successfully claimed &6{target}&e's bounty: &6${reward}&e!"),
	HEAD_DROPPED(new SoundReader(Sound.ENTITY_PLAYER_LEVELUP, 1, 1), "&eYou picked up &6{victim}&e's head, bring it to &6{creator}&e to claim the bounty."),

	ERROR_PLAYER("&cCouldn't find the player called {arg}."),
	NOT_VALID_NUMBER("&c{arg} is not a valid number."),
	NEW_HUNTER_ALERT(new SoundReader(VersionSound.ENTITY_ENDERMAN_HURT.toSound(), 1, 0), "&c{hunter} now targets you."),
	EMPTY_INV_FIRST("&cPlease empty your inventory first."),
	NOT_ENOUGH_PERMS("&cYou don't have enough permissions."),
	COMMAND_USAGE("&cUsage: {command}"),
	BOUNTY_IMUN("&cYou can't set a bounty on this player."),
	TRACK_IMUN("&cYou can't track this player."),
	CANT_TRACK_CREATOR("&cYou created this bounty, you can't target him."),
	TARGET_COOLDOWN("&cPlease wait another {remain} second{s} to target a player."),
	CANT_SET_BOUNTY_ON_YOURSELF("&cYou can't set a bounty on yourself!"),
	BOUNTY_EXPIRED(new SoundReader(Sound.ENTITY_VILLAGER_NO, 1, 2), "&eThe bounty on &6{target} &ehas expired."),
	BOUNTY_EXPIRED_INACTIVITY("&eThe bounty on &6{target} &ehas expired due to inactivity."),
	BOUNTY_DECREASED("&eBounty on &6{target} &ehas decreased from &6${old} &eto &6${new}&e."),
	REWARD_MUST_BE_HIGHER("&cReward must be higher than ${min}!"),
	REWARD_MUST_BE_LOWER("&cReward must be lower than ${max}!"),
	NOT_ENOUGH_MONEY("&cYou don't have enough money."),
	TOO_MANY_BOUNTIES("&cYou have created too many bounties."),
	BOUNTY_SET_RESTRICTION("&cYou must wait {left} more second{s} before creating a bounty."),
	TARGET_SET("&eTarget set."),
	TARGET_REMOVED("&eTarget removed."),
	BOUGHT_COMPASS(new SoundReader(Sound.ENTITY_PLAYER_LEVELUP, 1, 2), "&eYou succesfully bought a &6tracking compass&e."),
	TAX_EXPLAIN("&6{percent}%&e of the reward (&6${price}&e) were taken as tax."),
	PLAYER_MUST_BE_CONNECTED("&cThe player must be connected in order to be tracked."),
	BOUNTY_INDICATION("&eBounty on &6{player}&e is &6${reward}&e."),
	NO_BOUNTY_INDICATION("&c{player} has no bounty."),

	/*
	 * player heads
	 */
	MUST_REDEEM_HEAD("&eYou may redeem &6{target}&e's head using &6/redeembountyheads&e whenever you have some free inventory space."),
	OBTAINED_HEAD("&eYou obtained &6{target}&e's head."),
	REDEEM_HEAD(new SoundReader(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2), "&eYou successfully redeemed &6{target}&e's head."),
	NO_HEAD_TO_REDEEM("&cYou have no head to redeem."),

	/*
	 * level up and cosmetics
	 */
	LEVEL_UP("&e-----------------------------------------------------", "&e&lWell done!&e You're level &6{level} &enow!", "&eClaim &6{bounties} &emore bounties to level up again!"),
	SUCCESSFULLY_SELECTED(new SoundReader(Sound.ENTITY_PLAYER_LEVELUP, 1, 2), "&eYou successfully selected &6{item}&e."),
	UNLOCKED_TITLES("&e-----------------------------------------------------", "&eUnlocked titles:"),
	UNLOCKED_ANIMATIONS("&e-----------------------------------------------------", "&eUnlocked animations:"),

	;

	private List<String> message;
	private SoundReader sound;

	private Message(String... message) {
		this(null, message);
	}

	private Message(SoundReader sound, String... message) {
		this.message = Arrays.asList(message);
		this.sound = sound;
	}

	public String getPath() {
		return name().toLowerCase().replace("_", "-");
	}

	/*
	 * gives an editable copy of the format
	 */
	public List<String> getDefault() {
		return new ArrayList<>(message);
	}

	public SoundReader getSound() {
		return sound;
	}

	public boolean hasSound() {
		return sound != null;
	}

	public PlayerMessage format(Object... placeholders) {
		return new PlayerMessage(this).format(placeholders);
	}

	public void update(ConfigurationSection config) {
		List<String> format = config.getStringList("format");
		Validate.notNull(this.message = format, "Could not read message format");
		sound = config.contains("sound") ? new SoundReader(config.getConfigurationSection("sound")) : null;
	}
}