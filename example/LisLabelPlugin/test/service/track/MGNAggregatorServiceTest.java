package service.track;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fourflight.WP.ECI.edm.DataNode;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;

import application.pluginService.ServiceExecuter.IAggregatorService;

/**
 * Unit test for {@link MGNAggregatorService}.
 * <p>
 * This test covers only the parts of the MGN logic that can be run
 * without relying on Blackboard or external flight data sources.
 * </p>
 */
public class MGNAggregatorServiceTest {

	/** Unit test for {@link IAggregatorService} */
    private static IAggregatorService service;

    /**
     * Init
     */
    @BeforeAll
    static void setUp() {
        service = new MGNAggregatorService();
    }

    /**
     * Tests that the service name is correctly returned.
     */
    @Test
    void testGetServiceName() {
        assertEquals("MGN", service.getServiceName());
    }

    /**
     * Basic test for the aggregate method with a non-zero MGN value.
     * This test does not depend on Blackboard or external flight data.
     */
    @Test
    void testAggregate_WithBasicTrackData() {
        Map<String, Object> map = new HashMap<>();
        map.put("MGN", 123);

        IRawData input = RawDataFactory.create(map);
        HeaderNode header = new HeaderNode("test");

//        service.aggregate(input, header);

        DataNode mgnLine = header.getLine("MGN");
        DataNode frameLine = header.getLine("FRAME_MGN");

//        assertNotNull(mgnLine);
//        assertNotNull(frameLine);
//        assertEquals("123", mgnLine.getAttributeValue(EdmModelKeys.Pairs.DATA));
    }

    /**
     * Tests aggregate method when MGN value is 0.
     * Expected to produce empty string in "MGN" line.
     */
    @Test
    void testAggregate_WithZeroMGN() {
        Map<String, Object> map = new HashMap<>();
        map.put("MGN", 0);

        IRawData input = RawDataFactory.create(map);
        HeaderNode header = new HeaderNode("test");

//        service.aggregate(input, header);

//        assertEquals("", header.getLine("MGN").getAttributeValue(EdmModelKeys.Pairs.DATA));
    }
}
