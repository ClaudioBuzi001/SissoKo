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
package common;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.fourflight.WP.ECI.edm.DataNode;
import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.EdmModelKeys.TrajectoryGroundPoint;
import com.fourflight.WP.ECI.edm.EdmModelKeys.TrajectoryPoint;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.auxiliary.ConfigurationFile;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataArray;
import com.gifork.commons.data.IRawDataElement;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.Pair;
import com.leonardo.infrastructure.Strings;
import com.leonardo.infrastructure.log.ILogger;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import auxiliary.track.TrackInputConstants;
import auxiliary.track.TrackOutputConstants;
import common.CommonConstants.CoordType;

/**
 * The Class Utils.
 */
public enum Utils {
	;

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(Utils.class);

	/** The Constant dfOfgetDistanceDoubleToOneDigit. */

	private static final DecimalFormat dfOfgetDistanceDoubleToOneDigit = new DecimalFormat("#.#");

	static {

		dfOfgetDistanceDoubleToOneDigit.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	}

	/**
	 * Gets the alias.
	 *
	 * @param alias_key the alias key
	 * @param json      the json
	 * @param map       the map
	 * @return the alias
	 */
	public static Alias getAlias(final String alias_key, final IRawData json, final AliasMap map) {
		String output_key = alias_key;
		String output_data = json.getSafeString(alias_key);

		if (map != null) {
			final var keyData = map.getKeyData(alias_key);
			if (keyData != null) {

				try {
					Alias elem = keyData.get(output_data);
					if (elem != null) {
						output_key = elem.getAliasKey();
						output_data = elem.getAliasData();
					}
				} catch (final NullPointerException e) {

					logger.logWarn("Alias",
							"Utils.getAlias - WARNING - key: " + alias_key + " is not an alias value" + e.getMessage());
				}
			}
		}

		return new Alias(output_key, output_data);
	}

	/**
	 * Adds the element track.
	 *
	 * @param track      the track
	 * @param objectNode the object node
	 */
	public static void addElementTrack(final IRawData track, final HeaderNode objectNode) {
		final String id = Utils.getTrackId(track.getId());
		objectNode.addLine(CommonConstants.ID, id);
	}

	/**
	 * Adds the template.
	 *
	 * @param track      the track
	 * @param flight     the flight
	 * @param objectNode the object node
	 */
	public static void addTemplate(final Optional<IRawData> track, final Optional<IRawData> flight,
			final HeaderNode objectNode) {

		String data = TrackOutputConstants.TEMPLATE_PENDING;
		if (track.isPresent() && track.get().getSafeBoolean("IS_MISSLINK")) {
			data = FlightOutputConstants.TEMPLATE_AMBIGUITY;
		} else {
			if (flight.isPresent()) {
				data = flight.get().getSafeString(FlightInputConstants.TEMPLATE_STRING);
			} else if (flight.isEmpty() && track.isPresent()) {
				final String stn = track.get().getId();
				if (Utils_LIS.isCorrelatedVehicle(stn)) {
					if (Utils_LIS.getCorrelatedVehicle(stn).isPresent()) {
						final IRawData vehicCorr = Utils_LIS.getCorrelatedVehicle(stn).get();
						final var vehic = BlackBoardUtility.getDataOpt(DataType.VEHICLE.name(),
								vehicCorr.getSafeString("VID"));
						if (vehic.isPresent()) {
							data = vehic.get().getSafeString(FlightInputConstants.TEMPLATE_STRING);
						}
					}
				} else if (track.get().getSafeInt(TrackInputConstants.ECAT) == TrackInputConstants.ECAT_VEHIC) {

					if (Utils.isVisVehicle()) {
						data = TrackOutputConstants.TEMPLATE_VEHICLE_PENDING;
					}

					objectNode.addLine(FlightInputConstants.FLIGHT_NUM,
							"PEND" + "_" + track.get().getSafeString(CommonConstants.TRACK_ID));
				}

				else {
					objectNode.addLine(FlightInputConstants.FLIGHT_NUM,
							"PEND" + "_" + track.get().getSafeString(CommonConstants.TRACK_ID));
				}
			} else {
				objectNode.addLine(FlightInputConstants.FLIGHT_NUM,
						"PEND" + "_" + track.get().getSafeString(CommonConstants.TRACK_ID));
			}
		}
		if (data.contains(TrackOutputConstants.TEMPLATE_PENDING)) {
			objectNode.addLine(TrajectoryPoint.FP_POINT_NUM, "0");
			objectNode.addLine(TrajectoryGroundPoint.GT_POINT_NUM, "0");
			objectNode.addLine("ESB_TYPE", "");
		}

		if(flight.isPresent() && isPrototypingTest()) {
			final var flightOpt = BlackBoardUtility.getDataOpt(DataType.PROTO_FORCE_TEMPLATE.name(), flight.get().getSafeString("FLIGHT_NUM"));
			if(!flightOpt.isEmpty()) {
				data = flightOpt.get().getSafeString(FlightInputConstants.TEMPLATE);
			}
		}
				
		objectNode.addLine(TrackOutputConstants.TEMPLATE, data);

	}

