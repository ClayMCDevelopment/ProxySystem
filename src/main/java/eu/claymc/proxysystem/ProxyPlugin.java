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
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.message.IMessageChannel;
import eu.thesimplecloud.api.player.IOfflineCloudPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProxyPlugin extends Plugin {

    public static final String PREFIX = "§6•§e● ClayMC §8▎ §7";

    private final static Executor executor = Executors.newCachedThreadPool();

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }


    @Override
    public void onEnable() {

        IDatabase<Connection> database = new SQLDatabase();
        database.connect();

        ANotifierManager teamChatNotifier = new PermissionBasedNotifierManager("claymc.teamchat");
        ANotifierManager punishNotifier = new PermissionBasedNotifierManager("claymc.punish.notification");
        ANotifierManager reportNotifier = new PermissionBasedNotifierManager("claymc.report.notification");


        IPunishManager punishManager = new SQLPunishManager(database, punishNotifier);

        IReportManager reportManager = new SQLReportManager(database, punishNotifier);

        getProxy().getPluginManager().registerCommand(this, new TeamChatCommand(teamChatNotifier));

        getProxy().getPluginManager().registerCommand(this, new WhereAmICommand());

        getProxy().getPluginManager().registerCommand(this, new PunishCommand(punishManager));
        getProxy().getPluginManager().registerCommand(this, new ForgiveCommand(punishManager));

        getProxy().getPluginManager().registerCommand(this, new ReportCommand(reportManager, reportNotifier));

        getProxy().getPluginManager().registerCommand(this, new PingCommand());
        getProxy().getPluginManager().registerCommand(this, new JoinMeCommand());

        getProxy().getPluginManager().registerListener(this, new PunishLoginListener(punishManager, this, database));
        getProxy().getPluginManager().registerListener(this, new PunishChatListener(punishManager));
        getProxy().getPluginManager().registerListener(this, new PunishLeaveListener(punishManager));

        getProxy().getPluginManager().registerCommand(this, new PInfoCommand(punishManager, database));



        //auto close reports
        getProxy().getScheduler().schedule(this, () -> executor.execute(() -> {

            try (Connection connection = database.getConnection();
                 PreparedStatement pstmt =
                         connection.prepareStatement("UPDATE reports SET status=3 WHERE timestamp <= ? AND status=0")) {

                pstmt.setLong(1, System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30));
                pstmt.execute();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }), 1, 30, TimeUnit.SECONDS);

        IMessageChannel<String> messageChannel = CloudAPI.getInstance().getMessageChannelManager().registerMessageChannel(CloudAPI.getInstance().getThisSidesCloudModule(), "punish-cache-update", String.class);

        messageChannel.registerListener((s, iNetworkComponent) -> {

            try {
                System.out.println("messageing channel clear cache listener!");
                IOfflineCloudPlayer target = CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(UUID.fromString(s)).get();
                punishManager.clearCache(target);
                punishManager.getPunishCachedEntries(target);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

    }
}
