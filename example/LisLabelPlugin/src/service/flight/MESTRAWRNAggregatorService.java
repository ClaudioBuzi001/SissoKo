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
 * The Class MESTRAWRNAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class MESTRAWRNAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		String warning = inputJson.getSafeString(FlightInputConstants.MES_TRA_WARN);
		final String mesTraColor = inputJson.getSafeString(FlightInputConstants.MES_TRA_COLOR);
		String color = "";
		final boolean imLastSector = inputJson.getSafeBoolean(FlightInputConstants.IAMLASTSECTOR);
		String callBack = CommonConstants.RESET_CALLBACK; 
		
		boolean visible=true;
		
		if (!(warning.isEmpty())) {

			if (warning.equals("PCO") && !imLastSector) {
				warning = "";
				color = "";
			} else {
				/** The call back PCO. */
				boolean isExtPointTx = inputJson.getSafeBoolean(FlightInputConstants.EXT_POINT_OUT_TX_STATUS);
				String callBackPCO = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=PCO,    OBJECT_TYPE=PCO,     PREVIEW_NAME=previewPCO,   DATA={'ISHOOK':'track'})";
				String callBackMAC = "EXTERNAL_ORDER(ORDER_ID=MAC, PREVIEW_NAME=quickMAC,  OBJECT_TYPE=MAC)";
				String callBackACKPNT = "EXTERNAL_ORDER(ORDER_ID=ACKPNT, ALIAS_ORDERID=EPNT, OBJECT_TYPE=EPNT , PREVIEW_NAME=quickEPNT , DATA={'TYPE_MOD':'A'})";
				boolean isExtPointTxSect = inputJson.getSafeBoolean("EXT_POINT_OUT_TX");
				
				if (warning.equals("PCO"))
					callBack = callBackPCO;
				
				if (warning.equals("MAC"))
					callBack = callBackMAC;

				if (warning.equals("PNT") && isExtPointTx) {
					if (isExtPointTxSect) {
						callBack = callBackACKPNT;
					} else {
						String templateString = inputJson.getSafeString("TEMPLATE_STRING");
						if (templateString.contains("TENTATIVE_AIS")) {
							callBack = callBackACKPNT;
						} else {
							visible=false;
						}
					}
				}
	                
				switch (mesTraColor) {
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
		}
		if (visible) {
			dataNode.addLine(FlightOutputConstants.MES_TRA_WARN, warning) 
			.setAttribute(EdmModelKeys.Attributes.COLOR, color)
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
		String service_name = FlightOutputConstants.MES_TRA_WARN;
		return service_name;
	}

}
