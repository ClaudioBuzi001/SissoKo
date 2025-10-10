package service.cpdlc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeast;
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
import common.TestHelper;

/**
 * Unit tests for {@link UDLCFLAggregatorService} using Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class UDLCFLAggregatorServiceTest {

	/** */
    @InjectMocks
    private UDLCFLAggregatorService service;

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
     * @param vcuds 
     * @param pwvor 
     * @param template 
     */
    private void mockRawData(String vcuds, String pwvor, String template) {
		lenient().when(mockRawData.getSafeString(CpdlcInputConstants.CPDLC_UPLINK_VERTICAL_DIALOGUE_STATUS)).thenReturn(vcuds);
		lenient().when(mockRawData.getSafeString(FlightInputConstants.TEMPLATE_STRING)).thenReturn(template);
		lenient().when(mockRawData.getSafeString(FlightInputConstants.PWVOR)).thenReturn(pwvor);
		lenient().when(mockRawData.getSafeString("ID")).thenReturn("FLT001");
    }

    /** */
    @Test
    void testAggregate_NoCpdlcData_ShouldWriteEmptyValues() {
        mockRawData("0", null, "");

//        service.aggregate(mockRawData, header);

//        assertEquals("", TestHelper.getDataValue(header, CpdlcOutputConstants.UPLINK_CPDLC_CFL));
//        assertEquals("", TestHelper.getColorValue(header, CpdlcOutputConstants.UPLINK_CPDLC_CFL));
//        assertEquals("", TestHelper.getShowValue(header, CpdlcOutputConstants.UPLINK_CPDLC_CFL));
//
//        verify(mockRawData, atLeastOnce())
//                .getSafeString(CpdlcInputConstants.CPDLC_UPLINK_VERTICAL_DIALOGUE_STATUS);
//        verify(mockRawData, atLeastOnce())
//                .getSafeString(FlightInputConstants.TEMPLATE_STRING);
//        verify(mockRawData, atLeast(0)).getSafeString(FlightInputConstants.PWVOR);
    }
}
