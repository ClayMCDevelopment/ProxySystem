package eu.claymc.proxysystem.listener;

import eu.claymc.proxysystem.punish.IPunishManager;
import eu.thesimplecloud.api.CloudAPI;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.ExecutionException;

public class PunishLeaveListener implements Listener {
    private IPunishManager punishManager;

    public PunishLeaveListener(IPunishManager punishManager) {
        this.punishManager = punishManager;
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        try {
            punishManager.clearCache(CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(event.getPlayer().getUniqueId()).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
