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
import common.TestHelper;

/**
 * Unit tests for {@link UDLSQUAWKAggregatorService} using Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class UDLSQUAWKAggregatorServiceTest {

	/** */
    @InjectMocks
    private UDLSQUAWKAggregatorService service;

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
     * Helper to set mock values for IRawData.
     * @param cmuds 
     * @param template 
     */
    private void mockRawData(String cmuds, String template) {
        lenient().when(mockRawData.getSafeString(CpdlcInputConstants.CPDLC_UPLINK_SURV_DIALOGUE_STATUS))
                .thenReturn(cmuds);
        lenient().when(mockRawData.getSafeString(FlightInputConstants.TEMPLATE_STRING))
                .thenReturn(template);
        lenient().when(mockRawData.getSafeString("ID"))
                .thenReturn("FLT001");
    }


    /** */
    @Test
    void testAggregate_NoCpdlcData_ShouldReturnEmptyValues() {
        mockRawData("0", "");

//        service.aggregate(mockRawData, header);

//        assertEquals("", TestHelper.getDataValue(header, CpdlcOutputConstants.UPLINK_CPDLC_SQUAWK));
//        assertEquals("", TestHelper.getColorValue(header, CpdlcOutputConstants.UPLINK_CPDLC_SQUAWK));
//        assertEquals("", TestHelper.getShowValue(header, CpdlcOutputConstants.UPLINK_CPDLC_SQUAWK));

//        verify(mockRawData, atLeastOnce()).getSafeString(CpdlcInputConstants.CPDLC_UPLINK_SURV_DIALOGUE_STATUS);
//        verify(mockRawData, atLeastOnce()).getSafeString(FlightInputConstants.TEMPLATE_STRING);
    }

}
