package org.lushplugins.simplypronouns.commands.pronouns;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.data.PronounsUser;

import java.util.Collections;
import java.util.List;

public class SetPronounsCommand extends SubCommand {

    public SetPronounsCommand() {
        super("set");
        addRequiredPermission("simplypronouns.pronouns.set");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (args.length < 1) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("invalid-command")
                .replace("%command%", "/pronouns set <pronouns> <player>"));
            return true;
        }

        String requestedPronouns = args[0].toLowerCase();
        if (requestedPronouns.equals("<pronouns>")) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("invalid-command")
                .replace("%command%", "/pronouns set <pronouns> <player>"));
        }

        boolean other;
        Player player;
        if (args.length >= 2) {
            if (!sender.hasPermission("simplypronouns.pronouns.set.others")) {
                ChatColorHandler.sendMessage(sender, "&cInsufficient permissions");
                return true;
            }

            player = Bukkit.getPlayer(args[1]);
            other = true;
        } else {
            player = sender instanceof Player p ? p : null;
            other = false;
        }

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

            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("pronouns-set" + (other ? "-other" : ""))
                .replace("%pronouns%", requestedPronouns)
                .replace("%player%", player.getName()));
        });
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        return switch (args.length) {
            case 1 -> List.of("<pronouns>");
            case 2 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            default -> Collections.emptyList();
        };
    }
}
