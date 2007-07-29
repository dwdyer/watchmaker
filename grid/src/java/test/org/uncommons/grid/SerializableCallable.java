package org.uncommons.grid;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * Base class for {@link Callable}s that are also {@link Serializable}.
 */
abstract class SerializableCallable<V> implements Callable<V>,
                                                  Serializable
{
    // Intentionally blank.  Sub-classes will add call() method.
}
