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
package processorNew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;

import analyzer.AnalyzerLabel;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.cpdlc.CpdlcAggregatorMap;
import auxiliary.cpdlc.CpdlcAliasMap;
import auxiliary.cpdlc.CpdlcBlackMap;
import auxiliary.cpdlc.CpdlcInputConstants;
import auxiliary.flight.FlightInputConstants;
import common.CommonConstants;
import common.Utils;

/**
 * The Class CPDLCProcessor.
 */
public enum CPDLCProcessor {
	;

	/** The Constant CpdlcAliasMap. */
	private final static CpdlcAliasMap cpclcAliasMap = new CpdlcAliasMap();

	/** The Constant CpdlcBlackMap. */
	private final static CpdlcBlackMap cpclcBlackMap = new CpdlcBlackMap();

	/** The Constant CpdlcAggregatorMap. */
	public final static CpdlcAggregatorMap cpdlcAggregatorMap = new CpdlcAggregatorMap();

	/** The map CPDLC label dialogues. */
	public static final HashMap<String, ArrayList<String>> mapCPDLCLabelDialogues = new HashMap<>();

	/**
	 * Process.
	 *
	 * @param cpIRawData the cp I raw data
	 */
	public static void process(final IRawData cpIRawData) {
		final String CPDLC_ID = cpIRawData.getSafeString("CPDLC_ID");
		final String[] newMesg = CPDLC_ID.split("_");
		final String flightNum = newMesg[0];
		final var flight = BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(), flightNum);
		final var track = Utils_LIS.getTrkFromFn(flightNum);
		if (track.isPresent() && flight.isPresent()) {
			XMLCreation(track.get(), flight.get());
		}

