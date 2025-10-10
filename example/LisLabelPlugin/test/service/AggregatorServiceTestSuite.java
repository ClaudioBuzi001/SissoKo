package service;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import service.correlation.CORLMSTATAggregatorServiceTest;
import service.cpdlc.DDLMSGAggregatorServiceTest;
import service.cpdlc.UDLARCAggregatorServiceTest;
import service.cpdlc.UDLCFLAggregatorServiceTest;
import service.cpdlc.UDLCONTACTAggregatorServiceTest;
import service.cpdlc.UDLHDGAggregatorServiceTest;
import service.cpdlc.UDLSPEEDAggregatorServiceTest;
import service.cpdlc.UDLSQUAWKAggregatorServiceTest;
import service.flight.ARCAggregatorServiceTest;
import service.flight.CLKAggregatorServiceTest;
import service.flight.EMGSLAggregatorServiceTest;
import service.fpm.CFLFLAGAggregatorServiceTest;
import service.tct.EMG_TCTAggregatorServiceTest;
import service.track.ADSCAggregatorServiceTest;
import service.track.EMGAggregatorServiceTest;
import service.track.GNDAggregatorServiceTest;
import service.track.MGNAggregatorServiceTest;
import service.track.MODE4AggregatorServiceTest;
import service.track.TCASAggregatorServiceTest;
import service.track.VXAggregatorServiceTest;
import service.track.VYAggregatorServiceTest;

/**
 * Test suite that aggregates all unit tests related to AggregatorService implementations.
 *
 * This class allows executing all relevant test classes in a single run using JUnit 5. Add your
 * test classes to the {@link SelectClasses} annotation to include them in the suite.
 */
@Suite
@SelectClasses({ CORLMSTATAggregatorServiceTest.class, DDLMSGAggregatorServiceTest.class,
		UDLARCAggregatorServiceTest.class, UDLCFLAggregatorServiceTest.class, UDLCONTACTAggregatorServiceTest.class,
		UDLHDGAggregatorServiceTest.class, UDLSPEEDAggregatorServiceTest.class, UDLSQUAWKAggregatorServiceTest.class,
		ARCAggregatorServiceTest.class, CLKAggregatorServiceTest.class, EMGSLAggregatorServiceTest.class,
		CFLFLAGAggregatorServiceTest.class, EMG_TCTAggregatorServiceTest.class, ADSCAggregatorServiceTest.class,
		EMGAggregatorServiceTest.class, GNDAggregatorServiceTest.class, MGNAggregatorServiceTest.class,
		MODE4AggregatorServiceTest.class, TCASAggregatorServiceTest.class, VXAggregatorServiceTest.class,
		VYAggregatorServiceTest.class, })

public class AggregatorServiceTestSuite {
}
