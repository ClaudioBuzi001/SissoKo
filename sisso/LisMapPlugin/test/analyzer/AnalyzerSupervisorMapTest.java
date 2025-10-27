package analyzer;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Smoke tests for {@link AnalyzerSupervisorMap}.
 */
class AnalyzerSupervisorMapTest {

	@Test
	void update_shouldHandleDeleteOperationGracefully() throws Exception {
		Constructor<AnalyzerSupervisorMap> constructor = AnalyzerSupervisorMap.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		AnalyzerSupervisorMap analyzer = constructor.newInstance();

		IRawData rawData = RawDataFactory.create();
		rawData.setType(DataType.SUPERVISOR_MAP.name());
		rawData.setOperation(Operation.DELETE);
		rawData.put("SUPERVISOR_MAP_NAME", "TestMap");
		rawData.put("SUPERVISOR_MAP_DATA", "{}");

		assertDoesNotThrow(() -> analyzer.update(rawData));
	}
}
