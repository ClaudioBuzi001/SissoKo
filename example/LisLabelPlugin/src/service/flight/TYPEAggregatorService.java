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
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import common.Utils;

/**
 * The Class TYPEAggregatorService.
 */
public class TYPEAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final String dataATY = inputJson.getSafeString(FlightInputConstants.ATY);
		final String dataWTC = inputJson.getSafeString(FlightInputConstants.WTC);
		
		String visible = "";
		final String dataNff = inputJson.getSafeString(FlightInputConstants.NEXT_FIR_FRQ);
	  	
		if (!dataNff.isEmpty() && !dataNff.equals("0")) {
			visible = Utils.priorityFreqOn(inputJson);
		}

		dataNode.addLine(FlightOutputConstants.ATYPE, dataATY + " " + dataWTC)
				.setAttributeIf(visible.equals("true"), EdmModelKeys.Attributes.VISIBLE, "" );

		// Set the TYPE (ATY) color for Bulgaria V4 to be shown on the FHI dashboard
		// TODO: fields marked as "_BV4" will be moved to a dedicated GiFork plugin in the future
		String atypeColor = ""; // Default color (white)
		String flightDashboardColor;
		if (dataWTC.trim().equalsIgnoreCase("J")) {
			// Super-Heavy (Jumbo) color
			flightDashboardColor = "#FFA500";
			atypeColor = "#FFA500"; // Orange
		} else {
			// Other types color (default for label or dashboard
			flightDashboardColor = "#8DB3E2";
		}
		dataNode.addLine(FlightOutputConstants.ATY_BV4, dataATY)
				.setAttribute(EdmModelKeys.Attributes.COLOR, atypeColor)
				.setAttribute(EdmModelKeys.Attributes.FLIGHT_DASHBAORD_COLOR, flightDashboardColor);

    }

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = FlightOutputConstants.ATYPE;
		return service_name;
	}

}
