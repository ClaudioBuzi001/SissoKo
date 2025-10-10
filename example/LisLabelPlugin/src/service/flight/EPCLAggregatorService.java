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

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

/**
 * The Class EPCLAggregatorService.
 *
 * @author esegato
 */
public class EPCLAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		String ep_cl = inputJson.getSafeString(FlightInputConstants.EP_CL);

		if (ep_cl.equals(FlightInputConstants.EP_CL_NULL) || (ep_cl.isEmpty())) {
			ep_cl = FlightOutputConstants.EP_CL_NULL; 
		}

		dataNode.addLine(FlightOutputConstants.EP_CL, ep_cl);
    }

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = FlightOutputConstants.EP_CL;
		return service_name;
	}

}
