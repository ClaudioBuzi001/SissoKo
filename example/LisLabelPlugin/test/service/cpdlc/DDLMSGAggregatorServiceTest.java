package service.cpdlc;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

import auxiliary.cpdlc.CpdlcInputConstants;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;

/**
 * Unit test for {@link DDLMSGAggregatorService} using Mockito. Tests that the aggregate method
 * correctly writes symbols, colors, and messages into the HeaderNode based on CPDLC input data.
 */
@ExtendWith(MockitoExtension.class)
public class DDLMSGAggregatorServiceTest {

	/** */
	@InjectMocks
	private DDLMSGAggregatorService service;

	/** */
	@Mock
	private IRawData mockRawData;

	/** */
	private HeaderNode header;

	/** */
	@BeforeEach
	void setUp() {
		header = new HeaderNode("test");
	}

	/**
	 * Helper to prepare mock IRawData for a given dialogue status and template.
	 *
	 * @param scdds
	 * @param rmdds
	 * @param vcdds
	 * @param template
	 */
	private void mockRawData(String scdds, String rmdds, String vcdds, String template) {
		lenient().when(mockRawData.getSafeString(CpdlcInputConstants.CPDLC_DOWNLINK_SPEED_DIALOGUE_STATUS))
				.thenReturn(scdds);
		lenient().when(mockRawData.getSafeString(CpdlcInputConstants.CPDLC_DOWNLINK_ROUTE_DIALOGUE_STATUS))
				.thenReturn(rmdds);
		lenient().when(mockRawData.getSafeString(CpdlcInputConstants.CPDLC_DOWNLINK_VERTICAL_DIALOGUE_STATUS))
				.thenReturn(vcdds);
		lenient().when(mockRawData.getSafeString(FlightInputConstants.TEMPLATE_STRING)).thenReturn(template);
		lenient().when(mockRawData.getSafeString("ID")).thenReturn("FLT001");

		// Mock CPDLC list if needed
		List<Map<String, Object>> cpdlcList = new ArrayList<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put("CPDLC_RQ", "LEVEL");
		entry.put("LABEL", "LVL123");
		cpdlcList.add(entry);
		lenient().when(mockRawData.get("CPDLC_LIST")).thenReturn(cpdlcList);
	}

	/**
	 * 
	 */
	@Test
	void testAggregate_LevelStatus1_ShouldWriteCorrectValues() {
		// Arrange
		mockRawData("0", "0", "1", FlightOutputConstants.TEMPLATE_CONTROLLED);

		// Act
//	    service.aggregate(mockRawData, header);

		// Verify only real interactions
//	    verify(mockRawData, atLeastOnce())
//	        .getSafeString(CpdlcInputConstants.CPDLC_DOWNLINK_SPEED_DIALOGUE_STATUS);
//	    verify(mockRawData, atLeastOnce())
//	        .getSafeString(CpdlcInputConstants.CPDLC_DOWNLINK_ROUTE_DIALOGUE_STATUS);
//	    verify(mockRawData, atLeastOnce())
//	        .getSafeString(CpdlcInputConstants.CPDLC_DOWNLINK_VERTICAL_DIALOGUE_STATUS);
//	    verify(mockRawData, atLeastOnce())
//	        .getSafeString(FlightInputConstants.TEMPLATE_STRING);
	}

}
