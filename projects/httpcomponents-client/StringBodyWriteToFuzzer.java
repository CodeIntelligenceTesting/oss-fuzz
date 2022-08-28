import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.core5.http.ContentType;

public class StringBodyWriteToFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        // Create objects from fuzzer input
        final ContentType contentType = data.pickValue(contentTypes);
        final String stringData = data.consumeRemainingAsString();

        // Actual fuzzing begins here
        try {
            final StringBody stringBody = new StringBody(stringData, contentType);
            stringBody.writeTo(new ByteArrayOutputStream());
        } catch (IOException ignored) {
            // ignore expected exceptions
        }
    }

    private static final ContentType[] contentTypes = {ContentType.APPLICATION_ATOM_XML,
        ContentType.APPLICATION_FORM_URLENCODED, ContentType.APPLICATION_JSON, ContentType.APPLICATION_NDJSON,
        ContentType.APPLICATION_OCTET_STREAM, ContentType.APPLICATION_PDF, ContentType.APPLICATION_PROBLEM_JSON,
        ContentType.APPLICATION_PROBLEM_XML, ContentType.APPLICATION_RSS_XML, ContentType.APPLICATION_SOAP_XML,
        ContentType.APPLICATION_SVG_XML, ContentType.APPLICATION_XHTML_XML, ContentType.APPLICATION_XML,
        ContentType.DEFAULT_BINARY, ContentType.DEFAULT_TEXT, ContentType.IMAGE_BMP, ContentType.IMAGE_GIF,
        ContentType.IMAGE_JPEG, ContentType.IMAGE_PNG, ContentType.IMAGE_SVG, ContentType.IMAGE_TIFF,
        ContentType.IMAGE_WEBP, ContentType.MULTIPART_FORM_DATA, ContentType.MULTIPART_MIXED,
        ContentType.MULTIPART_RELATED, ContentType.TEXT_EVENT_STREAM, ContentType.TEXT_HTML, ContentType.TEXT_MARKDOWN,
        ContentType.TEXT_PLAIN, ContentType.TEXT_XML, ContentType.WILDCARD};
}
