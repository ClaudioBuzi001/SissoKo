package service.cpdlc;

import static org.mockito.Mockito.lenient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

import auxiliary.cpdlc.CpdlcInputConstants;
import auxiliary.flight.FlightInputConstants;

/**
 * 
 */
@ExtendWith(MockitoExtension.class)
public class UDLARCAggregatorServiceTest {

    /**
     * 
     */
    private UDLARCAggregatorService service;

    /**
     * 
     */
    @Mock
    private IRawData mockRawData;

    /**
     * 
     */
    private HeaderNode header;


    /**
     * 
     */
    @BeforeEach
    void setUp() {
        service = new UDLARCAggregatorService();
        header = new HeaderNode("test");
    }

    /**
     * @param vcuds
     * @param pwvor
     * @param template
     */
    private void mockRawData(String vcuds, String pwvor, String template) {
        lenient().when(mockRawData.getSafeString(CpdlcInputConstants.CPDLC_UPLINK_VERTICAL_DIALOGUE_STATUS))
                .thenReturn(vcuds);
        lenient().when(mockRawData.getSafeString(FlightInputConstants.TEMPLATE_STRING))
                .thenReturn(template);
        lenient().when(mockRawData.getSafeString(FlightInputConstants.PWVOR))
                .thenReturn(pwvor);
        lenient().when(mockRawData.getSafeString("ID")).thenReturn("FLT001");
    }

    /**
     * 
     */
    @Test
    void testAggregate_NoCpdlcData_ShouldWriteEmptyValues() {
        mockRawData("0", "", "");

//        service.aggregate(mockRawData, header);

//        assertEquals("", TestHelper.getDataValue(header, CpdlcOutputConstants.UPLINK_CPDLC_ARC));
//        assertEquals("", TestHelper.getColorValue(header, CpdlcOutputConstants.UPLINK_CPDLC_ARC));
//        assertEquals("", TestHelper.getShowValue(header, CpdlcOutputConstants.UPLINK_CPDLC_ARC));
    }
}
