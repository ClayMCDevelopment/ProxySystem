package eu.claymc.proxysystem.punish.sql;

import eu.claymc.proxysystem.ProxyPlugin;
import eu.claymc.proxysystem.database.IDatabase;
import eu.claymc.proxysystem.notifier.ANotifierManager;
import eu.claymc.proxysystem.punish.APunishEntry;
import eu.claymc.proxysystem.punish.PunishType;
import eu.thesimplecloud.api.CloudAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class SQLPunishEntry extends APunishEntry {

    private IDatabase<Connection> database;
    private ANotifierManager punishNotifier;

    public SQLPunishEntry(IDatabase<Connection> database, ANotifierManager punishNotifier) {
        this.database = database;
        this.punishNotifier = punishNotifier;
    }

    @Override
    public void commit() {

        try (Connection connection = database.getConnection(); PreparedStatement pstmt = connection.prepareStatement("INSERT INTO punishes(punishes.punisher, punishes.target, punishes.reason, punishes.timestamp, punishes.duration) VALUES (?,?,?,?,?);")) {
            pstmt.setString(1, punisher().getUniqueId().toString());
            pstmt.setString(2, target().getUniqueId().toString());
            pstmt.setString(3, reason().name());
            pstmt.setLong(4, timestamp());
            pstmt.setLong(5, duration());

            pstmt.execute();

            punishNotifier.notify(ProxyPlugin.PREFIX + " Der Spieler §e" + target().getName() + "§7 wurde wegen §e" + reason() + " bestraft");
            if (target().isOnline() && punishType().equals(PunishType.BAN)) {
                CloudAPI.getInstance().getCloudPlayerManager().getCloudPlayer(target().getUniqueId()).get().kick(
                        "\n§cDu wurdest vom Netzwerk gebannt" +
                                "\n§eGrund§8: §7" + reason() + "\n\n" +
                                "§eBan läuft ab am§8: §7" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(timestamp() + duration())) + "\n\n" +
                                "§aDu wurdest zu unrecht gebannt ? Stelle einen Entbannungsantrag im Forum. §ewww.claymc.eu/forum/");
            }

        } catch (SQLException | InterruptedException | ExecutionException throwables) {
            throwables.printStackTrace();
        }


    }

    @Override
    public void update() {
        if (id() == -1) {
            throw new IllegalStateException("Punishentry does not exists! First commit before updating!");
        }

        try (Connection connection = database.getConnection(); PreparedStatement pstmt = connection.prepareStatement("UPDATE punishes SET duration=? WHERE id=?")) {
            pstmt.setLong(1, duration());
            pstmt.setInt(2, id());
            pstmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
