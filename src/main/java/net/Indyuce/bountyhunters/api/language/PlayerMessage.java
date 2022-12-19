package net.Indyuce.bountyhunters.api.language;

import net.Indyuce.bountyhunters.BountyHunters;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class PlayerMessage {
    private final Message initialMessage;
    private final List<String> format;

    public PlayerMessage(Message message) {
        format = (this.initialMessage = message).getDefault();
    }

    public PlayerMessage format(Object... placeholders) {
        for (int k = 0; k < format.size(); k++)
            format.set(k, applyPlaceholders(format.get(k), placeholders));
        return this;
    }

    public void send(Collection<? extends Player> senders) {
        senders.forEach(sender -> send(sender));
    }

    public void send(CommandSender sender) {
        if (format.isEmpty())
            return;

        // Play sound
        if (initialMessage.hasSound() && sender instanceof Player)
            initialMessage.getSound().play((Player) sender);
        format.forEach(str -> sender.sendMessage(formatMessage(sender, str)));
    }

    private String formatMessage(CommandSender sender, String format) {
        return sender instanceof Player ? BountyHunters.getInstance().getPlaceholderParser().parse((Player) sender, format) : format;
    }

	private String applyPlaceholders(String str, Object... placeholders) {
		for (int k = 0; k < placeholders.length; k += 2)
			str = str.replace("{" + placeholders[k] + "}", placeholders[k + 1].toString());
		return ChatColor.translateAlternateColorCodes('&', str);
	}
}
