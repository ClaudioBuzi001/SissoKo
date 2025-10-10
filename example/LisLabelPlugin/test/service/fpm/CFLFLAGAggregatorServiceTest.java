package service.fpm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;

import auxiliary.fpm.FpmInputConstants;
import auxiliary.fpm.FpmOutputConstants;

/**
 * Unit tests for {@link CFLFLAGAggregatorService}.
 * Tests the behavior of the aggregate method with different boolean inputs.
 */
public class CFLFLAGAggregatorServiceTest {
	

	/** Instance of the service under test. */
    private static CFLFLAGAggregatorService service;

    /**
     * Initializes the CFLFLAGAggregatorService instance before all tests.
     */
    @BeforeAll
    static void init() {
        service = new CFLFLAGAggregatorService();
    }

    /**
     * Tests that {@link CFLFLAGAggregatorService#getServiceName()} returns the expected service name constant.
     * 
     */
    @Test
    void testGetServiceName_ReturnsExpectedValue() {
        assertEquals(FpmOutputConstants.CFL_INST_CONF, service.getServiceName());
    }

    /**
     * Test method for {@link CFLFLAGAggregatorService#aggregate(IRawData, HeaderNode)} 
     * <p>
     * Tests that when the input boolean is true, the aggregate method sets the header line
     * with the expected true value.
     * </p>
     */
    @Test
    void testAggregate_WithTrueInput_SetsTrueValue() {
        IRawData input = buildRawDataWithBoolean(FpmInputConstants.CFL_INST_CONF, true);
        HeaderNode header = new HeaderNode("service");

        service.aggregate(input, header);

        String outValue = header.getLine(FpmOutputConstants.CFL_INST_CONF)
        						.getAttributeValue(EdmModelKeys.Pairs.DATA);
        
        assertEquals(FpmOutputConstants.CFL_INST_CONF_TRUE, outValue);
    }

    /**
     * Test method for {@link CFLFLAGAggregatorService#aggregate(IRawData, HeaderNode)} 
     * <p>
     * Tests that when the input boolean is false, the aggregate method sets the header line
     * </p>
     * with an empty string.
     */
    @Test
    void testAggregate_WithFalseInput_SetsEmptyValue() {
        IRawData input = buildRawDataWithBoolean(FpmInputConstants.CFL_INST_CONF, false);
        HeaderNode header = new HeaderNode("service");

        service.aggregate(input, header);

        String outValue = header.getLine(FpmOutputConstants.CFL_INST_CONF)
        						.getAttributeValue(EdmModelKeys.Pairs.DATA);
        
        assertEquals("", outValue);
    }

    /**
     * Test method for {@link CFLFLAGAggregatorService#aggregate(IRawData, HeaderNode)} 
     * <p>
     * Tests that when the input is missing the relevant key, the aggregate method
     * </p>
     * sets the header line with an empty string.
     */
    @Test
    void testAggregate_WithMissingInput_SetsEmptyValue() {
        IRawData input = buildRawDataWithoutKey();
        HeaderNode header = new HeaderNode("service");

        service.aggregate(input, header);

        String outValue = header.getLine(FpmOutputConstants.CFL_INST_CONF)
        						.getAttributeValue(EdmModelKeys.Pairs.DATA);
        
        assertEquals("", outValue);
    }

    /**
     * Utility method to create an IRawData instance containing a boolean value for a given key.
     *
     * @param key the key to include in the raw data
     * @param value the boolean value associated with the key
     * @return a new IRawData instance containing the key-value pair
     */
    private static IRawData buildRawDataWithBoolean(String key, boolean value) {
        return RawDataFactory.create(Map.of(key, value));
    }

    /**
     * Utility method to create an empty IRawData instance without any keys.
     *
     * @return a new empty IRawData instance
     */
    private static IRawData buildRawDataWithoutKey() {
        return RawDataFactory.create(Map.of());
    }

}
