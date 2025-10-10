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
import com.gifork.auxiliary.ConfigurationFile;
import com.gifork.commons.data.IRawData;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import auxiliary.track.TrackInputConstants;
import common.ColorConstants;
import common.CommonConstants;

/**
 * The Class CALLSIGNAggregatorService.
 *
 * @author esegato
 */
public class CALLSIGNAggregatorService implements IAggregatorService {

	/** The Constant USE_MODE3A_FOR_C2_LOSS. */
	private static final String USE_MODE3A_FOR_C2_LOSS = "USE_MODE3A_FOR_C2_LOSS";

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final String callsign = inputJson.getSafeString(FlightInputConstants.CALLSIGN);
		final String isabinoroute = inputJson.getSafeString(FlightInputConstants.ISABINOROUTE);

		final String template = inputJson.getSafeString(FlightInputConstants.TEMPLATE_STRING);
		final String isLive = inputJson.getSafeString(FlightInputConstants.ISLIVE);
		final String isActive = inputJson.getSafeString(FlightInputConstants.ISACTIVE);
		final String isInbound = inputJson.getSafeString(FlightInputConstants.FLC);
		final String warningRev = inputJson.getSafeString(FlightInputConstants.MES_REV);
		final String warningRevColor = inputJson.getSafeString(FlightInputConstants.MES_REV_COLOR);
		final String warningMsg = inputJson.getSafeString(FlightInputConstants.MES_REV_WARN);
		String color = "";
		String CALLSIGN_FRAME = "OFF";
		String callback = "";

		boolean hasToBlink = false;

		final Optional<IRawData> track = Utils_LIS.getTrkFromFn(inputJson.getId());
		if (track.isPresent()) {
			final String TRACK_DATA = track.get().getSafeString(TrackInputConstants.RCS).trim();

			if (isabinoroute.equalsIgnoreCase(FlightInputConstants.ISABINOROUTE_TRUE)) {
				color = ColorConstants.CALLSIGN_WARNING_COLOR;
			}

			boolean EMERGENCY_RPAS = decodeEmergency(track);

			if (EMERGENCY_RPAS) {
				color = ColorConstants.RED;
			}

			if (template.contains(FlightOutputConstants.TEMPLATE_CONTROLLED)) {
				final int GMALARM = inputJson.getSafeInt("GMALARM");
				switch (GMALARM) {
				case 1:
					color = ColorConstants.RED;
					hasToBlink = true;
					break;
				case 2:
					color = ColorConstants.YELLOW;
					hasToBlink = true;
					break;
				default:
					break;

				}
			}

			if (!TRACK_DATA.isEmpty() && !TRACK_DATA.equals(TrackInputConstants.RCS_NULL)
					&& !TRACK_DATA.equals(TrackInputConstants.RCS_EMPTY) && !callsign.trim().equals(TRACK_DATA)) {
				CALLSIGN_FRAME = "ON";
			}
		}

		if (color.isEmpty()) {
			if (inputJson.getSafeBoolean(FlightInputConstants.POINT_OUT_TX)) {
				color = ColorConstants.WHITE;
				callback = "CONTEXT_MENU(MENU_FILE=POINT_OUT_TX.xml)";
			}
			if (inputJson.getSafeBoolean(FlightInputConstants.POINT_OUT_RX)) {
				color = ColorConstants.CYAN;
				callback = "CONTEXT_MENU(MENU_FILE=POINT_OUT_RX.xml)";
			}
		}

		if (inputJson.getSafeString(FlightInputConstants.MTS).equals("12")
				|| inputJson.getSafeString(FlightInputConstants.MTS).equals("13")) {
			callback = "CONTEXT_MENU(MENU_FILE=Notified_SMT.xml)";
		}

		else if (template.contains(FlightOutputConstants.TEMPLATE_NOTIFIED)) {

			if (warningRev.equals("SCO") && isActive.equals("true")
					&& (isInbound.equals("0") || isInbound.equals("1"))) {
				callback = "CONTEXT_MENU(MENU_FILE=Notified_SKC.xml)";

			} else if (inputJson.getSafeBoolean(FlightInputConstants.SIA)) {
				callback = "CONTEXT_MENU(MENU_FILE=Notified_UNSKP.xml)";
			}

		}

		else if (template.contains(FlightOutputConstants.TEMPLATE_TENTATIVE_AIS) && isLive.equals("true")) {
			callback = "EXTERNAL_ORDER(ORDER_ID=AOC , OBJECT_TYPE=AOC ,PREVIEW_NAME=quickAOC)";
		}
		if (warningRev.equals("HOP") && warningRevColor.equals(CommonConstants.COLOR_ORANGE)) {
			callback = "EXTERNAL_ORDER(ORDER_ID=ROF , OBJECT_TYPE=ROF ,  PREVIEW_NAME=quickROF )";
		}
		if (warningRev.equals("COF") && warningMsg.equals("COF")) {
			callback = "EXTERNAL_ORDER(ORDER_ID=AOC, OBJECT_TYPE=AOC,  PREVIEW_NAME=quickAOC)";
		}
		dataNode.addLine(FlightOutputConstants.CALLSIGN, callsign).setAttribute(EdmModelKeys.Attributes.COLOR, color)
				.setAttribute(EdmModelKeys.Attributes.MOD_BLINK, (hasToBlink ? "ON" : "OFF"))
				.setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, (!callback.isEmpty() ? callback : ""));

		dataNode.addLine("FRAME_CALLSIGN", CALLSIGN_FRAME);

		// Set the SSR code field from the Mode3A / SSR code
		// TODO: fields marked as "_BV4" will be moved to a dedicated GiFork plugin in the future
		if (track.isPresent()) {
			String mode3A = track.get().getSafeString("MODE_3A");
			dataNode.addLine(FlightOutputConstants.SSR_CODE_BV4, mode3A)
					.setAttribute(EdmModelKeys.Attributes.FLIGHT_DASHBAORD_COLOR, "#00F3F3");
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
		String service_name = FlightOutputConstants.CALLSIGN;
		return service_name;
	}

	/**
	 * Decode emergency.
	 *
	 * @param jtrack the jtrack
	 * @return true, if successful
	 */
	private static boolean decodeEmergency(Optional<IRawData> jtrack) {
		boolean retValue;
		boolean c2Loss = false;
		boolean c2Loss_fromMode3A = false;

		boolean rst_validity = jtrack.get().getSafeBoolean(TrackInputConstants.RST_VALIDITY);
		if (rst_validity) {
			c2Loss = jtrack.get().getSafeBoolean(TrackInputConstants.EMG_RPAS_C2L);
		}
		if (ConfigurationFile.getProperties(USE_MODE3A_FOR_C2_LOSS).isBlank()
			|| ConfigurationFile.getBoolProperties(USE_MODE3A_FOR_C2_LOSS)) {
			// se la property USE_MODE3A_FOR_C2L è attiva, allora il C2L viene segnalato anche
			// quando il codice Mode3A è A7400
			// (codice usato per indicare perdita del C2)
			// vedi ENAC_AIP_ENR_1.6 par.
			String track_mode_3A = jtrack.get().getSafeString("MODE_3A");
			if (!track_mode_3A.isEmpty()) {
				if (track_mode_3A.equals("A7400")) {
					c2Loss_fromMode3A = true;
				}
			}
		}
		retValue = c2Loss || c2Loss_fromMode3A;

		return retValue;

	}

}
