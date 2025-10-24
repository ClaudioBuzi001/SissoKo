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
 * The Class MissionAggregatorService.
 */
public class MissionAggregatorService  implements IAggregatorService {

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		
		/** The service name. */
		return "VEHICLE_MISSION";
	}

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		
		
		String data ="";
		
		if (!inputJson.getSafeString("MID").isEmpty()) { 
			/*
			 *  1 = TOW;
                2 = FOLLOW_ME 
                0xFF = NO MISSION (Default) 
			 */
			switch (inputJson.getSafeInt("MID")) {
				case 1:
					data = "TOW";
					break;
				case 2:
					data = "FLW";
					break;
				default:
					data = "";
					break;
			}
		}
		
		
		dataNode.addLine("MISSION", data);
	}

}
