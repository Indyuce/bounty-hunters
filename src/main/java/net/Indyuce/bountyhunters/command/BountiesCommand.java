package net.Indyuce.bountyhunters.command;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.ConfigFile;
import net.Indyuce.bountyhunters.api.CustomItem;
import net.Indyuce.bountyhunters.api.event.BountyExpireEvent;
import net.Indyuce.bountyhunters.api.language.Language;
import net.Indyuce.bountyhunters.api.language.Message;
import net.Indyuce.bountyhunters.api.player.PlayerData;
import net.Indyuce.bountyhunters.api.player.reward.BountyAnimation;
import net.Indyuce.bountyhunters.api.player.reward.HunterTitle;
import net.Indyuce.bountyhunters.gui.BountyEditor;
import net.Indyuce.bountyhunters.gui.BountyList;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Optional;

public class BountiesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        // Open bounties menu
        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command is for players only.");
                return true;
            }

            if (!sender.hasPermission("bountyhunters.list")) {
                Message.NOT_ENOUGH_PERMS.format().send(sender);
                return true;
            }

            new BountyList((Player) sender).open();
            return true;
        }

        // Help
        if (args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("bountyhunters.admin")) {
                Message.NOT_ENOUGH_PERMS.format().send(sender);
                return true;
            }

            sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-----------------[" + ChatColor.LIGHT_PURPLE + " BountyHunters Help " + ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "]-----------------");
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Player Commands");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounty <player> <reward>" + ChatColor.WHITE + " sets a bounty on a player.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties" + ChatColor.WHITE + " shows current bounties.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/hunters" + ChatColor.WHITE + " opens the hunter leaderboard.");
            sender.sendMessage("");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties animations" + ChatColor.WHITE + " lists available animations.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties titles" + ChatColor.WHITE + " lists available titles.");
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Admin");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties help" + ChatColor.WHITE + " shows the help page.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties reload" + ChatColor.WHITE + " reloads the config file.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties remove <player>" + ChatColor.WHITE + " removes a bounty.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/bounties edit <player>" + ChatColor.WHITE + " edits a bounty.");
            return true;
        }

        // Edit bounty
        if (args[0].equalsIgnoreCase("edit")) {
            if (!sender.hasPermission("bountyhunters.admin")) {
                Message.NOT_ENOUGH_PERMS.format().send(sender);
                return true;
            }

            if (!BountyHunters.getInstance().getConfig().getBoolean("bounty-stacking")) {
                sender.sendMessage(ChatColor.RED + "This command is not available while bounty stacking is disabled.");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command is only for players.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /bounties edit <player>");
                return true;
            }

            Optional<Bounty> bounty = BountyHunters.getInstance().getBountyManager().findFirstByName(args[1]);
            if (bounty.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Couldn't find a bounty on " + args[1] + ".");
                return true;
            }

            new BountyEditor((Player) sender, bounty.get()).open();
        }

        // Remove bounty
        if (args[0].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission("bountyhunters.admin")) {
                Message.NOT_ENOUGH_PERMS.format().send(sender);
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /bounties remove <player>");
                return true;
            }

            int removed = 0;
            Iterator<Bounty> ite = BountyHunters.getInstance().getBountyManager().getBounties().iterator();
            while (ite.hasNext()) {
                Bounty next = ite.next();
                if (next.getTarget().getName().equals(args[1])) {
                    BountyExpireEvent bountyEvent = new BountyExpireEvent(next);
                    Bukkit.getPluginManager().callEvent(bountyEvent);
                    if (bountyEvent.isCancelled())
                        continue;

                    ite.remove();
                    BountyHunters.getInstance().getBountyManager().unregisterBounty(next, false);
                    removed++;
                }
            }

            sender.sendMessage(ChatColor.RED + "Unregistered a total of " + removed + " bounties on " + args[1] + ".");
        }

        // Choose title
        if (args[0].equalsIgnoreCase("title") && args.length > 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command is for players only.");
                return true;
            }

            if (!sender.hasPermission("bountyhunters.title")) {
                Message.NOT_ENOUGH_PERMS.format().send(sender);
                return true;
            }

            if (!BountyHunters.getInstance().getLevelManager().hasTitle(args[1]))
                return true;

            PlayerData playerData = PlayerData.get(player);
            if (!playerData.canSelectItem())
                return true;

            HunterTitle item = BountyHunters.getInstance().getLevelManager().getTitle(args[1]);
            if (!playerData.hasUnlocked(item))
                return true;

            playerData.setTitle(item);
            playerData.setLastSelect();
            Message.SUCCESSFULLY_SELECTED.format("item", playerData.getTitle().format()).send(sender);
        }

        // Choose quote
        if (args[0].equalsIgnoreCase("animation") && args.length > 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command is for players only.");
                return true;
            }

            if (!player.hasPermission("bountyhunters.animation")) {
                Message.NOT_ENOUGH_PERMS.format().send(sender);
                return true;
            }

            if (!BountyHunters.getInstance().getLevelManager().hasAnimation(args[1]))
                return true;

            PlayerData playerData = PlayerData.get(player);
            if (!playerData.canSelectItem())
                return true;

            BountyAnimation item = BountyHunters.getInstance().getLevelManager().getAnimation(args[1]);
            if (!playerData.hasUnlocked(item))
                return true;

            playerData.setAnimation(item);
            playerData.setLastSelect();
            Message.SUCCESSFULLY_SELECTED.format("item", playerData.getAnimation().format()).send(sender);
        }

        // Choose title
        if (args[0].equalsIgnoreCase("titles")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command is for players only.");
                return true;
            }

            if (!player.hasPermission("bountyhunters.title")) {
                Message.NOT_ENOUGH_PERMS.format().send(player);
                return true;
            }

            Message.UNLOCKED_TITLES.format().send(player);

            PlayerData playerData = PlayerData.get(player);
            for (HunterTitle title : BountyHunters.getInstance().getLevelManager().getTitles())
                if (playerData.hasUnlocked(title))
                    ((Player) sender).spigot().sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText("{\"text\":\"* " + ChatColor.GREEN + title.format() + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bounties title " + title.getId() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Language.CLICK_SELECT.format() + "\",\"color\":\"white\"}]}}}"));
        }

        // Animations list
        if (args[0].equalsIgnoreCase("animations")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command is for players only.");
                return true;
            }

            if (!player.hasPermission("bountyhunters.animation")) {
                Message.NOT_ENOUGH_PERMS.format().send(player);
                return true;
            }

            Message.UNLOCKED_ANIMATIONS.format().send(player);

            PlayerData playerData = PlayerData.get(player);
            for (BountyAnimation anim : BountyHunters.getInstance().getLevelManager().getAnimations())
                if (playerData.hasUnlocked(anim))
                    ((Player) sender).spigot().sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText("{\"text\":\"* " + ChatColor.GREEN + anim.format() + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/bounties animation " + anim.getId() + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"" + Language.CLICK_SELECT.format() + "\",\"color\":\"white\"}]}}}"));
        }

        // Reload plugin
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("bountyhunters.admin")) {
                Message.NOT_ENOUGH_PERMS.format().send(sender);
                return true;
            }

            BountyHunters.getInstance().reloadConfig();
            BountyHunters.getInstance().reloadConfigFiles();
            BountyHunters.getInstance().getLevelManager().reload(new ConfigFile("levels").getConfig());

            FileConfiguration items = new ConfigFile("/language", "items").getConfig();
            for (CustomItem item : CustomItem.values())
                item.update(items.getConfigurationSection(item.name()));

            sender.sendMessage(ChatColor.YELLOW + BountyHunters.getInstance().getName() + " " + BountyHunters.getInstance().getDescription().getVersion() + " reloaded.");
        }

        return false;
    }
}
