package eu.claymc.proxysystem.command;

import eu.claymc.proxysystem.ProxyPlugin;
import eu.claymc.proxysystem.punish.APunishEntry;
import eu.claymc.proxysystem.punish.IPunishManager;
import eu.claymc.proxysystem.punish.PunishReason;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.IOfflineCloudPlayer;
import eu.thesimplecloud.api.player.SimpleCloudPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static eu.claymc.proxysystem.ProxyPlugin.PREFIX;

public class PunishCommand extends Command implements TabExecutor {

    private IPunishManager punishManager;

    public PunishCommand(IPunishManager punishManager) {
        super("punish", "claymc.command.punish", "p", "ban");
        this.punishManager = punishManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!hasPermission(sender)) {
            sender.sendMessage(PREFIX + "§cDas darfst du nicht.");
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(PREFIX + "§c/punish <target> <reason> [info]");
            sender.sendMessage(PREFIX + "§c/Reasons: ");
            for (PunishReason reason : PunishReason.values()) {
                sender.sendMessage(PREFIX + " §8§l > §e" + reason.name());
            }
        }
        if (args.length >= 2) {

            APunishEntry punishEntry = this.punishManager.createEntry();

            String targetName = args[0];
            PunishReason reason = null;
            try {
                reason = PunishReason.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                sender.sendMessage(PREFIX + "Der Grund wurde nicht gefunden! Bitte überprüfe deine Eingabe");
                return;
            }

            punishEntry.type(reason.getType());

            String info = "";

            for (int i = 2; i < args.length; i++) {
                info += args[i] + " ";
            }

            punishEntry.reason(reason);
            punishEntry.timestamp(System.currentTimeMillis());
            punishEntry.duration(reason.getDuration());

            try {
                IOfflineCloudPlayer targetCloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(targetName).get();
                punishEntry.target(targetCloudPlayer);
            } catch (InterruptedException | ExecutionException e) {
                sender.sendMessage(PREFIX + "§cDer Spieler \"" + targetName + "\" ist dem Netzwerk nicht bekannt! ");
                return;
            }

            try {
                punishEntry.punisher(CloudAPI.getInstance().getCloudPlayerManager().getCloudPlayer(sender.getName()).get());
            } catch (InterruptedException | ExecutionException e) {
                sender.sendMessage("§4§lUps. Da ist was schief gelaufen. Bitte wende dich an einen Developer/Admin");
                return;
            }

            ProxyPlugin.execute(() -> {
                punishEntry.commit();
                punishManager.addToCache(punishEntry.target(), punishEntry);
                punishManager.addToCache(punishEntry.punisher(), punishEntry);
            });


        }


    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

        List<String> result = new ArrayList<>();

        if (args.length == 1) {
            String arg = args[0];
            try {
                List<SimpleCloudPlayer> simpleCloudPlayers = CloudAPI.getInstance().getCloudPlayerManager().getAllOnlinePlayers().get();
                for (SimpleCloudPlayer simpleCloudPlayer : simpleCloudPlayers) {
                    if (simpleCloudPlayer.getName().toLowerCase().startsWith(arg.toLowerCase())) {
                        result.add(simpleCloudPlayer.getName());
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
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
