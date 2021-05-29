package com.birthdates.videotominecraft.versioning;

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

    private Version parseVersion(String ver) {
        String[] numbers = ver.split("\\.");
        return new Version(Integer.parseInt(numbers[1]));
    }

    @AllArgsConstructor
    private static class Version {
        private final int minor; //major & patch is useless in this case
    }
}
