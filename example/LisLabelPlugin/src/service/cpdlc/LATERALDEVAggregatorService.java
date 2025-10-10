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
package service.cpdlc;

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.flight.FlightOutputConstants;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

/**
 * The Class LATERALDEVAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class LATERALDEVAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		// Lateral deviation to show for the
		// TODO: fields marked as "_BV4" will be moved to a dedicated GiFork plugin in the future
		final String LAT_BV4 = inputJson.getSafeString(FlightOutputConstants.LAT);
		dataNode.addLine(FlightOutputConstants.LAT_BV4, LAT_BV4)
				.setAttribute(EdmModelKeys.Attributes.FLIGHT_DASHBAORD_COLOR, "#FFFF00");
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = FlightOutputConstants.LAT;
		return service_name;
	}
}
