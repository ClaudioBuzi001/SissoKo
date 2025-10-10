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
package analyzer;

import java.util.Map;
import java.util.Optional;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.leonardo.infrastructure.Convert;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxiliary.mtcd.MtcdInputConstants;
import processorNew.MTCDOXfNotify;
import processorNew.MTCDProcessor;

/**
 * The Class AnalyzerMtcd.
 */
public class AnalyzerMtcd implements IObserver {

	/**
	 * Instantiates a new analyzer mtcd.
	 */
	public AnalyzerMtcd() {
		StorageManager.register(this, DataType.MTCD_ITEM_NOTIFY.name());
		StorageManager.register(this, DataType.MTCD_OXF_TIME_NOTIFY.name());
		StorageManager.register(this, DataType.ENV_OWN.name());
		StorageManager.register(this, DataType.MTCD_INTERACTION_ITEM_NOTIFY.name());
		StorageManager.register(this, DataType.BB_CALLSIGN_FLIGHT.name());
	}

	/**
	 * Update.
	 *
	 * @param mtcd the mtcd
	 */
	@Override
	public void update(final IRawData mtcd) {
		if (mtcd.getOperation() == Operation.INSERT || mtcd.getOperation() == Operation.UPDATE) {
			switch (DataType.valueOf(mtcd.getType())) {
			case MTCD_ITEM_NOTIFY:
				updateForMtcdItemNotify(mtcd);
				break;
			case MTCD_OXF_TIME_NOTIFY:
			case ENV_OWN:
				MTCDOXfNotify.processOxfTimeNotify();
				break;
			default:
				break;
			}
		} else if (mtcd.getOperation() == Operation.DELETE) {

			switch (DataType.valueOf(mtcd.getType())) {
			case MTCD_ITEM_NOTIFY:
				updateForMtcdItemNotify(mtcd);
				break;
			case BB_CALLSIGN_FLIGHT:
				updateMtcdData(mtcd.getId());
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * Updates the mtcd data based on the callsign
	 * @param cs
	 */
	public static void updateMtcdData(final String cs) {
		
		Map<String, IRawData> listConflcit = MTCDProcessor.getAllCallsignConflict(cs);
		
		
		for (var conflict : listConflcit.values()) {
			MTCDProcessor.process(conflict, false);
		}

	}

	/**
	 * Update for mtcd item notify.
	 *
	 * @param mtcd the mtcd
	 */
	private static void updateForMtcdItemNotify(final IRawData mtcd) {

		if (!mtcd.getSafeString("CONFLICT_OR_RISK").equals("COP")) {
			sendXmlSendPPDGraphicToViewer(mtcd);
		}
	}

	/**
	 * Costruisce e mette in coda il msg con i dati del conflitto MTCD utilizzato anche da PPD.
	 *
	 * @param json the json
	 */
	private static void sendXmlSendPPDGraphicToViewer(final IRawData json) {
		final var conflictNumber = json.getSafeString("CONFLICT_ID");
		final var rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(
				json.getOperation() == Operation.DELETE ? Operation.DELETE : Operation.INSERT, json.getType());

		if (json.getOperation() == Operation.DELETE) {
			objectNode.addLine("ID", conflictNumber);
			objectNode.addLine("CONFLICT_NUMBER", conflictNumber);
			ManagerBlackboard.addJVOutputList(rootData);

		} else {
			final String conflictType = json.getSafeString("CONFLICT_TYPE");
			final String conflictOrRisk = json.getSafeString("CONFLICT_OR_RISK");

			final String flightCallsign1 = json
					.getSafeString(MtcdInputConstants.FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_1);
			if (BlackBoardUtility.getDataOpt(DataType.BB_CALLSIGN_FLIGHT.name(), flightCallsign1).isPresent()) {
				final String flightNum1 = BlackBoardUtility
						.getDataOpt(DataType.BB_CALLSIGN_FLIGHT.name(), flightCallsign1).get()
						.getSafeString("FLIGHT_NUM");

				final String flightCallsign2OrSua = json
						.getSafeString(MtcdInputConstants.FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_2);

				Optional<IRawData> jsonFlight2 = Optional.empty();
				if (!json.getSafeString("CONFLICT_TYPE").equals("SUA")) {
					jsonFlight2 = BlackBoardUtility.getDataOpt(DataType.BB_CALLSIGN_FLIGHT.name(),
							flightCallsign2OrSua);
					if (jsonFlight2.isPresent()) {
						final String flightNum2 = jsonFlight2.get().getSafeString("FLIGHT_NUM");
						objectNode.addLine("FLIGHT_NUM2", flightNum2);
					}
				}

				String conflictTemplate = "aircraft_to_airspace";
				if (jsonFlight2.isPresent()) {
					conflictTemplate = getMtcdTemplate(jsonFlight2.get(), conflictType, conflictOrRisk);
				}

				final String leftClickOnConflict = "SHOW_CONFLICT_TRAJECTORY(CALLSIGN1=" + flightCallsign1
						+ ", CALLSIGN2=" + flightCallsign2OrSua + " , CONFLICT_ID=" + conflictNumber
						+ ", COLOR=#ff0000)";
				final String leftClickOnTrack1 = "SHOW_CONFLICT_TRAJECTORY(CALLSIGN=" + flightCallsign1
						+ " , CONFLICT_ID=" + conflictNumber + ")";
				final String leftClickOnTrack2 = "SHOW_CONFLICT_TRAJECTORY(CALLSIGN=" + flightCallsign2OrSua
						+ " , CONFLICT_ID=" + conflictNumber + ")";

				objectNode.addLine("ID", conflictNumber).setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK,
						leftClickOnConflict);

				objectNode.addLine("FLIGHT_NUM1", flightNum1);

				objectNode.addLine("CONFLICT_NUMBER", conflictNumber)
						.setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, leftClickOnConflict);
				objectNode.addLine("CONFLICT_OR_RISK", conflictOrRisk);
				objectNode.addLine("CONFLICT_TYPE", conflictType);
				objectNode.addLine("TEMPLATE", conflictTemplate);
				objectNode.addLine("CALLSIGN_1", flightCallsign1)
						.setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, leftClickOnTrack1);
				objectNode.addLine("CALLSIGN_2", flightCallsign2OrSua)
						.setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, leftClickOnTrack2);

				objectNode.addLine("MIN_HORIZONTAL_DISTANCE", json.getSafeString("TMD_DISTANCE_DOUBLE"));

				final String tmdHours = json.getSafeString("TMD_HOURS");
				final String tmdMinutes = json.getSafeString("TMD_MINUTES");
				final String ttaHours = json.getSafeString("TTA_HOURS");
				final String ttaMinutes = json.getSafeString("TTA_MINUTES");
				final String tfiHours = json.getSafeString("TFI_HOURS");
				final String tfiMinutes = json.getSafeString("TFI_MINUTES");

				objectNode.addLine("TMD_ABSOLUTE_MINUTES",
						Integer.toString(60 * Convert.toInteger(tmdHours, 0) + Convert.toInteger(tmdMinutes, 0)));
				objectNode.addLine("TTA_ABSOLUTE_MINUTES",
						Integer.toString(60 * Convert.toInteger(ttaHours, 0) + Convert.toInteger(ttaMinutes, 0)));
				objectNode.addLine("TFI_ABSOLUTE_MINUTES",
						Integer.toString(60 * Convert.toInteger(tfiHours, 0) + Convert.toInteger(tfiMinutes, 0)));
				objectNode.addLine("OXF", json.getSafeString("OXF"));

				objectNode.addLine("TMD_HOURS", tmdHours);
				objectNode.addLine("TMD_MINUTES", tmdMinutes);
				objectNode.addLine("TTA_HOURS", ttaHours);
				objectNode.addLine("TTA_MINUTES", ttaMinutes);

				ManagerBlackboard.addJVOutputList(rootData);
			}
		}

	}

	/**
	 * Gets the mtcd template.
	 *
	 * @param jsonFlight     the json flight
	 * @param conflictType   the conflict type
	 * @param conflictOrRisk the conflict or risk
	 * @return the mtcd template
	 */
	private static String getMtcdTemplate(final IRawData jsonFlight, final String conflictType,
			final String conflictOrRisk) {
		var template = "aircraft_to_airspace";

		if (!jsonFlight.isEmpty() && !conflictType.equals("SUA")) {
			switch (conflictOrRisk) {
			case "RISK_A":
			case "RISK_B":
				template = "risks_of_conflict_of_aircraft_to_aircraft";
				break;
			case "VAW":
				template = "aircraft_to_aircraft_vertical_context";
				break;
			case "HAW":
				template = "aircraft_to_aircraft_horizontal_context";
				break;

			case "COP":
				break;
			case "CONFLICT":
			default:
				template = "conflicts_of_aircraft_to_aircraft";
				break;
			}
		}

		return template;
	}

	/**
	 *
	 */
	public void register() {
		StorageManager.register(this, DataType.MTCD_ITEM_NOTIFY.name());
		StorageManager.register(this, DataType.MTCD_OXF_TIME_NOTIFY.name());
		StorageManager.register(this, DataType.ENV_OWN.name());
		StorageManager.register(this, DataType.MTCD_INTERACTION_ITEM_NOTIFY.name());

	}
}
