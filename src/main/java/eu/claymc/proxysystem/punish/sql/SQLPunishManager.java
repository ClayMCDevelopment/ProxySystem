package eu.claymc.proxysystem.punish.sql;

import eu.claymc.proxysystem.database.IDatabase;
import eu.claymc.proxysystem.notifier.ANotifierManager;
import eu.claymc.proxysystem.punish.APunishEntry;
import eu.claymc.proxysystem.punish.IPunishManager;
import eu.claymc.proxysystem.punish.PunishReason;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.message.IMessageChannel;
import eu.thesimplecloud.api.player.IOfflineCloudPlayer;
import net.md_5.bungee.api.ProxyServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SQLPunishManager implements IPunishManager {

    private IDatabase<Connection> database;
    private ANotifierManager punishNotifier;

    private Map<IOfflineCloudPlayer, List<APunishEntry>> punishListCache = new ConcurrentHashMap<>();

    public SQLPunishManager(IDatabase<Connection> database, ANotifierManager punishNotifier) {
        this.database = database;
        this.punishNotifier = punishNotifier;

        //cache cleaner
        ProxyServer.getInstance().getScheduler().schedule(ProxyServer.getInstance().getPluginManager().getPlugin("ProxySystem"), () -> {

            for (IOfflineCloudPlayer offlinePlayer : punishListCache.keySet()) {
                if (!offlinePlayer.isOnline()) {
                    clearCache(offlinePlayer);
                }
            }

        }, 0, 5, TimeUnit.MINUTES);



        IMessageChannel<String> messageChannel = CloudAPI.getInstance().getMessageChannelManager().registerMessageChannel(CloudAPI.getInstance().getThisSidesCloudModule(), "punish-cache-update", String.class);

        messageChannel.registerListener((s, iNetworkComponent) -> {

            try {
                System.out.println("messageing channel clear cache listener!");
                IOfflineCloudPlayer target = CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(UUID.fromString(s)).get();
                clearCache(target);
                getPunishCachedEntries(target);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public SQLPunishEntry createEntry() {
        return new SQLPunishEntry(database, punishNotifier);
    }


    @Override
    public List<APunishEntry> getPunishEntries(IOfflineCloudPlayer cloudPlayer) {
        List<APunishEntry> list = new ArrayList<>();

        try (Connection connection = database.getConnection(); PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM punishes WHERE punisher=? OR target=?")) {
            pstmt.setString(1, cloudPlayer.getUniqueId().toString());
            pstmt.setString(2, cloudPlayer.getUniqueId().toString());


            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                APunishEntry entry = createEntry();
                entry.id(resultSet.getInt("id"));
                entry.punisher(CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(UUID.fromString(resultSet.getString("punisher"))).get());
                entry.target(CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(UUID.fromString(resultSet.getString("target"))).get());
                entry.reason(PunishReason.valueOf(resultSet.getString("reason")));
                entry.type(entry.reason().getType());
                entry.timestamp(resultSet.getLong("timestamp"));
                entry.duration(resultSet.getLong("duration"));

                list.add(entry);


            }

        } catch (SQLException | InterruptedException | ExecutionException throwables) {
            throwables.printStackTrace();
        }

        return list;
    }

    @Override
    public List<APunishEntry> getPunishCachedEntries(IOfflineCloudPlayer cloudPlayer) {
        if (!punishListCache.containsKey(cloudPlayer)) {
            punishListCache.put(cloudPlayer, getPunishEntries(cloudPlayer));
            return getPunishCachedEntries(cloudPlayer);
        } else {
            return punishListCache.get(cloudPlayer);
        }
    }

    @Override
    public List<APunishEntry> getAllPunishEntries() {
        List<APunishEntry> list = new ArrayList<>();

        try (Connection connection = database.getConnection(); PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM punishes")) {

            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                APunishEntry entry = createEntry();
                entry.id(resultSet.getInt("id"));
                entry.punisher(CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(UUID.fromString(resultSet.getString("punisher"))).get());
                entry.target(CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(UUID.fromString(resultSet.getString("target"))).get());
                entry.reason(PunishReason.valueOf(resultSet.getString("reason")));
                entry.type(entry.reason().getType());
                entry.timestamp(resultSet.getLong("timestamp"));
                entry.duration(resultSet.getLong("duration"));

                list.add(entry);

            }

        } catch (SQLException | InterruptedException | ExecutionException throwables) {
            throwables.printStackTrace();
        }

        return list;
    }

    @Override
    public void clearCache(IOfflineCloudPlayer cloudPlayer) {
        for (IOfflineCloudPlayer iOfflineCloudPlayer : punishListCache.keySet()) {
            if (iOfflineCloudPlayer.getName().equals(cloudPlayer.getName())) {
                punishListCache.remove(iOfflineCloudPlayer);
                return;
            }
        }
    }

    @Override
    public void addToCache(IOfflineCloudPlayer in, APunishEntry punishEntry) {

        for (IOfflineCloudPlayer iOfflineCloudPlayer : punishListCache.keySet()) {
            if(iOfflineCloudPlayer.getName().equals(in.getName())){
                List<APunishEntry> aPunishEntries = punishListCache.get(iOfflineCloudPlayer);

                aPunishEntries.add(punishEntry);
                punishListCache.put(iOfflineCloudPlayer, aPunishEntries);
                System.out.println("entry added to cache");

            }
        }


    }
}
