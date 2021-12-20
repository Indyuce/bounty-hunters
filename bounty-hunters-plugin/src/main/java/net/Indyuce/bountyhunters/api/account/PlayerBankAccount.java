package net.Indyuce.bountyhunters.api.account;

import net.Indyuce.bountyhunters.BountyHunters;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerBankAccount implements BankAccount {
	private final OfflinePlayer player;

	public PlayerBankAccount(String input) {
		Validate.notNull(input, "Could not read UUID");

		UUID uuid = UUID.fromString(input);
		Validate.notNull(uuid, "Could not read UUID from '" + uuid + "'");

		player = Bukkit.getOfflinePlayer(uuid);
		Validate.notNull(player, "Could not find player with UUID '" + uuid.toString() + "'");
	}

	@Override
	public void deposit(double amount) {
		BountyHunters.getInstance().getEconomy().depositPlayer(player, amount);
	}
}