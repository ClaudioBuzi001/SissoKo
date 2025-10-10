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
import auxiliary.track.TrackOutputConstants;

/**
 * Unit test for {@link EMGAggregatorService}.
 * <p>
 * This test verifies the correct aggregation of emergency states such as
 * EMG, HIJ, and RCF based on boolean flags.
 * </p>
 */
public class EMGAggregatorServiceTest {

	/** Unit test for {@link IAggregatorService} */
    private static IAggregatorService service;

    /**
     * Init
     */
    @BeforeAll
    static void setUp() {
        service = new EMGAggregatorService();
    }

    /**
     * Tests that the correct service name is returned.
     */
    @Test
    void testGetServiceName() {
        assertEquals(TrackOutputConstants.IS_EMG, service.getServiceName());
    }

    /**
     * Utility method to create test input data.
     *
     * @param isEmg true if EMG flag is set
     * @param isHij true if HIJ flag is set
     * @param isRcf true if RCF flag is set
     * @return an {@link IRawData} instance
     */
    private static IRawData createInput(boolean isEmg, boolean isHij, boolean isRcf) {
        Map<String, Object> map = new HashMap<>();
        map.put("IS_EMG", isEmg);
        map.put("IS_HIJ", isHij);
        map.put("IS_RCF", isRcf);
        return RawDataFactory.create(map);
    }

    /**
     * Tests that "EMG" has the highest priority if all flags are true.
     */
    @Test
    void testAggregate_EMG_TakesPrecedence() {
        IRawData input = createInput(true, true, true);
        HeaderNode header = new HeaderNode("test");

        service.aggregate(input, header);

        assertEquals("EMG", header.getLine(TrackOutputConstants.EMERGENCY).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Tests that "HIJ" is returned if only HIJ is true.
     */
    @Test
    void testAggregate_HIJ() {
        IRawData input = createInput(false, true, false);
        HeaderNode header = new HeaderNode("test");

        service.aggregate(input, header);

        assertEquals("HIJ", header.getLine(TrackOutputConstants.EMERGENCY).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Tests that "RCF" is returned if only RCF is true.
     */
    @Test
    void testAggregate_RCF() {
        IRawData input = createInput(false, false, true);
        HeaderNode header = new HeaderNode("test");

        service.aggregate(input, header);

        assertEquals("RCF", header.getLine(TrackOutputConstants.EMERGENCY).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Tests that an empty string is returned if no flags are set.
     */
    @Test
    void testAggregate_NoneSet() {
        IRawData input = createInput(false, false, false);
        HeaderNode header = new HeaderNode("test");

        service.aggregate(input, header);

        assertEquals("", header.getLine(TrackOutputConstants.EMERGENCY).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }
}
