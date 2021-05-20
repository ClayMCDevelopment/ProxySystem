package eu.claymc.proxysystem.listener;

import eu.claymc.proxysystem.ProxyPlugin;
import eu.claymc.proxysystem.punish.APunishEntry;
import eu.claymc.proxysystem.punish.IPunishManager;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.ICloudPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class PunishChatListener implements Listener {
    private final IPunishManager punishManager;

    public PunishChatListener(IPunishManager punishManager) {
        this.punishManager = punishManager;
    }

    @EventHandler
    public void handle(ChatEvent event) {
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getSender();
        ICloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getCachedCloudPlayer(proxiedPlayer.getUniqueId());

        List<APunishEntry> punishEntries = punishManager.getPunishCachedEntries(cloudPlayer);
        for (APunishEntry punishEntry : punishEntries) {
            if (punishEntry.target().getUniqueId().equals(proxiedPlayer.getUniqueId())) {

                if (punishEntry.timestamp() + punishEntry.duration() > System.currentTimeMillis()) {
                    event.setMessage(null);
                    proxiedPlayer.sendMessage(ProxyPlugin.PREFIX + "§cDu bist aus dem Chat ausgeschlossen. (" + punishEntry.reason() + ")");
                    event.setCancelled(true);
                }
            }


        }

    }
}
