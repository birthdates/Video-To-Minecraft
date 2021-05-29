package com.birthdates.videotominecraft.movie.removable.impl;

import com.birthdates.videotominecraft.movie.removable.IRemovable;
import org.bukkit.entity.Entity;

public class EntityRemovable extends IRemovable {

    public EntityRemovable(Entity entity) {
        super(entity);
    }

    @Override
    public void remove() {
        ((Entity) object).remove();
    }

}

