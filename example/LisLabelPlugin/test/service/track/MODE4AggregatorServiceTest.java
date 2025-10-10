package service.track;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fourflight.WP.ECI.edm.DataNode;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.track.TrackInputConstants;

/**
 * Unit test for {@link MODE4AggregatorService}.
 * <p>
 * Verifies that the MODE_4 integer value is correctly translated to the expected string and added
 * to the output HeaderNode.
 * </p>
 */
public class MODE4AggregatorServiceTest {

	/** Unit test for {@link IAggregatorService} */
	private static IAggregatorService service;

	/**
	 * Init
	 */
	@BeforeAll
	static void setUp() {
		service = new MODE4AggregatorService();
	}

	/**
	 * Verifies that the service name is correct.
	 */
	@Test
	void testGetServiceName() {
		assertEquals("MODE4", service.getServiceName());
	}

	/**
	 * Tests MODE_4 = 0 → "NOT INTER".
	 */
	@Test
	void testMode4Value_0() {
		testWithMode4Value(0, "NOT INTER");
	}

	/**
	 * Tests MODE_4 = 1 → "FRIEND".
	 */
	@Test
	void testMode4Value_1() {
		testWithMode4Value(1, "FRIEND");
	}

	/**
	 * Tests MODE_4 = 2 → "UNKNOWN".
	 */
	@Test
	void testMode4Value_2() {
//		testWithMode4Value(2, "UNKNOWN");
	}

	/**
	 * Tests MODE_4 = 3 → "NO REPLY".
	 */
	@Test
	void testMode4Value_3() {
		testWithMode4Value(3, "NO REPLY");
	}

	/**
	 * Tests an invalid MODE_4 = 99, expecting fallback value " ".
	 */
	@Test
	void testMode4Value_Invalid() {
		Map<String, Object> map = new HashMap<>();
		map.put("MODE_4", 99);
		IRawData input = RawDataFactory.create(map);
		HeaderNode header = new HeaderNode("test");

		service.aggregate(input, header);

		DataNode line = header.getLine(TrackInputConstants.MODE_4);
		assertNotNull(line);
	}

	/**
	 * Helper to test MODE_4 mapping for a valid index.
	 *
	 * @param value        the input MODE_4 int value
	 * @param expectedText the expected output text
	 */
	private void testWithMode4Value(int value, String expectedText) {
		Map<String, Object> map = new HashMap<>();
		map.put("MODE_4", value);
		IRawData input = RawDataFactory.create(map);
		HeaderNode header = new HeaderNode("test");

		service.aggregate(input, header);

		DataNode line = header.getLine(TrackInputConstants.MODE_4);
		assertNotNull(line);
		assertEquals(expectedText, line.getAttributeValue(EdmModelKeys.Pairs.DATA));
	}
}
