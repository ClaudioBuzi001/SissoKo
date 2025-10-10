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

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.track.TrackInputConstants;

/**
 * Unit tests for {@link TCASAggregatorService}.
 */
public class TCASAggregatorServiceTest {

	/** Unit test for {@link IAggregatorService} */
    private TCASAggregatorService service;
    
    /** Unit test for {@link IAggregatorService} */
    private HeaderNode dataNode;

    /**
     * Setup method called before each test.
     */
    @BeforeEach
    public void setUp() {
        service = new TCASAggregatorService();
        dataNode = new HeaderNode("ROOT");
    }

    /**
     * Creates an {@link IRawData} object with test boolean flags.
     *
     * @param bds30Rat   the BDS30_RAT flag
     * @param bds30Ara41 the BDS30_ARA41 flag
     * @param bds30Mte   the BDS30_MTE flag
     * @return an IRawData instance with the provided flags
     */
    private static IRawData createInput(boolean bds30Rat, boolean bds30Ara41, boolean bds30Mte) {
        Map<String, Object> map = new HashMap<>();
        map.put(TrackInputConstants.BDS30_RAT, bds30Rat);
        map.put(TrackInputConstants.BDS30_ARA41, bds30Ara41);
        map.put(TrackInputConstants.BDS30_MTE, bds30Mte);
        return RawDataFactory.create(map);
    }

    /**
     * 
     */
    @Test
    public void testAggregate_ACASCondition1() {
        IRawData input = createInput(false, true, false);
        service.aggregate(input, dataNode);

        DataNode tcasLine = dataNode.getLine(TrackInputConstants.TCAS);
        DataNode soundLine = dataNode.getLine("IS_SOUND_ACAS");

        assertEquals("ACAS", tcasLine.getAttributeValue(EdmModelKeys.Pairs.DATA));
        assertEquals(true, Boolean.valueOf(soundLine.getAttributeValue(EdmModelKeys.Pairs.DATA)));
    }

    /**
     * 
     */
    @Test
    public void testAggregate_ACASCondition2() {
        IRawData input = createInput(false, false, true);
        service.aggregate(input, dataNode);

        DataNode tcasLine = dataNode.getLine(TrackInputConstants.TCAS);
        DataNode soundLine = dataNode.getLine("IS_SOUND_ACAS");

        assertEquals("ACAS", tcasLine.getAttributeValue(EdmModelKeys.Pairs.DATA));
        assertEquals(true, Boolean.valueOf(soundLine.getAttributeValue(EdmModelKeys.Pairs.DATA)));
    }

    /**
     * 
     */
    @Test
    public void testAggregate_NoACAS() {
        IRawData input = createInput(true, true, true);
        service.aggregate(input, dataNode);

        DataNode tcasLine = dataNode.getLine(TrackInputConstants.TCAS);
        DataNode soundLine = dataNode.getLine("IS_SOUND_ACAS");

        assertEquals("", tcasLine.getAttributeValue(EdmModelKeys.Pairs.DATA));
        assertEquals(false, Boolean.valueOf(soundLine.getAttributeValue(EdmModelKeys.Pairs.DATA)));
    }
}
