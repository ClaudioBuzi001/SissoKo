package common;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;

/**
 * Utility helpers for reading values from a {@link HeaderNode} during tests.
 */
public final class TestHelper {

	private TestHelper() {
		// Utility class
	}

	/**
	 * Returns the DATA attribute of the requested line, if available.
	 *
	 * @param header the header node under test
	 * @param key    the identifier of the requested line
	 * @return the DATA attribute value (empty string if the line is missing)
	 */
	public static String getDataValue(HeaderNode header, String key) {
		var line = header.getLine(key);
		return line != null ? line.getAttributeValue(EdmModelKeys.Pairs.DATA) : "";
	}

	/**
	 * Returns the COLOR attribute of the requested line, if available.
	 *
	 * @param header the header node under test
	 * @param key    the identifier of the requested line
	 * @return the COLOR attribute value (empty string if the line is missing)
	 */
	public static String getColorValue(HeaderNode header, String key) {
		var line = header.getLine(key);
		return line != null ? line.getAttributeValue(EdmModelKeys.Attributes.COLOR) : "";
	}

	/**
	 * Returns any requested attribute of the requested line, if available.
	 *
	 * @param header    the header node under test
	 * @param key       the identifier of the requested line
	 * @param attribute the EDM attribute to read
	 * @return the attribute value (empty string if the line is missing)
	 */
	public static String getAttributeValue(HeaderNode header, String key, String attribute) {
		var line = header.getLine(key);
		return line != null ? line.getAttributeValue(attribute) : "";
	}
}