	/**
	 * Adds the template vehicle.
	 *
	 * @param jsonTrack  the json track
	 * @param vehicle    the vehicle
	 * @param objectNode the object node
	 */
	public static void addTemplateVehicle(final Optional<IRawData> jsonTrack, final Optional<IRawData> vehicle,
			final HeaderNode objectNode) {

		String data = TrackOutputConstants.TEMPLATE_VEHICLE_PENDING;

		if (vehicle.isPresent()) {
			data = vehicle.get().getSafeString(FlightInputConstants.TEMPLATE_STRING);
		} else if (vehicle.isEmpty() && jsonTrack.isPresent()) {
			objectNode.addLine(FlightInputConstants.FLIGHT_NUM,
					"PEND" + "_" + jsonTrack.get().getSafeString(CommonConstants.TRACK_ID));
			objectNode.addLine("VEHICLE_TYPE", "");
		}
		if (data.equals(TrackOutputConstants.TEMPLATE_VEHICLE_PENDING)) {
			objectNode.addLine(TrajectoryPoint.FP_POINT_NUM, "0");
			objectNode.addLine(TrajectoryGroundPoint.GT_POINT_NUM, "0");
		}

		objectNode.addLine(TrackOutputConstants.TEMPLATE, data);

	}

	/**
	 * Gets the track over symbol color.
	 *
	 * @param fieldTrackOverSymbol the field track over symbol
	 * @param valueTrackOverSymbol the value track over symbol
	 * @param defaultColor         the default color
	 * @return the track over symbol color
	 */
	public static String getTrackOverSymbolColor(final String fieldTrackOverSymbol, final String valueTrackOverSymbol,
			final String defaultColor) {

		final var listColorGeneric = BlackBoardUtility.getDataOpt(DataType.PRELOADED_COLOR_SET.name(),
				"LIST_COLOR_GENERIC");

		if (listColorGeneric.isPresent() && !fieldTrackOverSymbol.isEmpty()) {

			final IRawDataElement fieldColorMap = listColorGeneric.get().getSafeElement(fieldTrackOverSymbol);

			final IRawDataElement valueParameters = fieldColorMap.getSafeElement(valueTrackOverSymbol);

			return valueParameters.getSafeString("COLOR");
		}

		return defaultColor;
	}

	/**
	 * Gets the inits the coord sector.
	 *
	 * @param ID the id
	 * @return the inits the coord sector
	 */
	public static String getInitCoordSector(final String ID) {
		String initCoordSect = "";

		final var jsonCoordination = BlackBoardUtility.getDataOpt(DataType.FLIGHT_COORDINATION.name(), ID);
		if (jsonCoordination.isPresent()) {
			initCoordSect = jsonCoordination.get().getSafeString("INITCOORDSECTOR");
		}
		return initCoordSect;
	}

	/**
	 * Gets the template color.
	 *
	 * @param templateName the template name
	 * @return the template color
	 */
	public static String getTemplateColor(final String templateName) {

		String ret_color = "#FFFFFF";
		final var BBKScolor = BlackBoardUtility.getDataOpt(DataType.PRELOADED_COLOR_SET.name(), "LIST_COLOR_GENERIC");

		if (BBKScolor.isPresent()) {
			final IRawDataElement fieldColorMap = BBKScolor.get().getSafeElement("TEMPLATE");

			final IRawDataElement valueParameters = fieldColorMap.getSafeElement(templateName);

			ret_color = valueParameters.getSafeString("COLOR");
		}
		return ret_color;
	}

