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
import common.ColorConstants;
import common.CommonConstants;

/**
 * The Class MIOSAggregatorService.
 *
 * @author esegato
 */
public class MIOSAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final String mios = inputJson.getSafeString(FlightInputConstants.MIOS);
		final String miosColor = inputJson.getSafeString(FlightInputConstants.MIOS_COLOR);
		String color = "";
		
		if (!(mios.isEmpty())) {
			switch (miosColor) {
			case CommonConstants.COLOR_DEFAULT:
				color = ColorConstants.DEFAULT;
				break;
			case CommonConstants.COLOR_CYAN:
				color = ColorConstants.CYAN;
				break;
			case CommonConstants.COLOR_GREEN:
				color = ColorConstants.GREEN;
				break;
			case CommonConstants.COLOR_ORANGE:
				color = ColorConstants.ORANGE;
				break;
			case CommonConstants.COLOR_YELLOW:
				color = ColorConstants.YELLOW;
				break;
			case CommonConstants.COLOR_PINK:
				color = ColorConstants.PINK_OLDI;
				break;
			case CommonConstants.COLOR_MAGENTA:
				color = ColorConstants.MAGENTA;
				break;
			}
		}
		dataNode.addLine(FlightOutputConstants.MIOS, mios) 
				.setAttributeIf(!color.isEmpty(), EdmModelKeys.Attributes.COLOR, color);
    }

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = FlightOutputConstants.MIOS;
		return m_serviceName;
	}

}
