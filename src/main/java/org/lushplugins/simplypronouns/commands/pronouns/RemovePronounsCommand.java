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

public class RemovePronounsCommand extends SubCommand {

    public RemovePronounsCommand() {
        super("remove");
        addRequiredPermission("simplypronouns.pronouns.remove");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        boolean other;
        Player player;
        if (args.length >= 1) {
            if (!sender.hasPermission("simplypronouns.pronouns.remove.others")) {
                ChatColorHandler.sendMessage(sender, "&cInsufficient permissions");
                return true;
            }

            player = Bukkit.getPlayer(args[0]);
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

        user.setPronouns(null);

        ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("pronouns-remove" + (other ? "-other" : ""))
            .replace("%pronouns%", "none")
            .replace("%player%", player.getName()));
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }

        return Collections.emptyList();
    }
}
