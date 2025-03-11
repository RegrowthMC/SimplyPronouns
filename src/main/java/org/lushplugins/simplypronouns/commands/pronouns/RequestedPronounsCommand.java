package org.lushplugins.simplypronouns.commands.pronouns;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.simplypronouns.gui.RequestsGui;

public class RequestedPronounsCommand extends SubCommand {

    public RequestedPronounsCommand() {
        super("requests");
        addRequiredPermission("simplypronouns.pronouns.requests");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command cannot be ran by console");
            return true;
        }

        new RequestsGui(player).open();
        return true;
    }
}
