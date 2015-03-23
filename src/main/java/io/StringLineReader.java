package io;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * Created by jeremy on 3/22/15.
 */
public class StringLineReader implements ObjectReader<String> {

    private LineIterator iterator;

    public StringLineReader(@Nonnull File inputFile) throws IOException {
        Preconditions.checkNotNull(inputFile, "Input file cannot be null");
        iterator = FileUtils.lineIterator(inputFile, "UTF-8");
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public String getNextObject() {
        return iterator.next();
    }

    @Override
    public void close() {
        iterator.close();
    }
}
