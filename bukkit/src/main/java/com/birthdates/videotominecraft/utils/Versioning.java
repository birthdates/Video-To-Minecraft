package com.birthdates.videotominecraft.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class Versioning {

    private final Pattern versionPattern = Pattern.compile("[0-9]\\.[0-9]([0-9]|)"); //matches major.miner (i.e 1.17)
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
        Matcher matcher = versionPattern.matcher(ver);
        if(matcher.find())
            ver = matcher.group();
        String[] numbers = ver.split("\\.");
        return Integer.parseInt(numbers[1]);
    }
}
