package eu.claymc.proxysystem;

import eu.claymc.proxysystem.command.*;
import eu.claymc.proxysystem.database.IDatabase;
import eu.claymc.proxysystem.database.SQLDatabase;
import eu.claymc.proxysystem.listener.PunishChatListener;
import eu.claymc.proxysystem.listener.PunishLeaveListener;
import eu.claymc.proxysystem.listener.PunishLoginListener;
import eu.claymc.proxysystem.notifier.ANotifierManager;
import eu.claymc.proxysystem.notifier.PermissionBasedNotifierManager;
import eu.claymc.proxysystem.punish.IPunishManager;
import eu.claymc.proxysystem.punish.sql.SQLPunishManager;
import eu.claymc.proxysystem.report.IReportManager;
import eu.claymc.proxysystem.report.sql.SQLReportManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProxyPlugin extends Plugin {

    public static final String PREFIX = "§6•§e● ClayMC §8▎ §7";

    private Executor executor = Executors.newCachedThreadPool();


    @Override
    public void onEnable() {

        IDatabase<Connection> database = new SQLDatabase();
        database.connect();

        ANotifierManager teamChatNotifier = new PermissionBasedNotifierManager("claymc.teamchat");
        ANotifierManager punishNotifier = new PermissionBasedNotifierManager("claymc.punish.notification");

        IPunishManager punishManager = new SQLPunishManager(database, punishNotifier);

        IReportManager reportManager = new SQLReportManager(database, punishNotifier);

        getProxy().getPluginManager().registerCommand(this, new TeamChatCommand(teamChatNotifier));
        getProxy().getPluginManager().registerCommand(this, new PunishCommand(punishManager));
        getProxy().getPluginManager().registerCommand(this, new ReportCommand(reportManager));

        getProxy().getPluginManager().registerCommand(this, new PingCommand());
        getProxy().getPluginManager().registerCommand(this, new JoinMeCommand());

        getProxy().getPluginManager().registerListener(this, new PunishLoginListener(punishManager, this));
        getProxy().getPluginManager().registerListener(this, new PunishChatListener(punishManager));
        getProxy().getPluginManager().registerListener(this, new PunishLeaveListener(punishManager));


        getProxy().getScheduler().schedule(this, () -> executor.execute(() -> {

            try (Connection connection = database.getConnection();
                 PreparedStatement pstmt =
                         connection.prepareStatement("UPDATE reports SET closed=1 WHERE timestamp <= ? AND closed=0")) {

                pstmt.setLong(1, System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30));
                pstmt.execute();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }), 1, 30, TimeUnit.SECONDS);

    }
}
