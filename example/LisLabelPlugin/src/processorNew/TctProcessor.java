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

import java.util.Optional;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.commons.data.IRawData;
import com.leonardo.infrastructure.Strings;

import analyzer.AnalyzerLabel;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import auxiliary.tct.TctAggregatorMap;
import auxiliary.tct.TctBlackMap;
import auxiliary.track.TrackInputConstants;
import auxiliary.track.TrackOutputConstants;
import common.ColorConstants;
import common.CommonConstants;
import common.Utils;

/**
 * The Class TctProcessor.
 */
public enum TctProcessor {
	;

	/** The Constant tctAggregatorMap. */
	private static final TctAggregatorMap tctAggregatorMap = new TctAggregatorMap();

	/** The Constant tctBlackMap. */
	private static final TctBlackMap tctBlackMap = new TctBlackMap();

	/**
	 * Analyze tct.
	 *
	 * @param rawdataTrack the rawdata track
	 * @param objectNode   the object node
	 */
	private static void analyzeTct(final IRawData rawdataTrack, final HeaderNode objectNode) {

		if (!rawdataTrack.isEmpty()) {
			final String stn = rawdataTrack.getSafeString(CommonConstants.STN);
			final var totalMap = Utils.getAllStnConflict(stn);

			if (totalMap.size() > 0) {
//				final var conflictNode = objectNode.addNode(EdmModelKeys.Conflicts.TCTCONFLICTS);
				StringBuilder dataConfId = new StringBuilder();
				for (final String jsTCT : totalMap.keySet()) {
					final IRawData jsonTctMap = totalMap.get(jsTCT);

//					final var conflictNodeTrack = conflictNode.addNode(EdmModelKeys.Conflicts.CONFLICT).setAttribute(
//							EdmModelKeys.Conflicts.ID, jsonTctMap.getSafeString(CommonConstants.CONFLICT_NUMBER));

					String id = jsonTctMap.getSafeString(CommonConstants.CONFLICT_NUMBER);
					dataConfId.append(id).append(",");

					if (stn.equals(jsonTctMap.getSafeString(CommonConstants.TRACK_NUMBER1))) {
						String pointId = "1";
						String key = Strings.concat(EdmModelKeys.Conflicts.KEY_TCT_PREFIX, id, "#", pointId);
						objectNode.addLine(key, pointId).setAttribute(EdmModelKeys.ConflictPoint.ID, pointId)
								.setAttribute(EdmModelKeys.ConflictPoint.X,
										jsonTctMap.getSafeString(CommonConstants.X1_AT_MINIMUM_DISTANCE))
								.setAttribute(EdmModelKeys.ConflictPoint.Y,
										jsonTctMap.getSafeString(CommonConstants.Y1_AT_MINIMUM_DISTANCE));

						pointId = "0";
						key = Strings.concat(EdmModelKeys.Conflicts.KEY_TCT_PREFIX, id, "#", pointId);
						objectNode.addLine(key, pointId).setAttribute(EdmModelKeys.ConflictPoint.ID, pointId)
								.setAttribute(EdmModelKeys.ConflictPoint.X,
										jsonTctMap.getSafeString(CommonConstants.X1_AT_CONFLICT_TIME))
								.setAttribute(EdmModelKeys.ConflictPoint.Y,
										jsonTctMap.getSafeString(CommonConstants.Y1_AT_CONFLICT_TIME));
					} else {

						String pointId = "1";
						String key = Strings.concat(EdmModelKeys.Conflicts.KEY_TCT_PREFIX, id, "#", pointId);
						objectNode.addLine(key, "1").setAttribute(EdmModelKeys.ConflictPoint.ID, pointId)
								.setAttribute(EdmModelKeys.ConflictPoint.X,
										jsonTctMap.getSafeString(CommonConstants.X2_AT_MINIMUM_DISTANCE))
								.setAttribute(EdmModelKeys.ConflictPoint.Y,
										jsonTctMap.getSafeString(CommonConstants.Y2_AT_MINIMUM_DISTANCE));

						pointId = "0";
						key = Strings.concat(EdmModelKeys.Conflicts.KEY_TCT_PREFIX, id, "#", pointId);
						objectNode.addLine(key, "0").setAttribute(EdmModelKeys.ConflictPoint.ID, pointId)
								.setAttribute(EdmModelKeys.ConflictPoint.X,
										jsonTctMap.getSafeString(CommonConstants.X2_AT_CONFLICT_TIME))
								.setAttribute(EdmModelKeys.ConflictPoint.Y,
										jsonTctMap.getSafeString(CommonConstants.Y2_AT_CONFLICT_TIME));

					}
				}
				objectNode.addLine(EdmModelKeys.Conflicts.KEY_TCT_INDEX, dataConfId.toString());
			}
		}
	}