	/**
	 * Gets the track id.
	 *
	 * @param stn the stn
	 * @return the track id
	 */
	public static String getTrackId(final String stn) {
		return Strings.concat("LBL_", stn);
	}

	/**
	 * Gets the flight id.
	 *
	 * @param flight_num the flight num
	 * @return the flight id
	 */
	public static String getFlightId(final String flight_num) {
		return Strings.concat("FLG_", flight_num);
	}

	/**
	 * Gets the flight id.
	 *
	 * @param flight_num the flight num
	 * @return the flight id
	 */
	public static String getProbetId(final String flight_num) {
		return Strings.concat("PROBE_", flight_num);
	}

	/**
	 * Gets the flight id.
	 *
	 * @param flight_num the flight num
	 * @return the flight id
	 */
	public static String getLostFlightId(final String flight_num) {
		return Strings.concat("LOST_", flight_num);
	}

	/**
	 * Gets the esb flight id.
	 *
	 * @param flight_num the flight num
	 * @return the flight id for ESB
	 */
	public static String getFlightEsbId(final String flight_num) {
		return Strings.concat("ESB_", flight_num);
	}

	/**
	 * Gets the flight id.
	 *
	 * @param flight_num the flight num
	 * @return the flight id
	 */
	public static String getVehicleId(final String flight_num) {
		return Strings.concat("VEH_", flight_num);
	}

	/**
	 * Do TCP client sender.
	 *
	 * @param rootData the root data
	 */
	public static void doTCPClientSender(final DataRoot rootData) {
		rootData.getHeaderNode().ifPresent(t -> t.setValidForArps(true));
		ManagerBlackboard.addJVOutputList(rootData);
	}

	/**
	 * Se la data passata e' nel formato "HHmm" lo corregge a "HH:mm:ss".
	 *
	 * @param stringTime the string time
	 * @return the date in "HH:mm:ss" format
	 */
	public static String fixTimeFormat(final String stringTime) {
		if (!Strings.isNullOrEmpty(stringTime)) {
			String newStringTime = stringTime;
			if (!newStringTime.contains(":")) {
				newStringTime = Strings.concat(newStringTime.substring(0, 2), ":", newStringTime.substring(2));
			}
			return newStringTime;
		}
		return stringTime;
	}

	/**
	 * Checks if is sound filter.
	 *
	 * @param functionType the function type
	 * @return boolean
	 */
	public static boolean IsSoundFilter(final String functionType) {

		boolean isFlagSound = false;
		String functionName = functionType;

		if (functionType.equals("DAIW")) {
			functionName = "APW";
		}

		final var json = BlackBoardUtility.getDataOpt(DataType.FUNCTION_FILTER.name());
		if (json.isPresent()) {
			final var filterListArray = json.get().getSafeRawDataArray("FUNCTIONFILTER_NOTIFY_LIST");
			for (final IRawDataElement filterArrayElement : filterListArray) {
				if (filterArrayElement.getSafeString("FUNCTIONFILTER_NOTIFY_FUNCTIONNAME").equals(functionName)) {
					isFlagSound = filterArrayElement.getSafeBoolean("FUNCTIONFILTER_BUZZGLOBAL_DISABLED", false);
					break;
				}
			}
		}
		return !isFlagSound;
	}

	/**
	 * Gets the formatted HHMM.
	 *
	 * @param alarmTime the alarm time
	 * @return the formatted HHMM
	 */
	public static String getFormattedHHMM(final String alarmTime) {
		StringBuilder dateFormatted = new StringBuilder();
		final double seconds;
		if (!alarmTime.isEmpty()) {
			seconds = Integer.parseInt(alarmTime);

			final int hour = (int) (seconds / 3600);
			final int minute = (int) (seconds % 3600 / 60);
			dateFormatted = new StringBuilder(String.format("%02d", hour)).append(":")
					.append(String.format("%02d", minute));
		}

		return dateFormatted.toString();
	}

	/**
	 * Gets the distance double to one digit.
	 *
	 * @param distance the distance
	 * @return the distance double to one digit
	 */
	public static String getDistanceDoubleToOneDigit(final String distance) {

		String formattedString = "";
		final double distanceDouble;
		if (!distance.isEmpty()) {
			distanceDouble = Double.parseDouble(distance);
			formattedString = dfOfgetDistanceDoubleToOneDigit.format(distanceDouble);
		}
		return formattedString;
	}

