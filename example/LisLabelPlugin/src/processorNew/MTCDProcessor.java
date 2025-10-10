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
import java.util.Map;
import java.util.Optional;

import com.fourflight.WP.ECI.edm.DataNode;
import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.EdmModelKeys.TrajectoryPoint;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataArray;
import com.gifork.commons.data.IRawDataElement;
import com.leonardo.infrastructure.Pair;
import com.leonardo.infrastructure.Strings;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.mtcd.MtcdInputConstants;
import common.CommonConstants;
import common.Utils;

/**
 * The Class MTCDProcessor.
 */
public enum MTCDProcessor {
	;

	/**
	 * To xml.
	 *
	 * @param flight     the flight
	 * @param objectNode the object node
	 */

	private static void toXml(final IRawData flight, final HeaderNode objectNode) {

		final String callsign = flight.getSafeString(CommonConstants.CALLSIGN);
		final var list = StorageManager.getItemsStorage(DataType.MTCD_ITEM_NOTIFY.name());

		ArrayList<DataNode> listNode = new ArrayList<>();

		for (final var entry : list.entrySet()) {

			final IRawData mtcd_item = entry.getValue();

			final String callsign1 = mtcd_item.getSafeString("FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_1");
			final String callsign2 = mtcd_item.getSafeString("FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_2");

			String arrayName = "";
			if (callsign.equals(callsign1)) {
				arrayName = "FLIGHT_TO_FLIGHT_CONFLICT_POINT_1";
			} else if (callsign.equals(callsign2)) {
				arrayName = "FLIGHT_TO_FLIGHT_CONFLICT_POINT_2";
			}

			if (!arrayName.isEmpty()) {
				class point {
					private String lat;
					private String lon;
					private String eto;
					private int elo;
					private String routePointId;
				}
				final ArrayList<point> filteredFlightTrj = new ArrayList<>();

				final var flightToFlightConflictPoint = mtcd_item.getSafeRawDataArray(arrayName);
				if (flightToFlightConflictPoint.size() > 1) {
					final var firstPoint = flightToFlightConflictPoint.get(0);

					final var lastPoint = flightToFlightConflictPoint.get(1);

					String Z1 = "";
					String Z2 = "";
					final String arrayNameExtended = "FLIGHT_TO_FLIGHT_EXTENDED_AIR_INVOLVED";

					if (mtcd_item.has(arrayNameExtended)) {
						final IRawDataArray flightToFlightConflictPointExtended = mtcd_item
								.getSafeRawDataArray(arrayNameExtended);
						IRawDataElement firstPointExtended = null;
						IRawDataElement lastPointExtended = null;
						if (callsign.equals(callsign1)) {
							firstPointExtended = flightToFlightConflictPointExtended.get(0);
							lastPointExtended = flightToFlightConflictPointExtended.get(1);
						} else if (callsign.equals(callsign2)) {
							firstPointExtended = flightToFlightConflictPointExtended.get(1);
							lastPointExtended = flightToFlightConflictPointExtended.get(0);
						}

						if (firstPointExtended != null) {
							Z1 = Integer.toString(firstPointExtended.getSafeInt("LEVEL"));
						}
						if (lastPointExtended != null) {
							Z2 = Integer.toString(lastPointExtended.getSafeInt("LEVEL"));
						}
					}

					DataNode nodeList = new DataNode(EdmModelKeys.Conflicts.CONFLICT);
					nodeList.setAttribute(EdmModelKeys.Conflicts.ID,
							mtcd_item.getSafeString(MtcdInputConstants.CONFLICT_ID)) // ..
							.setAttribute(EdmModelKeys.Conflicts.CALLSIGN1, callsign1) // ..
							.setAttribute(EdmModelKeys.Conflicts.CALLSIGN2, callsign2) // ..
							.setAttribute(EdmModelKeys.Conflicts.TYPE, mtcd_item.getSafeString("CONFLICT_TYPE"))
							.setAttribute(EdmModelKeys.Conflicts.SUBTYPE, mtcd_item.getSafeString("CONFLICT_OR_RISK"))
							.setAttribute("encounter_type", mtcd_item.getSafeString("ENCOUNTER_TYPE"))
							.setAttribute("encounter_probability", mtcd_item.getSafeString("ENCOUNTER_PROBABILITY"));

					String TFI_HOURS = mtcd_item.getSafeString("TFI_HOURS");
					TFI_HOURS = Strings.concat("00".substring(TFI_HOURS.length()), TFI_HOURS);

					String TFI_MINUTES = mtcd_item.getSafeString("TFI_MINUTES");
					TFI_MINUTES = Strings.concat("00".substring(TFI_MINUTES.length()), TFI_MINUTES);

					String TFI_SECONDS = mtcd_item.getSafeString("TFI_SECONDS");
					TFI_SECONDS = Strings.concat("00".substring(TFI_SECONDS.length()), TFI_SECONDS);

					String TEI_HOURS = mtcd_item.getSafeString("TEI_HOURS");
					TEI_HOURS = Strings.concat("00".substring(TEI_HOURS.length()), TEI_HOURS);

					String TEI_MINUTES = mtcd_item.getSafeString("TEI_MINUTES");
					TEI_MINUTES = Strings.concat("00".substring(TEI_MINUTES.length()), TEI_MINUTES);

					String TEI_SECONDS = mtcd_item.getSafeString("TEI_SECONDS");
					TEI_SECONDS = Strings.concat("00".substring(TEI_SECONDS.length()), TEI_SECONDS);

					String TFI = Strings.concat(TFI_HOURS, ":", TFI_MINUTES, ":", TFI_SECONDS);
					String TEI = Strings.concat(TEI_HOURS, ":", TEI_MINUTES, ":", TEI_SECONDS);

					nodeList.setAttribute("TFI", TFI);
					nodeList.setAttribute("TEI", TEI);

					if (mtcd_item.getSafeString("CONFLICT_TYPE").equals("SUA")) {
						Optional<IRawData> mtcd_sua = BlackBoardUtility.getDataOpt(DataType.SNET_MAP_LIST.name(),
								Strings.concat("suadb_", callsign2));
						if (mtcd_sua.isPresent()) {
							final String hTop = String.valueOf(mtcd_sua.get().getSafeInt("hTop") / 100);
							Z1 = String.valueOf(mtcd_sua.get().getSafeInt("hBot") / 100);
							Z2 = hTop;
						}
					}

					nodeList.setAttribute("Z1", Z1);
					nodeList.setAttribute("Z2", Z2);

					int suffixConflictTrj = 0;

					nodeList.addNode(EdmModelKeys.ConflictPoint.POINT)
							.setAttribute(EdmModelKeys.ConflictPoint.ID, suffixConflictTrj)
							.setAttribute(EdmModelKeys.ConflictPoint.RPNTID, "")

							.setAttribute(EdmModelKeys.ConflictPoint.X, firstPoint.getSafeString("LON"))
							.setAttribute(EdmModelKeys.ConflictPoint.Y, firstPoint.getSafeString("LAT"))
							.setAttribute(EdmModelKeys.ConflictPoint.ELO, Z1)
							.setAttribute(EdmModelKeys.ConflictPoint.ETO, Strings.concat(TFI_HOURS, ":", TFI_MINUTES));

					int firstIndex = firstPoint.getSafeInt("TRJ_ID");

					final int seconIndex = lastPoint.getSafeInt("TRJ_ID");
					int secondIndex_1 = 0;

					if (seconIndex > 0) {
						secondIndex_1 = seconIndex - 1;
					}
					String latFpFlight, etoFpFlight, lonFpFlight, typeFpFlight, routePointId;

					Optional<IRawData> jsonFlTrnew = BlackBoardUtility.getDataOpt(DataType.FLIGHT_TRJ.name(),
							flight.getSafeString("FLIGHT_NUM"));
					if (jsonFlTrnew.isPresent()) {
						IRawDataArray rawTrjArra = jsonFlTrnew.get().getSafeRawDataArray(CommonConstants.TRJ_ARRAY);
						final int numPoint = rawTrjArra.size();
						for (int i = 0; i < numPoint; i++) {
							var elem = rawTrjArra.get(i);
							latFpFlight = elem.getSafeString(TrajectoryPoint.Y);
							lonFpFlight = elem.getSafeString(TrajectoryPoint.X);
							int eloFpFlight = elem.getSafeInt(TrajectoryPoint.LEV);
							etoFpFlight = Utils.fixTimeFormat(elem.getSafeString(TrajectoryPoint.ETO));
							typeFpFlight = elem.getSafeString(TrajectoryPoint.PT);
							routePointId = elem.getSafeString(TrajectoryPoint.NAME);

							if (typeFpFlight.equals("F") || typeFpFlight.equals("X") || typeFpFlight.equals("S")
									|| typeFpFlight.equals("W") || typeFpFlight.equals("G") || typeFpFlight.equals("Y")
									|| typeFpFlight.equals("I") || typeFpFlight.equals("J")
									|| typeFpFlight.equals("K")) {
								final point pt = new point();
								pt.lat = latFpFlight;
								pt.lon = lonFpFlight;
								pt.elo = eloFpFlight;
								pt.eto = etoFpFlight;
								pt.routePointId = routePointId;
								filteredFlightTrj.add(pt);
							}

						}
					}

					final int fixTrajPointNumber = filteredFlightTrj.size();

					if (firstIndex >= (fixTrajPointNumber + 1)) {
						firstIndex = fixTrajPointNumber - 1;
					}

					if (secondIndex_1 >= fixTrajPointNumber) {
						secondIndex_1 = fixTrajPointNumber - 1;
					}

					suffixConflictTrj = 1;
					if (fixTrajPointNumber > 0) {
						for (int index = firstIndex; index <= secondIndex_1; index++) {
							nodeList.addNode(EdmModelKeys.ConflictPoint.POINT)
									.setAttribute(EdmModelKeys.ConflictPoint.ID, suffixConflictTrj)
									.setAttribute(EdmModelKeys.ConflictPoint.RPNTID,
											filteredFlightTrj.get(index).routePointId)
									.setAttribute(EdmModelKeys.ConflictPoint.X, filteredFlightTrj.get(index).lon)
									.setAttribute(EdmModelKeys.ConflictPoint.Y, filteredFlightTrj.get(index).lat)
									.setAttribute(EdmModelKeys.ConflictPoint.ELO, filteredFlightTrj.get(index).elo)
									.setAttribute(EdmModelKeys.ConflictPoint.ETO, filteredFlightTrj.get(index).eto);
							suffixConflictTrj++;
						}
					}

					if (!lastPoint.get("POINT_TYPE").equals("F")) {

						nodeList.addNode(EdmModelKeys.ConflictPoint.POINT)
								.setAttribute(EdmModelKeys.ConflictPoint.ID, suffixConflictTrj)
								.setAttribute(EdmModelKeys.ConflictPoint.RPNTID,
										mtcd_item.getSafeString("CLOSEST_FIX_NAME"))
								.setAttribute(EdmModelKeys.ConflictPoint.X, lastPoint.getSafeString("LON"))
								.setAttribute(EdmModelKeys.ConflictPoint.Y, lastPoint.getSafeString("LAT"))
								.setAttribute(EdmModelKeys.ConflictPoint.ELO, Z2).setAttribute(
										EdmModelKeys.ConflictPoint.ETO, Strings.concat(TEI_HOURS, ":", TEI_MINUTES));

					}

					listNode.add(nodeList);
				}

			}
		}

		if (listNode.size() > 0) {
			final var mtcdconflictsNode = objectNode.addNode(EdmModelKeys.Conflicts.MTCDCONFLICTS);
			for (DataNode dataNode : listNode) {
				mtcdconflictsNode.addNode(dataNode);
			}
		}

	}

