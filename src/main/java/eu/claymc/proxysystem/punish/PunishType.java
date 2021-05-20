package eu.claymc.proxysystem.punish;

public enum PunishType {

    BAN(0),
    MUTE(1),
    SHADOW_MUTE(2);

    private int id;

    PunishType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
