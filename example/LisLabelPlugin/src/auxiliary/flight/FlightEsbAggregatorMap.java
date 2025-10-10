/*
 * (c) Copyright Leonardo Company S.p.A.. All rights reserved.
 *
 * Any right of industrial and intellectual property on this document,
 * and of technical Know-how herein contained, belongs to
 * Leonardo Company S.p.A. and/or third parties.
 * According to the law, it is forbidden to disclose, reproduce or however
 * use this document and any data herein contained for any use without
 * previous written authorization by Leonardo Company S.p.A.
 *
 */
package auxiliary.flight;

import common.AggregatorMap;
import service.flight.ARCAggregatorService;
import service.flight.ASSIGNEDSPEEDINKNOTSAggregatorService;
import service.flight.CALLSIGNAggregatorService;
import service.flight.CFLAggregatorService;
import service.flight.FreeTextAggregatorService;
import service.flight.GenericAggregatorService;
import service.flight.MNV_HDGAggregatorService;
import service.flight.NXSAggregatorService;
import service.flight.PELAggregatorService;
import service.flight.PKBAggregatorService;
import service.flight.RWYAggregatorService;
import service.flight.SCTAggregatorService;
import service.flight.TYPEAggregatorService;
import service.flight.XFLAggregatorService;

/**
 * The Class FlightAggregatorMap.
 */
public class FlightEsbAggregatorMap extends AggregatorMap {

	/**
	 * Instantiates a new flight aggregator map.
	 */

	public FlightEsbAggregatorMap() {

		put(FlightOutputConstants.ATYPE, new TYPEAggregatorService());

		put(FlightOutputConstants.RWY, new RWYAggregatorService());

		put(FlightOutputConstants.PKB, new PKBAggregatorService());

		put(FlightOutputConstants.SCT, new SCTAggregatorService());

		put(FlightOutputConstants.NXS, new NXSAggregatorService());

		put(FlightOutputConstants.XFL, new XFLAggregatorService());

		put(FlightOutputConstants.PEL, new PELAggregatorService());

		put(FlightOutputConstants.CALLSIGN, new CALLSIGNAggregatorService());

		put(FlightOutputConstants.CFL, new CFLAggregatorService());

		put(FlightOutputConstants.ARC, new ARCAggregatorService());

		put(FlightOutputConstants.ASSIGNEDSPEEDINKNOTS, new ASSIGNEDSPEEDINKNOTSAggregatorService());

		//put(FlightOutputConstants.LOGO, new LOGOCompanyAggregatorService());

		put(FlightOutputConstants.MNV_HDG, new MNV_HDGAggregatorService());

		// GenericAggregatore
		put(FlightInputConstants.FLIGHT_NUM, new GenericAggregatorService());
		
		// GenericAggregatore
		put("FREE_TEXT", new FreeTextAggregatorService());
		

	}

}
