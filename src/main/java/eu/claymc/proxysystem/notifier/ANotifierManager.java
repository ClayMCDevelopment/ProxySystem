package eu.claymc.proxysystem.notifier;

import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.ICloudPlayer;
import eu.thesimplecloud.api.player.SimpleCloudPlayer;
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise;
import net.md_5.bungee.api.ProxyServer;

import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class ANotifierManager {

    public void notify(String message) {
        System.out.println(ProxyServer.getInstance());
        ICommunicationPromise<List<SimpleCloudPlayer>> onlinePlayersPromise = CloudAPI.getInstance().getCloudPlayerManager().getAllOnlinePlayers();
        try {


            List<SimpleCloudPlayer> simpleCloudPlayers = onlinePlayersPromise.get();

            for (SimpleCloudPlayer simpleCloudPlayer : simpleCloudPlayers) {
                ICommunicationPromise<ICloudPlayer> cloudPlayerPromise = simpleCloudPlayer.getCloudPlayer();
                ICloudPlayer cloudPlayer = cloudPlayerPromise.get();
                if (canReceiveTeamMessage(cloudPlayer)) {
                    cloudPlayer.sendMessage(message);
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }

    public abstract boolean canReceiveTeamMessage(ICloudPlayer cloudPlayer);


}
