import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;

public class XMLConfigurationLoadFuzzer {
    private static File tempFile = null;

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        // Create needed objects
        final File tempFile;
        try {
            tempFile = File.createTempFile("XMLConfiguration", "xml");
        } catch (IOException ioe) {
            // Preparations failed ; exit early
            return;
        }
        final String absoluteFilepath = tempFile.getAbsolutePath();

        try {
            final FileWriter fileWriter = new FileWriter(tempFile);
            fileWriter.write(data.consumeRemainingAsString());
            fileWriter.close();
        } catch (IOException ioe) {
            // Preparations failed ; exit early
            return;
        }

        final XMLConfiguration xmlConfig = new XMLConfiguration();
        xmlConfig.setLogger(null); // disable logger

        final FileHandler fileHandler = new FileHandler(xmlConfig);
        fileHandler.setPath(absoluteFilepath);

        try {
            fileHandler.load();
        } catch (ConfigurationException ignored) {
            // expected Exceptions get ignored
        }
    }

    public static void fuzzerTearDown() {
        if (tempFile != null) {
            tempFile.delete();
        }
    }
}
