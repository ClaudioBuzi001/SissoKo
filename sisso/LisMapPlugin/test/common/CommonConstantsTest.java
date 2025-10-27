package common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Minimal sanity checks on {@link common.CommonConstants}.
 */
class CommonConstantsTest {

	@Test
	void weatherMapConstants_shouldExposeExpectedValues() {
		assertEquals("WEATHER_MAPS", CommonConstants.WEATHER_MAPS);
		assertEquals("Weather", CommonConstants.WEATHER_MAP_TYPE);
	}

	@Test
	void weatherMapTags_shouldNotBeBlank() {
		assertFalse(CommonConstants.WEATHER_MAP_TAG_START.isBlank());
		assertFalse(CommonConstants.WEATHER_MAP_TAG_CONTINUE.isBlank());
		assertFalse(CommonConstants.WEATHER_MAP_TAG_END.isBlank());
	}
}
