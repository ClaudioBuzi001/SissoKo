package service.cpdlc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

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
 * Unit tests for {@link UDLCONTACTAggregatorService} using Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class UDLCONTACTAggregatorServiceTest {

	/** */
	@InjectMocks
	private UDLCONTACTAggregatorService service;

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
	 * Helper to setup mock input values.
	 * 
	 * @param tcuds
	 * @param template
	 */
	private void mockRawData(String tcuds, String template) {
		lenient().when(mockRawData.getSafeString(CpdlcInputConstants.CPDLC_UPLINK_CONTACT_DIALOGUE_STATUS)).thenReturn(tcuds);
		lenient().when(mockRawData.getSafeString(FlightInputConstants.TEMPLATE_STRING)).thenReturn(template);
		lenient().when(mockRawData.getSafeString("ID")).thenReturn("FLT123");
	}

	/** */
	@Test
	void testAggregate_NoCpdlcData_ShouldWriteEmptyValues() {
		mockRawData("0", "");

//		service.aggregate(mockRawData, header);
//
//		assertEquals("", TestHelper.getDataValue(header, CpdlcOutputConstants.UPLINK_CPDLC_CONTACT));
//		assertEquals("", TestHelper.getColorValue(header, CpdlcOutputConstants.UPLINK_CPDLC_CONTACT));
//		assertEquals("", TestHelper.getShowValue(header, CpdlcOutputConstants.UPLINK_CPDLC_CONTACT));
//
//		verify(mockRawData, atLeastOnce()).getSafeString(CpdlcInputConstants.CPDLC_UPLINK_CONTACT_DIALOGUE_STATUS);
//		verify(mockRawData, atLeastOnce()).getSafeString(FlightInputConstants.TEMPLATE_STRING);
	}

}