		if (cpIRawData.getSafeString("STATUS_DIALOG").isEmpty() && !cpIRawData.getSafeString("response").equals("N")) {
			if (!mapCPDLCLabelDialogues.containsKey(flightNum)) {
				mapCPDLCLabelDialogues.put(flightNum, new ArrayList<>());
			}
			if (!mapCPDLCLabelDialogues.get(flightNum).contains(CPDLC_ID)) {
				mapCPDLCLabelDialogues.get(flightNum).add(CPDLC_ID);
			}
		}
	}

	/**
	 * XML creation.
	 *
	 * @param track  the track
	 * @param flight the flight
	 */
	private static void XMLCreation(final IRawData track, final IRawData flight) {
		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);

		Utils.addElementTrack(track, objectNode);

		if (!track.isEmpty()) {
			Utils.addTemplate(Optional.of(track), Optional.of(flight), objectNode);
		}

		final var cpdlcFlight = RawDataFactory.create();
		cpdlcFlight.put(CpdlcInputConstants.CPDLC_DOWNLINK_SPEED_DIALOGUE_STATUS,
				flight.getSafeString(CpdlcInputConstants.CPDLC_DOWNLINK_SPEED_DIALOGUE_STATUS));
		cpdlcFlight.put(CpdlcInputConstants.CPDLC_DOWNLINK_VERTICAL_DIALOGUE_STATUS,
				flight.getSafeString(CpdlcInputConstants.CPDLC_DOWNLINK_VERTICAL_DIALOGUE_STATUS));
		cpdlcFlight.put(CpdlcInputConstants.CPDLC_DOWNLINK_ROUTE_DIALOGUE_STATUS,
				flight.getSafeString(CpdlcInputConstants.CPDLC_DOWNLINK_ROUTE_DIALOGUE_STATUS));
		cpdlcFlight.put(CpdlcInputConstants.CPDLC_UPLINK_SPEED_DIALOGUE_STATUS,
				flight.getSafeString(CpdlcInputConstants.CPDLC_UPLINK_SPEED_DIALOGUE_STATUS));
		cpdlcFlight.put(CpdlcInputConstants.CPDLC_UPLINK_VERTICAL_DIALOGUE_STATUS,
				flight.getSafeString(CpdlcInputConstants.CPDLC_UPLINK_VERTICAL_DIALOGUE_STATUS));
		cpdlcFlight.put(CpdlcInputConstants.CPDLC_UPLINK_ROUTE_DIALOGUE_STATUS,
				flight.getSafeString(CpdlcInputConstants.CPDLC_UPLINK_ROUTE_DIALOGUE_STATUS));
		cpdlcFlight.put(CpdlcInputConstants.CPDLC_UPLINK_CONTACT_DIALOGUE_STATUS,
				flight.getSafeString(CpdlcInputConstants.CPDLC_UPLINK_CONTACT_DIALOGUE_STATUS));
		cpdlcFlight.put(CpdlcInputConstants.CPDLC_UPLINK_SURV_DIALOGUE_STATUS,
				flight.getSafeString(CpdlcInputConstants.CPDLC_UPLINK_SURV_DIALOGUE_STATUS));
		cpdlcFlight.put(FlightInputConstants.TEMPLATE_STRING,
				flight.getSafeString(FlightInputConstants.TEMPLATE_STRING));

		cpdlcFlight.put(FlightInputConstants.PWROR, flight.getSafeString(FlightInputConstants.PWROR));
		cpdlcFlight.put(FlightInputConstants.PWVOR, flight.getSafeString(FlightInputConstants.PWVOR));
		cpdlcFlight.setId(flight.getSafeString(CommonConstants.FLIGHT_NUM));

		AnalyzerLabel.analyze(cpclcBlackMap, cpdlcAggregatorMap, cpclcAliasMap, cpdlcFlight, objectNode);

		objectNode.addLine(CommonConstants.FLIGHT_NUM, flight.getSafeString(CommonConstants.FLIGHT_NUM));

		Utils.doTCPClientSender(rootData);

	}

	/**
	 * analyzeDialgue.
	 *
	 * @param flightNum     the flight num
	 * @param rawdataFlight
	 */
	static void analyzeDialgue(String flightNum, IRawData rawdataFlight) {
		if (mapCPDLCLabelDialogues.get(flightNum) != null) {
			final ArrayList<String> listDialogues = mapCPDLCLabelDialogues.get(flightNum);
			final ArrayList<String> listDialoguesToRemove = new ArrayList<>();
			for (final String cpdlcId : listDialogues) {
				final var bbkSource = BlackBoardUtility.getDataOpt(DataType.CPDLC.name(), cpdlcId);
				if (bbkSource.isPresent() && !bbkSource.get().getSafeString("response").equals("N")) {
					process(bbkSource.get());
					if (!bbkSource.get().getSafeString("STATUS_DIALOG").isEmpty()) {
						if (bbkSource.get().getSafeString("DirectionID").equals("downlink") || (bbkSource.get()
								.getSafeString("DirectionID").equals("uplink")
								&& checkStatusUplinkClosed(bbkSource.get().getSafeString("classeId"), rawdataFlight))) {
							listDialoguesToRemove.add(cpdlcId);
						}
					}
				}
			}
			if (!listDialoguesToRemove.isEmpty()) {
				for (final String cpdlcDialogue : listDialoguesToRemove) {
					mapCPDLCLabelDialogues.get(flightNum).remove(cpdlcDialogue);
				}
			}
			if (mapCPDLCLabelDialogues.get(flightNum).isEmpty()) {
				mapCPDLCLabelDialogues.remove(flightNum);
			}
		}
	}

	/**
	 * checkStatusUplinkClosed.
	 *
	 * @param dialogueClass the dialogue class
	 * @param flight        the flight data
	 * @return status closed true/false
	 */
	private static boolean checkStatusUplinkClosed(final String dialogueClass, final IRawData flight) {
		boolean statusClosed = false;
		String status = "-1";
		switch (dialogueClass) {
		case "2":

			status = flight.getSafeInt("VCUDS") + "";
			break;
		case "6":

			status = flight.getSafeInt("SCUDS") + "";
			break;
		case "5":

			status = flight.getSafeInt("RMUDS") + "";
			break;
		case "7":

			status = flight.getSafeInt("CMUDS") + "";
			break;
		case "13":

			status = flight.getSafeInt("TCUDS") + "";
			break;
		default:
			break;
		}

		if (status.equals("-1") || status.equals("5") || status.equals("17")) {
			statusClosed = true;
		}
		return statusClosed;
	}

	/**
	 * XML creation.
	 *
	 * @param track            the track
	 * @param flight           the flight
	 * @param cpdlcMapUpLink   the cpdlc map up link
	 * @param cpdlcMapDownLink the cpdlc map down link
	 */
	/*
	 * private static void XMLCreation(IRawData track, IRawData flight, IRawData cpdlcMapUpLink,
	 * IRawData cpdlcMapDownLink) { DataRoot rootData = DataRoot.createMsg(); var objectNode =
	 * rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
	 *
	 * toXml(flight, cpdlcMapUpLink, cpdlcMapDownLink, objectNode); Utils.addElementTrack(track,
	 * objectNode); Utils.doTCPClientSender(rootData); }
	 */

	/**
	 * To xml.
	 *
	 * @param jsonFlight         the json flight
	 * @param cpdlc_map_downlink the cpdlc map downlink
	 * @param cpdlc_map_Uplink   the cpdlc map uplink
	 * @param objectNode         the object node
	 */
	/*
	 * private static void toXml(IRawData jsonFlight, IRawData cpdlc_map_downlink, IRawData
	 * cpdlc_map_Uplink, HeaderNode objectNode) { downlink(jsonFlight, cpdlc_map_downlink, objectNode);
	 * uplink(jsonFlight, cpdlc_map_Uplink, objectNode);
	 *
	 * }
	 */

	/**
	 * Downlink.
	 *
	 * @param jsonFlight         the json flight
	 * @param cpdlc_map_downlink the cpdlc map downlink
	 * @param objectNode         the object node
	 */
	/*
	 * private static void downlink(IRawData jsonFlight, IRawData cpdlc_map_downlink, HeaderNode
	 * objectNode) { String CPDLC_RQ_value = ""; String CPDLC_RQ_color = ""; String CPDLC_RQ_blink = "";
	 * String CPDLC_RQ_DD_value = ""; String CPDLC_RQ_DD_color = "";
	 *
	 * String SCDDS = jsonFlight.getSafeString("SCDDS"); String RMDDS =
	 * jsonFlight.getSafeString("RMDDS"); String VCDDS = jsonFlight.getSafeString("VCDDS");
	 *
	 * var cpdlcArray = cpdlc_map_downlink.getSafeRawDataArray("CPDLC_LIST");
	 *
	 * for (int i = 0; i < cpdlcArray.size(); i++) { var jCpdlcDown = cpdlcArray.get(i);
	 *
	 * String fieldName = jCpdlcDown.getSafeString("CPDLC_RQ"); String fieldValue = ""; String valueOUT
	 * = ""; String valueColor = ""; String modeBlink = "OFF";
	 *
	 * switch (fieldName) {
	 *
	 * case "LEVEL": fieldValue = VCDDS; break; case "SPD": fieldValue = SCDDS; break; case "DCT":
	 * fieldValue = RMDDS; break; default: break; }
	 *
	 * switch (fieldValue) { case "1": valueOUT = "*"; valueColor = "#00FFFF"; break;
	 *
	 * case "2": modeBlink = "ON"; break;
	 *
	 * case "3": valueOUT = "||"; valueColor = "#FFFFFF"; break;
	 *
	 * case "13": valueOUT = "||"; valueColor = "#FFFFFF"; modeBlink = "ON"; break; default: break; }
	 *
	 * switch (fieldName) { case "LEVEL": case "SPD": case "DCT": CPDLC_RQ_value =
	 * jCpdlcDown.getSafeString("LABEL"); CPDLC_RQ_color = ColorConstants.GREEN; CPDLC_RQ_DD_value =
	 * valueOUT; CPDLC_RQ_DD_color = valueColor; CPDLC_RQ_blink = modeBlink; break;
	 *
	 * default: break; } }
	 *
	 * objectNode.addLine("CPDLC_RQ_DD", CPDLC_RQ_DD_value) .setAttribute(EdmModelKeys.Attributes.COLOR,
	 * CPDLC_RQ_DD_color).setAttributeIf( !CPDLC_RQ_value.isEmpty(), EdmModelKeys.Attributes.SHOW,
	 * EdmModelKeys.Attributes.Show.FORCED);
	 *
	 * objectNode.addLine("CPDLC_RQ", CPDLC_RQ_value) .setAttribute(EdmModelKeys.Attributes.COLOR,
	 * CPDLC_RQ_color) .setAttribute(EdmModelKeys.Attributes.MOD_BLINK, CPDLC_RQ_blink)
	 * .setAttributeIf(!CPDLC_RQ_value.isEmpty(), EdmModelKeys.Attributes.SHOW,
	 * EdmModelKeys.Attributes.Show.FORCED);
	 *
	 * }
	 */

	/**
	 * Uplink.
	 *
	 * @param jsonFlight       the json flight
	 * @param cpdlc_map_UpLink the cpdlc map up link
	 * @param objectNode       the object node
	 */
	/*
	 * private static void uplink(IRawData jsonFlight, IRawData cpdlc_map_UpLink, HeaderNode objectNode)
	 * { String RQ_CPDLC_CFL = "RQ_CPDLC_CFL"; String RQ_CPDLC_ARC = "RQ_CPDLC_ARC"; String
	 * RQ_CPDLC_SPEED = "RQ_CPDLC_SPEED"; String RQ_CPDLC_HDG = "RQ_CPDLC_HDG"; String RQ_CPDLC_CONTACT
	 * = "RQ_CPDLC_CONTACT"; String RQ_CPDLC_SQUAWK = "RQ_CPDLC_SQUAWK";
	 *
	 * String RQ_CPDLC_CFL_VALUE = ""; String RQ_CPDLC_ARC_VALUE = ""; String RQ_CPDLC_SPEED_VALUE = "";
	 * String RQ_CPDLC_HDG_VALUE = ""; String RQ_CPDLC_CONTACT_VALUE = ""; String RQ_CPDLC_SQUAWK_VALUE
	 * = "";
	 *
	 * String RQ_CPDLC_CFL_COLOR = ""; String RQ_CPDLC_ARC_COLOR = ""; String RQ_CPDLC_SPEED_COLOR = "";
	 * String RQ_CPDLC_HDG_COLOR = ""; String RQ_CPDLC_CONTACT_COLOR = ""; String RQ_CPDLC_SQUAWK_COLOR
	 * = ""; String show ; String VCUDS = jsonFlight.getSafeString("VCUDS"); String RMUDS =
	 * jsonFlight.getSafeString("RMUDS"); String SCUDS = jsonFlight.getSafeString("SCUDS"); String TCUDS
	 * = jsonFlight.getSafeString("TCUDS"); String CMUDS = jsonFlight.getSafeString("CMUDS"); String
	 * templateStr = jsonFlight.getSafeString(FlightInputConstants.TEMPLATE_STRING); var cpdlcArray =
	 * cpdlc_map_UpLink.getSafeRawDataArray("CPDLC_LIST");
	 *
	 * for (int i = 0; i < cpdlcArray.size(); i++) { var jCpdlcUp = cpdlcArray.get(i);
	 *
	 * String fieldName = jCpdlcUp.getSafeString("CPDLC_RQ"); String fieldValue = ""; String valueOUT =
	 * ""; String valueColor = ""; show = "";
	 *
	 *
	 *
	 *
	 * switch (fieldName) {
	 *
	 * case "RQ_CPDLC_CFL": fieldValue = VCUDS; break; case "RQ_CPDLC_ARC": fieldValue = VCUDS; break;
	 * case "RQ_CPDLC_SPEED": fieldValue = SCUDS; break; case "RQ_CPDLC_HDG": fieldValue = RMUDS; break;
	 * case "RQ_CPDLC_SQUAWK": fieldValue = CMUDS; break; case "RQ_CPDLC_CONTACT": fieldValue = TCUDS;
	 * break;
	 *
	 * default: break; }
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 * switch (fieldValue) { case "0": case "2": valueOUT = ">"; valueColor = "#FFFFFF"; break;
	 *
	 * case "1": valueOUT = "*"; valueColor = "#00FFFF"; break;
	 *
	 * case "3": case "13": valueOUT = "||"; valueColor = "#FFFFFF"; break;
	 *
	 * case "4": case "18": case "19": valueOUT = "x"; valueColor = "#FFFF00"; break;
	 *
	 * case "6": case "8": case "9": case "16": case "20": valueOUT = "#"; valueColor = "#FFA500";
	 * break;
	 *
	 * case "7":
	 *
	 *
	 *
	 * valueOUT = "#"; valueColor = "#FFA500"; break;
	 *
	 * case "10": break; case "11":
	 *
	 * break; case "12":
	 *
	 * break;
	 *
	 * case "14":
	 *
	 * break; case "15": break;
	 *
	 * case "17": break;
	 *
	 * case "21": valueOUT = "#"; valueColor = "#FFFFFF"; break; default: break; }
	 *
	 * switch (fieldName) {
	 *
	 * case "RQ_CPDLC_CFL": RQ_CPDLC_CFL_VALUE = valueOUT; RQ_CPDLC_CFL_COLOR = valueColor; break; case
	 * "RQ_CPDLC_ARC": RQ_CPDLC_ARC_VALUE = valueOUT; RQ_CPDLC_ARC_COLOR = valueColor; break; case
	 * "RQ_CPDLC_SPEED": RQ_CPDLC_SPEED_VALUE = valueOUT; RQ_CPDLC_SPEED_COLOR = valueColor; break; case
	 * "RQ_CPDLC_HDG": RQ_CPDLC_HDG_VALUE = valueOUT; RQ_CPDLC_HDG_COLOR = valueColor; break;
	 *
	 * case "RQ_CPDLC_CONTACT": RQ_CPDLC_CONTACT_VALUE = valueOUT; RQ_CPDLC_CONTACT_COLOR = valueColor;
	 * break; case "RQ_CPDLC_SQUAWK": RQ_CPDLC_SQUAWK_VALUE = valueOUT; RQ_CPDLC_SQUAWK_COLOR =
	 * valueColor; break; default: break; } }
	 *
	 * if (!RQ_CPDLC_CFL_VALUE.isEmpty() &&
	 * templateStr.contains(FlightOutputConstants.TEMPLATE_CONTROLLED)) { show = "forced"; }else { show
	 * = "optional"; }
	 *
	 * objectNode.addLine(RQ_CPDLC_CFL, RQ_CPDLC_CFL_VALUE) .setAttribute(EdmModelKeys.Attributes.COLOR,
	 * RQ_CPDLC_CFL_COLOR) .setAttribute(EdmModelKeys.Attributes.SHOW, show);
	 *
	 * if (!RQ_CPDLC_ARC_VALUE.isEmpty() &&
	 * templateStr.contains(FlightOutputConstants.TEMPLATE_CONTROLLED)) { show = "forced"; }else { show
	 * = "optional"; } objectNode.addLine(RQ_CPDLC_ARC, RQ_CPDLC_ARC_VALUE)
	 * .setAttribute(EdmModelKeys.Attributes.COLOR, RQ_CPDLC_ARC_COLOR)
	 * .setAttribute(EdmModelKeys.Attributes.SHOW, show);
	 *
	 * if (!RQ_CPDLC_SPEED_VALUE.isEmpty() &&
	 * templateStr.contains(FlightOutputConstants.TEMPLATE_CONTROLLED)) { show = "forced"; }else { show
	 * = "optional"; } objectNode.addLine(RQ_CPDLC_SPEED, RQ_CPDLC_SPEED_VALUE)
	 * .setAttribute(EdmModelKeys.Attributes.COLOR, RQ_CPDLC_SPEED_COLOR)
	 * .setAttribute(EdmModelKeys.Attributes.SHOW, show);
	 *
	 * if (!RQ_CPDLC_HDG_VALUE.isEmpty() &&
	 * templateStr.contains(FlightOutputConstants.TEMPLATE_CONTROLLED)) { show = "forced"; }else { show
	 * = "optional"; } objectNode.addLine(RQ_CPDLC_HDG, RQ_CPDLC_HDG_VALUE)
	 * .setAttribute(EdmModelKeys.Attributes.COLOR, RQ_CPDLC_HDG_COLOR)
	 * .setAttribute(EdmModelKeys.Attributes.SHOW, show);
	 *
	 * objectNode.addLine(RQ_CPDLC_CONTACT, RQ_CPDLC_CONTACT_VALUE)
	 * .setAttribute(EdmModelKeys.Attributes.COLOR, RQ_CPDLC_CONTACT_COLOR);
	 *
	 * objectNode.addLine(RQ_CPDLC_SQUAWK, RQ_CPDLC_SQUAWK_VALUE)
	 * .setAttribute(EdmModelKeys.Attributes.COLOR, RQ_CPDLC_SQUAWK_COLOR);
	 *
	 * }
	 */

}
