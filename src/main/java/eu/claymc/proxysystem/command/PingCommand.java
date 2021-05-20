package eu.claymc.proxysystem.command;

import eu.claymc.proxysystem.ProxyPlugin;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
        proxiedPlayer.sendMessage(ProxyPlugin.PREFIX + "PONG! Dein Ping: §b" + proxiedPlayer.getPing() + "ms ");

    }
}
