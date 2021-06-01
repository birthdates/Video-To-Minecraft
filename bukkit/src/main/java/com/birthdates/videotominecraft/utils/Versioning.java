package com.birthdates.videotominecraft.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class Versioning {

    private final int bukkitVersion = parseVersion(Bukkit.getBukkitVersion());

    public boolean isAheadOrEqualTo(int minor) {
        return bukkitVersion >= minor;
    }

    public boolean isBehind(int minor) {
        return !isAheadOrEqualTo(minor);
    }

    /**
     * Check if our current Bukkit version is a legacy version for maps
     *
     * @return Version < 1.13
     */
    public boolean isMapLegacy() {
        return isBehind(13);
    }

    private int parseVersion(String ver) {
        String[] numbers = ver.split("\\.");
        return Integer.parseInt(numbers[1]);
    }
}
