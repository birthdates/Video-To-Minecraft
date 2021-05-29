package com.birthdates.videotominecraft.movie.removable;

/**
 * Removable class used for scalable removing (i.e if you wanted to add a new element to movies but wanted to remove it)
 */
public abstract class IRemovable {

    protected final Object object;

    public IRemovable(Object object) {
        this.object = object;
    }

    public abstract void remove();

    @Override
    public boolean equals(Object other) {
        return super.equals(other) || other.getClass() == object.getClass() && object.equals(other);
    }
}
