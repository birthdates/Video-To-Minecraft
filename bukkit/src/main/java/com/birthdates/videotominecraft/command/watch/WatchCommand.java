package com.birthdates.videotominecraft.command.watch;

import com.birthdates.videotominecraft.VideoToMinecraft;
import com.birthdates.videotominecraft.command.PlayerOnlyCommand;
import com.birthdates.videotominecraft.maps.Maps;
import com.birthdates.videotominecraft.maps.renderer.MapImageRenderer;
import com.birthdates.videotominecraft.worker.impl.ExtractWorker;
import com.birthdates.videotominecraft.worker.impl.FrameWorker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

/**
 * Base watch command to watch a video on a map item
 */
public class WatchCommand extends PlayerOnlyCommand {

    private final boolean rotate;
    private final String permission;

    public WatchCommand() {
        rotate = false;
        permission = "videotominecraft.watch";
    }

    public WatchCommand(boolean rotate, String permission) {
        this.rotate = rotate;
        this.permission = permission;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!super.onCommand(sender, command, label, args)) return false; //is not a player

        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <name>");
            return false;
        }

        String id = args[0];
        String filePath = VideoToMinecraft.getInstance().getDataFolder() + "/" + id + ".mp4";
        File file = new File(filePath);

        if (!file.exists()) {
            sender.sendMessage(ChatColor.RED + "Invalid video!");
            return false;
        }

        /*
        ID used for the output directory (i.e rotated-5-videoname-20)
        Use fps to ensure the correct framerate & grid size to ensure correct resolution
         */
        String newId = (rotate ? "rotated-" + VideoToMinecraft.getInstance().getConfiguration().getGridSize() + "-" : "") + id + "-" + VideoToMinecraft.getInstance().getFPS();
        String outputDir = VideoToMinecraft.getInstance().getDataFolder() + "/frames/" + newId + "/";
        sender.sendMessage(ChatColor.GREEN + "Extracting...");

        //start extracting each frame for the video
        ExtractWorker extractor = new ExtractWorker(file, outputDir);
        extractor.start(rotate, () -> handleVideo((Player) sender, outputDir));
        return true;
    }

    protected void handleVideo(Player player, String framePath) {
        handleExtract(player, framePath);
    }

    private void handleExtract(Player player, String framePath) {
        MapImageRenderer imageRenderer = new MapImageRenderer();
        ItemStack itemStack = Maps.createMap(player, player.getWorld(), imageRenderer);
        FrameWorker frameWorker = new FrameWorker(itemStack, framePath);

        frameWorker.start((bufferedImage -> renderAndSendToPlayer(bufferedImage, player, imageRenderer)), () -> onVideoEnd(player, itemStack));
        addOrSetItemInHand(player, itemStack);
        player.sendMessage(ChatColor.GREEN + "Spawned.");
    }

    private void addOrSetItemInHand(Player player, ItemStack itemStack) {
        if (player.getItemInHand().getType() == Material.AIR) {
            player.setItemInHand(itemStack);
            return;
        }
        player.getInventory().addItem(itemStack);
    }

    private void onVideoEnd(Player player, ItemStack toRemove) {
        player.getInventory().remove(toRemove);
        player.sendMessage(ChatColor.GREEN + "Video complete.");
    }

    private void renderAndSendToPlayer(byte[] bufferedImage, Player player, MapImageRenderer imageRenderer) {
        imageRenderer.drawRawPixels(bufferedImage);
        imageRenderer.sendToPlayer(player);
    }
}
