package service.track;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.track.TrackInputConstants;

/**
 * Unit test for {@link GNDAggregatorService}.
 * <p>
 * This test verifies the formatting and aggregation logic of speed values,
 * specifically the correct prefixing and right-padding with zeros for both
 * SPEED and SPEED_ECHO fields.
 * </p>
 */
public class GNDAggregatorServiceTest {

	/** Unit test for {@link IAggregatorService} */
    private static IAggregatorService service;

    /**
     *  Init
     */
    @BeforeAll
    static void setUp() {
        service = new GNDAggregatorService();
    }

    /**
     * Tests that the correct service name is returned.
     */
    @Test
    void testGetServiceName() {
        assertEquals("GND", service.getServiceName());
    }

    /**
     * Utility method to create a basic IRawData with SPD_MOD.
     *
     * @param spdMod the SPD_MOD value to set
     * @return an {@link IRawData} instance
     */
    private static IRawData createInput(String spdMod) {
        Map<String, Object> map = new HashMap<>();
        map.put(TrackInputConstants.SPD_MOD, spdMod);
        return RawDataFactory.create(map);
    }

    /**
     * Tests that SPEED and SPEED_ECHO are correctly formatted
     * when SPD_MOD is a single digit.
     */
    @Test
    void testAggregate_WithSingleDigitSpdMod() {
        IRawData input = createInput("7");
        HeaderNode header = new HeaderNode("test");

        service.aggregate(input, header);

        assertEquals("G007", header.getLine(TrackInputConstants.SPEED).getAttributeValue(EdmModelKeys.Pairs.DATA));
        assertEquals("G0007", header.getLine("SPEED_ECHO").getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Tests that SPEED and SPEED_ECHO are correctly formatted
     * when SPD_MOD is a three-digit value.
     */
    @Test
    void testAggregate_WithThreeDigitSpdMod() {
        IRawData input = createInput("245");
        HeaderNode header = new HeaderNode("test");

        service.aggregate(input, header);

        assertEquals("G245", header.getLine(TrackInputConstants.SPEED).getAttributeValue(EdmModelKeys.Pairs.DATA));
        assertEquals("G0245", header.getLine("SPEED_ECHO").getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Tests that SPEED and SPEED_ECHO are correctly formatted
     * when SPD_MOD is empty.
     */
    @Test
    void testAggregate_WithEmptySpdMod() {
        IRawData input = createInput("");
        HeaderNode header = new HeaderNode("test");

        service.aggregate(input, header);

        assertEquals("G000", header.getLine(TrackInputConstants.SPEED).getAttributeValue(EdmModelKeys.Pairs.DATA));
        assertEquals("G0000", header.getLine("SPEED_ECHO").getAttributeValue(EdmModelKeys.Pairs.DATA));
    }
}