	/**
	 * Process.
	 *
	 * @param rawdataTct the rawdata tct
	 */
	public static void process(final IRawData rawdataTct) {

		final String stnTct1 = rawdataTct.getSafeString(CommonConstants.TRACK_NUMBER1);
		final String stnTct2 = rawdataTct.getSafeString(CommonConstants.TRACK_NUMBER2);

		final var rawdataTrack1 = Utils_LIS.getTrkFromStn(stnTct1);
		final var rawdataTrack2 = Utils_LIS.getTrkFromStn(stnTct2);

		rawdataTrack1.ifPresent(iRawData -> addAlarmTrack(rawdataTct, iRawData));
		rawdataTrack2.ifPresent(iRawData -> addAlarmTrack(rawdataTct, iRawData));

		if (rawdataTrack1.isPresent() && rawdataTrack2.isPresent()) {
			final var rawDataFlight1 = Utils_LIS.getFlightFromStn(stnTct1);
			final var rawDataFlight2 = Utils_LIS.getFlightFromStn(stnTct2);

			sendTCTConflict(rawdataTrack1.get(), rawdataTrack2.get(), rawDataFlight1, rawDataFlight2, rawdataTct);
		}
	}

	/**
	 * Adds the alarm track.
	 *
	 * @param rawdataTct the rawdata tct
	 * @param trackData  the track data
	 */
	private static void addAlarmTrack(final IRawData rawdataTct, final IRawData trackData) {
		rawdataTct.put(CommonConstants.STN, trackData.getSafeString(CommonConstants.STN));
		XMLCreation(rawdataTct, trackData);
	}

	/**
	 * XML creation.
	 *
	 * @param rawdataTct the rawdata tct
	 * @param trackData  the track data
	 */
	private static void XMLCreation(final IRawData rawdataTct, final IRawData trackData) {
		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
		Utils.addElementTrack(trackData, objectNode);

		if (!trackData.isEmpty()) {
			final Optional<IRawData> flight = Utils_LIS.getFlightFromTrkId(trackData.getId());
			Utils.addTemplate(Optional.of(trackData), flight, objectNode);
		}

		AnalyzerLabel.analyze(tctBlackMap, tctAggregatorMap, null, rawdataTct, objectNode);
		analyzeTct(trackData, objectNode);
		Utils.doTCPClientSender(rootData);

	}

	/**
	 * Delete.
	 *
	 * @param rawdataTct the rawdata tct
	 */
	public static void delete(final IRawData rawdataTct) {

		final String stnTct1 = rawdataTct.getSafeString(CommonConstants.TRACK_NUMBER1);
		final String stnTct2 = rawdataTct.getSafeString(CommonConstants.TRACK_NUMBER2);

		final var rawdataTrack1 = Utils_LIS.getTrkFromStn(stnTct1);
		rawdataTrack1.ifPresent(iRawData -> deleteConflictOnTrack(rawdataTct, iRawData));

		final var rawdataTrack2 = Utils_LIS.getTrkFromStn(stnTct2);
		rawdataTrack2.ifPresent(iRawData -> deleteConflictOnTrack(rawdataTct, iRawData));

		final var rootData = DataRoot.createMsg();
		rootData.addHeaderOfObject(Operation.DELETE, EdmModelKeys.HeaderType.TCT_CONFLICT)
				.addLine(CommonConstants.CONFLICT_ID, rawdataTct.getSafeString(CommonConstants.CONFLICT_NUMBER));

		Utils.doTCPClientSender(rootData);
	}

