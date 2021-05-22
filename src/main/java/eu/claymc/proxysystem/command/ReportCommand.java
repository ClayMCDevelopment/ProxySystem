package eu.claymc.proxysystem.command;

import eu.claymc.proxysystem.ProxyPlugin;
import eu.claymc.proxysystem.report.AReportEntry;
import eu.claymc.proxysystem.report.IReportManager;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.IOfflineCloudPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.ExecutionException;

import static eu.claymc.proxysystem.ProxyPlugin.PREFIX;

public class ReportCommand extends Command {
    private IReportManager reportManager;

    public ReportCommand(IReportManager reportManager) {
        super("report");
        this.reportManager = reportManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0){
            sender.sendMessage(ProxyPlugin.PREFIX + "§c/report <target> <reason>");
        }else if(args.length == 2){
            String targetName = args[0];
            String reason = args[1];

            AReportEntry entry = reportManager.createEntry();
            entry.timestamp(System.currentTimeMillis());
            entry.reason(reason);
            try {
                IOfflineCloudPlayer suspectCloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(targetName).get();
                entry.suspect(suspectCloudPlayer);
            } catch (InterruptedException | ExecutionException e) {
                sender.sendMessage(PREFIX + "§cDer Spieler \"" + targetName + "\" ist dem Netzwerk nicht bekannt! ");
                return;
            }

            try {
                IOfflineCloudPlayer reporterCloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getOfflineCloudPlayer(sender.getName()).get();
                entry.reporter(reporterCloudPlayer);
            } catch (InterruptedException | ExecutionException e) {
                sender.sendMessage(PREFIX + "§cDer Spieler \"" + targetName + "\" ist dem Netzwerk nicht bekannt! ");
                return;
            }

            sender.sendMessage(PREFIX + "Du hast den Spieler " + entry.suspect().getName() + " erfolgreich reported");


            entry.commit();

        }
    }
}
