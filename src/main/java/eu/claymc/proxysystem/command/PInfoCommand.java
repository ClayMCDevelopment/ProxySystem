package eu.claymc.proxysystem.command;

import eu.claymc.proxysystem.database.IDatabase;
import eu.claymc.proxysystem.punish.APunishEntry;
import eu.claymc.proxysystem.punish.IPunishManager;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.IOfflineCloudPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static eu.claymc.proxysystem.ProxyPlugin.PREFIX;

public class PInfoCommand extends Command {

    private IPunishManager punishManager;

    private IDatabase<Connection> database;

    public PInfoCommand(IPunishManager punishManager, IDatabase<Connection> database) {
        super("pinfo", "claymc.command.pinfo");
        this.punishManager = punishManager;
        this.database = database;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(PREFIX + "/pinfo <username>");
        } else if (args.length == 1) {
            String targetInput = args[0];

            IOfflineCloudPlayer targetCloudPlayer;
            try {
                targetCloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(targetInput).get();
            } catch (InterruptedException | ExecutionException e) {
                sender.sendMessage(PREFIX + "§cDer Spieler \"" + targetInput + "\" ist dem Netzwerk nicht bekannt! ");
                return;
            }

            List<APunishEntry> punishCachedEntries = punishManager.getPunishCachedEntries(targetCloudPlayer);

            APunishEntry activePunishEntry = null;

            for (APunishEntry punishCachedEntry : punishCachedEntries) {
                if (punishCachedEntry.duration() + punishCachedEntry.timestamp() >= System.currentTimeMillis()) {
                    activePunishEntry = punishCachedEntry;
                    break;
                }
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            try {
                sender.sendMessage(PREFIX + "Status§8: " + (targetCloudPlayer.isOnline() ? "§a§lOnline §8(§e" +
                        CloudAPI.getInstance().getCloudPlayerManager().getCloudPlayer(targetCloudPlayer.getUniqueId()).get().getConnectedServer().getName() + "§8)"
                        : "§c§lOffline"));


            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            TextComponent hoverPunishment = new TextComponent(PREFIX + "Active Punishment§8: " + (activePunishEntry == null ? "§a§lNo" : "§c§lYes"));
            if (activePunishEntry != null) {
                String hoverText = PREFIX + "Punish Reason§8: §e" + activePunishEntry.reason() + "\n" +
                        PREFIX + "Punish Timestamp§8: §e" + simpleDateFormat.format(new Date(activePunishEntry.timestamp())) + "\n" +
                        PREFIX + "Punish Duration§8: §e" + timeFormat.format(new Date(activePunishEntry.duration())) + "\n" +
                        PREFIX + "Punish expire§8: §e" + timeFormat.format(new Date(activePunishEntry.timestamp() + activePunishEntry.duration()));
                hoverPunishment.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
            }

            sender.sendMessage(hoverPunishment);

            sender.sendMessage(PREFIX + "First Login§8: §e" + simpleDateFormat.format(new Date(targetCloudPlayer.getFirstLogin())));
            sender.sendMessage(PREFIX + "Last Login§8: §e" + simpleDateFormat.format(new Date(targetCloudPlayer.getLastLogin())));

            sender.sendMessage(PREFIX + "Onlinetime§8: §e" + timeFormat.format(new Date(targetCloudPlayer.getOnlineTime())));

            try (Connection connection = database.getConnection(); PreparedStatement pstmt = connection.prepareStatement("SELECT altAccount FROM multiaccounts WHERE mainAccount=?")) {
                pstmt.setString(1, targetCloudPlayer.getUniqueId().toString());
                ResultSet resultSet = pstmt.executeQuery();
                sender.sendMessage(PREFIX + "MultiAccounts§8:");

                while (resultSet.next()) {

                    try {
                        String name = CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(UUID.fromString(resultSet.getString("altAccount"))).get().getName();

                        sender.sendMessage(PREFIX + "§8> §e" + name);

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }


        }

    }
}
