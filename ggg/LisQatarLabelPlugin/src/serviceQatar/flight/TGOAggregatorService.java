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
package serviceQatar.flight;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.flight.FlightOutputConstants;

/**
 * The Class MISAggregatorService.
 */
public class TGOAggregatorService implements IAggregatorService {

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = FlightOutputConstants.TGO;
		return service_name;
	}

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final int tgo = inputJson.getSafeInt(FlightOutputConstants.TGO, 0);

		if (tgo != 0) {
			dataNode.addLine(FlightOutputConstants.TGO, "T" + tgo) 
					.setAttribute(EdmModelKeys.Attributes.VISIBLE, true);
			dataNode.addLine(FlightOutputConstants.MISSED_APPROACH, "") 
					.setAttribute(EdmModelKeys.Attributes.VISIBLE, false);
		}
		else {
			dataNode.addLine(FlightOutputConstants.TGO, "") 
			.setAttribute(EdmModelKeys.Attributes.VISIBLE, false);
			dataNode.addLine(FlightOutputConstants.MISSED_APPROACH, "") 
			.setAttribute(EdmModelKeys.Attributes.VISIBLE, true);
		}
			
	}
}
