package com.birthdates.videotominecraft.utils.locations;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;

@UtilityClass
public class Locations {

    public Direction getDirection(Location location) {
        double rot = location.getYaw() % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        if (0 <= rot && rot < 45 || 315 <= rot && rot < 360.0) {
            return Direction.SOUTH;
        } else if (45 <= rot && rot < 135) {
            return Direction.WEST;
        } else if (135 <= rot && rot < 225) {
            return Direction.NORTH;
        } else if (225 <= rot && rot < 315) {
            return Direction.EAST;
        }
        return Direction.EAST;
    }

}
