package eu.claymc.proxysystem.command;

import eu.claymc.proxysystem.ProxyPlugin;
import eu.claymc.proxysystem.notifier.ANotifierManager;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.module.permission.PermissionPool;
import eu.thesimplecloud.module.permission.permission.Permission;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TeamChatCommand extends Command {

    private ANotifierManager teamChatManager;

    public TeamChatCommand(ANotifierManager teamChatManager) {
        super("tc");
        this.teamChatManager = teamChatManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("claymc.teamchat")) {

            if (args.length < 1) {
                //maybe toggle
                sender.sendMessage(ProxyPlugin.PREFIX + "§c/tc <msg...>");
            } else {
                StringBuilder s = new StringBuilder();

                for (String arg : args) {
                    s.append(arg).append(" ");
                }

                String formattedMessage = "§6•§e● TeamChat §8▎ §7§e " + sender.getName() + " §8»§7 " + s.toString();

                teamChatManager.notify(formattedMessage);

            }

        } else {
            sender.sendMessage(ProxyPlugin.PREFIX + "§cDu darfst das nicht.");
        }
    }
}
