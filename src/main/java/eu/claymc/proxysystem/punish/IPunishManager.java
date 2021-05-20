package eu.claymc.proxysystem.punish;

import eu.thesimplecloud.api.player.IOfflineCloudPlayer;

import java.util.List;

public interface IPunishManager {

    APunishEntry createEntry();

    List<APunishEntry> getPunishEntries(IOfflineCloudPlayer cloudPlayer);

    List<APunishEntry> getPunishCachedEntries(IOfflineCloudPlayer cloudPlayer);

    void clearCache(IOfflineCloudPlayer cloudPlayer);

    void addToCache(IOfflineCloudPlayer cloudPlayer, APunishEntry punishEntry);
}
