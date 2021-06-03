package eu.claymc.proxysystem;

import eu.claymc.proxysystem.command.*;
import eu.claymc.proxysystem.database.IDatabase;
import eu.claymc.proxysystem.database.SQLDatabase;
import eu.claymc.proxysystem.listener.PunishChatListener;
import eu.claymc.proxysystem.listener.PunishLeaveListener;
import eu.claymc.proxysystem.listener.PunishLoginListener;
import eu.claymc.proxysystem.listener.TimeOutListener;
import eu.claymc.proxysystem.notifier.ANotifierManager;
import eu.claymc.proxysystem.notifier.PermissionBasedNotifierManager;
import eu.claymc.proxysystem.punish.IPunishManager;
import eu.claymc.proxysystem.punish.sql.SQLPunishManager;
import eu.claymc.proxysystem.report.IReportManager;
import eu.claymc.proxysystem.report.sql.SQLReportManager;
import eu.claymc.proxysystem.timeout.ITimeoutManager;
import eu.claymc.proxysystem.timeout.SimpleTimeoutManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProxyPlugin extends Plugin {

    public static final String PREFIX = "§6•§e● ClayMC §8▎ §7";

    private final static Executor executor = Executors.newCachedThreadPool();

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    private static Plugin plugin;

    public static Plugin getInstance() {
        return plugin;
    }


    @Override
    public void onEnable() {
        plugin = this;
        IDatabase<Connection> database = new SQLDatabase();
        database.connect();

        ANotifierManager teamChatNotifier = new PermissionBasedNotifierManager("claymc.teamchat");
        ANotifierManager punishNotifier = new PermissionBasedNotifierManager("claymc.punish.notification");
        ANotifierManager reportNotifier = new PermissionBasedNotifierManager("claymc.report.notification");


        IPunishManager punishManager = new SQLPunishManager(database, punishNotifier);

        IReportManager reportManager = new SQLReportManager(database, punishNotifier);

        ITimeoutManager timeoutManager = new SimpleTimeoutManager();

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

        getProxy().getPluginManager().registerCommand(this, new TimeoutCommand(timeoutManager));
        getProxy().getPluginManager().registerListener(this, new TimeOutListener(timeoutManager));

    }
}
