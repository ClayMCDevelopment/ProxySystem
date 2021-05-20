package eu.claymc.proxysystem.punish;

import java.util.concurrent.TimeUnit;

public enum PunishReason {

    HACKS(PunishType.BAN, TimeUnit.DAYS.toMillis(30)),
    INSULT(PunishType.MUTE, TimeUnit.DAYS.toMillis(1)),
    REPORT_ABUSE(PunishType.BAN, TimeUnit.DAYS.toMillis(7)),
    CHAT_ABUSE(PunishType.MUTE, TimeUnit.DAYS.toMillis(30)),
    ADVERTISEMENT(PunishType.SHADOW_MUTE, TimeUnit.DAYS.toMillis(90))
    ;


    private final PunishType type;
    private final long duration;

    PunishReason(PunishType type, long duration) {
        this.type = type;
        this.duration = duration;
    }

    public PunishType getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }
}