	/**
	 * Delete conflict on track.
	 *
	 * @param rawdataTct the rawdata tct
	 * @param trackData  the track data
	 */
	private static void deleteConflictOnTrack(final IRawData rawdataTct, final IRawData trackData) {
		AnalyzerLabel.alarmedTct.remove(trackData.getSafeString(CommonConstants.STN));
		rawdataTct.put(CommonConstants.STN, trackData.getSafeString(CommonConstants.STN));
		XMLCreation(rawdataTct, trackData);
	}

	/**
	 * Send TCT conflict.
	 *
	 * @param jsonTrack1  the json track 1
	 * @param jsonTrack2  the json track 2
	 * @param jsonFlight1 the json flight 1
	 * @param jsonFlight2 the json flight 2
	 * @param jsonTct     the json tct
	 */
	private static void sendTCTConflict(final IRawData jsonTrack1, final IRawData jsonTrack2,
			final Optional<IRawData> jsonFlight1, final Optional<IRawData> jsonFlight2, final IRawData jsonTct) {

		String emergencyTct;
		String arrow = "";
		String colorArrow;

		final int GNDTrack_1 = jsonTrack1.getSafeInt("GND");
		final int GNDTrack_2 = jsonTrack2.getSafeInt("GND");

		final String alarmType = jsonTct.getSafeString(CommonConstants.ALARM_TYPE);
		String outAlarmTctType = alarmType;

		switch (alarmType) {
		case "TMM":
			emergencyTct = "TMM";
			colorArrow = ColorConstants.ORANGE;
			break;

		case "TSV":
		case "TTT":
		case "TTS":
			emergencyTct = "TCA";
			colorArrow = ColorConstants.RED;
			break;

		case "HTSV":
		case "HTTT":
		case "HTTS":
		case "VTSV":
		case "VTTT":
		case "VTTS":
			emergencyTct = "CONTEXT";
			colorArrow = ColorConstants.MAGENTA;

			outAlarmTctType = alarmType.substring(1, 4);
			break;

		default:
			emergencyTct = "";
			colorArrow = "";
			break;
		}

		if (GNDTrack_1 > GNDTrack_2) {
			arrow = "LEFT";
		} else if (GNDTrack_1 < GNDTrack_2) {
			arrow = "RIGHT";
		} else {
			colorArrow = "";
		}

		final var rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TCT_CONFLICT);

		objectNode.addLine(FlightOutputConstants.TEMPLATE, "TCT_" + emergencyTct);
		objectNode.addLine(CommonConstants.CATEGORY, jsonTct.getSafeString(CommonConstants.ALARM_TYPE));
		objectNode.addLine(CommonConstants.URGENCY, outAlarmTctType);

		String tctSyle = "TCT_" + jsonTct.getSafeString(CommonConstants.ALARM_TYPE);
		if (jsonFlight1.isPresent() && !jsonFlight1.get().getSafeString("LAT").isEmpty()) {
			tctSyle = "TCT_TSV";
		}

		objectNode.addLine(CommonConstants.STYLE_FIRST_LINE, tctSyle);

		String conflictTime = Utils.getFormattedHHMM(jsonTct.getSafeString(CommonConstants.CONFLICT_TIME));
		objectNode.addLine(CommonConstants.TCT_TIME, conflictTime);

