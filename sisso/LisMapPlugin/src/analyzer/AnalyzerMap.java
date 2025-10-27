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

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.ColorGeneric;
import com.gifork.auxiliary.ConfigurationFile;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import applicationLIS.BlackBoardConstants_LIS.DataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Class AnalyzerMap.
 */
public class AnalyzerMap implements IObserver {

	/**
	 * The Constant logger.
	 */
	private static final ILogger LOGGER = LoggerFactory.CreateLogger(AnalyzerMap.class);

	/**
	 * The Class SingletonLoader.
	 */
	private enum SingletonLoader {
		;

		/**
		 * The Constant instance.
		 */
		private static final AnalyzerMap INSTANCE = new AnalyzerMap();

		static {
			StorageManager.register(INSTANCE, DataType.APW_MAP.name());
			StorageManager.register(INSTANCE, DataType.SNET_MAP_LIST.name());
			StorageManager.register(INSTANCE, DataType.AIW_MAP.name());
			StorageManager.register(INSTANCE, DataType.ENV_OWN.name());
		}

		/**
		 * Activate.
		 */
		public static void activate() {

		}
	}

	/**
	 * Instantiates a new analyzer map.
	 */
	private AnalyzerMap() {
	}

	/**
	 * Activate.
	 */
	public static void activate() {
		SingletonLoader.activate();
	}

	/**
	 * Update.
	 *
	 * @param json the json
	 */
	@Override
	public void update(final IRawData json) {

		final String loggerCaller = "update()";
		switch (DataType.valueOf(json.getType())) {
		case APW_MAP:
			processMessageApwMap(json);
			break;
		case SNET_MAP_LIST:
			processMessageDescr(json);
			break;
		case AIW_MAP:
			processAiwMaps(null);
		case ENV_OWN:
			processAiwMaps(json);
			break;
		default:
			LOGGER.logDebug(loggerCaller, "event not managed.");
			break;
		}

	}

	/**
	 * Process message map.
	 *
	 * @param json the json
	 */
	private static void processMessageApwMap(final IRawData json) {
		final DataRoot rootData = DataRoot.createMsg();
		final var headerNode = rootData.addHeaderOfService("MAP_LOAD");

		jsonToXml(addMapDescriptorData(json, DataType.SNET_MAP_LIST.name()), headerNode);

		ManagerBlackboard.addJVOutputList(rootData);
	}


