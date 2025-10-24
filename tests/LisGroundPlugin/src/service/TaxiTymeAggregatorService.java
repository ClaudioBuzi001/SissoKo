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
package service;

import application.pluginService.ServiceExecuter.IAggregatorService;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

/**
 * The Class TaxiTymeAggregatorService.
 */
public class TaxiTymeAggregatorService  implements IAggregatorService {

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		
		/** The service name. */
		return "TAXI_TIME";
	}

	/**
	 * Aggregate.
	 *
	 * @param vehicleJson the vehicle json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData vehicleJson, final HeaderNode dataNode) {
		
		
		String data ="";
		
		if (vehicleJson.getSafeString("TAXI_TIME").isEmpty()) { 
			
			data ="1";
		}
		
		
		dataNode.addLine("CLK", data);
		
	}

}
