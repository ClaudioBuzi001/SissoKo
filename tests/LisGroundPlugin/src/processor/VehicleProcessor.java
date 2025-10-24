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
package processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.EdmModelKeys.TrajectoryGroundPoint;
import com.fourflight.WP.ECI.edm.EdmModelKeys.TrajectoryPoint;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;

import XAI.XAIData.vehicle.VehicleConstants;
import application.pluginService.ServiceExecuter.ServiceExecuter;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightInputConstants;
import auxilliary.VehicleAggregatorMap;
import auxilliary.VehicleAliasMap;
import auxilliary.VehicleBlackMap;
import common.Alias;
import common.CommonConstants;
import common.Utils;

/**
 * The Class VehicleProcessor.
 */
public enum VehicleProcessor {
	;

	/** The Constant vehicleAliasMap. */
	private final static VehicleAliasMap vehicleAliasMap = new VehicleAliasMap();

	/** The Constant vehicleAggregatorMap. */
	private final static VehicleAggregatorMap vehicleAggregatorMap = new VehicleAggregatorMap();

	/** The Constant vehicleBlackMap. */
	private final static VehicleBlackMap vehicleBlackMap = new VehicleBlackMap();
	/** The Constant flightDeleted. */
	private static final ArrayList<String> vehicleDeleted = new ArrayList<>();

	/**
	 * Process.
	 *
	 * @param jsonVehicle the json vehicle
	 */
	public static void process(final IRawData jsonVehicle) {
		XMLCreation(jsonVehicle);
	}

	/**
	 * XML creation.
	 *
	 * @param jsonVehicle the json vehicle
	 */
	private static void XMLCreation(final IRawData jsonVehicle) {
		final DataRoot rootData = DataRoot.createMsg();
		HeaderNode objectNode;

		final String stn = jsonVehicle.getSafeString(CommonConstants.STN);
		final String vid = jsonVehicle.getSafeString("VID");

		String id;
		final var trk = Utils_LIS.getCorrelatedVehicleTrack(vid);
		final Optional<IRawData> vehicleJson;
		if (stn.equals(CommonConstants.ABSENT_STN) || stn.isEmpty()) {
			if (trk.isPresent()) {
				id = Utils.getTrackId(trk.get().getSafeString("STN"));

				objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
				vehicleJson = Optional.empty();

				BlackBoardUtility.removeData(DataType.BB_VID_STN.name(), vid);
				BlackBoardUtility.removeData(DataType.BB_STN_VID.name(), trk.get().getSafeString("STN"));
			} else {
				id = Utils.getVehicleId(vid);
				vehicleJson = Optional.of(jsonVehicle);
				objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.FLIGHT);
				Utils.addTemplateVehicle(trk, Optional.of(jsonVehicle), objectNode);
			}
		} else {
			BlackBoardUtility.updateData(DataType.BB_VID_STN.name(), vid, CommonConstants.STN, stn);
			BlackBoardUtility.updateData(DataType.BB_STN_VID.name(), stn, "VID", vid);
			if (!vehicleDeleted.contains(vid)) {
				deleteVehicleToViewer(vid);
			}
			vehicleJson = Optional.of(jsonVehicle);
			objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
			id = Utils.getTrackId(stn);
		}

