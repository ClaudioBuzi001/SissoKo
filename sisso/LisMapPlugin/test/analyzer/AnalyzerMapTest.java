package analyzer;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Lightweight behavioural checks for {@link AnalyzerMap}.
 */
class AnalyzerMapTest {

	@Test
	void update_withUnhandledDataType_shouldNotThrow() throws Exception {
		Constructor<AnalyzerMap> constructor = AnalyzerMap.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		AnalyzerMap analyzer = constructor.newInstance();

		IRawData rawData = RawDataFactory.create();
		rawData.setType(DataType.UNK.name());
		rawData.setOperation(Operation.UPDATE);

		assertDoesNotThrow(() -> analyzer.update(rawData));
	}
}
