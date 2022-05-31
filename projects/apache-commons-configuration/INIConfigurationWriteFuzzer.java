import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.IOException;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class INIConfigurationWriteFuzzer extends INIConfiguration {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        // Create helper objects from fuzzer data
        final char comment_character = data.consumeChar();
        final char input_separator_character = data.consumeChar();
        final char output_separator_character = data.consumeChar();
        final byte[] byte_array = data.consumeBytes(Integer.MAX_VALUE);

        // Create the needed objects
        INIConfiguration  ini_config   = new INIConfiguration();
        InputStream       input_stream = new ByteArrayInputStream(byte_array);
        InputStreamReader reader       = new InputStreamReader(input_stream);
        StringWriter      writer       = new StringWriter();

        try {
            ini_config.setSeparatorUsedInInput(Character.toString(input_separator_character));
            ini_config.setCommentLeadingCharsUsedInInput(Character.toString(comment_character));
            ini_config.read(reader);

            ini_config.setSeparatorUsedInOutput(Character.toString(output_separator_character));
            ini_config.write(writer);
        } catch ( IOException | ConfigurationException ignored ) {
            // expected Exceptions get ignored
        }
    }
}
