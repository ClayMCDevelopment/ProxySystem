package eu.claymc.proxysystem.command;

import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.SimpleCloudPlayer;
import eu.thesimplecloud.api.player.text.CloudText;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.ExecutionException;

import static eu.claymc.proxysystem.ProxyPlugin.PREFIX;

public class JoinMeCommand extends Command {
    public JoinMeCommand() {
        super("joinme", "claymc.command.joinme");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!hasPermission(commandSender)) {
            commandSender.sendMessage("§CDu darfst das nicht!");
            return;
        }

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;

        try {


            for (SimpleCloudPlayer cloudPlayer : CloudAPI.getInstance().getCloudPlayerManager().getAllOnlinePlayers().get()) {
                cloudPlayer.getCloudPlayer().get().sendMessage("\n\n" + PREFIX + "Der Spieler §e" + commandSender.getName() + "§7 Spielt nun auf " + proxiedPlayer.getServer().getInfo().getName() + "\n");
                cloudPlayer.getCloudPlayer().get().sendMessage(new CloudText(PREFIX + "Klicke hier zum Joinen").addClickEvent(CloudText.ClickEventType.RUN_COMMAND, "/server " + proxiedPlayer.getServer().getInfo().getName()));
            }


        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
        }

    }
}
