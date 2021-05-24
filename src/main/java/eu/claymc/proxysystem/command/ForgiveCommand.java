package eu.claymc.proxysystem.command;

import eu.claymc.proxysystem.ProxyPlugin;
import eu.claymc.proxysystem.punish.APunishEntry;
import eu.claymc.proxysystem.punish.IPunishManager;
import eu.claymc.proxysystem.punish.PunishReason;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.IOfflineCloudPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static eu.claymc.proxysystem.ProxyPlugin.PREFIX;

public class ForgiveCommand extends Command implements TabExecutor {
    private IPunishManager punishManager;

    public ForgiveCommand(IPunishManager punishManager) {
        super("forgive", "claymc.command.punish", "unban");
        this.punishManager = punishManager;
    }


    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!hasPermission(sender)) {
            sender.sendMessage(PREFIX + "§cDas darfst du nicht.");
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(PREFIX + "§c/forgive <username>");
        } else if (args.length == 1) {
            String targetName = args[0];
            IOfflineCloudPlayer targetPlayer;
            try {
                targetPlayer = CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(targetName).get();
            } catch (InterruptedException | ExecutionException e) {
                sender.sendMessage(PREFIX + "§cDer Spieler \"" + targetName + "\" ist dem Netzwerk nicht bekannt!");
                return;
            }

            List<APunishEntry> punishEntries = punishManager.getPunishCachedEntries(targetPlayer);

            if (punishEntries.isEmpty()) {
                sender.sendMessage(PREFIX + "§cDer Spieler §e" + targetPlayer.getName() + " §cwurde noch NIE bestraft");
                return;
            }

            AtomicBoolean found = new AtomicBoolean(false);
            for (APunishEntry punishEntry : punishEntries) {
                //filter where punish target is the target player
                if (punishEntry.target().getName().equals(targetPlayer.getName())) {

                    if (punishEntry.timestamp() + punishEntry.duration() > System.currentTimeMillis()) {

                        ProxyPlugin.execute(() -> {
                            punishEntry.duration(0);
                            punishEntry.update();


                            sender.sendMessage(PREFIX + "Dem Spieler §e" + targetPlayer.getName() + "§7 wurde §e#" + punishEntry.id() + " §7vergeben");


                            found.set(true);
                        });
                    }


                }

            }


            if (!found.get()) {
                sender.sendMessage(PREFIX + " Der Spieler §e " + targetPlayer.getName() + "§7 hat keine aktive Strafe");
            } else {
                punishManager.clearCache(targetPlayer);
                punishManager.getPunishCachedEntries(targetPlayer);
            }
        }


    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        List<String> result = new ArrayList<>();

        if (args.length == 1) {
            String arg = args[0];

            List<APunishEntry> entries = punishManager.getAllPunishEntries();

            for (APunishEntry entry : entries) {
                if (entry.timestamp() + entry.duration() > System.currentTimeMillis()) {
                    if (entry.target().getName().toLowerCase().startsWith(arg.toLowerCase())) {
                        result.add(entry.target().getName());
                    }
                }

            }

        } else if (args.length == 2) {
            String arg = args[1];
            for (PunishReason value : PunishReason.values()) {
                if (value.name().startsWith(arg)) {
                    result.add(value.name());

                }
            }
        }

        return result;
    }
}
