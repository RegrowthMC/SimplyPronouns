package org.lushplugins.simplypronouns.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.Command;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.data.PronounsUser;

import java.util.regex.Pattern;

public class PronounsCommand extends Command {
    private static final Pattern PRONOUNS_PATTERN = Pattern.compile("[\\w ]+/?[\\w ]*");

    public PronounsCommand() {
        super("pronouns");
        addSubCommand(new BlacklistCommand());
        addSubCommand(new RequestsCommand());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Console cannot run this command");
            return true;
        }

        if (args.length != 1) {
            // TODO: Make message configurable
            ChatColorHandler.sendMessage(sender, "Invalid command format try: /pronouns <pronouns>");
            return true;
        }

        String requestedPronouns = args[0];
        if (requestedPronouns.length() > 24) {
            ChatColorHandler.sendMessage(sender, "Pronouns cannot exceed 24 characters");
            return true;
        }

        // TODO: Test matcher
        if (PRONOUNS_PATTERN.matcher(requestedPronouns).matches()) {
            ChatColorHandler.sendMessage(sender, "Invalid command format try: /pronouns <pronouns>");
            return true;
        }

        PronounsUser user = SimplyPronouns.getInstance().getUserManager().getCachedUser(player.getUniqueId());
        if (user == null) {
            // TODO: Make message configurable
            ChatColorHandler.sendMessage(sender, "Could not set your pronouns, please try again later");
            return true;
        }

        SimplyPronouns.getInstance().getPronounManager().getPronouns(requestedPronouns.split("/")).thenAccept(pronouns -> {
            user.setPronouns(pronouns);

            // TODO: Make message configurable
            ChatColorHandler.sendMessage(sender, String.format("Your pronouns have been set to %s", requestedPronouns));
        });

        return true;
    }
}
