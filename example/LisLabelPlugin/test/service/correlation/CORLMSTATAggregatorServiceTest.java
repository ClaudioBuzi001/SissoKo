package service.correlation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

import auxiliary.correlation.CorrelationInputConstants;
import auxiliary.correlation.CorrelationOutputConstants;

/**
 * Unit tests for {@link CORLMSTATAggregatorService} using Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class CORLMSTATAggregatorServiceTest {

	/** */
    @InjectMocks
    private CORLMSTATAggregatorService service;

    /** */
    @Mock
    private IRawData mockRawData;

    /** */
    private HeaderNode header;

    /** */
    @BeforeEach
    void setUp() {
        header = new HeaderNode("service");
    }

    /** */
    @Test
    void testGetServiceName_ReturnsExpectedValue() {
        assertEquals(CorrelationOutputConstants.CORLM_STATUS, service.getServiceName());
    }

    /** */
    @Test
    void testAggregate_WithValidStatus_AddsExpectedHeaderLine() {
        // Setup mock
        when(mockRawData.getSafeString(CorrelationInputConstants.CORLM_STATUS))
            .thenReturn(CorrelationInputConstants.CORLM_STATUS_DEC_VALUE);

        // Execute
        service.aggregate(mockRawData, header);

        // Verify result
        String outValue = header.getLine(CorrelationOutputConstants.CORLM_STATUS)
                                .getAttributeValue(EdmModelKeys.Pairs.DATA);
        
        assertEquals(CorrelationInputConstants.CORLM_STATUS_DEC_VALUE, outValue);
    }

    /** */
    @Test
    void testAggregate_WithDifferentStatus_AddsEmptyHeaderLine() {
        when(mockRawData.getSafeString(CorrelationInputConstants.CORLM_STATUS))
            .thenReturn("UNEXPECTED_VALUE");

        service.aggregate(mockRawData, header);

        String outValue = header.getLine(CorrelationOutputConstants.CORLM_STATUS)
                                .getAttributeValue(EdmModelKeys.Pairs.DATA);
        
        assertEquals("", outValue);
    }

    /** */
    @Test
    void testAggregate_WithMissingStatus_AddsEmptyHeaderLine() {
        when(mockRawData.getSafeString(CorrelationInputConstants.CORLM_STATUS))
            .thenReturn(null);

        service.aggregate(mockRawData, header);

        String outValue = header.getLine(CorrelationOutputConstants.CORLM_STATUS)
                                .getAttributeValue(EdmModelKeys.Pairs.DATA);
        
        assertEquals("", outValue);
    }

    /** */
    @Test
    void testAggregate_VerifyRawDataCalledOnce() {
        when(mockRawData.getSafeString(CorrelationInputConstants.CORLM_STATUS))
            .thenReturn(CorrelationInputConstants.CORLM_STATUS_DEC_VALUE);

        service.aggregate(mockRawData, header);

        // Verify interaction with mock
        verify(mockRawData, times(1)).getSafeString(CorrelationInputConstants.CORLM_STATUS);
    }
}