		objectNode.addLine(FlightOutputConstants.CALLSIGN + "1",
				(jsonFlight1.isPresent() ? jsonFlight1.get().getSafeString(FlightOutputConstants.CALLSIGN)
						: (!(jsonTrack1.getSafeString(TrackInputConstants.RCS).isEmpty())
								? jsonTrack1.getSafeString(TrackInputConstants.RCS)
								: jsonTrack1.getSafeString(TrackInputConstants.MODE_3A))));

		objectNode.addLine(FlightOutputConstants.CALLSIGN + "2",
				(jsonFlight2.isPresent() ? jsonFlight2.get().getSafeString(FlightOutputConstants.CALLSIGN)
						: (!(jsonTrack2.getSafeString(TrackInputConstants.RCS).isEmpty())
								? jsonTrack2.getSafeString(TrackInputConstants.RCS)
								: jsonTrack2.getSafeString(TrackInputConstants.MODE_3A))));

		objectNode.addLine(CommonConstants.X1_AT_MINIMUM_DISTANCE,
				jsonTct.getSafeString(CommonConstants.X1_AT_MINIMUM_DISTANCE));
		objectNode.addLine(CommonConstants.Y1_AT_MINIMUM_DISTANCE,
				jsonTct.getSafeString(CommonConstants.Y1_AT_MINIMUM_DISTANCE));
		objectNode.addLine(CommonConstants.X1_AT_CONFLICT_TIME,
				jsonTct.getSafeString(CommonConstants.X1_AT_CONFLICT_TIME));
		objectNode.addLine(CommonConstants.Y1_AT_CONFLICT_TIME,
				jsonTct.getSafeString(CommonConstants.Y1_AT_CONFLICT_TIME));

		objectNode.addLine(CommonConstants.X2_AT_MINIMUM_DISTANCE,
				jsonTct.getSafeString(CommonConstants.X2_AT_MINIMUM_DISTANCE));
		objectNode.addLine(CommonConstants.Y2_AT_MINIMUM_DISTANCE,
				jsonTct.getSafeString(CommonConstants.Y2_AT_MINIMUM_DISTANCE));
		objectNode.addLine(CommonConstants.X2_AT_CONFLICT_TIME,
				jsonTct.getSafeString(CommonConstants.X2_AT_CONFLICT_TIME));
		objectNode.addLine(CommonConstants.Y2_AT_CONFLICT_TIME,
				jsonTct.getSafeString(CommonConstants.Y2_AT_CONFLICT_TIME));

		objectNode.addLine(TrackOutputConstants.AFL + "1", jsonTrack1.getSafeString(TrackInputConstants.LEV));
		objectNode.addLine(TrackOutputConstants.AFL + "2", jsonTrack2.getSafeString(TrackInputConstants.LEV));

		objectNode.addLine("ATYPE1",
				(jsonFlight1.isPresent() ? jsonFlight1.get().getSafeString(FlightInputConstants.ATY) : ""));
		objectNode.addLine("ATYPE2",
				(jsonFlight2.isPresent() ? jsonFlight2.get().getSafeString(FlightInputConstants.ATY) : ""));

		objectNode.addLine(FlightInputConstants.EFX + "1",
				(jsonFlight1.isPresent() ? jsonFlight1.get().getSafeString(FlightInputConstants.EFIX) : ""));
		objectNode.addLine(FlightInputConstants.EFX + "2",
				(jsonFlight2.isPresent() ? jsonFlight2.get().getSafeString(FlightInputConstants.EFIX) : ""));

		final String actDIST = Utils
				.getDistanceDoubleToOneDigit(jsonTct.getSafeString(CommonConstants.PRESENT_PLANAR_DISTANCE));
		final String minDIST = Utils
				.getDistanceDoubleToOneDigit(jsonTct.getSafeString(CommonConstants.ESTIMATED_MINIMUM_PLANAR_DISTANCE));
		final String sepDIST = Utils
				.getDistanceDoubleToOneDigit(jsonTct.getSafeString(CommonConstants.D_AT_CONFLICT_TIME));
		final String dist1 = Utils
				.getDistanceDoubleToOneDigit(jsonTct.getSafeString(CommonConstants.EST_DISTANCE_TO_SEP_LOSS_1));
		final String dist2 = Utils
				.getDistanceDoubleToOneDigit(jsonTct.getSafeString(CommonConstants.EST_DISTANCE_TO_SEP_LOSS_2));

