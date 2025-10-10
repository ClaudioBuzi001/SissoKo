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

/**
 * The Class COOAggregatorService.
 */
public class COOAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final String cooKey = FlightInputConstants.COO;
		String cooRem_data = inputJson.getSafeString(FlightInputConstants.COO_REM);
		final boolean imLastSector = inputJson.getSafeBoolean(FlightInputConstants.IAMLASTSECTOR);
		
		
		final String stringTemplate = inputJson.getSafeString(FlightInputConstants.TEMPLATE_STRING);
		String color = "";
		String callBack = "";
		
		if (cooRem_data.equals("11") && imLastSector) {
			
			if (!stringTemplate.contains(FlightOutputConstants.TEMPLATE_NOTIFIED) 
					&& !stringTemplate.contains(FlightOutputConstants.TEMPLATE_NEARBY)){
				cooRem_data = "PCO";
				/** The call back PCO. */
				String callBackPCO = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=PCO,    OBJECT_TYPE=PCO,     PREVIEW_NAME=previewPCO,   DATA={'ISHOOK':'track'})";
				callBack = callBackPCO;
				color = ColorConstants.ORANGE;
			} else {
				cooRem_data = "";
			}
		} else if (cooRem_data.equals("6") && imLastSector) {
			cooRem_data = "COO";
			/** The call back COO. */
			String callBackCOO = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=COO,    OBJECT_TYPE=COO,     PREVIEW_NAME=previewCOO,   DATA={'ISHOOK':'track'})";
			callBack = callBackCOO;
			color = ColorConstants.YELLOW;
		} else {
			cooRem_data = "";
		}

		
		dataNode.addLine(cooKey, cooRem_data).setAttribute(EdmModelKeys.Attributes.COLOR, color)
				.setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, callBack);
    }

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = FlightOutputConstants.COO_REM;
		return m_serviceName;
	}

}
