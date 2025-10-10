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
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;


/**
 * The Class MCPAggregatorService.
 *
 */
public class MCPAggregatorService implements IAggregatorService {

	/** callBackMCR */
	private static final String callBackMCR = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=MCR,    OBJECT_TYPE=MCR,     PREVIEW_NAME=choiceMCR   )";
	
	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final String template = inputJson.getSafeString(FlightInputConstants.TEMPLATE_STRING);
		final String MCP_CODE = inputJson.getSafeString("MCP_CODE");
		final String SCH_FLAG = inputJson.getSafeString("SCH","0");
		String color = "";
		String callBack = "";
		String data = "";
		if (!template.contains("NEARBY")){
			if (!MCP_CODE.isEmpty()) {
				data = MCP_CODE;
				color = getMcpColor("MCP_CODE", SCH_FLAG, color);
				callBack = callBackMCR;
			}
		}
		dataNode.addLine("MCP_CODE", data)
				.setAttribute(EdmModelKeys.Attributes.COLOR, color)
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
		String service_name = "MCP_CODE";
		return service_name;
	}

	
	/**
	 * @param field
	 * @param valueField
	 * @param defaultColor
	 * @return mcpColor
	 */
	public static String getMcpColor(final String field, final String valueField, final String defaultColor) {

		final var listColorGeneric = BlackBoardUtility.getDataOpt(DataType.PRELOADED_COLOR_SET.name(),
				"LIST_COLOR_GENERIC");

		if (listColorGeneric.isPresent() && !field.isEmpty()) {
			final IRawDataElement fieldColorMap = listColorGeneric.get().getSafeElement(field);
			final IRawDataElement valueParameters = fieldColorMap.getSafeElement(valueField);
			return valueParameters.getSafeString("COLOR");
		}

		return defaultColor;
	}
	
}
