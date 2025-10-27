package analyzer;

import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Basic entry-point tests for {@link AnalyzerWeatherMaps}.
 */
class AnalyzerWeatherMapsTest {

	@Test
	void update_deleteAll_shouldNotThrow() throws Exception {
		Constructor<AnalyzerWeatherMaps> constructor = AnalyzerWeatherMaps.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		AnalyzerWeatherMaps analyzer = constructor.newInstance();

		IRawData rawData = RawDataFactory.create();
		rawData.setOperation(Operation.DELETE_ALL);
		rawData.setType("WEATHER_MAPS");

		assertDoesNotThrow(() -> analyzer.update(rawData));
	}
}
