package org.lushplugins.simplypronouns.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.Command;

public class SimplyPronounsCommand extends Command {

    public SimplyPronounsCommand() {
        super("simplypronouns");
        addSubCommand(new ReloadCommand());
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String[] strings, @NotNull String[] strings1) {
        return true;
    }
}