	/**
	 * Gets the all stn conflict.
	 *
	 * @param stn the stn
	 * @return the all stn conflict
	 */
	public static Map<String, IRawData> getAllStnConflict(final String stn) {
		final var selectedTCTList = BlackBoardUtility.getSelectedData(CommonConstants.TCT,
				new Pair<>("TRACK_NUMBER1", stn));
		final var selectedTCTList2 = BlackBoardUtility.getSelectedData(CommonConstants.TCT,
				new Pair<>("TRACK_NUMBER2", stn));
		final HashMap<String, IRawData> totalMap = new HashMap<>(selectedTCTList);
		totalMap.putAll(selectedTCTList2);
		return totalMap;
	}

	/**
	 * Gets the flight coord key.
	 *
	 * @param flightNum the flight num
	 * @param coordType the coord type
	 * @return the flight coord key
	 */
	public static String getFlightCoordKey(final String flightNum, final CoordType coordType) {
		return flightNum + '_' + coordType.toString();
	}

	/**
	 * Priority freq on.
	 *
	 * @param jsonFlight the json flight
	 * @return the string
	 */
	public static String priorityFreqOn(final IRawData jsonFlight) {

		final boolean wcf = jsonFlight.getSafeBoolean(FlightInputConstants.WCF, false);
		final String warningTraWarn = jsonFlight.getSafeString(FlightInputConstants.MES_TRA_WARN);

		final String warningRev = jsonFlight.getSafeString(FlightInputConstants.MES_REV);
		final String warningTra = jsonFlight.getSafeString(FlightInputConstants.MES_TRA);
		final String dataNff = jsonFlight.getSafeString(FlightInputConstants.NEXT_FIR_FRQ);

		final String template = jsonFlight.getSafeString(FlightInputConstants.TEMPLATE_STRING);
		String visible = "";
		String color = "";

		if (wcf) {
			if (Utils.isMySectorByFreq(dataNff)) {
				if (!template.contains(FlightOutputConstants.TEMPLATE_CONTROLLED)) {
					color = ColorConstants.ORANGE;
				}
			} else if (template.contains(FlightOutputConstants.TEMPLATE_TENTATIVE)) {
				color = ColorConstants.ORANGE;
			}
		} else if (template.contains(FlightOutputConstants.TEMPLATE_CONTROLLED)) {
			if ((warningTraWarn.equals("ROF") || warningTra.equals("SCO") || warningRev.equals("SDM"))) {

				color = (ColorConstants.GREEN);
			}
		}

		if (!color.isEmpty() && (!color.equals(ColorConstants.GREEN) && (!color.equals(ColorConstants.ORANGE))
				&& (!template.equals(FlightOutputConstants.TEMPLATE_HANDOVER_EXITING)))) {
			visible = "true";
		}

		return visible;
	}

