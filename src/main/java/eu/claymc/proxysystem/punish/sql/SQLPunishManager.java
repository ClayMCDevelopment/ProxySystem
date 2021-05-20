package eu.claymc.proxysystem.punish.sql;

import eu.claymc.proxysystem.database.IDatabase;
import eu.claymc.proxysystem.notifier.ANotifierManager;
import eu.claymc.proxysystem.punish.APunishEntry;
import eu.claymc.proxysystem.punish.IPunishManager;
import eu.claymc.proxysystem.punish.PunishReason;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.IOfflineCloudPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class SQLPunishManager implements IPunishManager {

    private IDatabase<Connection> database;
    private ANotifierManager punishNotifier;

    public SQLPunishManager(IDatabase<Connection> database, ANotifierManager punishNotifier) {
        this.database = database;
        this.punishNotifier = punishNotifier;
    }

    @Override
    public SQLPunishEntry createEntry() {
        return new SQLPunishEntry(database, punishNotifier);
    }

    @Override
    public List<APunishEntry> getPunishEntries(IOfflineCloudPlayer cloudPlayer) {
        //TODO implement

        List<APunishEntry> list = new ArrayList<>();

        try (Connection connection = database.getConnection(); PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM punishes WHERE punisher=? OR target=?")) {
            pstmt.setString(1, cloudPlayer.getUniqueId().toString());
            pstmt.setString(2, cloudPlayer.getUniqueId().toString());


            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                APunishEntry entry = createEntry();
                System.out.println("try to get result from: " + resultSet.getString("punisher"));
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
}
