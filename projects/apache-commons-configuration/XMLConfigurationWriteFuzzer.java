import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.configuration2.io.FileHandler;

public class XMLConfigurationWriteFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        // Create needed objects
        final File tempFile;
        final String absoluteFilepath;
        try {
            tempFile = File.createTempFile("XMLConfiguration", "xml");
            tempFile.deleteOnExit();

            absoluteFilepath = tempFile.getAbsolutePath();

            final FileWriter fileWriter = new FileWriter(tempFile);
            fileWriter.write(data.consumeRemainingAsString());
            fileWriter.close();
        } catch (IOException ioe) {
            // Preparations failed ; exit early
            return;
        }

        final XMLConfiguration xmlConfig = new XMLConfiguration();
        xmlConfig.setLogger(null); // disable the logger

        final FileHandler fileHandler = new FileHandler(xmlConfig);
        fileHandler.setPath(absoluteFilepath);

        final StringWriter writer = new StringWriter();

        try {
            fileHandler.load();
            xmlConfig.write(writer);
        } catch (ConfigurationException | IOException ignored) {
            // expected Exceptions get ignored
        }
    }
}
