package org.lushplugins.simplypronouns.commands.preferredname;

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

public class RemovePreferredNameCommand extends SubCommand {

    public RemovePreferredNameCommand() {
        super("remove");
        addRequiredPermission("simplypronouns.preferredname.remove");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        boolean other;
        Player player;
        if (args.length >= 1) {
            if (!sender.hasPermission("simplypronouns.preferredname.remove.others")) {
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

        user.setPreferredName(null);

        ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("preferred-name-remove" + (other ? "-other" : ""))
            .replace("%preferred_name%", "none")
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
