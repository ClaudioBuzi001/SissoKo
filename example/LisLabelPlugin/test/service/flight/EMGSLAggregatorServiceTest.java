package service.flight;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;

import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;

/**
 * Unit test for {@link EMGSLAggregatorService}.
 * This test validates the behavior of the aggregate method under different conditions
 * of emergency status and copy level.
 */
public class EMGSLAggregatorServiceTest {

    /** Instance of the service under test. */
    private static EMGSLAggregatorService service;

    /** Initializes the service instance before all tests. */
    @BeforeAll
    static void init() {
        service = new EMGSLAggregatorService();
    }

    /**
     * Test method for {@link EMGSLAggregatorService#getServiceName()}.
     * <p>
     * Verifies that {@code getServiceName} returns the correct service name constant.
     * </p>
     */
    @Test
    void testGetServiceName_ReturnsExpectedValue() {
        assertEquals(FlightOutputConstants.EMERGENCY_SL, service.getServiceName());
    }

    /**
     * Test method for {@link EMGSLAggregatorService#aggregate(IRawData, HeaderNode)}.
     * <p>
     * Verifies that the aggregate method clears the emergency status when it matches 
     * "SL" and lev_copy is less than lev_copy_up.
     * </p>
     */
    @Test
    void testAggregate_ClearsEmergencySLWhenLevCopyLow() {
        IRawData raw = buildRawDataWithEmergencyStatus(FlightInputConstants.EMERGENCY_SL_SL, "0", "1");
        HeaderNode header = new HeaderNode("service");

        service.aggregate(raw, header);

        assertEquals("", header.getLine(FlightOutputConstants.EMERGENCY_SL).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Test method for {@link EMGSLAggregatorService#aggregate(IRawData, HeaderNode)}.
     * <p>
     * Verifies that the aggregate method preserves the emergency status when it is not 
     * "SL", regardless of lev_copy or lev_copy_up.
     * </p>
     */
    @Test
    void testAggregate_PreservesNonSLEmergencyStatus() {
        IRawData raw = buildRawDataWithEmergencyStatus("XX", "0", "1");
        HeaderNode header = new HeaderNode("service");

        service.aggregate(raw, header);

        assertEquals("XX", header.getLine(FlightOutputConstants.EMERGENCY_SL).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Test method for {@link EMGSLAggregatorService#aggregate(IRawData, HeaderNode)}.
     * <p>
     * Verifies that the aggregate method sets an empty string in the header when the 
     * emergency level is missing (i.e., null).
     * </p>
     */
    @Test
    void testAggregate_WithMissingEmergencyLevel_AddsEmptyLine() {
        IRawData raw = buildRawDataWithEmergencyStatus(null, "0", "1");
        HeaderNode header = new HeaderNode("service");

        service.aggregate(raw, header);

        assertEquals("", header.getLine(FlightOutputConstants.EMERGENCY_SL).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Utility method to create an IRawData instance with the specified emergency status and lev_copy levels.
     *
     * @param emgStatus the emergency status to set
     * @param levCopy the lev_copy value
     * @param levCopyUp the lev_copy_up value
     * @return the created IRawData instance
     */
    private static IRawData buildRawDataWithEmergencyStatus(String emgStatus, String levCopy, String levCopyUp) {
        Map<String, Object> map = new HashMap<>();
        map.put(FlightInputConstants.EMERGENCY_SL, emgStatus);
        map.put(FlightInputConstants.LEV_COPY, levCopy);
        map.put(FlightInputConstants.LEV_COPY_UP, levCopyUp);
        return RawDataFactory.create(map);
    }
}
