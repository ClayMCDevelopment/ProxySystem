package eu.claymc.proxysystem.command;

import eu.claymc.proxysystem.ProxyPlugin;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.ICloudPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import static eu.claymc.proxysystem.ProxyPlugin.PREFIX;

public class WhereAmICommand extends Command {
    public WhereAmICommand() {
        super("whereami", "", "wmi");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        ProxyPlugin.execute(() -> {
            ICloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getCloudPlayer(commandSender.getName()).getBlocking();

            commandSender.sendMessage(PREFIX + "§7ProxyServer§8: §e" + cloudPlayer.getConnectedProxyName());
            commandSender.sendMessage(PREFIX + "§7Server§8: §e" + cloudPlayer.getConnectedServerName());

        });

    }
}
