package org.lushplugins.simplypronouns.commands.pronouns;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.command.Command;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.data.PronounsUser;
import org.lushplugins.simplypronouns.pronouns.Pronouns;

import java.util.Collections;
import java.util.List;

public class CheckPronounsCommand extends Command {

    public CheckPronounsCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (args.length != 1) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("invalid-command")
                .replace("%command%", "/checkpronouns <player>"));
            return true;
        }

        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("player-not-online"));
            return true;
        }

        PronounsUser user = SimplyPronouns.getInstance().getUserManager().getCachedUser(player.getUniqueId());
        if (user == null) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("player-not-online"));
            return true;
        }

        Pronouns pronouns = user.getPronouns();
        if (pronouns == null) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("check-pronouns-none")
                .replace("%player%", playerName));
            return true;
        } else {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("check-pronouns")
                .replace("%player%", playerName)
                .replace("%pronouns%", pronouns.asString()));
            return true;
        }
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }

        return Collections.emptyList();
    }
}
