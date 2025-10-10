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
package service.track;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightInputConstants;
import auxiliary.track.TrackInputConstants;
import auxiliary.track.TrackOutputConstants;
import common.CommonConstants;

/**
 * The Class IASAggregatorService.
 */
public class IASAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		String data = inputJson.getSafeString("IAS");
		String IAS_FRAME = "OFF";
		String show = "";
		String callBack = "";

		if (data.equals(TrackInputConstants.IAS_0)) {
			data = TrackOutputConstants.IAS_NULL;
		}

		final IRawDataElement auxiliaryData = inputJson.getAuxiliaryData();
		int numBattute = auxiliaryData.getSafeInt("numBattuteIAS", 0);

		final var jsonFlight = Utils_LIS.getFlightFromTrkId(inputJson.getId());

		String SUN = "";
		if (jsonFlight.isPresent()) {
			final String flight_num = jsonFlight.get().getSafeString(CommonConstants.FLIGHT_NUM);
			SUN = jsonFlight.get().getSafeString("SUN");

			String template = jsonFlight.get().getSafeString(FlightInputConstants.TEMPLATE);
			if (FlightInputConstants.TEMPLATE_AIS.equals(template)
					|| FlightInputConstants.TEMPLATE_CONTROLLED.equals(template)) {
				final var jsonCoordination = BlackBoardUtility.getDataOpt(DataType.FLIGHT_COORDINATION.name(),
						flight_num + "_" + "CSP");
				if (jsonCoordination.isPresent()) {
					final String coordIn = jsonCoordination.get().getSafeString("ISCIN");
					final String coordOut = jsonCoordination.get().getSafeString("ISCOUT");
					final String coordType = jsonCoordination.get().getSafeString("COORDTYPE");
					final int stateCoordType = jsonCoordination.get().getSafeInt("COORDSTATE");

					if ("CSP".equals(coordType)) {
						if (("true".equals(coordIn))) {
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
								break;
							case 4:
								callBack = callBackCTPCSP;
								break;
							default:
								break;
							}
						} else if ("true".equals(coordOut)) {

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
		}

		double sogliaDeviation = 0.0;
		double maxNumeroBattute = 0.0;
		double flightData = 0.0;

		if (SUN.equals("2")) {

			String FLIGHT_DATA_MACH = jsonFlight.get().getSafeString("SPDMACH");
			if (!FLIGHT_DATA_MACH.isEmpty()) {
				if (FLIGHT_DATA_MACH.equals("1+")) {
					FLIGHT_DATA_MACH = "1.0";
				}
				flightData = Double.parseDouble(FLIGHT_DATA_MACH);
			}

			final var deviation = BlackBoardUtility.getDataOpt(DataType.PRELOADED_DEVIATION_DOWNLINKED.name());
			if (deviation.isPresent()) {
				sogliaDeviation = deviation.get().getSafeDouble("IAS_ASSIGNEDMACH_DEVIATION", 0);
				maxNumeroBattute = deviation.get().getSafeDouble("IAS_ASSIGNEDMACH_MAX_TICK", 0);
			}

		} else if (SUN.equals("1")) {

			String FLIGHT_DATA_KNOTS = jsonFlight.get().getSafeString("SPDKNOTS");
			if (!FLIGHT_DATA_KNOTS.isEmpty()) {
				flightData = Double.parseDouble(FLIGHT_DATA_KNOTS);
			}

			final var deviation = BlackBoardUtility.getDataOpt(DataType.PRELOADED_DEVIATION_DOWNLINKED.name());
			if (deviation.isPresent()) {
				sogliaDeviation = deviation.get().getSafeDouble("IAS_ASSIGNEDKNOTS_DEVIATION", 0);
				maxNumeroBattute = deviation.get().getSafeDouble("IAS_ASSIGNEDKNOTS_MAX_TICK", 0);
			}

		}

		final int trackData = inputJson.getSafeInt("IAS", -1);

		if (trackData != 0 && flightData != 0.0
				&& (trackData > flightData + sogliaDeviation || trackData < flightData - sogliaDeviation)) {

			if (numBattute >= maxNumeroBattute) {
				IAS_FRAME = "ON";
			}
			numBattute++;
		} else {
			numBattute = 0;
		}
		auxiliaryData.put("numBattuteIAS", numBattute);

		dataNode.addLine("IAS", data).setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, callBack);

		if (IAS_FRAME.equals("ON"))

		{
			show = "forced";
		}

		dataNode.addLine("FRAME_IAS", IAS_FRAME).setAttribute(EdmModelKeys.Attributes.SHOW, show)
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
		String service_name = "IAS";
		return service_name;
	}

}
