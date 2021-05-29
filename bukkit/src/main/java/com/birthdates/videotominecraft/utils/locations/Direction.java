package com.birthdates.videotominecraft.utils.locations;

import lombok.Getter;

public enum Direction {
    NORTH(false), SOUTH(false), EAST(true), WEST(true);

    @Getter
    private final boolean z;

    Direction(boolean z) {
        this.z = z;
    }
}