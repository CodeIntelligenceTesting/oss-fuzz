import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.yaml.snakeyaml.LoaderOptions;

public class YAMLConfigurationReadFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        // Create helper objects from fuzzer data
        final boolean useInputStream = data.consumeBoolean();
        final boolean useLoaderOptions = data.consumeBoolean();

        // Create needed objects
        final YAMLConfiguration yamlConfig = new YAMLConfiguration();
        yamlConfig.setLogger(null); // disable logging

        // the actual fuzzing starts here
        try {
            final InputStream inputStream;
            final InputStreamReader reader;
            if (useInputStream) {
                if (useLoaderOptions) {
                    final LoaderOptions loaderOptions = createLoaderOptions(data);
                    inputStream = new ByteArrayInputStream(data.consumeBytes(Integer.MAX_VALUE));
                    yamlConfig.read(inputStream, loaderOptions);
                } else {
                    inputStream = new ByteArrayInputStream(data.consumeBytes(Integer.MAX_VALUE));
                    yamlConfig.read(inputStream);
                }
            } else {
                if (useLoaderOptions) {
                    final LoaderOptions loaderOptions = createLoaderOptions(data);
                    inputStream = new ByteArrayInputStream(data.consumeBytes(Integer.MAX_VALUE));
                    reader = new InputStreamReader(inputStream);
                    yamlConfig.read(reader, loaderOptions);
                } else {
                    inputStream = new ByteArrayInputStream(data.consumeBytes(Integer.MAX_VALUE));
                    reader = new InputStreamReader(inputStream);
                    yamlConfig.read(reader);
                }
            }
        } catch (ConfigurationException ignored) {
            // expected Exceptions get ignored
        }
    }

    private static LoaderOptions createLoaderOptions(FuzzedDataProvider data) {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowDuplicateKeys(data.consumeBoolean());
        loaderOptions.setWrappedToRootException(data.consumeBoolean());
        loaderOptions.setAllowRecursiveKeys(data.consumeBoolean());
        loaderOptions.setProcessComments(data.consumeBoolean());
        loaderOptions.setEnumCaseSensitive(data.consumeBoolean());

        // seems to not yet be implemented in the version that apache-commons-configuration2 uses
        // loaderOptions.setNestingDepthLimit(data.consumeInt(50, 100)); // 50 as minimum as that is
        // the default value

        return loaderOptions;
    }
}
