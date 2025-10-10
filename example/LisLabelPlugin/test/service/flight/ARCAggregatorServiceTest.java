package service.flight;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;

import auxiliary.flight.FlightInputConstants;

/**
 * Unit tests for {@link ARCAggregatorService}.
 * Validates the behavior of the aggregate method under different conditions.
 */
public class ARCAggregatorServiceTest {

    /** Service instance under test. */
    private ARCAggregatorService service;

    /** Initializes the service instance before each test. */
    @BeforeEach
    public void setUp() {
        service = new ARCAggregatorService();
    }

    /**
     * Test method for {@link ARCAggregatorService#getServiceName()}.
     * Verifies the returned service name is "ARC".
     */
    @Test
    public void testGetServiceName() {
        assertEquals("ARC", service.getServiceName());
    }

    /**
     * Test method for {@link ARCAggregatorService#aggregate(IRawData, HeaderNode)}
     * with valid ARC and ARC_ARROW input values.
     * Checks that the header node contains the expected values and attributes.
     */
    @Test
    public void testAggregateWithValidValues() {
        IRawData input = buildRawData("5", "↑");
        HeaderNode header = new HeaderNode("service");

        service.aggregate(input, header);

        assertNotNull(header.getLine(FlightInputConstants.ARC));
        assertEquals("R05", header.getLine(FlightInputConstants.ARC).getAttributeValue(EdmModelKeys.Pairs.DATA));
        assertEquals("true", header.getLine(FlightInputConstants.ARC).getAttributeValue("visible"));
        assertEquals("forced", header.getLine(FlightInputConstants.ARC).getAttributeValue("show"));

        assertEquals("↑", header.getLine(FlightInputConstants.ARC_ARROW).getAttributeValue(EdmModelKeys.Pairs.DATA));
        assertEquals("true", header.getLine(FlightInputConstants.ARC_ARROW).getAttributeValue("visible"));
    }

    /**
     * Test method for {@link ARCAggregatorService#aggregate(IRawData, HeaderNode)}
     * with empty ARC and ARC_ARROW values.
     * Checks that the header node contains default or empty values and attributes.
     */
    @Test
    public void testAggregateWithEmptyArc() {
        IRawData input = buildRawData(null, null);
        HeaderNode header = new HeaderNode("service");

        service.aggregate(input, header);

        assertNotNull(header.getLine(FlightInputConstants.ARC));
        assertEquals("ARC", header.getLine(FlightInputConstants.ARC).getAttributeValue(EdmModelKeys.Pairs.DATA));
        assertEquals("", header.getLine(FlightInputConstants.ARC).getAttributeValue("visible"));
        assertEquals("", header.getLine(FlightInputConstants.ARC).getAttributeValue("show"));

        assertNotNull(header.getLine(FlightInputConstants.ARC_ARROW));
        assertEquals("", header.getLine(FlightInputConstants.ARC_ARROW).getAttributeValue(EdmModelKeys.Pairs.DATA));
        assertEquals("false", header.getLine(FlightInputConstants.ARC_ARROW).getAttributeValue("visible"));
    }

    /**
     * Utility method to build IRawData with given ARC and ARC_ARROW values.
     *
     * @param arc      the ARC value to set, or null if absent
     * @param arcArrow the ARC_ARROW value to set, or null if absent
     * @return an IRawData instance with the specified values
     */
    private static IRawData buildRawData(String arc, String arcArrow) {
        Map<String, Object> map = new HashMap<>();
        if (arc != null) {
            map.put(FlightInputConstants.ARC, arc);
        }
        if (arcArrow != null) {
            map.put(FlightInputConstants.ARC_ARROW, arcArrow);
        }
        return RawDataFactory.create(map);
    }
}
