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
import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxiliary.flight.FlightInputConstants;

import java.util.Optional;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.leonardo.infrastructure.collections.IReadOnlyMap;

/**
 * The Class STAMAggregatorService.
 * 
 */
public class STAMAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		String data = "";
		boolean isStamVis = false;
		final String flightNum = inputJson.getId();
		final String template = inputJson.getSafeString(FlightInputConstants.TEMPLATE);
		
		if (template.equals(FlightInputConstants.TEMPLATE_CONTROLLED)) {
			final IReadOnlyMap<String, IRawData> list = BlackBoardUtility.getAllData(DataType.TCAT_MESSAGE.name());
			if ((list != null) && !list.isEmpty()) {
				for (IRawData item : list.values()) {
					if (item.getSafeString("FLIGHT_NUM").equals(flightNum)) {
						final Optional<IRawData> tcatAck = BlackBoardUtility.getDataOpt(DataType.BB_TCAT_ACK.name(),item.getId());
						if (!tcatAck.isPresent()) {
							isStamVis = true;
							break;
						}
					}	
				}
			}
		}
		
		if (isStamVis) {
			data="STAM";
		}
		dataNode.addLine("STAM", data);
    }

	/**
	 * Gets the service name.
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		return "STAM";
	}
}
