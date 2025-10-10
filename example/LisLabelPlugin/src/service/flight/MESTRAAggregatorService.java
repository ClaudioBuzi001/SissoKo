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
 * The Class MESTRAAggregatorService.
 */
public class MESTRAAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		String mesTra = inputJson.getSafeString(FlightInputConstants.MES_TRA);
		final String mesTraColor = inputJson.getSafeString(FlightInputConstants.MES_TRA_COLOR);
		String ssrCODE_Warning = inputJson.getSafeString(FlightInputConstants.SSR_CODE);
		String color = "";
		String colorSSR_ESB = "";

		if (!(mesTra.isEmpty())) {
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
			
			if (mesTra.equals("MAC")) {
				mesTra = "MAC";
				color = ColorConstants.ORANGE;
			}
		}
		
		dataNode.addLine(FlightOutputConstants.MES_TRA, mesTra) 
				.setAttributeIf(!color.isEmpty(), EdmModelKeys.Attributes.COLOR, color);
		
		
		if (mesTra.equals("COD") && mesTraColor.equals(CommonConstants.COLOR_ORANGE)) {
			if (ssrCODE_Warning.trim().isEmpty())
			{
				ssrCODE_Warning = "A9999";
				colorSSR_ESB = color;
			}
		}
		dataNode.addLine(FlightOutputConstants.SCODE_ESB_COLOR, colorSSR_ESB);
		dataNode.addLine(FlightOutputConstants.SSR_CODE_ESB, ssrCODE_Warning);	
		

    }

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = FlightOutputConstants.MES_TRA;
		return service_name;
	}

}
