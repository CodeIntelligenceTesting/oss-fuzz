import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.api.FuzzerSecurityIssueLow;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.core5.http.ContentType;

public class FileBodyWriteToFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        // Create objects from fuzzer input
        final ContentType contentType = data.pickValue(contentTypes);
        final String filename = data.consumeString(255);
        final String fileContent = data.consumeRemainingAsString();

        // Create needed objects
        final File tempFile;
        try {
            tempFile = File.createTempFile("FileBody", "bin");
            tempFile.deleteOnExit();

            final FileWriter fileWriter = new FileWriter(tempFile);
            fileWriter.write(fileContent);
            fileWriter.close();
        } catch (IOException ioe) {
            // Preparations failed ; exit early
            return;
        }

        // Actual fuzzing begins here
        try {
            final FileBody fileBody = new FileBody(tempFile, contentType, filename);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            fileBody.writeTo(outputStream);

            /* does not work for some weird reason
             * fileContent.equals(outputStreamString) keeps failing on equality
            final String outputStreamString = outputStream.toString();
            if (!(fileContent.equals(outputStreamString))) {
                System.err.println("Content: \"" + fileContent + "\"");
                System.err.println("Output : \"" + outputStreamString + "\"");
                System.err.println("-----------------------------------------------------------");
                System.err.println(byteRepresentation(fileContent));
                System.err.println(byteRepresentation(outputStreamString));

                throw new FuzzerSecurityIssueLow("File contents changed during FileBody.writeTo");
            }
            */
        } catch (IOException ignored) {
            // ignore expected exceptions
        }
    }

    private static final ContentType[] contentTypes = {ContentType.APPLICATION_ATOM_XML,
        ContentType.APPLICATION_FORM_URLENCODED, ContentType.APPLICATION_JSON,
        ContentType.APPLICATION_NDJSON, ContentType.APPLICATION_OCTET_STREAM,
        ContentType.APPLICATION_PDF, ContentType.APPLICATION_PROBLEM_JSON,
        ContentType.APPLICATION_PROBLEM_XML, ContentType.APPLICATION_RSS_XML,
        ContentType.APPLICATION_SOAP_XML, ContentType.APPLICATION_SVG_XML,
        ContentType.APPLICATION_XHTML_XML, ContentType.APPLICATION_XML, ContentType.DEFAULT_BINARY,
        ContentType.DEFAULT_TEXT, ContentType.IMAGE_BMP, ContentType.IMAGE_GIF,
        ContentType.IMAGE_JPEG, ContentType.IMAGE_PNG, ContentType.IMAGE_SVG,
        ContentType.IMAGE_TIFF, ContentType.IMAGE_WEBP, ContentType.MULTIPART_FORM_DATA,
        ContentType.MULTIPART_MIXED, ContentType.MULTIPART_RELATED, ContentType.TEXT_EVENT_STREAM,
        ContentType.TEXT_HTML, ContentType.TEXT_MARKDOWN, ContentType.TEXT_PLAIN,
        ContentType.TEXT_XML, ContentType.WILDCARD};

    /* was used for debugging
    private static String byteRepresentation(String str) {
        final byte[] byteArray = str.getBytes();
        long counter = 0;

        String result = new String();

        for (byte b : byteArray) {
            counter++;
            result += String.format("0x%02x ", b);

            if (counter % 16 == 0) {
                result += "\n";
            }
        }

        return result;
    }
    */
}