	/**
	 * Process.
	 *
	 * @param rawdataMtcd the rawdata mtcd
	 * @param forceUpdate boolean value used to force the FLIGHT_EXTFLIGHT BlackBoard
	 */
	public static void process(final IRawData rawdataMtcd, boolean forceUpdate) {

		
		final String cs1 = rawdataMtcd
				.getSafeString(MtcdInputConstants.FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_1);
		final String cs2 = rawdataMtcd
				.getSafeString(MtcdInputConstants.FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_2);
		
		Optional<IRawData> flight1 = BlackBoardUtility.getDataOpt(DataType.BB_CALLSIGN_FLIGHT.name(), cs1);
		Optional<IRawData> flight2 = BlackBoardUtility.getDataOpt(DataType.BB_CALLSIGN_FLIGHT.name(), cs2);
		
		String idFlight1 = "";
		String idFlight2 = "";
		Optional<IRawData> rawdataFlight2=Optional.empty();
		Optional<IRawData> rawdataFlight1 = Optional.empty();
		if (flight1.isPresent()) {
			 rawdataFlight1 = BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(), flight1.get().getSafeString("FLIGHT_NUM"));
			if (rawdataFlight1.isPresent()) {
				idFlight1 = getTrackFlightID(rawdataFlight1.get().getSafeString(CommonConstants.FLIGHT_NUM));
				if (flight2.isPresent()) {
					rawdataFlight2 = BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(), flight2.get().getSafeString("FLIGHT_NUM"));
					if (!rawdataMtcd.getSafeString("CONFLICT_TYPE").equals("SUA")) {
						if (rawdataFlight2.isPresent()) {
							idFlight2 = getTrackFlightID(rawdataFlight2.get().getSafeString(CommonConstants.FLIGHT_NUM));
						}
					}
				}
			}
		}
		
		
		
		if(idFlight1.isEmpty())
			return;
		rawdataFlight1.ifPresent(MTCDProcessor::addAlarmFlight);
		rawdataFlight2.ifPresent(MTCDProcessor::addAlarmFlight);

		
		sendMTCDConflict(idFlight1, idFlight2, rawdataMtcd.getId(), Operation.UPDATE);
		
		if (forceUpdate) {
			ManagerBlackboard.forcedUpdate(DataType.FLIGHT_EXTFLIGHT.name(), idFlight1);
			ManagerBlackboard.forcedUpdate(DataType.FLIGHT_EXTFLIGHT.name(), idFlight2);
		}
	}

	/**
	 * Adds the alarm flight.
	 *
	 * @param flightData the flight data
	 */
	private static void addAlarmFlight(final IRawData flightData) {
		XMLCreation(flightData);
	}

	/**
	 * XML creation.
	 *
	 * @param flightData the flight data
	 */
	private static void XMLCreation(final IRawData flightData) {
		final DataRoot rootData = DataRoot.createMsg();
		HeaderNode objectNode;

		String id;

		if (!flightData.isEmpty()) {
			final Optional<IRawData> track = Utils_LIS
					.getTrkFromFn(flightData.getSafeString(CommonConstants.FLIGHT_NUM));
			if (track.isPresent()) {
				id = Utils.getTrackId(track.get().getId());
				objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
				Utils.addTemplate(track, Optional.of(flightData), objectNode);
			} else {
				id = Utils.getFlightId(flightData.getSafeString(CommonConstants.FLIGHT_NUM));
				objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.FLIGHT);
				Utils.addTemplate(Optional.empty(), Optional.of(flightData), objectNode);
			}
			toXml(flightData, objectNode);
			objectNode.addLine(CommonConstants.ID, id);
			Utils.doTCPClientSender(rootData);
		}

	}

	/**
	 * Delete.
	 *
	 * @param rawdataMtcd the rawdata mtcd
	 */
	public static void delete(final IRawData rawdataMtcd) {

		final String flightNum1 = rawdataMtcd
				.getSafeString(MtcdInputConstants.FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_FLIGHT_NUM_1);
		final String flightNum2 = rawdataMtcd
				.getSafeString(MtcdInputConstants.FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_FLIGHT_NUM_2);

		final var rawdataFlight1 = BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(), flightNum1);
		rawdataFlight1.ifPresent(MTCDProcessor::deleteConflictOnFlight);

		final var rawdataFlight2 = BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(), flightNum2);
		rawdataFlight2.ifPresent(MTCDProcessor::deleteConflictOnFlight);

		String idFlight1 = "";
		String idFlight2 = "";
		final String idConflict = rawdataMtcd.getSafeString(MtcdInputConstants.CONFLICT_ID);
		if (rawdataFlight1.isPresent()) {
			idFlight1 = getTrackFlightID(rawdataFlight1.get().getSafeString(CommonConstants.FLIGHT_NUM));
			if (!rawdataMtcd.getSafeString("CONFLICT_TYPE").equals("SUA")) {
				if (rawdataFlight2.isPresent()) {
					idFlight2 = getTrackFlightID(rawdataFlight2.get().getSafeString(CommonConstants.FLIGHT_NUM));
				}
			}
		}
		sendMTCDConflict(idFlight1, idFlight2, idConflict, Operation.DELETE);

		ManagerBlackboard.forcedUpdate(DataType.FLIGHT_EXTFLIGHT.name(), flightNum1);
		ManagerBlackboard.forcedUpdate(DataType.FLIGHT_EXTFLIGHT.name(), flightNum2);

	}

	/**
	 * Delete conflict on flight.
	 *
	 * @param flightData the flight data
	 */
	static void deleteConflictOnFlight(final IRawData flightData) {
		XMLCreation(flightData);
	}

	/**
	 * Get ID for Track or Flight.
	 *
	 * @param flightNum the flight num
	 * @return the track flight ID
	 */
	static String getTrackFlightID(final String flightNum) {
		final Optional<IRawData> track = Utils_LIS.getTrkFromFn(flightNum);
		String id = track.map(iRawData -> Utils.getTrackId(iRawData.getId()))
				.orElseGet(() -> Utils.getFlightId(flightNum));
		return id;
	}

	/**
	 * Send MTCD conflict.
	 *
	 * @param id1    the id 1
	 * @param id2    the id 2
	 * @param idMTCD the id MTCD
	 * @param oper   the id oper
	 */
	public static void sendMTCDConflict(final String id1, final String id2, final String idMTCD, final Operation oper) {

		final var rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(oper, EdmModelKeys.HeaderType.MTCD_CONFLICT);

		objectNode.addLine(CommonConstants.ID, idMTCD);
		objectNode.addLine(Strings.concat(CommonConstants.ID, "1"), id1);
		objectNode.addLine(Strings.concat(CommonConstants.ID, "2"), id2);

		Utils.doTCPClientSender(rootData);
	}

	/**
	 * Gets the all callsign conflict.
	 *
	 * @param callsign the callsign
	 * @return the all callsign conflict
	 */
	public static Map<String, IRawData> getAllCallsignConflict(final String callsign) {
		final var selectedMTCDList = BlackBoardUtility.getSelectedData(DataType.MTCD_ITEM_NOTIFY.name(),
				new Pair<>(MtcdInputConstants.FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_1, callsign));
		final var selectedMTCDList2 = BlackBoardUtility.getSelectedData(DataType.MTCD_ITEM_NOTIFY.name(),
				new Pair<>(MtcdInputConstants.FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_2, callsign));
		final HashMap<String, IRawData> totalMap = new HashMap<>(selectedMTCDList);
		totalMap.putAll(selectedMTCDList2);
		return totalMap;
	}

}
