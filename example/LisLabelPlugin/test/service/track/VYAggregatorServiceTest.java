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
import service.cpdlc.DDLMSGAggregatorService;

/**
 * Unit test for {@link VYAggregatorServiceTest}.
 */
public class VYAggregatorServiceTest {

	/** Unit test for {@link DDLMSGAggregatorService} */
    private VYAggregatorService service;

    /**
     * Init
     */
    @BeforeEach
    void setUp() {
        service = new VYAggregatorService();
    }

    /**
     * @param vyValue
     * @return
     */
    private static IRawData createInput(double vyValue) {
        Map<String, Object> map = new HashMap<>();
        map.put(TrackInputConstants.VY, vyValue);
        return RawDataFactory.create(map);
    }

    /**
     * 
     */
    @Test
    void testGetServiceName() {
        assertEquals(TrackInputConstants.VY, service.getServiceName());
    }

    /**
     * 
     */
    @Test
    void testAggregate_withPositiveVY() {
        IRawData input = createInput(150.0); // 150 * 3600 = 540000.0
        HeaderNode dataNode = new HeaderNode("test");

        service.aggregate(input, dataNode);

        DataNode line = dataNode.getLine(TrackInputConstants.VY);
        String value = line.getAttributeValue(EdmModelKeys.Pairs.DATA);

        assertEquals("540000.0", value);
    }

    /**
     * 
     */
    @Test
    void testAggregate_withZeroVY() {
        IRawData input = createInput(0.0);
        HeaderNode dataNode = new HeaderNode("test");

        service.aggregate(input, dataNode);

        DataNode line = dataNode.getLine(TrackInputConstants.VY);
        String value = line.getAttributeValue(EdmModelKeys.Pairs.DATA);

        assertEquals("0.0", value);
    }
}
