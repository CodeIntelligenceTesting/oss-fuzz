import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.api.FuzzerSecurityIssueHigh;
import com.google.common.base.Charsets;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import java.lang.IllegalArgumentException;
import java.math.BigInteger;
import java.net.URLDecoder;

public class UrlEscapersFuzzer {

	/*
	 * these constants are private members copy-pasted from
	 * com.google.common.net.UrlEscapers
	 */
	static final String URL_FORM_PARAMETER_OTHER_SAFE_CHARS = "-_.*";
	static final String URL_PATH_OTHER_SAFE_CHARS_LACKING_PLUS =
      "-._~" // Unreserved characters.
          + "!$'()*,;&=" // The subdelim characters (excluding '+').
          + "@:"; // The gendelim characters permitted in paths.

    private static String toHex(String arg) {
		return String.format("%x", new BigInteger(1, arg.getBytes(Charsets.UTF_8)));
	}

	private static void rejectSample(String original, String result, String encoded, String percentEncoded) {
		System.out.println("sample: " + toHex(original));
		System.out.println("encoded: " + encoded);
		System.out.println("percentEncoded: " + percentEncoded);
		System.out.println("result: " + toHex(result));
		throw new RuntimeException();
	}

	private static boolean containsUnsafeCharacters(String string, String additionalSafeChars) {
		String safe = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		/*
		 * the percent character is always safe
		 */
		safe += "%";
		
		safe += additionalSafeChars;

		for (int i = 0; i < string.length(); ++i) {
			if (safe.indexOf(string.charAt(i)) < 0) {
				return true;
			}
		}
		return false;
	}

	public static void testUrlEscaper(Escaper escaper, String additionalSafeChars, String sample, boolean plusIsSpace) {
		String encoded = escaper.escape(sample);

		if (containsUnsafeCharacters(encoded, additionalSafeChars)) {
			rejectSample(sample, "<null>", encoded, "<null>");
		}

		String percentEncoded = encoded.replace("+", (plusIsSpace ? "%20" : "%2B"));

		String decoded = URLDecoder.decode(percentEncoded, Charsets.UTF_8);

		if (!decoded.equals(sample)) {
			rejectSample(sample, decoded, encoded, percentEncoded);
		}
	}

	public static void fuzzerTestOneInput(FuzzedDataProvider data) {
		String value = data.consumeRemainingAsString();
		
		try {
			testUrlEscaper(UrlEscapers.urlFormParameterEscaper(), URL_FORM_PARAMETER_OTHER_SAFE_CHARS + "+",      value, true);
			testUrlEscaper(UrlEscapers.urlFragmentEscaper(),      URL_PATH_OTHER_SAFE_CHARS_LACKING_PLUS + "+/?", value, false);
			testUrlEscaper(UrlEscapers.urlPathSegmentEscaper(),   URL_PATH_OTHER_SAFE_CHARS_LACKING_PLUS + "+",   value, false);
		} catch (IllegalArgumentException e) {
			/* ignore */
	    }
	}
}