	/**
	 * Gets the sector freq.
	 *
	 * @param freq the freq id
	 * @return the sector freq
	 */
	public static boolean isMySectorByFreq(final String freq) {

		int ownP = -1;
		final Optional<IRawData> bbKsource = BlackBoardUtility.getDataOpt(DataType.ENV_MYRESPONSIBILITIES.name());
		final Optional<IRawData> bbKsourceOwn = BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name());
		if (bbKsourceOwn.isPresent()) {
			ownP = bbKsourceOwn.get().getSafeInt("OWNP", -1);
		}
		if (bbKsource.isPresent()) {
			final IRawData json = bbKsource.get();

			final var jsonTable = json.getSafeRawDataArray("ATSULIST");

			for (final com.gifork.commons.data.IRawDataElement jsonElement : jsonTable) {
				if (jsonElement.getSafeString("FREQINUSE").trim().equals(freq.trim())) {
					if (ownP == jsonElement.getSafeInt("SECTOR_ID")) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * isVehicleTemplate.
	 *
	 * @return the VIS_VEHICLE
	 */
	public static boolean isVisVehicle() {
		return !ConfigurationFile.getBoolProperties("IS_NOT_VIS_VEHICLE");
	}
	
	/**
	 * Checks if Prototyping Test is enabled.
	 *
	 * @return true if Prototyping Test is enabled.
	 */
	public static boolean isPrototypingTest() {
		return ConfigurationFile.isPrototypingTest();
	}
	
	/**
	 * forceFieldColor
	 * PROTO: da utilizzare solo per test e prototyping<br>
	 * 
	 * @param rawdataFlight
	 * @param rootData
	 */
	public static void forceFieldColor(final IRawData rawdataFlight, final DataRoot rootData) {
		final var jsonOpt = BlackBoardUtility.getDataOpt(DataType.PROTO_FORCE_FIELD_COLOR.name(),
				rawdataFlight.getSafeString(CommonConstants.FLIGHT_NUM));
		if (!jsonOpt.isEmpty()) {
			IRawDataArray list = jsonOpt.get().getSafeRawDataArray("LIST");
			for (IRawDataElement data : list) {
				String fieldName = data.getSafeString("FIELD");
				String fieldColor = data.getSafeString("VALUE");
				DataNode dataNode = rootData.getHeaderNode().get().getLine(fieldName);
				if (dataNode != null) {
					dataNode.setAttribute(EdmModelKeys.Attributes.COLOR, fieldColor);
				}
			}
		}
	}
	
	/**
	 * forceAllFieldsColor
	 * PROTO: da utilizzare solo per test e prototyping<br>
	 * 
	 * @param rawdataFlight
	 * @param rootData
	 */
	public static void forceAllFieldsColor(final IRawData rawdataFlight, final DataRoot rootData) {
		final var jsonOpt = BlackBoardUtility.getDataOpt(DataType.PROTO_FORCE_ALLFIELDS_COLOR.name(),
				rawdataFlight.getSafeString(CommonConstants.FLIGHT_NUM));
		if (!jsonOpt.isEmpty()) {
			String color = jsonOpt.get().getSafeString("COLOR");
			for (DataNode dataNode : rootData.getHeaderNode().get().getNodes()) {
				dataNode.setAttribute(EdmModelKeys.Attributes.COLOR, color);
			}
		}
	}
	
	/**
	 * forceFieldValuePRE
	 * PROTO: da utilizzare solo per test e prototyping<br>
	 * 
	 * @param rawdataFlight
	 * @param isTrack 
	 */
	public static void forceFieldValuePRE(final IRawData rawdataFlight, boolean isTrack) {
		final var jsonOpt = BlackBoardUtility.getDataOpt(DataType.PROTO_FORCE_FIELD_VALUE_PRE.name(),
				rawdataFlight.getSafeString(CommonConstants.FLIGHT_NUM));
		if (!jsonOpt.isEmpty()) {
			if(isTrack) {
				boolean isToForceTrack = jsonOpt.get().getSafeBoolean("FORCE_TRACK");
				if(!isToForceTrack) {
					return;
				} 
			}
			
			IRawDataArray list = jsonOpt.get().getSafeRawDataArray("LIST");
			for (IRawDataElement data : list) {
				String fieldName = data.getSafeString("FIELD");
				String fieldValue = data.getSafeString("VALUE");
				rawdataFlight.put(fieldName, fieldValue);
			}
		}
	}
	
	/**
	 * forceFieldValuePOST
	 * PROTO: da utilizzare solo per test e prototyping<br>
	 * 
	 * @param rawdataFlight
	 * @param rootData
	 * @param isTrack 
 	 */
	public static void forceFieldValuePOST(final IRawData rawdataFlight, final DataRoot rootData, boolean isTrack) {
		final var jsonOpt = BlackBoardUtility.getDataOpt(DataType.PROTO_FORCE_FIELD_VALUE_POST.name(),
				rawdataFlight.getSafeString(CommonConstants.FLIGHT_NUM));
		if (!jsonOpt.isEmpty()) {
			if(isTrack) {
				boolean isToForceTrack = jsonOpt.get().getSafeBoolean("FORCE_TRACK");
				if(!isToForceTrack) {
					return;
				} 
			}
			
			IRawDataArray list = jsonOpt.get().getSafeRawDataArray("LIST");
			for (IRawDataElement data : list) {
				String fieldName = data.getSafeString("FIELD");
				String fieldValue = data.getSafeString("VALUE");
				rootData.getHeaderNode().get().addLine(fieldName, fieldValue);
			}
		}
	}

}
