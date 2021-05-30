package com.birthdates.videotominecraft.movie.removable.impl;

import com.birthdates.videotominecraft.movie.removable.IRemovable;
import org.bukkit.Location;
import org.bukkit.Material;

public class BlockRemovable extends IRemovable {

    public BlockRemovable(Location location) {
        super(location);
    }

    private Location getLocation() {
        return (Location) object;
    }

    @Override
    public void remove() {
        getLocation().getBlock().setType(Material.AIR);
    }

    /**
     * Override equals as {@link Location#equals(Object)} looks at the exact location
     * However we only need the block location
     *
     * @param other Object to compare to
     * @return If {@code other} is equal to {@link object}
     */
    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) return true;
        if (!(other instanceof Location)) return false;
        Location location2 = (Location) other;
        Location location = getLocation();
        return location.getBlockX() == location2.getBlockX() && location.getBlockY() == location2.getBlockY() && location.getBlockZ() == location2.getBlockZ();
    }
}
