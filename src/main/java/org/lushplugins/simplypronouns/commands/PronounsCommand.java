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
        addSubCommand(new ReloadCommand());
        addSubCommand(new RequestsCommand());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Console cannot run this command");
            return true;
        }

        if (args.length < 1) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("invalid-command")
                .replace("%command%", "/pronouns <pronouns>"));
            return true;
        }

        String requestedPronouns = args[0];
        if (requestedPronouns.length() > 24) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("pronouns-char-limit"));
            return true;
        }

        if (!PRONOUNS_PATTERN.matcher(requestedPronouns).matches()) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("invalid-command")
                .replace("%command%", "/pronouns <pronouns>"));
            return true;
        }

        PronounsUser user = SimplyPronouns.getInstance().getUserManager().getCachedUser(player.getUniqueId());
        if (user == null) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("failed-to-set"));
            return true;
        }

        SimplyPronouns.getInstance().getPronounManager().getPronouns(requestedPronouns.split("/")).thenAccept(pronouns -> {
            user.setPronouns(pronouns);

            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("pronouns-set")
                .replace("%pronouns%", requestedPronouns));
        });

        return true;
    }
}
