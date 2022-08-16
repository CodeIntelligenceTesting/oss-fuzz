import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import org.apache.hc.client5.http.entity.mime.Header;
import org.apache.hc.client5.http.entity.mime.MimeField;

public class HeaderAddFieldFuzzer {
    private static final int MAX_NAME_LENGTH = 300;
    private static final int MAX_VALUE_LENGTH = 700;

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        final Header header = new Header();

        while (data.remainingBytes() > 0) {
            header.addField(new MimeField(
                data.consumeString(MAX_NAME_LENGTH), data.consumeString(MAX_VALUE_LENGTH)));
        }
    }
}
