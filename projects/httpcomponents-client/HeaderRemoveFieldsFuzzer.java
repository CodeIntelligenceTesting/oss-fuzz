import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.hc.client5.http.entity.mime.Header;
import org.apache.hc.client5.http.entity.mime.MimeField;

public class HeaderRemoveFieldsFuzzer {
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_VALUE_LENGTH = 300;
    private static final int MIN_ARRAYLIST_CAPACITY = 5;
    private static final int MAX_ARRAYLIST_CAPACITY = 700;

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        final int arrayCapacity = data.consumeInt(MIN_ARRAYLIST_CAPACITY, MAX_ARRAYLIST_CAPACITY);
        final int numberOfMimeFields = data.consumeInt(1, arrayCapacity);
        final ArrayList<MimeField> mimeFields =
            generateMimeFields(arrayCapacity, numberOfMimeFields, data);

        final Header header = new Header();
        for (MimeField mimeField : mimeFields) {
            header.addField(mimeField);
        }

        for (MimeField mimeField : mimeFields) {
            header.removeFields(mimeField != null ? mimeField.getName() : null);
        }
    }

    private static ArrayList<MimeField> generateMimeFields(
        int arrayCapacity, int numberOfMimeFields, FuzzedDataProvider data) {
        final ArrayList<MimeField> mimeFields = new ArrayList<>(arrayCapacity);

        int counter = 0;
        for (int i = 0; i < arrayCapacity; i++) {
            final MimeField mimeField;
            if (counter < numberOfMimeFields) {
                mimeField = new MimeField(
                    data.consumeString(MAX_NAME_LENGTH), data.consumeString(MAX_VALUE_LENGTH));
                counter++;
            } else {
                mimeField = null;
            }

            mimeFields.add(mimeField);
        }

        Collections.shuffle(mimeFields);

        return mimeFields;
    }
}
