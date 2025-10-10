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
import com.gifork.auxiliary.SafeDataRetriever;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.leonardo.infrastructure.Pair;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import common.ColorConstants;
import common.CommonConstants;
import common.CommonConstants.CoordState;
import common.CommonConstants.CoordType;
import common.Utils;

/**
 * The Class MNV_HDGAggregatorService.
 */
public class MNV_HDGAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		String data = "AHDG";
		String color = "";
		String callback = "";

		String show = "";
		String visible = "";

		final String FLIGHT_NUM = inputJson.getId();

		final String RJC = inputJson.getSafeString("RJC");
		String FLIGHT_DATA_HDG = inputJson.getSafeString("MNV_HDG");
		final String AHDG = inputJson.getSafeString("AHDG");
		final int MNV_TYPE = inputJson.getSafeInt("MNV_TYPE", 0);
		final int HDM = inputJson.getSafeInt("HDM", 0);

		final boolean flag_Coord_CHE = inputJson.getSafeBoolean("IS_COORD_CHE");
		final boolean flag_Coord_DCT = inputJson.getSafeBoolean("IS_COORD_DCT");

		double flightData = SafeDataRetriever.strToDouble(FLIGHT_DATA_HDG);
		final double flightAhdg = SafeDataRetriever.strToDouble(AHDG);
		final String FLIGHT_DATA_DPN = inputJson.getSafeString("MNV_DPN");
		final String template = inputJson.getSafeString(FlightInputConstants.TEMPLATE);

		if ((MNV_TYPE == 2 || MNV_TYPE == 4) && (HDM == 4 || HDM == 5)) {
			final Optional<IRawData> jsonTrack = Utils_LIS.getTrkFromFn(inputJson.getId());
			if (jsonTrack.isPresent()) {
				final int MGN_TRACK = jsonTrack.get().getSafeInt("MGN", 0);
				final double MaxHdg = 359.0;
				if (HDM == 4) {
					flightData = (flightData + MGN_TRACK) % MaxHdg;
				} else if (HDM == 5) {
					flightData = (flightData - MGN_TRACK) % MaxHdg;
					if (flightData != Math.abs(flightData)) {
						flightData = (flightData * -1);
					}
				}
				FLIGHT_DATA_HDG = String.valueOf((int) flightData);
			}
		}

		if (MNV_TYPE != 0) {

			if (MNV_TYPE == 1) {

				data = FLIGHT_DATA_DPN;
				var jsonCoordination = BlackBoardUtility.getDataOpt(DataType.FLIGHT_COORDINATION.name(),
						FLIGHT_NUM + "_" + "DCT");
				if (jsonCoordination.isPresent()) {
					int stateCoordType = jsonCoordination.get().getSafeInt("COORDSTATE");
					boolean isCIN = jsonCoordination.get().getSafeBoolean("ISCIN", false);
					if (isCIN) {
						color = ColorConstants.MAGENTA;
						String callbackDCT = "EXTERNAL_ORDER(GRAPHIC_ORDER=DIRECT_TO , ORDER_ID=DCT , PREVIEW_NAME=quickDCT,  OBJECT_TYPE=DCT)";
						callback = callbackDCT;
					} else {
						color = ColorConstants.CYAN;
					}
					if (RJC.equals("RJC") && flag_Coord_DCT && stateCoordType == 3) {
						color = ColorConstants.ORANGE;
					}
				}
			} else if (MNV_TYPE == 2 || MNV_TYPE == 4) {
				if (flightData == 0.00) {
					data = FLIGHT_DATA_HDG;
				} else if (flightData < 10) {
					data = "H00" + FLIGHT_DATA_HDG;
				} else if (flightData < 100) {
					data = "H0" + FLIGHT_DATA_HDG;
				} else {
					data = "H" + FLIGHT_DATA_HDG;
				}
			} else if (MNV_TYPE == 3) {
				data = "CLHDG";
			}
		} else if (!AHDG.equals("0")) {
			if (flightAhdg < 10) {
				data = "H00" + AHDG;
			} else if (flightAhdg < 100) {
				data = "H0" + AHDG;
			} else {
				data = "H" + AHDG;
			}

		} else {
			data = "AHDG";
		}

		if (callback.isEmpty()) {
			Pair<String, String> callCoord = getHdgCallback(FLIGHT_NUM, template);
			callback = callCoord.getX();
			if (!callCoord.getY().isEmpty()) {
				color = callCoord.getY();
			}
			if (RJC.equals("RJC") && flag_Coord_CHE) {
				final var fligCoord = BlackBoardUtility.getDataOpt(DataType.FLIGHT_COORDINATION.name(),
						FLIGHT_NUM + "_" + "CHE");
				if (fligCoord.isPresent()) {
					final int stateCoordType = fligCoord.get().getSafeInt("COORDSTATE");
					if (stateCoordType == 3) {
						color = ColorConstants.ORANGE;
					}
				}

			}

		}

		if (!data.equals("AHDG") && !data.equals("CLHDG")) {
			show = "forced";
			visible = "true";
		}

		dataNode.addLine(FlightOutputConstants.MVN_SECTION_VALUE, data)
				.setAttribute(EdmModelKeys.Attributes.VISIBLE, visible).setAttribute(EdmModelKeys.Attributes.SHOW, show)
				.setAttribute(EdmModelKeys.Attributes.COLOR, color)
				.setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, callback);

		dataNode.addLine(FlightOutputConstants.MGN_CHOICE, " ");

	}

	/**
	 * Gets the hdg callback.
	 *
	 * @param FLIGHT_NUM the flight num
	 * @param template   the template
	 * @return the hdg callback
	 */
	public static Pair<String, String> getHdgCallback(final String FLIGHT_NUM, final String template) {
		String callback = "";
		String colorCoord = "";

		final var hdgCoord = BlackBoardUtility.getDataOpt(DataType.FLIGHT_COORDINATION.name(),
				Utils.getFlightCoordKey(FLIGHT_NUM, CoordType.HDG));
		if (hdgCoord.isEmpty()) {
			if (template.equals(FlightInputConstants.TEMPLATE_AIS)) {
				callback = CommonConstants.callbackCHE_INIT;
			}
			if (template.equals(FlightInputConstants.TEMPLATE_CONTROLLED)) {
				callback = CommonConstants.callbackOHD;
			}
		} else {
			if (template.equals(FlightInputConstants.TEMPLATE_AIS)
					|| template.equals(FlightInputConstants.TEMPLATE_CONTROLLED)) {
				callback = CommonConstants.RESET_CALLBACK;
			}

			final String coordIn = hdgCoord.get().getSafeString("ISCIN");
			final String coordOut = hdgCoord.get().getSafeString("ISCOUT");
			if (("true".equals(coordIn))) {
				colorCoord = ColorConstants.MAGENTA;
			} else if ("true".equals(coordOut)) {
				colorCoord = ColorConstants.CYAN;
			}

			final CoordState coordState = CoordState.fromValue(hdgCoord.get().getSafeInt(CommonConstants.COORDSTATE));
			switch (coordState) {
			case MODIFIED: {
				if (template.equals(FlightInputConstants.TEMPLATE_AIS)) {
					callback = CommonConstants.callbackCHE_ACPRJC;
				}
				break;
			}
			case PROPOSED: {
				if (template.equals(FlightInputConstants.TEMPLATE_CONTROLLED)) {
					callback = CommonConstants.callbackCHE_CTP;
				}
				break;
			}
			default:
				break;
			}
		}

		return new Pair<>(callback, colorCoord);
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = "MNV_HDG";
		return service_name;
	}

}
