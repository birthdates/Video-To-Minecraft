package com.birthdates.videotominecraft.movie.removable.impl;

import com.birthdates.videotominecraft.movie.removable.IRemovable;
import org.bukkit.Location;
import org.bukkit.Material;

public class BlockRemovable implements IRemovable {

    private final Location location;

    public BlockRemovable(Location location) {
        this.location = location;
    }

    @Override
    public void remove() {
        location.getBlock().setType(Material.AIR);
    }

}
