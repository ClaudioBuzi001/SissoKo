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
import auxiliary.track.TrackOutputConstants;

/**
 * Unit test for {@link ADSCAggregatorService}.
 * <p>
 * This test verifies the correct behavior of the ADSC Aggregator
 * based on ADSC tracker status and FOM (Figure of Merit) value.
 * </p>
 */
public class ADSCAggregatorServiceTest {

	/** Unit test for {@link IAggregatorService} */
    private static IAggregatorService service;

    /**
     *  Init
     */
    @BeforeAll
    static void setUp() {
        service = new ADSCAggregatorService();
    }

    /**
     * Tests that the service name is correctly returned.
     */
    @Test
    void testGetServiceName() {
        assertEquals(TrackOutputConstants.IS_ADSC_TRACKER, service.getServiceName());
    }

    /**
     * Creates an {@link IRawData} object with test values.
     *
     * @param isAdsc whether the track is ADSC
     * @param fom    the Figure of Merit value
     * @return an IRawData instance with test data
     */
    private static IRawData createInput(boolean isAdsc, int fom) {
        Map<String, Object> map = new HashMap<>();
        map.put(TrackOutputConstants.IS_ADSC_TRACKER, isAdsc);
        map.put(TrackInputConstants.FOM, fom);
        return RawDataFactory.create(map);
    }

    /**
     * Tests aggregation when ADSC is true and FOM is less than 7.
     * Expects oversymbol = "1"
     */
    @Test
    void testAggregate_ADSCTrue_FomLessThan7() {
        IRawData input = createInput(true, 5);
        HeaderNode header = new HeaderNode("test");

        service.aggregate(input, header);

        assertEquals("1", header.getLine(TrackInputConstants.OVERSYMBOL).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Tests aggregation when ADSC is true but FOM is >= 7.
     * Expects oversymbol = "0"
     */
    @Test
    void testAggregate_ADSCTrue_FomGreaterOrEqual7() {
        IRawData input = createInput(true, 7);
        HeaderNode header = new HeaderNode("test");

        service.aggregate(input, header);

        assertEquals("0", header.getLine(TrackInputConstants.OVERSYMBOL).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Tests aggregation when ADSC is false regardless of FOM.
     * Expects oversymbol = "0"
     */
    @Test
    void testAggregate_ADSCFalse() {
        IRawData input = createInput(false, 3);
        HeaderNode header = new HeaderNode("test");

        service.aggregate(input, header);

        assertEquals("0", header.getLine(TrackInputConstants.OVERSYMBOL).getAttributeValue(EdmModelKeys.Pairs.DATA));
    }
}
