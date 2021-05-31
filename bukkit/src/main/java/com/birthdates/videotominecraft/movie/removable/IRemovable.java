package com.birthdates.videotominecraft.movie.removable;

/**
 * Removable class used for scalable removing (i.e if you wanted to add a new element to movies but wanted to remove it)
 * Acts as a middleware for objects
 */
public abstract class IRemovable {

    protected final Object object;

    public IRemovable(Object object) {
        this.object = object;
    }

    public abstract void remove();

    /**
     * Act as a middleware for {@link Object#equals(Object)}
     *
     * @param other Object to compare with
     * @return If we or {@link object} are equal to {@code other}
     */
    @Override
    public boolean equals(Object other) {
        return super.equals(other) || other.getClass() == object.getClass() && object.equals(other);
    }
}
