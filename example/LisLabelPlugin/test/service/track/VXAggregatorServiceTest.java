package service.track;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fourflight.WP.ECI.edm.DataNode;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;

import auxiliary.track.TrackInputConstants;

/**
 * Unit test for {@link VXAggregatorServiceTest}.
 */
public class VXAggregatorServiceTest {

	/** Unit test for {@link VXAggregatorService} */
    private VXAggregatorService service;

    /**
     * 
     */
    @BeforeEach
    void setUp() {
        service = new VXAggregatorService();
    }

    /**
     * @param vxValue
     * @return
     */
    private static IRawData createInput(double vxValue) {
        Map<String, Object> map = new HashMap<>();
        map.put(TrackInputConstants.VX, vxValue);
        return RawDataFactory.create(map);
    }

    /**
     * 
     */
    @Test
    void testGetServiceName() {
        assertEquals(TrackInputConstants.VX, service.getServiceName());
    }

    /**
     * 
     */
    @Test
    void testAggregate_withPositiveVX() {
        IRawData input = createInput(100.5); // 100.5 * 3600 = 361800.0
        HeaderNode dataNode = new HeaderNode("test");

        service.aggregate(input, dataNode);

        DataNode line = dataNode.getLine(TrackInputConstants.VX);
        String value = line.getAttributeValue(EdmModelKeys.Pairs.DATA);

        assertEquals("361800.0", value);
    }

    /**
     * 
     */
    @Test
    void testAggregate_withZeroVX() {
        IRawData input = createInput(0.0);
        HeaderNode dataNode = new HeaderNode("test");

        service.aggregate(input, dataNode);

        DataNode line = dataNode.getLine(TrackInputConstants.VX);
        String value = line.getAttributeValue(EdmModelKeys.Pairs.DATA);

        assertEquals("0.0", value);
    }
}
