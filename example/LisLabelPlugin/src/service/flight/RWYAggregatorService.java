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
package service.flight;

import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;

/**
 * The Class RWYAggregatorService.
 *
 * @author esegato
 */
public class RWYAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		String data = "";
		if (inputJson.getSafeBoolean(FlightInputConstants.ISARR)) {
			data = inputJson.getSafeString(FlightInputConstants.ARWY);
		}

		if (inputJson.getSafeBoolean(FlightInputConstants.ISDEP)) {
			data = inputJson.getSafeString(FlightInputConstants.DEPRWY);
		}
		String slash = "";
		if (!data.isEmpty() && !data.isBlank()) {
			slash = "/";
		}
		dataNode.addLine(FlightOutputConstants.RWY, data);

		// TODO: questo serve per far vedere uno slash sulla lasbel ma va modificata, per ora va bene cosi'
		dataNode.addLine("LITERAL_SLASH", slash);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = FlightOutputConstants.RWY;
		return service_name;
	}

}
