package org.lushplugins.simplypronouns.commands.preferredname;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.Command;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.data.PronounsUser;
import org.lushplugins.simplypronouns.gui.TermsAcceptGui;

public class PreferredNameCommand extends Command {

    public PreferredNameCommand() {
        super("preferredname");
//        addSubCommand(new RequestedPreferredNamesCommand());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Console cannot run this command");
            return true;
        }

        if (args.length < 1) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("invalid-command")
                .replace("%command%", "/preferredname <name>"));
            return true;
        }

        String preferredName = args[0];
        if (preferredName.length() > 12) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("preferred-name-char-limit"));
            return true;
        }

        PronounsUser user = SimplyPronouns.getInstance().getUserManager().getCachedUser(player.getUniqueId());
        if (user == null) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("failed-to-set"));
            return true;
        }

        if (SimplyPronouns.getInstance().blockedByFilters(preferredName)) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("failed-to-set"));
            return true;
        }

        TermsAcceptGui.builder()
            .terms("""
                Misuse of the preferred name feature is not tolerated.
                Only set a name that you want others to use
                when referring to you.
                Chat rules also apply to preferred names.
                """)
            .onAccept(() -> {
                user.setPreferredName(preferredName);

                ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("preferred-name-set")
                    .replace("%preferred_name%", preferredName));
            })
            .onDecline(() -> {
                ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("failed-to-set"));
            })
            .open(player);

        return true;
    }
}
