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
package service.track;

import java.util.Optional;

import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightOutputConstants;
import auxiliary.track.TrackInputConstants;
import processorNew.FlightProcessor;

/**
 * The Class RCSAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class RCSAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		Optional<IRawData> jsonFlight = Utils_LIS.getFlightFromTrkId(inputJson.getId());
		if (jsonFlight.isPresent()) {
			final IAggregatorService service = FlightProcessor.flightAggregatorMap
					.getService(FlightOutputConstants.CALLSIGN);
			service.aggregate(jsonFlight.get(), dataNode);
		}

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = TrackInputConstants.RCS;
		return service_name;
	}

}
