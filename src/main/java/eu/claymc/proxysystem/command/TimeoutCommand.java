package eu.claymc.proxysystem.command;

import eu.claymc.proxysystem.timeout.ITimeoutManager;
import eu.claymc.proxysystem.util.TimeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.TimeUnit;

import static eu.claymc.proxysystem.ProxyPlugin.PREFIX;

public class TimeoutCommand extends Command {

    private ITimeoutManager timeoutManager;

    public TimeoutCommand(ITimeoutManager timeoutManager) {
        super("timeout", "claymc.command.timeout");
        this.timeoutManager = timeoutManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(PREFIX + "§c/timeout <time in hours>");
        } else if (args.length == 1) {
            long time = TimeUnit.HOURS.toMillis(Integer.parseInt(args[0]));
            sender.sendMessage(PREFIX + "Timeout length: §e" + TimeUtil.convertString(time));
            timeoutManager.timeout(System.currentTimeMillis() + time);
        }

    }
}
