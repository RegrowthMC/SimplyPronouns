package org.lushplugins.simplypronouns.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.SubCommand;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.simplypronouns.SimplyPronouns;

public class ReloadCommand extends SubCommand {

    public ReloadCommand() {
        super("reload");
        addRequiredPermission("simplypronouns.reload");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        SimplyPronouns.getInstance().getConfigManager().reloadConfig();
        ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("reloaded"));
        return true;
    }
}
