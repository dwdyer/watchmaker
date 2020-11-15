package org.uncommons.watchmaker.framework;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a sample implementation of a storage Pool of Releasable objects.
 * If something is in this Pool, it is available to be reused.  The main functions
 * are add(T) to add an item and nextOrCreate() which returns the next item in this pool
 * or creates a new one.
 *
 * @param <T> The type of the objects in this pool (must extend Releasable).  It must
 *           either have an empty constructor, or a constructor with parameter T (to make
 *           a copy).
 */
public class ReleasablePool<T extends Releasable> {
    private Constructor<T> constructorCopy=null;
    private Constructor<T> constructorEmpty=null;
    private List<T> items = new ArrayList<T>();
    private T prototype;

    /**
     * The prototype should be an object of the given type.  It is used as the prototype
     * when we ask for a new object and there are no released objects available.
     * @param prototype
     */
    public ReleasablePool(T prototype) {
        if (prototype == null)
            throw new IllegalArgumentException("Prototype cannot be null");
        this.prototype = prototype;
        setupConstructors();
    }

    private void setupConstructors() {
        try {
            constructorCopy = (Constructor<T>) prototype.getClass().getConstructor(prototype.getClass());
        } catch (NoSuchMethodException e) {
            try {
                constructorEmpty =  (Constructor<T>) prototype.getClass().getConstructor();
            } catch (NoSuchMethodException e1) {
                throw new IllegalArgumentException("Prototype had no empty, nor copy constructor");
            }
        }
    }

    /**
     * Adds an item to this pool.  Makes sure the item.clearReleased() is called
     * @param item
     */
    public void add(T item) {
        items.add(item);
    }

    /**
     * Gives the next released item from this pool.  If there are no items
     * that are released in this pool, create a new one from the prototype.
     * @return
     */
    public T nextOrCreate() {
        if (items.size() > 0)
            return items.remove(0);
        //We made it here, we have nothing
        //We have to create one

        T rval = null;
        try {
            rval = createItem();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return rval;
    }

    /**
     * Used internally to create a new object for the pool, assumed there are no
     * available items to give.
     * @return A newly created item using either the new T(T src) constructor or
     * new T() constructor.
     */
    private T createItem() {
        if (constructorCopy != null) {
            try {
                return constructorCopy.newInstance(prototype);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        } else {
            try {
                return constructorEmpty.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * @return The number of items in this pool.
     */
    public int size() {
        return items.size();
    }

    /**
     * Changes the prototype for this pool.
     * @param prototype
     */
    public void setProtoype(T prototype) {
        this.prototype = prototype;
        setupConstructors();;
    }
}
