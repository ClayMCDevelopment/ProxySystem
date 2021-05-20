package eu.claymc.proxysystem.listener;

import eu.claymc.proxysystem.punish.APunishEntry;
import eu.claymc.proxysystem.punish.IPunishManager;
import eu.claymc.proxysystem.punish.PunishType;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.IOfflineCloudPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PunishLoginListener implements Listener {
    private IPunishManager punishManager;
    private Plugin plugin;

    private Executor executor = Executors.newCachedThreadPool();

    public PunishLoginListener(IPunishManager punishManager, Plugin plugin) {
        this.punishManager = punishManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void handle(LoginEvent event) {


        try {
            IOfflineCloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(event.getConnection().getUniqueId()).get();
            List<APunishEntry> punishEntries = this.punishManager.getPunishEntries(cloudPlayer);

            if (!punishEntries.isEmpty()) {

                for (APunishEntry punishEntry : punishEntries) {

                    //punish type is ban
                    if (punishEntry.punishType().equals(PunishType.BAN)) {

                        //player was banned in history
                        if (punishEntry.target().getUniqueId().equals(cloudPlayer.getUniqueId())) {

                            //player is still banned
                            if (punishEntry.timestamp() + punishEntry.duration() > System.currentTimeMillis()) {
                                event.setCancelReason("Du bist gebannt! \nGrund: " + punishEntry.reason() + "\nBan läuft aus: " + new Date(punishEntry.timestamp() + punishEntry.duration()));
                                event.setCancelled(true);
                                return;
                            }
                        }

                    }
                }

            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }

}
