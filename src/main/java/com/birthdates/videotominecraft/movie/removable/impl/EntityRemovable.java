package com.birthdates.videotominecraft.movie.removable.impl;

import com.birthdates.videotominecraft.movie.removable.IRemovable;
import org.bukkit.entity.Entity;

public class EntityRemovable implements IRemovable {

    private final Entity entity;

    public EntityRemovable(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void remove() {
        entity.remove();
    }

}

