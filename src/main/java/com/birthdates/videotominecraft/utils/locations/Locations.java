package com.birthdates.videotominecraft.utils.locations;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;

@UtilityClass
public class Locations {

    /**
     * Get a {@link Direction} based off location yaw
     *
     * @param location Target location
     * @return {@link Direction} associated with the location
     */
    public Direction getDirection(Location location) {
        double rot = location.getYaw() % 360D;
        if (rot < 0D) {
            rot += 360.0D;
        }
        if (0D <= rot && rot < 45D || 315D <= rot && rot < 360.0D) {
            return Direction.SOUTH;
        } else if (45D <= rot && rot < 135D) {
            return Direction.WEST;
        } else if (135D <= rot && rot < 225D) {
            return Direction.NORTH;
        } else if (225D <= rot && rot < 315D) {
            return Direction.EAST;
        }
        return Direction.EAST;
    }

}
