package org.lushplugins.simplypronouns.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.Command;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.data.PronounsUser;
import org.lushplugins.simplypronouns.pronouns.Pronouns;

public class CheckPronounsCommand extends Command {

    public CheckPronounsCommand() {
        super("checkpronouns");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (args.length != 1) {
            // TODO: Make message configurable
            ChatColorHandler.sendMessage(sender, "Invalid command format try: /checkpronouns <player>");
            return true;
        }

        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            // TODO: Make message configurable
            ChatColorHandler.sendMessage(sender, String.format("Could not find player '%s' online", playerName));
            return true;
        }

        PronounsUser user = SimplyPronouns.getInstance().getUserManager().getCachedUser(player.getUniqueId());
        if (user == null) {
            // TODO: Make message configurable
            ChatColorHandler.sendMessage(sender, String.format("Could not find player '%s' online", playerName));
            return true;
        }

        Pronouns pronouns = user.getPronouns();
        if (pronouns == null) {
            // TODO: Make message configurable
            ChatColorHandler.sendMessage(sender, String.format("%s has not set any pronouns", playerName));
            return true;
        } else {
            // TODO: Make message configurable
            ChatColorHandler.sendMessage(sender, String.format("%s's pronouns are '%s'", playerName, user.getPronouns().asString()));
            return true;
        }
    }
}
