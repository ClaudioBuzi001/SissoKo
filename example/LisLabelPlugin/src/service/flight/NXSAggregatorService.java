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

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardUtility_LIS;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import common.Utils;

/**
 * The Class NXSAggregatorService.
 *
 * @author esegato
 */
public class NXSAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final int nextSector = inputJson.getSafeInt(FlightInputConstants.NXS, 0);
		String visible = "";
		final String dataNff = inputJson.getSafeString(FlightInputConstants.NEXT_FIR_FRQ);
		String dataNxs = inputJson.getSafeString("NEXT_FIR");

		if (nextSector > 2) {
			dataNxs = BlackBoardUtility_LIS.searchSECTOR_NAME(nextSector + "");
		}

		if (dataNxs.isEmpty()) {
			dataNxs = FlightInputConstants.NXS;
		}

		if (!dataNff.isEmpty() && !dataNff.equals("0")) {
			visible = Utils.priorityFreqOn(inputJson);
		}

		dataNode.addLine(FlightOutputConstants.NXS, dataNxs).setAttributeIf(visible.equals("true"),
				EdmModelKeys.Attributes.VISIBLE, "");
		
		dataNode.addLine("NXS_LABEL", dataNxs);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = FlightOutputConstants.NXS;
		return service_name;
	}

}
