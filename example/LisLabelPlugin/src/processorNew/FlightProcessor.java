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
import java.util.Optional;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmMetaKeys;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.ColorGeneric;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;

import analyzer.AnalyzerLabel;
import application.pluginService.ServiceExecuter.ServiceExecuter;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightAggregatorMap;
import auxiliary.flight.FlightAliasMap;
import auxiliary.flight.FlightBlackMap;
import auxiliary.flight.FlightEsbAggregatorMap;
import auxiliary.flight.FlightInputConstants;
import auxiliary.track.TrackOutputConstants;
import common.Alias;
import common.CommonConstants;
import common.Utils;

/**
 * The Class FlightProcessor.
 */
public enum FlightProcessor {
	;

	/** The Constant flightAliasMap. */
	private final static FlightAliasMap flightAliasMap = new FlightAliasMap();

	/** The Constant flightBlackMap. */
	private final static FlightBlackMap flightBlackMap = new FlightBlackMap();

	/** The Constant flightAggregatorMap. */
	public final static FlightAggregatorMap flightAggregatorMap = new FlightAggregatorMap();

	/** The Constant flightEsbAggregatorMap. */
	public final static FlightEsbAggregatorMap flightEsbAggregatorMap = new FlightEsbAggregatorMap();

	/** The new trajectory. */
	private static boolean newTrajectory;

	/** The Constant flightDeleted. */
	private static final ArrayList<String> flightDeleted = new ArrayList<>();

	/**
	 * Delete.
	 *
	 * @param jsonFlight the json flight
	 */
	public static void delete(final IRawData jsonFlight) {

		final String flightNum = jsonFlight.getSafeString(CommonConstants.FLIGHT_NUM);
		final String callsign = jsonFlight.getSafeString(CommonConstants.CALLSIGN);

		BlackBoardUtility.removeData(DataType.BB_CALLSIGN_FLIGHT.name(), callsign);

		/* TCAS_ADSC remove from alarmedTrack */
		final String stn = jsonFlight.getSafeString(CommonConstants.STN);
		final Optional<IRawData> jsonTrack = Utils_LIS.getTrkFromStn(stn);
		if (jsonTrack.isPresent()) {
			final DataRoot rootData = DataRoot.createMsg();
			final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
			Utils.addTemplate(jsonTrack, Optional.empty(), objectNode);
			final String id = Utils.getTrackId(jsonTrack.get().getId());
			objectNode.addLine(CommonConstants.ID, id);
			Utils.doTCPClientSender(rootData);
		}

		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.DELETE, EdmModelKeys.HeaderType.FLIGHT);
		objectNode.addLine(CommonConstants.FLIGHT_NUM, flightNum);
		objectNode.addLine(CommonConstants.ID, Utils.getFlightId(flightNum));
		Utils.doTCPClientSender(rootData);
		flightDeleted.remove(flightNum);

		BlackBoardUtility.removeData(CommonConstants.BB_FLIGHT_CPDLC_DOWNLINK, flightNum);

