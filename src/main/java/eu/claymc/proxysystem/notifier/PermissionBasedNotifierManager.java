package eu.claymc.proxysystem.notifier;

import eu.thesimplecloud.api.player.ICloudPlayer;

public class PermissionBasedNotifierManager extends ANotifierManager {

    private String permission;

    public PermissionBasedNotifierManager(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean canReceiveTeamMessage(ICloudPlayer cloudPlayer) {
        return cloudPlayer.hasPermissionSync(permission);
    }
}
