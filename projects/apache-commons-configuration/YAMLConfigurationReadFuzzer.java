import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class YAMLConfigurationReadFuzzer {
    public static void fuzzerTestOneInput(byte[] data) {
        // Create needed objects
        YAMLConfiguration yaml_config  = new YAMLConfiguration();
        InputStream       input_stream = new ByteArrayInputStream(data);

        try {
            yaml_config.read(input_stream);
        } catch ( ConfigurationException ignored ) {
            // expected Exceptions get ignored
        }
    }
}
