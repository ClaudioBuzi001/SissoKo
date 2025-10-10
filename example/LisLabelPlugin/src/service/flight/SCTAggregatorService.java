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
import applicationLIS.BlackBoardUtility_LIS;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import common.Utils;

/**
 * The Class SCTAggregatorService.
 *
 * @author esegato
 */
public class SCTAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final int sct = inputJson.getSafeInt(FlightInputConstants.SCT, 0);
		final String dataNff = inputJson.getSafeString(FlightInputConstants.NEXT_FIR_FRQ);
		String data = "   ";
		String visible = "";
		
		
		if (sct > 2) {
			data = BlackBoardUtility_LIS.searchSECTOR_NAME(sct + "");
		}
		
		if (data.isEmpty()) {
			data = FlightInputConstants.SCT;
		}
		
		if (!dataNff.isEmpty() && !dataNff.equals("0")) {
			visible = Utils.priorityFreqOn(inputJson);
		}
		
		dataNode.addLine(FlightOutputConstants.SCT, data)
			.setAttributeIf(visible.equals("true"), EdmModelKeys.Attributes.VISIBLE, "" );
			

			
    }


	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = FlightOutputConstants.SCT;
		return service_name;
	}

}
