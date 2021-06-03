package eu.claymc.proxysystem.listener;

import eu.claymc.proxysystem.timeout.ITimeoutManager;
import eu.claymc.proxysystem.util.TimeUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import static eu.claymc.proxysystem.ProxyPlugin.PREFIX;

public class TimeOutListener implements Listener {
    private ITimeoutManager timeoutManager;

    public TimeOutListener(ITimeoutManager timeoutManager) {
        this.timeoutManager = timeoutManager;
    }

    @EventHandler
    public void handle(ChatEvent event) {
        if (event.getMessage().startsWith("/")) {
            return;
        }

        if (timeoutManager.isActive()) {
            event.setCancelled(true);
            ((ProxiedPlayer) event.getSender()).sendMessage(PREFIX + "Der Chat ist noch für §e" + TimeUtil.convertString(timeoutManager.getTimeoutLength() - System.currentTimeMillis()) + " §7gesperrt");
        }

    }

}
