import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class INIConfigurationReadFuzzer extends INIConfiguration {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        // Create helper objects from fuzzer data
        final char comment_character = data.consumeChar();
        final char separator_character = data.consumeChar();
        final byte[] byte_array = data.consumeBytes(Integer.MAX_VALUE);

        // Create needed objects
        INIConfiguration  ini_config   = new INIConfiguration();
        InputStream       input_stream = new ByteArrayInputStream(byte_array);
        InputStreamReader reader       = new InputStreamReader(input_stream);

        try {
            ini_config.setSeparatorUsedInInput(Character.toString(separator_character));
            ini_config.setCommentLeadingCharsUsedInInput(Character.toString(comment_character));
            ini_config.read(reader);
        } catch ( IOException | ConfigurationException ignored ) {
            // expected Exceptions get ignored
        }
    }
}
