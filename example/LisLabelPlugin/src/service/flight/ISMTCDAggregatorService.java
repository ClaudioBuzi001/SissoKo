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

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.leonardo.infrastructure.Pair;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import common.ColorConstants;
import common.CommonConstants;
import common.Utils;

/**
 * The Class ISMTCDAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class ISMTCDAggregatorService implements IAggregatorService {

	/** the service name */
	private final static String serviceName = "IS_MTCD";

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final String isMTCD = inputJson.getSafeString(CommonConstants.MTCD_FILTER);
		final String callsign = inputJson.getSafeString(CommonConstants.CALLSIGN);
		final String showMapSua = "MAP_SET_VISIBLE(MAP_TYPE=SNET, MAP_SUBTYPE=APWSUA, MAP_NAME="
				+ inputJson.getSafeString(FlightInputConstants.VSN) + ",SHOW=SWITCH";
		String soundMTCD = "";

		String data;
		String dataShow = "";
		String mtcdPriorityType = "";
		String color;
		String loav = "";
		String dataSUA = "";
		String colorSUA = "";
		boolean atLeastNotSuaConflict = false;

		String MTCD_FRAME = "OFF";

		final String is_sua = inputJson.getSafeString(FlightInputConstants.IS_SUA);
		if (is_sua.equals("1")) {
			colorSUA = ColorConstants.YELLOW;
			dataShow = showMapSua;
			dataSUA = "1";
		}

		if (!isMTCD.isEmpty()) {
			data = isMTCD;
			color = ColorConstants.GRAY;
		} else {
			String prConflict = "";

			final HashMap<String, IRawData> totalMap = getAllCallsignConflict(callsign);
			for (final Entry<String, IRawData> mtcdID : totalMap.entrySet()) {
				Optional<IRawData> jsonMTCD = BlackBoardUtility.getDataOpt(DataType.MTCD_ITEM_NOTIFY.name(),
						mtcdID.getKey());
				final String type = jsonMTCD.isPresent() ? jsonMTCD.get().getSafeString("CONFLICT_OR_RISK") : "";
				if (!type.equals("COP")) {
					if (!type.equals("SUA") && !prConflict.equals("CONFLICT")) {
						prConflict = checkPriorityMTCD(type);
					}

					final String appoDataSUA = jsonMTCD.get().getSafeString("SUA");
					if (appoDataSUA.equals("1")) {
						colorSUA = ColorConstants.ORANGE;

						dataShow = "SHOW_CONFLICT_TRAJECTORY(CALLSIGN=" + callsign + ", MTCD_TYPE=SUA)";
						dataSUA = appoDataSUA;
					} else {
						atLeastNotSuaConflict = true;
					}
				}
				if (type.equals("COP")) {
					loav = jsonMTCD.get().getSafeString("COP_NAME");
				}

				final var sectorList = jsonMTCD.get().getSafeRawDataArray("DISTRIBUTION_LIST_SECTORS");
				for (final com.gifork.commons.data.IRawDataElement colItem : sectorList) {
					final int sectId = getSectorID();
					final int sop = colItem.getSafeInt("SOP");
					if (sop == sectId) {
						if (colItem.getSafeInt("ACK_WARNING_MASK") == 0) {
							MTCD_FRAME = "OFF";
						} else {
							MTCD_FRAME = "ON";
						}
						break;
					}
				}
			}

			if (atLeastNotSuaConflict) {
				data = prConflict;
				mtcdPriorityType = prConflict;
			} else {

				data = "";
			}

			switch (prConflict) {
			case "CONFLICT":
				color = ColorConstants.RED;
				break;

			case "VAW":
			case "HAW":
				color = ColorConstants.MAGENTA;
				break;

			case "RISK_A":
			case "RISK_B":
				color = ColorConstants.BLU;
				break;

			default:
				color = "";
				break;
			}

			if (!prConflict.isEmpty() && Utils.IsSoundFilter("MTCD")) {
				soundMTCD = CommonConstants.IS_SOUND_MTCD_Value;
			}

			if (inputJson.getSafeBoolean("GMSTATUS", false)) {
				data = "GM";
			}

		}

		dataNode.addLine(FlightOutputConstants.MTCD_PRIORITY_TYPE, mtcdPriorityType);
		dataNode.addLine(FlightOutputConstants.MTCD_LOAV, loav);

		dataNode.addLine(FlightOutputConstants.IS_MTCD, data).setAttribute(EdmModelKeys.Attributes.COLOR, color)
				.setAttributeIf(!data.isEmpty(), EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK,
						"SHOW_CONFLICT_TRAJECTORY(CALLSIGN=" + callsign + ", MTCD_TYPE=FLIGHT)");
		dataNode.addLine(FlightOutputConstants.IS_SUA, dataSUA).setAttribute(EdmModelKeys.Attributes.COLOR, colorSUA)
				.setAttributeIf(!dataSUA.isEmpty(), EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, dataShow);
		dataNode.addLine(CommonConstants.IS_SOUND_MTCD, soundMTCD);

		dataNode.addLine("FRAME_MTCD", MTCD_FRAME);
	}

	/**
	 * Check priority MTCD.
	 *
	 * @param riskConflict the risk conflict
	 * @return the string
	 */

	private static String checkPriorityMTCD(final String riskConflict) {

		if (!riskConflict.isEmpty()) {
			switch (riskConflict) {
			case "CONFLICT":
			case "VAW":
			case "HAW":
			case "RISK_A":
			case "RISK_B":
				return riskConflict;
			}

		}
		return riskConflict;
	}

	/**
	 * Gets the all callsign conflict.
	 *
	 * @param callsign the callsign
	 * @return the all callsign conflict
	 */
	private static HashMap<String, IRawData> getAllCallsignConflict(final String callsign) {

		final var selectedMTCDList = BlackBoardUtility.getSelectedData(DataType.MTCD_ITEM_NOTIFY.name(),
				new Pair<>("FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_1", callsign));

		final var selectedMTCDList2 = BlackBoardUtility.getSelectedData(DataType.MTCD_ITEM_NOTIFY.name(),
				new Pair<>("FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_2", callsign));

		final HashMap<String, IRawData> totalMap = new HashMap<>(selectedMTCDList);
		totalMap.putAll(selectedMTCDList2);

		return totalMap;
	}

	/**
	 * Gets the sector ID.
	 *
	 * @return the sector ID
	 */
	private static int getSectorID() {
		final Optional<IRawData> json = BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name());
		return json.map(iRawData -> iRawData.getSafeInt("OWNP")).orElse(-1);

	}

}
