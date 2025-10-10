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
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import common.ColorConstants;
import common.CommonConstants;

/**
 * The Class MESREVWRNAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class MESREVWRNAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		final String warning = inputJson.getSafeString(FlightInputConstants.MES_REV_WARN);
		final String mesRevColor = inputJson.getSafeString(FlightInputConstants.MES_REV_COLOR);
		String callBack = CommonConstants.RESET_CALLBACK; 

		String color = "";

		boolean visible=true;
		
		if (!warning.isEmpty()) {

			String callBackACKPNT = "EXTERNAL_ORDER(ORDER_ID=ACKPNT, ALIAS_ORDERID=EPNT, OBJECT_TYPE=EPNT , PREVIEW_NAME=quickEPNT , DATA={'TYPE_MOD':'A'})";
			boolean isExtPointRx = inputJson.getSafeBoolean(FlightInputConstants.EXT_POINT_OUT_RX_STATUS);
			boolean isExtPointRxSect = inputJson.getSafeBoolean("EXT_POINT_OUT_RX");
			
			if (warning.equals("PNT") && isExtPointRx) {
				if (isExtPointRxSect) {
					callBack = callBackACKPNT;
				} else {
					/**  VA gestito che su TENTATIVE AIS solo se non ho ricvevuto il settoreAIS **/
					String templateString = inputJson.getSafeString("TEMPLATE_STRING");
					if (templateString.contains("TENTATIVE_AIS")) {
						callBack = callBackACKPNT;
					} else {
						visible=false;
					}
				}
			}
			switch (mesRevColor) {
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
		
		if (visible) {
			dataNode.addLine(FlightOutputConstants.MES_REV_WARN, warning) 
			.setAttributeIf(!color.isEmpty(), EdmModelKeys.Attributes.COLOR, color)
			.setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, callBack);	
		}
		
		

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = FlightOutputConstants.MES_REV_WARN;
		return m_serviceName;
	}

}
