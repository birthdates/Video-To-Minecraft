package com.birthdates.videotominecraft.command.watch;

import com.birthdates.videotominecraft.command.PlayerOnlyCommand;
import com.birthdates.videotominecraft.maps.Maps;
import com.birthdates.videotominecraft.worker.Worker;
import com.birthdates.videotominecraft.worker.impl.FrameWorker;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

/**
 * Command used to stop watching a video on a map
 */
public class StopWatchingCommand extends PlayerOnlyCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!super.onCommand(sender, command, label, args)) return false; //is not a player

        Player player = (Player) sender;
        boolean removed =
                removeMapAndWorker(player, player.getInventory().getContents()) ||
                        removeMapAndWorker(player, player.getInventory().getExtraContents());
        sender.sendMessage((removed ? ChatColor.GREEN : ChatColor.RED) + "You have " + (removed ? "" : "not") + " removed any video players.");
        return true;
    }

    private boolean removeMapAndWorker(Player player, ItemStack[] contents) {
        if (contents == null)
            return false;
        boolean found = false;
        for (ItemStack content : contents) {
            if (content == null || !content.hasItemMeta() ||
                    !content.getItemMeta().hasDisplayName() || !content.getItemMeta().getDisplayName().equals(Maps.getMapName()))
                continue;
            player.getInventory().remove(content);

            //stop worker
            Optional<Worker> workerOptional = FrameWorker.getWorkers()
                    .stream()
                    .filter(worker -> Objects.equals(worker.getId(), content)).findAny();
            workerOptional.ifPresent(Worker::finish);

            found = true;
        }
        return found;
    }
}
