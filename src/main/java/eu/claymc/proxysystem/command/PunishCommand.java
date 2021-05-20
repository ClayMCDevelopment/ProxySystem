package eu.claymc.proxysystem.command;

import eu.claymc.proxysystem.punish.APunishEntry;
import eu.claymc.proxysystem.punish.IPunishManager;
import eu.claymc.proxysystem.punish.PunishReason;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.IOfflineCloudPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.ExecutionException;

import static eu.claymc.proxysystem.ProxyPlugin.PREFIX;

public class PunishCommand extends Command {
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
                sender.sendMessage(PREFIX + " §8 > §7" + reason.name());
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

            punishEntry.commit();
            punishManager.addToCache(punishEntry.target(), punishEntry);
            punishManager.addToCache(punishEntry.punisher(), punishEntry);


        }


    }
}