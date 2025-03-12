package org.lushplugins.simplypronouns.commands.example;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.data.PronounsUser;

public class SetPronounsCommand extends SubCommand {

    public SetPronounsCommand() {
        super("set");
        addRequiredPermission("simplypronouns.set");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (args.length < 2) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("invalid-command")
                .replace("%command%", "/pronouns set <player> <pronouns>"));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        String requestedPronouns = args[1];

        if (player == null) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("player-not-online"));
            return true;
        }

        PronounsUser user = SimplyPronouns.getInstance().getUserManager().getCachedUser(player.getUniqueId());
        if (user == null) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("failed-to-set"));
            return true;
        }

        SimplyPronouns.getInstance().getPronounManager().compilePronouns(requestedPronouns.split("/")).thenAccept(pronouns -> {
            user.setPronouns(pronouns);

            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("pronouns-set")
                .replace("%pronouns%", requestedPronouns));
        });
        return true;
    }
}
