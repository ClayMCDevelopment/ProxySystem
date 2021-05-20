package eu.claymc.proxysystem;

import eu.claymc.proxysystem.command.PunishCommand;
import eu.claymc.proxysystem.command.TeamChatCommand;
import eu.claymc.proxysystem.database.IDatabase;
import eu.claymc.proxysystem.database.SQLDatabase;
import eu.claymc.proxysystem.listener.PunishLoginListener;
import eu.claymc.proxysystem.notifier.ANotifierManager;
import eu.claymc.proxysystem.notifier.PermissionBasedNotifierManager;
import eu.claymc.proxysystem.punish.IPunishManager;
import eu.claymc.proxysystem.punish.sql.SQLPunishManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;

public class ProxyPlugin extends Plugin {

    public static final String PREFIX = "§8[§bProxy§8] §7";

    @Override
    public void onEnable() {

        IDatabase<Connection> database = new SQLDatabase();
        database.connect();

        ANotifierManager teamChatNotifier = new PermissionBasedNotifierManager("claymc.teamchat");
        ANotifierManager punishNotifier = new PermissionBasedNotifierManager("claymc.punish.notification");

        IPunishManager punishManager = new SQLPunishManager(database, punishNotifier);

        getProxy().getPluginManager().registerCommand(this, new TeamChatCommand(teamChatNotifier));
        getProxy().getPluginManager().registerCommand(this, new PunishCommand(punishManager));

        getProxy().getPluginManager().registerListener(this, new PunishLoginListener(punishManager, this));


    }
}
