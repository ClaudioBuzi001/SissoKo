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
 * Unit tests for {@link UDLHDGAggregatorService} using Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class UDLHDGAggregatorServiceTest {

	/** */
    @InjectMocks
    private UDLHDGAggregatorService service;

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
     * @param rmuds 
     * @param template 
     * @param pwror 
     */
    private void mockInput(String rmuds, String template, String pwror) {
		lenient().when(mockRawData.getSafeString(CpdlcInputConstants.CPDLC_UPLINK_ROUTE_DIALOGUE_STATUS)).thenReturn(rmuds);
		lenient().when(mockRawData.getSafeString(FlightInputConstants.TEMPLATE_STRING)).thenReturn(template);
		lenient().when(mockRawData.getSafeString(FlightInputConstants.PWROR)).thenReturn(pwror);
		lenient().when(mockRawData.getSafeString("ID")).thenReturn("FLT456");
    }

    /** */
    @Test
    void testAggregate_NoCpdlcData_ShouldReturnEmptyValues() {
        mockInput("0", "", null);

//        service.aggregate(mockRawData, header);
//
//        assertEquals("", TestHelper.getDataValue(header, CpdlcOutputConstants.UPLINK_CPDLC_HDG));
//        assertEquals("", TestHelper.getColorValue(header, CpdlcOutputConstants.UPLINK_CPDLC_HDG));
//        assertEquals("", TestHelper.getShowValue(header, CpdlcOutputConstants.UPLINK_CPDLC_HDG));
//
//        verify(mockRawData, atLeastOnce()).getSafeString(CpdlcInputConstants.CPDLC_UPLINK_ROUTE_DIALOGUE_STATUS);
//        verify(mockRawData, atLeastOnce()).getSafeString(FlightInputConstants.TEMPLATE_STRING);
    }

   

}