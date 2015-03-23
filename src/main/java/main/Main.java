package main;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import io.StringLineReader;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.mapdb.DBMaker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.kohsuke.args4j.ExampleMode.ALL;

/**
 * Created by jeremy on 3/22/15.
 */
public class Main {

    @Option(name = "--inputFile", usage = "Absolute path of input file.", required = true)
    public String inputFilePath;

    @Option(name = "--outputFile", usage = "Absolute path of output file.", required = true)
    public String outputFilePath;

    @Option(name = "--tempDir", usage = "Asbolute path of temp folder.", required = true)
    public String tempFolder;

    public static void main(String[] args) throws IOException {
        new Main().doIt(args);
    }

    public void doIt(String[] args) throws IOException {
        CmdLineParser parser = new CmdLineParser(this);
        // if you have a wider console, you could increase the value;
        // here 80 is also the default
        parser.setUsageWidth(80);

        try {
            // parse the arguments.
            parser.parseArgument(args);

        } catch (CmdLineException e) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            System.err.println("java duplicateObjectFinder [options...] arguments...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();

            // print option sample. This is useful some time
            System.err.println("  Example: " + parser.printExample(ALL));

            return;
        }

        checkTempDir(tempFolder);
        checkInputFile(inputFilePath);
        checkOutputFile(outputFilePath);

        // An off-heap set.
        Set<String> objectSet = DBMaker.newTempHashSet();

        // This map will be put in the memory because potentially there will be a few same strings.
        Map<String, Integer> dupStrMap = new HashMap<String, Integer>();

        StringLineReader reader = null;
        try {
            reader = new StringLineReader(new File(inputFilePath));
            while (reader.hasNext()) {
                // Use trim() to remove line delimiter and some white spaces. Hopefully, this will save some space.
                String line = reader.getNextObject().trim();

                // Maybe this is a line contains lots of spaces... After being trimmed, nothing left.
                if (StringUtils.isBlank(line)) {
                    continue;
                }

                if (objectSet.contains(line)) {
                    if (dupStrMap.containsKey(line)) {
                        dupStrMap.put(line, dupStrMap.get(line) + 1);
                    } else {
                        // If this happens, this line has been seen twice.
                        dupStrMap.put(line, 2);
                    }
                } else {
                    objectSet.add(line);
                }
            }
        } catch (Exception e) {
            // Too lazy, just throw exceptions.
            throw Throwables.propagate(e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        System.out.println("Finish reading all input data. Now writing results.");

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(new File(outputFilePath)));
            for (Map.Entry<String, Integer> mapEntry : dupStrMap.entrySet()) {
                writer.write("Key:[" + mapEntry.getKey() + "], # of occurrences:[" + mapEntry.getValue() + "]\n");
            }
        } catch (Exception e) {
            // Too lazy, just throw exceptions.
            throw Throwables.propagate(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        System.out.println("Exit.");
    }

    /**
     * Checks if the given file path is a valid temp directory
     */
    public static void checkTempDir(String tempFolder) throws IOException {
        File tempDir = new File(tempFolder);
        if (!(tempDir.exists() && tempDir.isDirectory())) {
            throw new IOException("Please specify correct temp dir for MapDB");
        }
    }

    public static void checkInputFile(String inputFilePath) throws IOException {
        Preconditions.checkNotNull(inputFilePath, "File path should not be null");

        File f = new File(inputFilePath);
        if (!f.exists() && f.isDirectory()) {
            throw new IOException("Input file does not exist or it is a directory");
        }
    }

    public static void checkOutputFile(String outputFilePath) throws IOException {
        Preconditions.checkNotNull(outputFilePath, "File path should not be null");

        File f = new File(outputFilePath);
        if (f.exists() || f.isDirectory()) {
            throw new IOException("Output file exists.");
        }
    }

}
