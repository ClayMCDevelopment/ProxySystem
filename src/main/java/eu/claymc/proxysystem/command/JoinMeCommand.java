package eu.claymc.proxysystem.command;

import eu.claymc.proxysystem.util.ImageChar;
import eu.claymc.proxysystem.util.ImageMessage;
import eu.thesimplecloud.api.CloudAPI;
import eu.thesimplecloud.api.player.SimpleCloudPlayer;
import eu.thesimplecloud.api.player.text.CloudText;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class JoinMeCommand extends Command {
    public JoinMeCommand() {
        super("joinme");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!hasPermission(commandSender)) {
            commandSender.sendMessage("§CDu darfst das nicht!");
            return;
        }


        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;

        try {
            BufferedImage imageToSend = ImageIO.read(new URL("https://cravatar.eu/avatar/" + commandSender.getName() + "/64"));
            System.out.println("image: " + imageToSend + " - " + "https://cravatar.eu/avatar/" + commandSender.getName() + "/64");

            TextComponent t1 = new TextComponent();
            TextComponent msg2 = new TextComponent("Klicke hier um zu Joinen");

            msg2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("to Server")).create()));
            msg2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + proxiedPlayer.getServer().getInfo().getName()));

            ImageMessage message = new ImageMessage(imageToSend, 8, ImageChar.BLOCK.getChar());
            message.appendCenteredText(" "," "," ",proxiedPlayer.getName() + " Spielt auf", proxiedPlayer.getServer().getInfo().getName());


            for (SimpleCloudPlayer cloudPlayer : CloudAPI.getInstance().getCloudPlayerManager().getAllOnlinePlayers().get()) {
                message.sendToPlayer(cloudPlayer.getCloudPlayer().get());
                cloudPlayer.getCloudPlayer().get().sendMessage(new CloudText("Klicke hier zum Joinen").addClickEvent(CloudText.ClickEventType.RUN_COMMAND, "/server " + proxiedPlayer.getServer().getInfo().getName()));
            }


        } catch (IOException | InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
        }

    }
}
