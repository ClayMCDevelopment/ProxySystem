package eu.claymc.proxysystem.report;

import eu.thesimplecloud.api.player.IOfflineCloudPlayer;

import java.util.List;

public interface IReportManager {

    AReportEntry createEntry();

    List<AReportEntry> getAllReports();

    List<AReportEntry> getReports(IOfflineCloudPlayer cloudPlayer);

}
