package org.lushplugins.simplypronouns.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.pronouns.Pronoun;

public class BlacklistCommand extends SubCommand {

    public BlacklistCommand() {
        super("blacklist");
        addRequiredPermission("simplypronouns.blacklist");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (args.length != 1) {
            // TODO: Make message configurable
            ChatColorHandler.sendMessage(sender, "Invalid command format try: /pronouns blacklist <pronoun>");
            return true;
        }

        String rawPronoun = args[0];
        SimplyPronouns.getInstance().getPronounManager().getOrCreatePronoun(rawPronoun).thenAccept(pronoun -> {
            pronoun.setStatus(Pronoun.Status.DENIED);
        });

        return true;
    }
}
