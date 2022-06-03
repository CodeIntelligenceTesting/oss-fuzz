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

	private static boolean contains(String string, char character) {
		for (int i=0; i<string.length(); ++i) {
			if (string.charAt(i) == character) {
				return true;
			}
		}
		return false;
	}

	private static boolean containsUnsafeCharacters(String string, String additionalSafeChars) {
		String safe = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		/*
		 * the percent character is always safe
		 */
		safe += "%";
		
		safe += additionalSafeChars;

		for (int i=0; i<string.length(); ++i) {
			if (!contains(safe, string.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static void testUrlFormParameterEscaper(String sample) {
		/*
		 * "handle" CR and LF
		 */
		if(sample.contains("\n") || sample.contains("\r")) {
			throw new IllegalArgumentException();
		}

		String encoded = UrlEscapers.urlFormParameterEscaper().escape(sample);

		if (containsUnsafeCharacters(encoded, URL_FORM_PARAMETER_OTHER_SAFE_CHARS + "+")) {
			rejectSample(sample, "<null>", encoded, "<null>");
		}
		String percentEncoded = encoded.replace("+", "%20");

		String decoded = URLDecoder.decode(percentEncoded, Charsets.UTF_8);

		if (!decoded.equals(sample)) {
			rejectSample(sample, decoded, encoded, percentEncoded);
		}
	}

	public static void testUrlPathSegmentEscaper(String sample) {
		String encoded = UrlEscapers.urlPathSegmentEscaper().escape(sample);

		if (containsUnsafeCharacters(encoded, URL_PATH_OTHER_SAFE_CHARS_LACKING_PLUS + "+")) {
			rejectSample(sample, "<null>", encoded, "<null>");
		}

		String percentEncoded = encoded.replace("+", "%2B");

		String decoded = URLDecoder.decode(percentEncoded, Charsets.UTF_8);

		if (!decoded.equals(sample)) {
			rejectSample(sample, decoded, encoded, percentEncoded);
		}
	}

	public static void testUrlFragmentEscaper(String sample) {
		String encoded = UrlEscapers.urlFragmentEscaper().escape(sample);

		if (containsUnsafeCharacters(encoded, URL_PATH_OTHER_SAFE_CHARS_LACKING_PLUS + "+/?")) {
			rejectSample(sample, "<null>", encoded, "<null>");
		}

		String percentEncoded = encoded.replace("+", "%2B");

		String decoded = URLDecoder.decode(percentEncoded, Charsets.UTF_8);

		if (!decoded.equals(sample)) {
			rejectSample(sample, decoded, encoded, percentEncoded);
		}
	}

	public static void fuzzerTestOneInput(FuzzedDataProvider data) {
		String value = data.consumeRemainingAsString();
		
		try {
			testUrlFormParameterEscaper(value);
			testUrlFragmentEscaper(value);
			testUrlPathSegmentEscaper(value);
		} catch (IllegalArgumentException e) {
			/* ignore */
	    }
	}
}
