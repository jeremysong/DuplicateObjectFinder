package io;

import java.io.Closeable;

/**
 * Created by jeremy on 3/22/15.
 */
public interface ObjectReader<E> extends Closeable {

    public boolean hasNext();

    public E getNextObject();
}
