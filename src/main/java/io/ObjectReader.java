package io;

import java.io.Closeable;

/**
 * Ideally this could support many formats depends on the implementation. The underline implementation should use
 * {@link java.util.Iterator} because the input source is potentially too large to be fitted into memory.
 */
public interface ObjectReader<E> extends Closeable {

    public boolean hasNext();

    public E getNextObject();
}