		Utils.addTemplateVehicle(trk, vehicleJson, objectNode);
		analyze(jsonVehicle, objectNode);
		objectNode.addLine(FlightInputConstants.FLIGHT_NUM, vid);
		toXml(jsonVehicle, objectNode);
		objectNode.addLine(CommonConstants.ID, id);
		Utils.doTCPClientSender(rootData);

	}

	/**
	 * To xml.
	 *
	 * @param vehicleData the vehicle data
	 * @param objectNode  the object node
	 */
	static void toXml(final IRawData vehicleData, final HeaderNode objectNode) {
		if (vehicleData != null) {
			if (vehicleData.getSafeInt("VEHICLE_TRAJECTORY_NUM") > 0) {

				final var jsonArr = vehicleData.getSafeRawDataArray("VT_POINT");
				objectNode.addLine(TrajectoryGroundPoint.GT_POINT_NUM,
						vehicleData.getSafeString("VEHICLE_TRAJECTORY_NUM"));
				objectNode.addLine(TrajectoryPoint.FP_POINT_NUM, vehicleData.getSafeString("VEHICLE_TRAJECTORY_NUM"));

				for (int i = 0; i < jsonArr.size(); i++) {

					final var jsonObj = jsonArr.get(i);

					final HashMap<String, String> attributes = new HashMap<>();

					attributes.put("ID", jsonObj.getSafeString("VT_ID"));
					attributes.put("CLEARED", jsonObj.getSafeString("VT_CLEARED"));
					attributes.put("REPORTED", jsonObj.getSafeString("VT_REPORTED"));
					attributes.put("ETO", jsonObj.getSafeString("VT_ETO"));
					attributes.put("STATUS", jsonObj.getSafeString("VT_STATUS"));
					attributes.put("TYPE", jsonObj.getSafeString("VT_TYPE"));
					attributes.put("ALIAS", jsonObj.getSafeString("VT_ALIAS"));
					attributes.put("SECTOR", jsonObj.getSafeString("VT_SECTOR"));
					attributes.put("PERCENTOFGREEN", jsonObj.getSafeString("VT_PERCENTOFGREEN"));
					attributes.put("LAT", jsonObj.getSafeString("VT_Y"));
					attributes.put("LON", jsonObj.getSafeString("VT_X"));

					objectNode.addLine("GT_POINT_" + (i + 1), "", attributes);
				}
			}
		}
	}

	/**
	 * Delete F light plan to viewer.
	 *
	 * @param vehicle_id the vehicle id
	 */
	private static void deleteVehicleToViewer(final String vehicle_id) {
		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.DELETE, EdmModelKeys.HeaderType.FLIGHT);
		objectNode.addLine(VehicleConstants.VID, vehicle_id);
		objectNode.addLine(CommonConstants.ID, Utils.getVehicleId(vehicle_id));
		Utils.doTCPClientSender(rootData);
		vehicleDeleted.add(vehicle_id);
	}

	/**
	 * Delete.
	 *
	 * @param jsonVehicle the json vehicle
	 */
	public static void delete(final IRawData jsonVehicle) {

		final String vid = jsonVehicle.getSafeString("VID");

		final String stn = jsonVehicle.getSafeString(CommonConstants.STN);
		if (Utils_LIS.isCorrelatedVehicle(stn)) {
			BlackBoardUtility.removeData(DataType.BB_VID_STN.name(), vid);
			BlackBoardUtility.removeData(DataType.BB_STN_VID.name(), stn);
			final Optional<IRawData> jsonTrack = BlackBoardUtility.getDataOpt(DataType.TRACK.name(), stn);
			if (jsonTrack.isPresent()) {
				final DataRoot rootData = DataRoot.createMsg();
				final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
				Utils.addTemplateVehicle(jsonTrack, Optional.empty(), objectNode);
				final String id = Utils.getTrackId(jsonTrack.get().getSafeString(CommonConstants.STN));
				objectNode.addLine(CommonConstants.ID, id);
				Utils.doTCPClientSender(rootData);
			}
		} else {
			deleteVehicleToViewer(vid);
		}

		BlackBoardUtility.removeData(DataType.BB_VEHICLE_CPDLC_DOWNLINK.name(), vid);
		final var cpldList = BlackBoardUtility.getAllData(DataType.CPDLC_VIF.name());

		final HashMap<String, IRawData> selectedList = new HashMap<>();
		for (final var entry : cpldList.entrySet()) {
			final String key = entry.getKey();
			final IRawData json = entry.getValue();
			if (json.getSafeString("VID").trim().equals(vid)) {
				selectedList.put(key, json);
			}
		}

		for (final String item : selectedList.keySet()) {
			BlackBoardUtility.removeData(DataType.CPDLC_VIF.name(), item);
		}

		vehicleDeleted.remove(vid);

	}

	/**
	 * Analyze.
	 *
	 * @param json       the json
	 * @param objectNode the object node
	 */
	private static void analyze(final IRawData json, final HeaderNode objectNode) {

		json.getKeys().stream().filter(strKey -> !VehicleProcessor.vehicleBlackMap.containsKey(strKey)
				&& !strKey.equals("VT_POINT") && !strKey.equals("GT_POINT")).forEach(key -> {
					final Alias alias = Utils.getAlias(key, json, VehicleProcessor.vehicleAliasMap);
					objectNode.addLine(alias.getAliasKey(), alias.getAliasData());
				});

		VehicleProcessor.vehicleAggregatorMap.keySet().forEach(key -> ServiceExecuter.getInstance().executeAggregator(
				VehicleProcessor.vehicleAggregatorMap.getService(key).getServiceName(), json, objectNode));

	}

}
