package org.lushplugins.simplypronouns.commands.pronouns;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.pronouns.Pronoun;

public class BlacklistPronounsCommand extends SubCommand {

    public BlacklistPronounsCommand() {
        super("blacklist");
        addRequiredPermission("simplypronouns.pronouns.blacklist");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (args.length != 1) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("invalid-command")
                .replace("%command%", "/pronouns blacklist <pronoun>"));
            return true;
        }

        String rawPronoun = args[0];
        SimplyPronouns.getInstance().getPronounManager().getOrCreatePronoun(rawPronoun).thenAccept(pronoun -> {
            pronoun.setStatus(Pronoun.Status.DENIED);
        });

        return true;
    }
}
