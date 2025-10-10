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

import java.util.Optional;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import common.CommonConstants;

/**
 * The Class PBCSAggregatorService.
 *
 */
public class PBCSAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		String colorRnp = "";

		String dataRcp = inputJson.getSafeString(CommonConstants.FLIGHT_VALUE_RCP);
		String dataRsp = inputJson.getSafeString(CommonConstants.FLIGHT_VALUE_RSP);
		String dataRnp1 = inputJson.getSafeString(CommonConstants.FLIGHT_FLAG_IS_RNP1);
		String dataRnp10 = inputJson.getSafeString(CommonConstants.FLIGHT_FLAG_IS_RNP10);
		String dataRnp2 = inputJson.getSafeString(CommonConstants.FLIGHT_FLAG_IS_RNP2);
		String dataRnp4 = inputJson.getSafeString(CommonConstants.FLIGHT_FLAG_IS_RNP4);
		String dataRnpapch = inputJson.getSafeString(CommonConstants.FLIGHT_FLAG_IS_RNPACHP);

		if (inputJson.getSafeBoolean(CommonConstants.IS_RNP)) {
			colorRnp = getPBCSColor(CommonConstants.IS_RNP, "true", "");
		}

		String colorRsp = getPBCSColor(CommonConstants.IS_RSP, inputJson.getSafeString(CommonConstants.IS_RSP), "");
		String colorRcp = getPBCSColor(CommonConstants.IS_RCP, inputJson.getSafeString(CommonConstants.IS_RCP), "");

		dataNode.addLine(CommonConstants.FLIGHT_FLAG_IS_RNP1, dataRnp1).setAttributeIf(!colorRnp.isEmpty(),
				EdmModelKeys.Attributes.COLOR, colorRnp);
		dataNode.addLine(CommonConstants.FLIGHT_FLAG_IS_RNP10, dataRnp10).setAttributeIf(!colorRnp.isEmpty(),
				EdmModelKeys.Attributes.COLOR, colorRnp);
		dataNode.addLine(CommonConstants.FLIGHT_FLAG_IS_RNP2, dataRnp2).setAttributeIf(!colorRnp.isEmpty(),
				EdmModelKeys.Attributes.COLOR, colorRnp);
		dataNode.addLine(CommonConstants.FLIGHT_FLAG_IS_RNP4, dataRnp4).setAttributeIf(!colorRnp.isEmpty(),
				EdmModelKeys.Attributes.COLOR, colorRnp);
		dataNode.addLine(CommonConstants.FLIGHT_FLAG_IS_RNPACHP, dataRnpapch).setAttributeIf(!colorRnp.isEmpty(),
				EdmModelKeys.Attributes.COLOR, colorRnp);

		dataNode.addLine(CommonConstants.IS_RCP, dataRcp).setAttributeIf(!colorRcp.isEmpty(),
				EdmModelKeys.Attributes.COLOR, colorRcp);

		dataNode.addLine(CommonConstants.IS_RSP, dataRsp).setAttributeIf(!colorRsp.isEmpty(),
				EdmModelKeys.Attributes.COLOR, colorRsp);

		String warnPBCS = "";

		if (inputJson.getSafeBoolean(CommonConstants.IS_RNP)) {
			warnPBCS = "RNP";
		} else if (inputJson.getSafeBoolean(CommonConstants.IS_RSP)) {
			warnPBCS = "RSP";
		} else if (inputJson.getSafeBoolean(CommonConstants.IS_RCP)) {
			warnPBCS = "RCP";
		}

		dataNode.addLine("WNG_PBCS", warnPBCS);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		return CommonConstants.IS_RNP;
	}

	/**
	 * Gets the PBCS color.
	 *
	 * @param fieldName    the field name
	 * @param valueField   the value field
	 * @param defaultColor the default color
	 * @return the PBCS color
	 */
	private static String getPBCSColor(final String fieldName, final String valueField, final String defaultColor) {
		String color = defaultColor;
		final Optional<IRawData> listColorGeneric = BlackBoardUtility.getDataOpt(DataType.PRELOADED_COLOR_SET.name(),
				"LIST_COLOR_GENERIC");

		if (listColorGeneric.isPresent() && !fieldName.isEmpty()) {
			final IRawDataElement fieldColorMap = listColorGeneric.get().getSafeElement(fieldName);
			final IRawDataElement valueParameters = fieldColorMap.getSafeElement(valueField);
			color = valueParameters.getSafeString("COLOR");
		}

		return color;
	}

}
