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
package auxiliaryQatar;

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.flight.FlightAggregatorMap;
import auxiliary.track.TrackAggregatorMap;
import common.AggregatorMap;
import common.CommonConstants;
import processorNew.FlightProcessor;
import processorNew.TrackProcessor;
import serviceQatar.flight.CFLQatarAggregatorService;
import serviceQatar.flight.GenericQatarAggregatorService;
import serviceQatar.flight.MNV_HDGQatarAggregatorService;
import serviceQatar.flight.NXSQatarAggregatorService;
import serviceQatar.flight.TGOAggregatorService;
import serviceQatar.track.LEVQatarAggregatorService;
import serviceQatar.track.SALQatarAggregatorService;

/**
 * The Class AmiLabelAggregatorMap.
 */
public class QatarLabelAggregatorMap extends AggregatorMap {

	/**
	 * Instantiates a new qatar label aggregator map.
	 */

	public void register() {
		IAggregatorService service;

		final TrackAggregatorMap mapTrack = TrackProcessor.trackAggregatorMap;

		service = new LEVQatarAggregatorService();
		mapTrack.addServiceExt(QatarInputConstants.LEV, service);

		service = new SALQatarAggregatorService();
		mapTrack.addServiceExt(QatarInputConstants.SAL, service);

		final FlightAggregatorMap mapFlight = FlightProcessor.flightAggregatorMap;

		service = new CFLQatarAggregatorService();
		mapFlight.addServiceExt(QatarInputConstants.CFL, service);

		service = new NXSQatarAggregatorService();
		mapFlight.addServiceExt(QatarInputConstants.NXSNAME, service);

		service = new TGOAggregatorService();
		mapFlight.addServiceExt(QatarInputConstants.TGO, service);

		service = new MNV_HDGQatarAggregatorService();
		mapFlight.addServiceExt(QatarInputConstants.MNV_HDG, service);

		service = new GenericQatarAggregatorService();
		mapFlight.addServiceExt(CommonConstants.FLIGHT_NUM, service);

	}

}
