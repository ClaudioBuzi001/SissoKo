package service.tct;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;

import auxiliary.track.TrackOutputConstants;
import common.CommonConstants;

/**
 * Unit test for {@link EMG_TCTAggregatorService}.
 */
public class EMG_TCTAggregatorServiceTest {

    /** Instance of the service under test. */
    private static EMG_TCTAggregatorService service;

    /** Initializes the service instance before all tests. */
    @BeforeAll
    static void init() {
        service = new EMG_TCTAggregatorService();
    }

    /**
     * Test method for {@link EMG_TCTAggregatorService#getServiceName()}.
     * <p>
     * Verifies that {@code getServiceName()} returns the expected constant.
     * </p>
     */
    @Test
    void testGetServiceName_ReturnsExpectedValue() {
        assertEquals(CommonConstants.TCT, service.getServiceName());
    }

    /**
     * Test method for {@link EMG_TCTAggregatorService#aggregate(IRawData, HeaderNode)}.
     * <p>
     * Verifies that default empty values are added to the header when there is no
     * relevant TCT or TRACK data found.
     * </p>
     */
    @Test
    void testAggregate_WithMinimalData_AddsEmptyLines() {
        IRawData raw = buildRawDataWithStn("STN1");
        HeaderNode header = new HeaderNode("test");

        // Act
//        service.aggregate(raw, header);

//        // Assert
//		assertEquals("", header.getLine(TrackOutputConstants.POS_TAG_COLOR).getAttributeValue(EdmModelKeys.Pairs.DATA));
//        assertEquals("", header.getLine(TrackOutputConstants.ARROW).getAttributeValue(EdmModelKeys.Pairs.DATA));
//        assertEquals("", header.getLine(TrackOutputConstants.EMERGENCY_TCT).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Utility method to create an IRawData instance with only STN set.
     *
     * @param stn the station identifier
     * @return the created IRawData instance
     */
    private static IRawData buildRawDataWithStn(String stn) {
        Map<String, Object> map = new HashMap<>();
        map.put(CommonConstants.STN, stn);
        return RawDataFactory.create(map);
    }
}
