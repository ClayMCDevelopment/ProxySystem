package eu.claymc.proxysystem.punish;

import eu.thesimplecloud.api.player.IOfflineCloudPlayer;

public abstract class APunishEntry {


    private PunishType punishType;

    private IOfflineCloudPlayer target;
    private IOfflineCloudPlayer punisher;

    private PunishReason reason;
    private long timestamp;
    private long duration;

    public APunishEntry type(PunishType punishType) {
        this.punishType = punishType;
        return this;
    }

    public APunishEntry target(IOfflineCloudPlayer target) {
        this.target = target;
        return this;
    }

    public APunishEntry punisher(IOfflineCloudPlayer punisher) {
        this.punisher = punisher;
        return this;
    }

    public APunishEntry reason(PunishReason reason) {
        this.reason = reason;
        return this;
    }

    public APunishEntry timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public APunishEntry duration(long duration) {
        this.duration = duration;
        return this;
    }


    public PunishType punishType() {
        return this.punishType;
    }

    public IOfflineCloudPlayer target() {
        return this.target;
    }

    public IOfflineCloudPlayer punisher() {
        return this.punisher;
    }

    public long timestamp() {
        return this.timestamp;
    }

    public PunishReason reason() {
        return this.reason;
    }

    public long duration() {
        return this.duration;
    }

    public abstract void commit();

    public abstract void update();

}
