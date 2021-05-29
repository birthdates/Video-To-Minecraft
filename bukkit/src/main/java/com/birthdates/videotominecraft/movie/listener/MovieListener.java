package com.birthdates.videotominecraft.movie.listener;

import com.birthdates.videotominecraft.movie.Movie;
import com.birthdates.videotominecraft.movie.removable.IRemovable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MovieListener implements Listener {

    private final Movie parent;

    public MovieListener(Movie parent) {
        this.parent = parent;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        cancelIfRemovable(event, event.getRightClicked());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        cancelIfRemovable(event, event.getBlock().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        cancelIfRemovable(event, event.getEntity());
    }

    /**
     * Cancel a {@link Cancellable} if {@code object} is apart of {@link parent}
     * @param event Event to cancel
     * @param object Target object
     */
    private void cancelIfRemovable(Cancellable event, Object object) {
        for (IRemovable removable : parent.getToRemove()) {
            if (!removable.equals(object)) continue;
            event.setCancelled(true);
            return;
        }
    }
}
