import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import org.apache.tomcat.util.http.parser.HttpParser;

public class HttpParserFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        String input = data.consumeRemainingAsString();
        HttpParser.unquote(input);
    }
}
