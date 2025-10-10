package common;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;

/**
 * Utility class with helper methods to extract common attributes from a {@link HeaderNode} during
 * unit testing.
 */
public final class TestHelper {

	/**
	 * Returns the data value stored under the given key in the {@link HeaderNode}.
	 *
	 * @param header the header node to read from
	 * @param key    the key identifying the line
	 * @return the DATA attribute value, or an empty string if the line is missing
	 */
	public static String getDataValue(HeaderNode header, String key) {
		var line = header.getLine(key);
		return line != null ? line.getAttributeValue(EdmModelKeys.Pairs.DATA) : "";
	}

	/**
	 * Returns the color value stored under the given key in the {@link HeaderNode}.
	 *
	 * @param header the header node to read from
	 * @param key    the key identifying the line
	 * @return the COLOR attribute value, or an empty string if the line is missing
	 */
	public static String getColorValue(HeaderNode header, String key) {
		var line = header.getLine(key);
		return line != null ? line.getAttributeValue(EdmModelKeys.Attributes.COLOR) : "";
	}

	/**
	 * Returns the blink status stored under the given key in the {@link HeaderNode}.
	 *
	 * @param header the header node to read from
	 * @param key    the key identifying the line
	 * @return the MOD_BLINK attribute value, or an empty string if the line is missing
	 */
	public static String getBlinkValue(HeaderNode header, String key) {
		var line = header.getLine(key);
		return line != null ? line.getAttributeValue(EdmModelKeys.Attributes.MOD_BLINK) : "";
	}
	
	/**
	 * Returns the show status stored under the given key in the {@link HeaderNode}.
	 *
	 * @param header the header node to read from
	 * @param key    the key identifying the line
	 * @return the MOD_BLINK attribute value, or an empty string if the line is missing
	 */
	public static String getShowValue(HeaderNode header, String key) {
		var line = header.getLine(key);
		return line != null ? line.getAttributeValue(EdmModelKeys.Attributes.SHOW) : null;
	}
	
	/**
	 * Returns attribute value stored under the given key in the {@link HeaderNode}.
	 *
	 * @param header the header node to read from
	 * @param key    the key identifying the line
	 * @param attribute 
	 * @return the MOD_BLINK attribute value, or an empty string if the line is missing
	 */
	public static String getGenericValue(HeaderNode header, String key, String attribute) {
		var line = header.getLine(key);
		return line != null ? line.getAttributeValue(attribute) : null;
	}
}
