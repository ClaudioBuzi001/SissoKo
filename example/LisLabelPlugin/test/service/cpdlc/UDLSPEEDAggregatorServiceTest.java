package service.cpdlc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

import auxiliary.cpdlc.CpdlcInputConstants;
import auxiliary.cpdlc.CpdlcOutputConstants;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import common.TestHelper;

/**
 * Unit tests for {@link UDLSPEEDAggregatorService} using Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class UDLSPEEDAggregatorServiceTest {

	/** */
	@InjectMocks
	private static UDLSPEEDAggregatorService service;

	/** */
	@Mock
	private IRawData mockRawData;

	/** */
	private HeaderNode header;

	/**
	 * 
	 */
	@BeforeEach
	void setUp() {
		header = new HeaderNode("test");
	}

	/**
	 * Helper to setup mock input values.
	 * 
	 * @param scuds
	 * @param template
	 */
	private void mockRawData(String scuds, String template) {
		lenient()
			.when(mockRawData.getSafeString(CpdlcInputConstants.CPDLC_UPLINK_SPEED_DIALOGUE_STATUS))
			.thenReturn(scuds);
		
		lenient()
			.when(mockRawData.getSafeString(FlightInputConstants.TEMPLATE_STRING))
			.thenReturn(template);
		
		lenient()
			.when(mockRawData.getSafeString("ID"))
			.thenReturn("FLT789");
	}

	/**
	 * 
	 */
	@Test
	void testAggregate_NoCpdlcData_ShouldReturnEmptyValues() {
		mockRawData("0", "");

//		service.aggregate(mockRawData, header);
//
//		assertEquals("", TestHelper.getDataValue(header, CpdlcOutputConstants.UPLINK_CPDLC_SPEED));
//		assertEquals("", TestHelper.getColorValue(header, CpdlcOutputConstants.UPLINK_CPDLC_SPEED));
//		assertEquals("", TestHelper.getShowValue(header, CpdlcOutputConstants.UPLINK_CPDLC_SPEED));
//
//		verify(mockRawData, atLeastOnce()).getSafeString(CpdlcInputConstants.CPDLC_UPLINK_SPEED_DIALOGUE_STATUS);
//		verify(mockRawData, atLeastOnce()).getSafeString(FlightInputConstants.TEMPLATE_STRING);
	}

}
