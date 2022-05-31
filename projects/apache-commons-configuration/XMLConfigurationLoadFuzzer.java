import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;

public class XMLConfigurationLoadFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        // Create needed objects
        final File temp_file;
        try {
            temp_file = File.createTempFile("XMLConfiguration", "xml");
            temp_file.deleteOnExit();
        } catch ( IOException ioe ) {
            // Preparations failed ; exit early
            return;
        }
        final String absolute_filepath = temp_file.getAbsolutePath();

        try {
            final FileWriter file_writer = new FileWriter(temp_file);
            file_writer.write(data.consumeRemainingAsString());
            file_writer.close();
        } catch ( IOException ioe ) {
            // Preparations failed ; exit early
            return;
        }

        final FileHandler file_handler = new FileHandler(new XMLConfiguration());
        file_handler.setPath(absolute_filepath);

        try {
            file_handler.load();
        } catch ( ConfigurationException ignored ) {
            // expected Exceptions get ignored
        }
    }
}
