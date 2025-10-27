package application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Basic unit tests for {@link GiForkPluginMap}.
 */
class GiForkPluginMapTest {

	@Test
	void getters_shouldReturnPluginMetadata() {
		var plugin = new GiForkPluginMap();

		assertEquals("GiForkPluginMap", plugin.getName());
		assertEquals("GiForkPluginMap", plugin.getTitle());
		assertEquals("", plugin.getDescription());
		assertTrue(plugin.getDependencies().isEmpty());
		assertTrue(plugin.getExtensions().isEmpty());
	}
}
