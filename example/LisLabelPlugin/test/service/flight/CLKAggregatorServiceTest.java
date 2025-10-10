package service.flight;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;

import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;

/**
 * Unit tests for {@link CLKAggregatorService}.
 */
public class CLKAggregatorServiceTest {

    /**
     * 
     */
    private CLKAggregatorService service;

    /**
     * 
     */
    @BeforeEach
    public void setUp() {
        service = new CLKAggregatorService();
    }

    /**
     * Test method for {@link CLKAggregatorService#getServiceName()}.
     * Verifies the service name matches the expected constant.
     */
    @Test
    public void testGetServiceName() {
        assertEquals(FlightOutputConstants.CLK, service.getServiceName());
    }

    /**
     * Test aggregate method when ISGROUND is true.
     * The expected value in the header is an empty string.
     */
    @Test
    public void testAggregateIsGroundTrue() {
        IRawData input = buildRawData("true");
        HeaderNode header = new HeaderNode("service");

        service.aggregate(input, header);

        assertNotNull(header.getLine(FlightOutputConstants.CLK));
        assertEquals("", header.getLine(FlightOutputConstants.CLK).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Test aggregate method when ISGROUND is false.
     * The expected value in the header is an empty string.
     */
    @Test
    public void testAggregateIsGroundFalse() {
        IRawData input = buildRawData("false");
        HeaderNode header = new HeaderNode("service");

        service.aggregate(input, header);

        assertNotNull(header.getLine(FlightOutputConstants.CLK));
        assertEquals("", header.getLine(FlightOutputConstants.CLK).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Test aggregate method when ISGROUND key is missing.
     * The expected value in the header is an empty string.
     */
    @Test
    public void testAggregateIsGroundMissing() {
        IRawData input = buildRawData(null);
        HeaderNode header = new HeaderNode("service");

        service.aggregate(input, header);

        assertNotNull(header.getLine(FlightOutputConstants.CLK));
        assertEquals("", header.getLine(FlightOutputConstants.CLK).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Utility method to create IRawData with the given ISGROUND value.
     *
     * @param isGround the value for ISGROUND key, or null if absent
     * @return IRawData instance with specified ISGROUND value
     */
    private static IRawData buildRawData(String isGround) {
        Map<String, Object> map = new HashMap<>();
        if (isGround != null) {
            map.put(FlightInputConstants.ISGROUND, isGround);
        }
        return RawDataFactory.create(map);
    }
}