		final String distToMD1 = Utils.getDistanceDoubleToOneDigit(jsonTct.getSafeString(CommonConstants.D_TO_MD1));
		final String distToMD2 = Utils.getDistanceDoubleToOneDigit(jsonTct.getSafeString(CommonConstants.D_TO_MD2));

		objectNode.addLine(CommonConstants.ACT_DIST, actDIST);
		objectNode.addLine(CommonConstants.MIN_DIST, minDIST);
		objectNode.addLine(CommonConstants.SEP_DIST, sepDIST);

		objectNode.addLine(CommonConstants.DIST + "1", dist1);
		objectNode.addLine(CommonConstants.DIST + "2", dist2);

		objectNode.addLine(CommonConstants.D_TO_MD1, distToMD1);
		objectNode.addLine(CommonConstants.D_TO_MD2, distToMD2);

		String trk1 = Utils.getTrackId(Utils_LIS.getTrkIdFromStn(jsonTct.getSafeString(CommonConstants.TRACK_NUMBER1)));
		String trk2 = Utils.getTrackId(Utils_LIS.getTrkIdFromStn(jsonTct.getSafeString(CommonConstants.TRACK_NUMBER2)));

		objectNode.addLine(CommonConstants.ID + "1", trk1);
		objectNode.addLine(CommonConstants.ID + "2", trk2);

		objectNode.addLine(CommonConstants.TIME_MIN_DIST,
				Utils_LIS.formatSecondsToMMSS(jsonTct.getSafeInt(CommonConstants.TIME_TO_CONFLICT)));
		objectNode.addLine(CommonConstants.CONFLICT_ID, jsonTct.getSafeString(CommonConstants.CONFLICT_NUMBER));
		objectNode.addLine(CommonConstants.ARROW, arrow).setAttribute(EdmModelKeys.Attributes.COLOR, colorArrow);

		final var tctConflictsNode = objectNode.addNode("trajectories");

		final var tctConflictsNodeTrack1 = tctConflictsNode.addNode("track")
				.setAttribute("id", CommonConstants.ID + "1").setAttribute("data", trk1);
		tctConflictsNodeTrack1.addNode("point").setAttribute("id", "1")
				.setAttribute("x", jsonTct.getSafeString(CommonConstants.X1_AT_MINIMUM_DISTANCE))
				.setAttribute("y", jsonTct.getSafeString(CommonConstants.Y1_AT_MINIMUM_DISTANCE));
		tctConflictsNodeTrack1.addNode("point").setAttribute("id", "0")
				.setAttribute("x", jsonTct.getSafeString(CommonConstants.X1_AT_CONFLICT_TIME))
				.setAttribute("y", jsonTct.getSafeString(CommonConstants.Y1_AT_CONFLICT_TIME));

		final var tctConflictsNodeTrack2 = tctConflictsNode.addNode("track")
				.setAttribute("id", CommonConstants.ID + "2").setAttribute("data", trk2);
		tctConflictsNodeTrack2.addNode("point").setAttribute("id", "1")
				.setAttribute("x", jsonTct.getSafeString(CommonConstants.X2_AT_MINIMUM_DISTANCE))
				.setAttribute("y", jsonTct.getSafeString(CommonConstants.Y2_AT_MINIMUM_DISTANCE));
		tctConflictsNodeTrack2.addNode("point").setAttribute("id", "0")
				.setAttribute("x", jsonTct.getSafeString(CommonConstants.X2_AT_CONFLICT_TIME))
				.setAttribute("y", jsonTct.getSafeString(CommonConstants.Y2_AT_CONFLICT_TIME));

		Utils.doTCPClientSender(rootData);
	}

}