		BlackBoardUtility.removeData(CommonConstants.BB_FLIGHT_CPDLC_UPLINK, flightNum);

	}

	/**
	 * Process.
	 *
	 * @param rawdataFlight the rawdata flight
	 */
	public static void process(final IRawData rawdataFlight) {
		final String flightNum = rawdataFlight.getSafeString("FLIGHT_NUM");
		CPDLCProcessor.analyzeDialgue(flightNum, rawdataFlight);
		if (Utils.isPrototypingTest()) {
			var rawDataClone = RawDataFactory.create(rawdataFlight);
			rawDataClone.setId(rawdataFlight.getId());
			Utils.forceFieldValuePRE(rawDataClone, false);

			DataRoot rootData = XMLCreation(rawDataClone);

			Utils.forceAllFieldsColor(rawDataClone, rootData);
			Utils.forceFieldColor(rawDataClone, rootData);
			Utils.forceFieldValuePOST(rawDataClone, rootData, false);

			Utils.doTCPClientSender(rootData);
		} else {
			DataRoot rootData = XMLCreation(rawdataFlight);
			Utils.doTCPClientSender(rootData);
		}
	}

	/**
	 * XML creation.
	 *
	 * @param rawdataFlight the rawdata flight
	 * @return DataRoot
	 */
	private static DataRoot XMLCreation(final IRawData rawdataFlight) {
		final DataRoot rootData = DataRoot.createMsg();
		HeaderNode objectNode;

		final String flight_num = rawdataFlight.getSafeString(CommonConstants.FLIGHT_NUM);
		final String callsign = rawdataFlight.getSafeString(CommonConstants.CALLSIGN);
		BlackBoardUtility.updateData(DataType.BB_CALLSIGN_FLIGHT.name(), callsign, CommonConstants.FLIGHT_NUM,
				flight_num);

		String id;
		final String stn = rawdataFlight.getSafeString(CommonConstants.STN);
		final var trk = Utils_LIS.getTrkFromStn(stn);
		if (stn.equals(CommonConstants.ABSENT_STN) || stn.isEmpty()) {
			if (trk.isPresent()) {
				id = Utils.getTrackId(trk.get().getId());

				objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
				Utils.addTemplate(trk, Optional.empty(), objectNode);

//				ManagerBlackboard.forcedUpdate(DataType.FLIGHT_EXTFLIGHT.name(), rawdataFlight.getId());
//				ManagerBlackboard.forcedUpdate(DataType.FLIGHT_TRJ.name(), rawdataFlight.getId());
			} else {
				id = Utils.getFlightId(flight_num);
				objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.FLIGHT);
				Utils.addTemplate(trk, Optional.of(rawdataFlight), objectNode);
			}
		} else {
			if (trk.isPresent()) {
				id = Utils.getTrackId(trk.get().getId());
				deleteFLightPlanToViewer(flight_num);
				objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
			} else {
				id = Utils.getFlightId(flight_num);
				objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.FLIGHT);
			}

			Utils.addTemplate(trk, Optional.of(rawdataFlight), objectNode);
		}

		if (newTrajectory) {
			flightBlackMap.put(CommonConstants.TRJ_ARRAY);
		}
		AnalyzerLabel.analyze(flightBlackMap, flightAggregatorMap, flightAliasMap, rawdataFlight, objectNode);

		if (objectNode.getPairValue(EdmMetaKeys.NODE_V, TrackOutputConstants.TEMPLATE)
				.equals(TrackOutputConstants.TEMPLATE_PENDING)) {
			objectNode.addLine("ESB_TYPE", "");
		}

		RoutePlanProcessor.toXml(rawdataFlight, objectNode);
		objectNode.addLine(CommonConstants.ID, id);
		
		return rootData;
	}

	/**
	 * Delete F light plan to viewer.
	 *
	 * @param flight_num the flight num
	 */
	private static void deleteFLightPlanToViewer(final String flight_num) {
		if (!flightDeleted.contains(flight_num)) {
			final DataRoot rootData = DataRoot.createMsg();
			final var objectNode = rootData.addHeaderOfObject(Operation.DELETE, EdmModelKeys.HeaderType.FLIGHT);
			objectNode.addLine(CommonConstants.FLIGHT_NUM, flight_num);
			objectNode.addLine(CommonConstants.ID, Utils.getFlightId(flight_num));
			Utils.doTCPClientSender(rootData);
			flightDeleted.add(flight_num);
			ManagerBlackboard.forcedUpdate(DataType.FLIGHT_TRJ.name(), flight_num);
		}
	}

	/**
	 * Process lost flight.
	 *
	 * @param rawdataFlight the rawdata flight
	 */
	public static void processLostFlight(final IRawData rawdataFlight) {

		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.INSERT, EdmModelKeys.HeaderType.LOST_TRACK);
		final String flight_num = rawdataFlight.getSafeString(CommonConstants.FLIGHT_NUM);
		// AnalyzerLabel.analyze(flightBlackMap, flightAggregatorMap, flightAliasMap, rawdataFlight,
		// objectNode);

		final String LATITUDE = rawdataFlight.getSafeString(FlightInputConstants.LAT_COAST);
		objectNode.addLine("LATITUDE", LATITUDE);

		final String LONGITUDE = rawdataFlight.getSafeString(FlightInputConstants.LON_COAST);
		objectNode.addLine("LONGITUDE", LONGITUDE);

		final String callSign = rawdataFlight.getSafeString(CommonConstants.CALLSIGN);
		String callBack = "DELETE_OBJECT(OBJECT_TYPE=LOST_TRACK, OBJECT_ID=" + rawdataFlight.getId() + ")";
		objectNode.addLine("CALLSIGN", callSign).setAttributeIf(!callBack.isEmpty(),
				EdmModelKeys.Attributes.RIGHT_MOUSE_CALLBACK, callBack);
		objectNode.addLine("TYPE_TRK", "LOST");
		objectNode.addLine(CommonConstants.FLIGHT_NUM, "LOST_" + flight_num);
		String color = Utils.getTrackOverSymbolColor("LOST", "true", "RED");

		objectNode.addLine("COLOR_TRK", color);
		objectNode.addLine(CommonConstants.ID, "LOST_" + flight_num);
		Utils.doTCPClientSender(rootData);
	}

	/**
	 * Process lost flight.
	 *
	 * @param rawdataFlight the rawdata flight
	 */
	public static void processPntFlight(final IRawData rawdataFlight) {

		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.INSERT, EdmModelKeys.HeaderType.LOST_TRACK);
		objectNode.addLine(CommonConstants.ID, rawdataFlight.getId());

		final String LATITUDE = rawdataFlight.getSafeString("LAT");
		objectNode.addLine("LATITUDE", LATITUDE);

		final String LONGITUDE = rawdataFlight.getSafeString("LON");

		final String sector = rawdataFlight.getSafeString("SCT");
		objectNode.addLine("LONGITUDE", LONGITUDE);

		final String callSign = rawdataFlight.getSafeString(CommonConstants.CALLSIGN);
		String callBack = "DELETE_OBJECT(OBJECT_TYPE=LOST_TRACK, OBJECT_ID=" + rawdataFlight.getId() + ")";
		objectNode.addLine("CALLSIGN", callSign);

		objectNode.addLine("PNT", "PNT").setAttributeIf(!callBack.isEmpty(),
				EdmModelKeys.Attributes.RIGHT_MOUSE_CALLBACK, callBack);
		objectNode.addLine("SCT", sector);
		objectNode.addLine("TYPE_TRK", "PNT");
		String color = ColorGeneric.getInstance().getColor("PNT_TRACK_SYMBOL");
		
		if (color.isEmpty()) {
			color = "#00FF00";
		}
		objectNode.addLine("COLOR_TRK", color);
		
		Utils.doTCPClientSender(rootData);
	}

	/**
	 * Delete.
	 *
	 * @param json the flight
	 */
	public static void deletePntFlight(final IRawData json) {

		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.DELETE, EdmModelKeys.HeaderType.LOST_TRACK);
		final String flight_num = json.getSafeString(CommonConstants.FLIGHT_NUM);
		objectNode.addLine(CommonConstants.ID, flight_num);

		Utils.doTCPClientSender(rootData);

	}

	/**
	 * Delete.
	 *
	 * @param json the flight
	 */
	public static void deleteLostFlight(final IRawData json) {

		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.DELETE, EdmModelKeys.HeaderType.LOST_TRACK);
		final String flight_num = json.getSafeString(CommonConstants.FLIGHT_NUM);
		objectNode.addLine(CommonConstants.ID, "LOST_" + flight_num);
		Utils.doTCPClientSender(rootData);

	}

	/**
	 * Process esb.
	 *
	 * @param rawdataFlight the rawdata flight
	 */
	public static void processEsb(IRawData rawdataFlight) {

		final String flight_num = rawdataFlight.getSafeString(CommonConstants.FLIGHT_NUM);

		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.ESB);

		String id = Utils.getFlightEsbId(flight_num);

		objectNode.addLine(TrackOutputConstants.TEMPLATE,
				rawdataFlight.getSafeString(FlightInputConstants.TEMPLATE_STRING));
		objectNode.addLine(CommonConstants.ID, id);

		rawdataFlight.getKeys().stream().filter(strKey -> !flightBlackMap.containsKey(strKey)
				&& !strKey.startsWith("GT_") && !strKey.startsWith("FP_POINT")).forEach(key -> {
					final Alias alias = Utils.getAlias(key, rawdataFlight, flightAliasMap);
					objectNode.addLine(alias.getAliasKey(), alias.getAliasData());
				});

		flightEsbAggregatorMap.keySet().forEach(key -> ServiceExecuter.getInstance()
				.executeAggregator(flightEsbAggregatorMap.getService(key).getServiceName(), rawdataFlight, objectNode));

		if (objectNode.getPairValue(EdmMetaKeys.NODE_V, TrackOutputConstants.TEMPLATE)
				.equals(TrackOutputConstants.TEMPLATE_PENDING)) {
			objectNode.addLine("ESB_TYPE", "");
		}
		Utils.doTCPClientSender(rootData);

	}

	/**
	 * Delete esb.
	 *
	 * @param json the json
	 */
	public static void deleteEsb(IRawData json) {

		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.DELETE, EdmModelKeys.HeaderType.ESB);

		final String flightNum = json.getSafeString(CommonConstants.FLIGHT_NUM);

		objectNode.addLine(CommonConstants.FLIGHT_NUM, flightNum);
		objectNode.addLine(CommonConstants.ID, Utils.getFlightEsbId(flightNum));
		Utils.doTCPClientSender(rootData);
	}
}
