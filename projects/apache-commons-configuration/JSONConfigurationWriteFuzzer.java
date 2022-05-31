import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.IOException;

import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class JSONConfigurationWriteFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        // Create helper objects from fuzzer data
        final boolean use_reader = data.consumeBoolean();
        final byte[]  byte_array = data.consumeBytes(Integer.MAX_VALUE);

        // Create needed objects
        JSONConfiguration json_config  = new JSONConfiguration();
        InputStream       input_stream = new ByteArrayInputStream(byte_array);
        InputStreamReader reader;
        StringWriter      writer       = new StringWriter();

        try {
            if ( use_reader ) {
                reader = new InputStreamReader(input_stream);
                json_config.read(reader);
            } else {
                json_config.read(input_stream);
            }
            json_config.write(writer);
        } catch ( IOException | ConfigurationException ignored ) {
            // expected Exceptions get ignored
        }
    }
}
