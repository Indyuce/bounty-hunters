package net.Indyuce.bountyhunters.command;

import net.Indyuce.bountyhunters.BountyHunters;
import net.Indyuce.bountyhunters.api.Bounty;
import net.Indyuce.bountyhunters.api.BountyCommands;
import net.Indyuce.bountyhunters.api.NumberFormat;
import net.Indyuce.bountyhunters.api.Utils;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent;
import net.Indyuce.bountyhunters.api.event.BountyCreateEvent.BountyCause;
import net.Indyuce.bountyhunters.api.event.BountyIncreaseEvent;
import net.Indyuce.bountyhunters.api.event.BountyIncreaseEvent.BountyChangeCause;
import net.Indyuce.bountyhunters.api.language.Message;
import net.Indyuce.bountyhunters.gui.BountyList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Optional;

public class AddBountyCommand implements CommandExecutor {
    private static final DecimalFormat digit1 = new DecimalFormat("0.#");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

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

        if (!sender.hasPermission("bountyhunters.add")) {
            Message.NOT_ENOUGH_PERMS.format().send(sender);
            return true;
        }

        // Check for player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            Message.ERROR_PLAYER.format("arg", args[0]).send(sender);
            return true;
        }

        // Check player's current bounty
        if (args.length < 2) {

            Optional<Bounty> bounty = BountyHunters.getInstance().getBountyManager().getBounty(target);
            if (!bounty.isPresent()) {
                Message.NO_BOUNTY_INDICATION.format("player", target.getName()).send(sender);
                return true;
            }

            Message.BOUNTY_INDICATION.format("player", target.getName(), "reward", new NumberFormat().format(bounty.get().getReward())).send(sender);
            return true;
        }

        if (target.equals(sender) && !sender.hasPermission("bountyhunters.self")) {
            Message.CANT_SET_BOUNTY_ON_YOURSELF.format().send(sender);
            return true;
        }

        // Permission
        if (target.hasPermission("bountyhunters.immunity") && !sender.hasPermission("bountyhunters.immunity.bypass")) {
            Message.BOUNTY_IMUN.format().send(sender);
            return true;
        }

        // Reward
        double reward = 0;
        try {
            reward = Double.parseDouble(args[1]);
        } catch (NumberFormatException exception) {
            Message.NOT_VALID_NUMBER.format("arg", args[1]).send(sender);
            return true;
        }
        reward = Utils.truncate(reward, 1);

        // Minimum and maximum checks do not apply for console.
        CommandArguments arguments = new CommandArguments(args);
        if (!sender.hasPermission("bountyhunters.admin") || !arguments.bypassMinMax) {
            double min = BountyHunters.getInstance().getConfig().getDouble("min-reward");
            double max = BountyHunters.getInstance().getConfig().getDouble("max-reward");
            if (reward < min) {
                Message.REWARD_MUST_BE_HIGHER.format("min", new NumberFormat().format(min)).send(sender);
                return true;
            }
            if (max > 0 && reward > max) {
                Message.REWARD_MUST_BE_LOWER.format("max", new NumberFormat().format(max)).send(sender);
                return true;
            }
        }

        // Set restriction
        if (sender instanceof Player) {
            Player player = (Player) sender;
            long restriction = BountyHunters.getInstance().getConfig().getInt("bounty-set-restriction") * 1000;
            long left = BountyHunters.getInstance().getPlayerDataManager().get(player).getLastBounty() + restriction - System.currentTimeMillis();

            if (left > 0) {
                Message.BOUNTY_SET_RESTRICTION.format("left", left / 1000, "s", left / 1000 > 1 ? "s" : "").send(sender);
                return true;
            }
        }

        // Money restriction
        if (sender instanceof Player)
            if (!BountyHunters.getInstance().getEconomy().has((Player) sender, reward)) {
                Message.NOT_ENOUGH_MONEY.format().send(sender);
                return true;
            }

        // Tax calculation
        double tax = sender.hasPermission("bountyhunters.admin") && arguments.noTax ? 0
                : Math.max(0, Math.min(1, BountyHunters.getInstance().getConfig().getDouble("bounty-tax.bounty-creation") / 100));
        double taxed = Utils.truncate(reward * tax, 1);

        // Add to existing bounty
        Optional<Bounty> currentBounty = BountyHunters.getInstance().getBountyManager().getBounty(target);

        // Maximum amount of contributions per player
        int maxAmount = BountyHunters.getInstance().getConfig().getInt("bounty-amount-restriction");
        if (maxAmount > 0 && sender instanceof Player && (!currentBounty.isPresent() || !currentBounty.get().hasContributed((Player) sender))
                && BountyHunters.getInstance().getBountyManager().getContributions((Player) sender).size() >= maxAmount) {
            Message.TOO_MANY_BOUNTIES.format().send(sender);
            return true;
        }

        if (currentBounty.isPresent() && BountyHunters.getInstance().getConfig().getBoolean("bounty-stacking")) {

            // API
            Bounty bounty = currentBounty.get();
            BountyIncreaseEvent bountyEvent = new BountyIncreaseEvent(bounty, sender instanceof Player ? (Player) sender : null, reward - taxed,
                    sender instanceof Player ? BountyChangeCause.PLAYER : BountyChangeCause.CONSOLE);
            Bukkit.getPluginManager().callEvent(bountyEvent);
            if (bountyEvent.isCancelled())
                return true;

            // Remove balance, refresh bounty cooldown
            if (sender instanceof Player) {
                BountyHunters.getInstance().getEconomy().withdrawPlayer((Player) sender, reward);
                BountyHunters.getInstance().getPlayerDataManager().get((Player) sender).setLastBounty();
            }

            new BountyCommands("increase." + bountyEvent.getCause().name().toLowerCase().replace("_", "-"), bounty, sender).send();
            if (sender instanceof Player)
                bounty.addContribution((Player) sender, bountyEvent.getAdded());
            else
                bounty.addReward(bountyEvent.getAdded());
            bountyEvent.sendAllert();
            return true;
        }

        // API
        Bounty bounty = new Bounty(sender instanceof Player ? (Player) sender : null, target, reward - taxed);
        BountyCreateEvent bountyEvent = new BountyCreateEvent(bounty, sender instanceof Player ? (Player) sender : null,
                sender instanceof Player ? BountyCause.PLAYER : BountyCause.CONSOLE);
        Bukkit.getPluginManager().callEvent(bountyEvent);
        if (bountyEvent.isCancelled())
            return true;

        // Remove balance and register last bounty
        if (sender instanceof Player) {
            BountyHunters.getInstance().getEconomy().withdrawPlayer((Player) sender, reward);
            BountyHunters.getInstance().getPlayerDataManager().get((Player) sender).setLastBounty();
        }

        // Handle tax
        if (taxed > 0 && BountyHunters.getInstance().getTaxBankAccount() != null)
            BountyHunters.getInstance().getTaxBankAccount().deposit(taxed);

        new BountyCommands("place." + bountyEvent.getCause().name().toLowerCase().replace("_", "-"), bounty, sender).send();
        BountyHunters.getInstance().getBountyManager().registerBounty(bounty);
        bountyEvent.sendAllert();

        if (tax > 0)
            Message.TAX_EXPLAIN.format("percent", digit1.format(tax * 100), "price", new NumberFormat().format(taxed)).send(sender);
        return true;
    }

    public class CommandArguments {
        private final boolean bypassMinMax, noTax;

        public CommandArguments(String[] args) {
            bypassMinMax = has(args, "any");
            noTax = has(args, "notax");
        }

        private boolean has(String[] args, String arg) {
            for (String checked : args)
                if (checked.equalsIgnoreCase("-" + arg))
                    return true;
            return false;
        }
    }
}
