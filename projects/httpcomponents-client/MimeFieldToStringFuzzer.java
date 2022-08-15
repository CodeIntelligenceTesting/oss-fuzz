import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import org.apache.hc.client5.http.entity.mime.MimeField;

public class MimeFieldToStringFuzzer {
    private static final int nameLength = 300;

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        // Create objects from fuzzer input
        final String name = data.consumeString(nameLength);
        final String value = data.consumeRemainingAsString();

        // fuzzing begins here
        final MimeField mimeField = new MimeField(name, value);
        mimeField.toString();
    }
}
