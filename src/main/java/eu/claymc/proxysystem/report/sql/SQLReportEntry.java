package eu.claymc.proxysystem.report.sql;

import eu.claymc.proxysystem.database.IDatabase;
import eu.claymc.proxysystem.notifier.ANotifierManager;
import eu.claymc.proxysystem.report.AReportEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLReportEntry extends AReportEntry {


    private IDatabase<Connection> database;
    private ANotifierManager punishNotifier;

    public SQLReportEntry(IDatabase<Connection> database, ANotifierManager punishNotifier) {
        this.database = database;
        this.punishNotifier = punishNotifier;
    }

    @Override
    public void commit() {
        try (Connection connection = database.getConnection(); PreparedStatement pstmt = connection.prepareStatement("INSERT INTO reports (reporter, suspect, reason, timestamp) VALUES (?,?,?,?)")) {
            pstmt.setString(1, reporter().getUniqueId().toString());
            pstmt.setString(2, suspect().getUniqueId().toString());
            pstmt.setString(3, reason());
            pstmt.setLong(4, timestamp());

            pstmt.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