	/**
	 * Processes the AIW maps based on the assigned sectors and their geographical polygons.
	 *
	 * @param envOwnData The raw data containing information about the own environment (if any).
	 */
	private void processAiwMaps(IRawData envOwnData) {
		// Get info about the assigned sectors from the "OWN_INFO" data and the "AIW_MAP" data
		final IRawData envOwn = envOwnData != null ? envOwnData :
				BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name()).orElse(null);
		if (envOwn != null) {
			boolean forceClearMap = false;
			final String ownSectorID = envOwn.getSafeString("OWN_SECTORID").trim();
			final String respListString = envOwn.getSafeString("MY_RESPLIST").trim();
			if (!ownSectorID.isBlank() && !respListString.isBlank()) {
				final String[] respList = respListString.trim().split(",");
				// Populate the list of all the AIW_MAP data IDs that match the assigned sectors
				int totalResps = 0;
				List<IRawData> aiwMapList = new ArrayList<>();
				for (final String mapId : respList) {
					if (mapId.trim().isBlank()) {
						continue;
					}
					totalResps++;
					final var aiwMapData = BlackBoardUtility.getDataOpt(DataType.AIW_MAP.name(), mapId);
					aiwMapData.ifPresent(aiwMapList::add);
				}
				if (Math.abs(totalResps - aiwMapList.size()) > 1) {	// Main sector may be in respList without a map
					LOGGER.logWarn("processAiwMaps()", String.format(
							"Expected %d AIW_MAP entries based on the total assigned sectors, but only found %d.",
							totalResps, aiwMapList.size()));
				}
				if (!aiwMapList.isEmpty()) {
					// Get the list of points' lists from all the maps
					final List<List<String>> allPointsList = new ArrayList<>();
					for (final IRawData aiwMap : aiwMapList) {
						final String points = aiwMap.getSafeString("points").trim();
						if (!points.isEmpty()) {
							final List<String> pointsList = Arrays.stream(points.split(" "))
									.filter(s -> !s.isEmpty()).map(String::trim).collect(Collectors.toList());
							allPointsList.add(pointsList);
						}
					}
					// Merge the points into a single polygon
					final GeometryFactory geometryFactory = new GeometryFactory();
					Geometry union = null;
					for (List<String> polygonPoints : allPointsList) {
						Coordinate[] coords = new Coordinate[polygonPoints.size() + 1];
						for (int i = 0; i < polygonPoints.size(); i++) {
							String[] parts = polygonPoints.get(i).split(",");
							double lat = Double.parseDouble(parts[0].trim());
							double lon = Double.parseDouble(parts[1].trim());
							coords[i] = new Coordinate(lat, lon);
						}
						// Close polygon by repeating first point
						coords[polygonPoints.size()] = coords[0];
						LinearRing linearRing = geometryFactory.createLinearRing(coords);
						Polygon polygon = geometryFactory.createPolygon(linearRing, null);
						if (union == null) {
							union = polygon;
						} else {
							union = union.union(polygon);
						}
					}
					if (union != null) {
						// Get the final polygon coordinates
						var finalCoordinates = union.getCoordinates();
						// Get the string of points from the final polygon
						List<String> unionPoints = new ArrayList<>();
						for (Coordinate coord : finalCoordinates) {
							unionPoints.add(String.format("%s,%s", coord.x, coord.y));
						}
						// Fuse the data into a single final data JSON
						String pointsStr = String.join(" ", unionPoints);
						if (!pointsStr.trim().isBlank()) {
							IRawData json = RawDataFactory.create();
							json.setOperation(Operation.UPDATE);
							json.put("UID", "aiwdb_FUSED");
							json.put("DBNAME", "aiwdb");
							json.put("KEY_ID", "FUSED");
							json.put("shape", "polygon");
							json.put("areas", "");
							json.put("polygon", " ");
							json.put("style", "");
							json.put("points", pointsStr);
							// Finally send the XML data
							final DataRoot rootData = DataRoot.createMsg();
							final var headerNode = rootData.addHeaderOfService("MAP_LOAD");
							jsonToXml(json, headerNode);
							ManagerBlackboard.addJVOutputList(rootData);
						}
					}
				} else {
					LOGGER.logError("processAiwMaps()", "No AIW_MAP data found for the assigned sectors.");
				}
			} else {
				// No sectors assigned, force to clear the map
				forceClearMap = true;
			}
			if (forceClearMap) {
				// Make the map empty if no sectors are assigned or no map was processed
				final IRawData json = RawDataFactory.create();
				json.setOperation(Operation.UPDATE);
				json.put("UID", "aiwdb_FUSED");
				json.put("DBNAME", "aiwdb");
				json.put("KEY_ID", "FUSED");
				json.put("shape", "polygon");
				json.put("points", "");
				json.put("areas", "");
				json.put("polygon", " ");
				json.put("style", "");
				final DataRoot rootData = DataRoot.createMsg();
				final var headerNode = rootData.addHeaderOfService("MAP_LOAD");
				jsonToXml(json, headerNode);
				ManagerBlackboard.addJVOutputList(rootData);
			}
		}
	}

	/**
	 * Process message descr.
	 *
	 * @param json the json
	 */
	private static void processMessageDescr(final IRawData json) {
		final DataRoot rootData = DataRoot.createMsg();
		final var headerNode = rootData.addHeaderOfService("MAP_LOAD");

		jsonToXml(addMapDescriptorData(json, DataType.APW_MAP.name()), headerNode);

		ManagerBlackboard.addJVOutputList(rootData);
	}

	/**
	 * Adds the map descriptor data.
	 *
	 * @param jsonToModify the json to modify
	 * @param dataType     the data type
	 * @return the i raw data
	 */
	private static IRawData addMapDescriptorData(final IRawData jsonToModify, final String dataType) {
		IRawData jsonToreturn;
		if (BlackBoardUtility.getCloneDataOpt(dataType, jsonToModify.getId()).isPresent()) {
			jsonToreturn = BlackBoardUtility.getCloneDataOpt(dataType, jsonToModify.getId()).get();
		} else {
			jsonToreturn = RawDataFactory.create();
		}
		for (final String key : jsonToModify.getKeys()) {
			jsonToreturn.put(key, jsonToModify.getSafeString(key));
		}
		jsonToreturn.setId(jsonToModify.getId());
		jsonToreturn.setOperation(jsonToModify.getOperation());
		jsonToreturn.setType(jsonToModify.getType());
		return jsonToreturn;
	}

	/**
	 * Json to xml.
	 *
	 * @param json       the json
	 * @param headerNode the header node
	 */
	private static void jsonToXml(final IRawData json, final HeaderNode headerNode) {
		String mapType = "SNET";
		String mapTreeBranch = "SNET";
		String defaultStyle = "-fx-fill: rgba(192, 192, 192, 0.1)";

		String mapName = json.getSafeString("KEY_ID");

		boolean editable = ConfigurationFile.getBoolProperties("IS_SNET_MAP_EDITABLE");

		boolean forceShowMap = false;

		boolean forceUpdatePoints = false;

		String mapLayerLevelApplicant = "";

		String defaultStatusOnMapApwSua = ColorGeneric.getInstance().getColor("COLOR_APWSUA_STATUS_ON");


		final var dbName = json.getSafeString("DBNAME");

		switch (dbName) {
		case "stcadb":
			mapTreeBranch = "STCA";
			defaultStyle = "-fx-fill: rgba(192, 192, 192, 0.1)";
			headerNode.addLine("UPPER_LIMIT", json.getSafeString("hTop"));
			headerNode.addLine("LOWER_LIMIT", json.getSafeString("hBot"));
			headerNode.addLine("PLA_CONV", json.getSafeString("psc"));
			headerNode.addLine("PLA_DIV", json.getSafeString("psd"));
			headerNode.addLine("RVSM_CONV", json.getSafeString("rsc"));
			headerNode.addLine("RVSM_DIV", json.getSafeString("rsd"));
			headerNode.addLine("VER_CONV", json.getSafeString("vsc"));
			headerNode.addLine("VER_DIV", json.getSafeString("vsd"));
			break;
		case "msawdb":
			mapTreeBranch = "MSAW";
			defaultStyle = "-fx-fill: rgba(128, 128, 128, 0.1)";
			headerNode.addLine("AIRPORT", json.getSafeString("airportId"));
			headerNode.addLine("PEN", json.getSafeString("pdv"));
			headerNode.addLine("MIL", json.getSafeString("mil"));
			headerNode.addLine("IFR", json.getSafeString("ifr"));
			headerNode.addLine("VFR", json.getSafeString("vfr"));
			headerNode.addLine("GAT", json.getSafeString("gat"));
			headerNode.addLine("OAT", json.getSafeString("oat"));
			headerNode.addLine("UPPER_LIMIT", json.getSafeString("hMax"));
			headerNode.addLine("LOWER_LIMIT", json.getSafeString("hMin"));
			break;
		case "daiwdb":
		case "suadb":
			mapTreeBranch = "APWSUA";
			defaultStyle = "-fx-fill: rgba(192, 192, 192, 0.1)";
			headerNode.addLine("UPPER_LIMIT", json.getSafeString("hTop"));
			headerNode.addLine("LOWER_LIMIT", json.getSafeString("hBot"));
			headerNode.addLine("FLAG_8_33", json.getSafeString("is833"));
			headerNode.addLine("CRO", json.getSafeString("cro"));
			headerNode.addLine("PEN", json.getSafeString("pdv"));
			headerNode.addLine("MIL", json.getSafeString("mil"));
			headerNode.addLine("IFR", json.getSafeString("irf"));
			headerNode.addLine("VFR", json.getSafeString("vrf"));
			headerNode.addLine("GAT", json.getSafeString("gat"));
			headerNode.addLine("OAT", json.getSafeString("oat"));
			headerNode.addLine("TEXT", json.getSafeString("freeText"));
			break;
		case "aiwdb":
			//defaultStyle =
			//		"-fx-fill: rgba(192, 192, 192, 0.1); -fx-stroke: rgba(219, 238, 244, 1.0); -fx-stroke-width: 0.0; -fx-opacity: 1.0";
			defaultStyle = "-fx-fill: rgba(0, 0, 0, 0.4)";
			editable = false;
			// Allows showing the map as a single item inside the map tree (root item "Dynamic Sector Map")
			mapType = "";
			mapTreeBranch = "";
			mapName = "Dynamic Sector Map";
			forceShowMap = true;
			forceUpdatePoints = true;
			mapLayerLevelApplicant = "GeographicMapSectors";
			break;
		default:
			headerNode.addLine("UPPER_LIMIT", json.getSafeString("hTop"));
			headerNode.addLine("LOWER_LIMIT", json.getSafeString("hBot"));
			break;
		}

		headerNode.addLine("MAP_NAME", mapName);
		headerNode.addLine("LAT_DEV", json.getSafeString("tlook"));
		headerNode.addLine("STATUS", json.getSafeString("status"));
		headerNode.addLine("AREA_TYPE", json.getSafeString("type"));

		headerNode.addLine("ACTIVATION", json.getSafeString("dateFr"));
		headerNode.addLine("DEACTIVATION", json.getSafeString("dateTo"));
		headerNode.addLine("MON", (json.getSafeString("monFr") + "-" + json.getSafeString("monTo")));
		headerNode.addLine("TUE", (json.getSafeString("tueFr") + "-" + json.getSafeString("tueTo")));
		headerNode.addLine("WED", (json.getSafeString("wedFr") + "-" + json.getSafeString("wedTo")));
		headerNode.addLine("THU", (json.getSafeString("thuFr") + "-" + json.getSafeString("thuTo")));
		headerNode.addLine("FRI", (json.getSafeString("friFr") + "-" + json.getSafeString("friTo")));
		headerNode.addLine("SAT", (json.getSafeString("satFr") + "-" + json.getSafeString("satTo")));
		headerNode.addLine("SUN", (json.getSafeString("sunFr") + "-" + json.getSafeString("sunTo")));

		headerNode.addLine("DBNAME", dbName);
		headerNode.addLine("MAP_TYPE", mapType);
		headerNode.addLine("MAP_SUBTYPE", mapTreeBranch);

		if (!json.getSafeString("points").isBlank() || forceUpdatePoints) {
			headerNode.addLine("POINTS", json.getSafeString("points"));
		}
		if (!json.getSafeString("shape").isBlank()) {
			headerNode.addLine("SHAPE", json.getSafeString("shape"));
		}
		if (!json.getSafeString("center").isBlank()) {
			headerNode.addLine("CENTER", json.getSafeString("center"));
		}
		if (!json.getSafeString("radius").isBlank()) {
			headerNode.addLine("RADIUS", json.getSafeString("radius"));
		}
		if (!json.getSafeString("radiusPoint").isBlank()) {
			headerNode.addLine("RADIUSPOINT", json.getSafeString("radiusPoint"));
		}

		String deleteMap = "false";

		if (json.getOperation() == Operation.DELETE) {
			deleteMap = "true";
		}

		headerNode.addLine("DELETE", deleteMap);
		if (json.getSafeString("DBNAME").equals("daiwdb") || json.getSafeString("DBNAME").equals("suadb")) {

			final String statusON_OFF = json.getSafeString("status", "");
			final boolean status = statusON_OFF.equals("ON");
			final boolean localVisibility = json.getSafeBoolean("LOCAL_VISIBILITY", false);
			final boolean show = status || localVisibility;
			if (show && !defaultStatusOnMapApwSua.isEmpty()) {
				defaultStyle = defaultStatusOnMapApwSua;
			}
			headerNode.addLine("SHOW", Boolean.toString(show));
			headerNode.addLine("SHOWONUPDATE", Boolean.toString(show));
		} else {
			headerNode.addLine("SHOW", "false");
		}

		if (forceShowMap) {
			headerNode.addLine("SHOW", "true");
		}

		if (!mapLayerLevelApplicant.isBlank()) {
			headerNode.addLine("APPLICANT", mapLayerLevelApplicant);
		}

		headerNode.addLine("STYLE", defaultStyle);

		headerNode.addLine("EDITABLE", editable);
	}
}
