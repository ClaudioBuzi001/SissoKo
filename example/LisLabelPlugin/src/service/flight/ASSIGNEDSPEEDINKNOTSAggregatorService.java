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
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxiliary.flight.FlightInputConstants;
import common.ColorConstants;

/**
 * The Class ASSIGNEDSPEEDINKNOTSAggregatorService.
 */
public class ASSIGNEDSPEEDINKNOTSAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		String color = "";
		final String FLIGHT_NUM = inputJson.getId();

		String callBack = "";

		String show = "";
		String visible = "";
		String dataSpeed = "ASP";

		final String sun = inputJson.getSafeString(FlightInputConstants.SUN);

		switch (sun) {
		case "1":
			String spdKnots = inputJson.getSafeString("SPDKNOTS");
			if (!spdKnots.isEmpty()) {
				final int spdKnotsDecine = Integer.parseInt(spdKnots) / 10;
				spdKnots = Integer.toString(spdKnotsDecine);
				dataSpeed = "K" + spdKnots;
			}
			break;
		case "2":
			String spMach = inputJson.getSafeString("SPDMACH");
			if (spMach.equals("1+")) {
				spMach = "1.0";
			}
			final String spMachNewStr = spMach.replace(".", "-");
			final String[] spMachNew = spMachNewStr.split("-");
			if (spMachNew.length > 1) {
				String dataMerge = spMachNew[1];
				dataSpeed = ("M" + dataMerge);
			}
			break;

		default:
			dataSpeed = "ASP";
			break;
		}

		String template = inputJson.getSafeString(FlightInputConstants.TEMPLATE);
		if (FlightInputConstants.TEMPLATE_AIS.equals(template)
				|| FlightInputConstants.TEMPLATE_CONTROLLED.equals(template)) {
			final var jsonCoordination = BlackBoardUtility.getDataOpt(DataType.FLIGHT_COORDINATION.name(),
					FLIGHT_NUM + "_" + "CSP");
			if (jsonCoordination.isPresent()) {
				final String coordIn = jsonCoordination.get().getSafeString("ISCIN");
				final String coordOut = jsonCoordination.get().getSafeString("ISCOUT");
				final String coordType = jsonCoordination.get().getSafeString("COORDTYPE");
				final int stateCoordType = jsonCoordination.get().getSafeInt("COORDSTATE");

				if ("CSP".equals(coordType)) {
					if (("true".equals(coordIn))) {

						color = ColorConstants.MAGENTA;

						/** The call back ACKCSP. */

						String callBackACKCSP = "EXTERNAL_ORDER(ORDER_ID=ACKCSP, OBJECT_TYPE=ACKCSP,   PREVIEW_NAME=quickCSP,  DATA={'SPS':'5'})";
						/** The call back ACPCSP. */

						String callBackACPCSP = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPCSP, OBJECT_TYPE=ACPCSP,   PREVIEW_NAME=choiceCSP)";
						/** The call back CTPCSP. */

						String callBackCTPCSP = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=CTPCSP, OBJECT_TYPE=CTPCSP,   PREVIEW_NAME=choiceCTPCSP)";
						switch (stateCoordType) {
						case 1:
							callBack = callBackACPCSP;
							break;
						case 3:
							callBack = callBackACKCSP;
							color = ColorConstants.ORANGE;
							break;
						case 4:
							callBack = callBackCTPCSP;
							break;
						default:

							break;
						}

					} else if ("true".equals(coordOut)) {
						color = ColorConstants.CYAN;
						if (stateCoordType == 4) {
							/** The call back UNDO. */

							String callBackUNDO = "EXTERNAL_ORDER(ORDER_ID=UNDOCSP, OBJECT_TYPE=UNDOCSP,   PREVIEW_NAME=quickUNDOCSP,  DATA={'SPS':'0'})";
							callBack = callBackUNDO;
						}
					}
				}
			} else {
				/** The call back CONTROLLED. */

				String callBackCONTROLLED = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=SPD, OBJECT_TYPE=SPD,   PREVIEW_NAME=choiceSPD)";
				/** The call back AIS. */

				String callBackAIS = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=CSP, OBJECT_TYPE=CSP,   PREVIEW_NAME=choiceCSP)";
				if (FlightInputConstants.TEMPLATE_AIS.equals(template)) {
					callBack = callBackAIS;
				} else if (FlightInputConstants.TEMPLATE_CONTROLLED.equals(template)) {
					callBack = callBackCONTROLLED;
				}
			}
		}

		if (!dataSpeed.equals("ASP"))

		{
			show = "forced";
			visible = "true";
		}

		dataNode.addLine("SPD_DISPLAY_2CHAR", dataSpeed).setAttribute(EdmModelKeys.Attributes.VISIBLE, visible)
				.setAttribute(EdmModelKeys.Attributes.SHOW, show)
				.setAttributeIf(!color.isEmpty(), EdmModelKeys.Attributes.COLOR, color)
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
		String service_name = "ASSIGNEDSPEEDINKNOTS";
		return service_name;
	}

}
