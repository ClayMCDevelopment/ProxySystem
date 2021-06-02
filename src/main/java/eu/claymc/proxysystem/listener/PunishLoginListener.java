package eu.claymc.proxysystem.listener;

import eu.claymc.proxysystem.ProxyPlugin;
import eu.claymc.proxysystem.database.IDatabase;
import eu.claymc.proxysystem.punish.APunishEntry;
import eu.claymc.proxysystem.punish.IPunishManager;
import eu.claymc.proxysystem.punish.PunishType;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.IOfflineCloudPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class PunishLoginListener implements Listener {
    private IPunishManager punishManager;
    private Plugin plugin;

    private Executor executor = Executors.newCachedThreadPool();
    private IDatabase<Connection> database;

    public PunishLoginListener(IPunishManager punishManager, Plugin plugin, IDatabase<Connection> database) {
        this.punishManager = punishManager;
        this.plugin = plugin;
        this.database = database;
    }

    @EventHandler
    public void handle(LoginEvent event) {
        try {

            IOfflineCloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(event.getConnection().getUniqueId()).get();


            ProxyPlugin.execute(() -> {
                try (Connection connection = database.getConnection();
                     PreparedStatement pstmt = connection.prepareStatement("INSERT INTO player_addresses (uuid, ip, timestamp) VALUES (?,?,?) ON DUPLICATE KEY UPDATE ip=?, timestamp=?")) {

                    pstmt.setString(1, cloudPlayer.getUniqueId().toString());
                    pstmt.setString(2, cloudPlayer.getLastPlayerConnection().getAddress().getHostname());
                    pstmt.setLong(3, System.currentTimeMillis());

                    pstmt.setString(4, cloudPlayer.getLastPlayerConnection().getAddress().getHostname());
                    pstmt.setLong(5, System.currentTimeMillis());

                    pstmt.execute();


                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                List<String> multiAccs = new CopyOnWriteArrayList<>();

                try (Connection connection = database.getConnection();
                     PreparedStatement pstmt = connection.prepareStatement("SELECT uuid FROM player_addresses WHERE ip=? AND uuid NOT IN (?) AND player_addresses.timestamp>=?")) {
                    pstmt.setString(1, cloudPlayer.getLastPlayerConnection().getAddress().getHostname());
                    pstmt.setString(2, cloudPlayer.getUniqueId().toString());
                    pstmt.setLong(3, System.currentTimeMillis() - TimeUnit.HOURS.toMillis(6));

                    ResultSet resultSet = pstmt.executeQuery();


                    while (resultSet.next()) {
                        multiAccs.add(resultSet.getString("uuid"));
                    }
                    resultSet.close();

                    for (String multiAcc : multiAccs) {
                        try (PreparedStatement tmpPstmt = connection.prepareStatement("SELECT mainAccount FROM multiaccounts WHERE mainAccount=? AND altAccount=?")) {
                            tmpPstmt.setString(1, cloudPlayer.getUniqueId().toString());
                            tmpPstmt.setString(2, multiAcc);
                            System.out.println(tmpPstmt);
                            ResultSet resultSet1 = tmpPstmt.executeQuery();

                            if(resultSet1.next()){
                                multiAccs.remove(multiAcc);
                            }

                            resultSet1.close();
                        }
                    }

                    try (PreparedStatement tmpPstmt = connection.prepareStatement("INSERT INTO multiaccounts (mainAccount, altAccount) VALUES (?,?),(?,?)")) {

                        for (String multiAcc : multiAccs) {
                            tmpPstmt.setString(1, multiAcc);
                            tmpPstmt.setString(2, cloudPlayer.getUniqueId().toString());

                            tmpPstmt.setString(3, cloudPlayer.getUniqueId().toString());
                            tmpPstmt.setString(4, multiAcc);
                            tmpPstmt.execute();
                        }


                    }


                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            });

            List<APunishEntry> punishEntries = this.punishManager.getPunishCachedEntries(cloudPlayer);

            if (!punishEntries.isEmpty()) {

                for (APunishEntry punishEntry : punishEntries) {

                    //punish type is ban
                    if (punishEntry.punishType().equals(PunishType.BAN)) {

                        //player was banned in history
                        if (punishEntry.target().getUniqueId().equals(cloudPlayer.getUniqueId())) {

                            //player is still banned
                            if (punishEntry.timestamp() + punishEntry.duration() > System.currentTimeMillis()) {
                                event.setCancelReason("\n§cDu wurdest vom Netzwerk gebannt" +
                                        "\n§eGrund§8: §7" + punishEntry.reason() + "\n\n" +
                                        "§eBan läuft ab am§8: §7" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(punishEntry.timestamp() + punishEntry.duration())) + "\n\n" +
                                        "§aDu wurdest zu unrecht gebannt ? Stelle einen Entbannungsantrag im Forum. §ewww.claymc.eu/forum/");
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
