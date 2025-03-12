package org.lushplugins.simplypronouns.commands.pronouns;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.command.Command;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.simplypronouns.SimplyPronouns;
import org.lushplugins.simplypronouns.data.PronounsUser;
import org.lushplugins.simplypronouns.gui.TermsAcceptGui;

import java.util.regex.Pattern;

public class PronounsCommand extends Command {
    private static final Pattern PRONOUNS_PATTERN = Pattern.compile("[\\w ]+/?[\\w ]*");

    public PronounsCommand() {
        super("pronouns");
        addSubCommand(new BlacklistPronounsCommand());
        addSubCommand(new CheckPronounsCommand("check"));
        addSubCommand(new ModeratePronounsCommand());
        addSubCommand(new RemovePronounsCommand());
        addSubCommand(new SetPronounsCommand());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args, @NotNull String[] fullArgs) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Console cannot run this command");
            return true;
        }

        if (args.length < 1) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("invalid-command")
                .replace("%command%", "/pronouns <pronouns>"));
            return true;
        }

        String requestedPronouns = args[0];
        if (requestedPronouns.length() > 24) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("pronouns-char-limit"));
            return true;
        }

        if (!PRONOUNS_PATTERN.matcher(requestedPronouns).matches()) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("invalid-command")
                .replace("%command%", "/pronouns <pronouns>"));
            return true;
        }

        if (SimplyPronouns.getInstance().isBlockedByFilters(requestedPronouns)) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("failed-to-set"));
            return true;
        }

        PronounsUser user = SimplyPronouns.getInstance().getUserManager().getCachedUser(player.getUniqueId());
        if (user == null) {
            ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("failed-to-set"));
            return true;
        }

        SimplyPronouns.getInstance().getPronounManager().compilePronouns(requestedPronouns.split("/")).thenAccept(pronouns -> {
            Bukkit.getScheduler().runTask(SimplyPronouns.getInstance(), () -> {
                TermsAcceptGui.builder()
                    .terms("""
                        Misuse of the pronouns feature is not tolerated.
                        Only set pronouns that you want others to use
                        when referring to you.
                        Chat rules also apply to pronouns.
                        """)
                    .onAccept(() -> {
                        user.setPronouns(pronouns);

                        ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("pronouns-set")
                            .replace("%pronouns%", requestedPronouns));
                    })
                    .onDecline(() -> {
                        ChatColorHandler.sendMessage(sender, SimplyPronouns.getInstance().getConfigManager().getMessage("failed-to-set"));
                    })
                    .open(player);
            });
        });

        return true;
    }
}
