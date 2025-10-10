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
 * The Class IRFAggregatorService.
 *
 * @author saffiotif
 * @version $Revision$
 */
public class IRFAggregatorService implements  IAggregatorService {


	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
	
		final boolean irf = inputJson.getSafeBoolean(FlightInputConstants.IRF);
		final String template = inputJson.getSafeString(FlightInputConstants.TEMPLATE);
		String data = "";
		String color ="";
		
		if (irf) {
			data = "ROF";
			if (template.equals(FlightInputConstants.TEMPLATE_AIS)) {
				color = Utils.getTemplateColor(FlightInputConstants.TEMPLATE_HANDOVER_EXITING) ;
				
			} else if (template.equals(FlightInputConstants.TEMPLATE_CONTROLLED)) {
				color = Utils.getTemplateColor(FlightInputConstants.TEMPLATE_HANDOVER_ENTERING);
			}
		}
		
		dataNode.addLine(FlightOutputConstants.ROF, data)
		.setAttributeIf(!color.isEmpty(), EdmModelKeys.Attributes.COLOR, color);

    }

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName()
	{
		/** The service name. */
		String service_name = FlightInputConstants.IRF;
		return service_name;
	}
}
