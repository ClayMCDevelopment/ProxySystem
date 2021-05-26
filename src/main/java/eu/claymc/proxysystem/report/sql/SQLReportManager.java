package eu.claymc.proxysystem.report.sql;

import eu.claymc.proxysystem.database.IDatabase;
import eu.claymc.proxysystem.notifier.ANotifierManager;
import eu.claymc.proxysystem.report.AReportEntry;
import eu.claymc.proxysystem.report.IReportManager;
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

public class SQLReportManager implements IReportManager {

    private IDatabase<Connection> database;
    private ANotifierManager punishNotifier;

    public SQLReportManager(IDatabase<Connection> database, ANotifierManager punishNotifier) {
        this.database = database;
        this.punishNotifier = punishNotifier;
    }

    @Override
    public AReportEntry createEntry() {
        return new SQLReportEntry(database, punishNotifier);
    }

    @Override
    public List<AReportEntry> getAllReports() {

        List<AReportEntry> result = new ArrayList<>();

        try (Connection connection = database.getConnection(); PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM reports")) {

            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                int status = resultSet.getInt("status");
                int priority = resultSet.getInt("priority");
                UUID reporterUUID = UUID.fromString(resultSet.getString("reporter"));
                UUID suspectUUID = UUID.fromString(resultSet.getString("suspect"));
                String reason = resultSet.getString("reason");
                long timestamp = resultSet.getLong("timestamp");

                AReportEntry entry = createEntry();
                entry.reportId(id);
                entry.status(status);
                entry.priority(priority);
                entry.reporter(CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(reporterUUID).get());
                entry.suspect(CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(suspectUUID).get());
                entry.reason(reason);
                entry.timestamp(timestamp);

                result.add(entry);

            }

        } catch (SQLException | InterruptedException | ExecutionException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }

    @Override
    public List<AReportEntry> getReports(IOfflineCloudPlayer cloudPlayer) {

        List<AReportEntry> result = new ArrayList<>();

        try (Connection connection = database.getConnection(); PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM reports WHERE reporter=? OR suspect=?")) {
            pstmt.setString(0, cloudPlayer.getUniqueId().toString());
            pstmt.setString(1, cloudPlayer.getUniqueId().toString());
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {

                int closed = resultSet.getInt("status");
                int priority = resultSet.getInt("priority");
                UUID reporterUUID = UUID.fromString(resultSet.getString("reporter"));
                UUID suspectUUID = UUID.fromString(resultSet.getString("suspect"));
                String reason = resultSet.getString("reason");
                long timestamp = resultSet.getLong("timestamp");

                AReportEntry entry = createEntry();
                entry.priority(priority);
                entry.status(closed);
                entry.reporter(CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(reporterUUID).get());
                entry.suspect(CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(suspectUUID).get());
                entry.reason(reason);
                entry.timestamp(timestamp);

                result.add(entry);

            }

        } catch (SQLException | InterruptedException | ExecutionException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }
}
