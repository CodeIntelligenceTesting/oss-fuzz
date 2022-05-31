import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.IOException;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class YAMLConfigurationWriteFuzzer {
    public static void fuzzerTestOneInput(byte[] data) {
        // Create needed objects
        YAMLConfiguration yaml_config  = new YAMLConfiguration();
        InputStream       input_stream = new ByteArrayInputStream(data);
        StringWriter      writer       = new StringWriter();

        try {
            yaml_config.read(input_stream);
            yaml_config.write(writer);
        } catch ( IOException | ConfigurationException ignored ) {
            // expected Exceptions get ignored
        }
    }
}
