package net.Indyuce.bountyhunters.api.language;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class PlayerMessage {
    private final Message message;
    private final List<String> format;

    public PlayerMessage(Message message) {
        format = (this.message = message).getDefault();
    }

    public PlayerMessage format(Object... placeholders) {
        format.replaceAll(str -> apply(str, placeholders));
        return this;
    }

    private String apply(String str, Object... placeholders) {
        for (int k = 0; k < placeholders.length; k += 2)
            str = str.replace("{" + placeholders[k] + "}", placeholders[k + 1].toString());
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public void send(Collection<? extends Player> senders) {
        senders.forEach(this::send);
    }

    public void send(CommandSender sender) {
        if (format.isEmpty())
            return;

        if (message.hasSound() && sender instanceof Player)
            message.getSound().play((Player) sender);
        format.forEach(sender::sendMessage);
    }
}
