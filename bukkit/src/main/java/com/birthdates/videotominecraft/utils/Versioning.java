package com.birthdates.videotominecraft.utils;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class Versioning {

    private final Version bukkitVersion = parseVersion(Bukkit.getBukkitVersion());

    public boolean isAheadOrEqualTo(int minor) {
        return bukkitVersion.minor >= minor;
    }

    public boolean isBehind(int minor) {
        return !isAheadOrEqualTo(minor);
    }

    /**
     * Check if our current Bukkit version is a legacy version for maps
     * @return Version < 1.13
     */
    public boolean isMapLegacy() {
        return isBehind(13);
    }

    private Version parseVersion(String ver) {
        String[] numbers = ver.split("\\.");
        return new Version(Integer.parseInt(numbers[1]));
    }

    @AllArgsConstructor
    private static class Version {
        private final int minor; //major & patch is useless in our case
    }
}
