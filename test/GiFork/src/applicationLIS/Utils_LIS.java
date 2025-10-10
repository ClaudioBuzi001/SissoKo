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
package applicationLIS;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;
import com.gifork.commons.data.RawDataFactory;
import com.leonardo.infrastructure.Pair;
import com.leonardo.infrastructure.Strings;

import applicationLIS.BlackBoardConstants_LIS.DataType;

/**
 * @author ALBANESED
 *
 */
public class Utils_LIS {

	/** The RST_VALIDITY. */
	public static final String RST_VALIDITY = "RST_VALIDITY";
	/** The EMG_RPAS_C2L. */
	public static final String EMG_RPAS_C2L = "C2L";
	/** The EMG_RPAS_DAAL. */
	public static final String EMG_RPAS_DAAL = "DAAL";

	/**
	 * correctionTableQnh
	 */
	public final static int[] CORRECTION_TABLE_QNH = { -31, -31, -31, -30, -30, -30, -30, -29, -29, -29, -29, -28, -28,
			-28, -27, -27, -27, -27, -26, -26, -26, -26, -25, -25, -25, -24, -24, -24, -24, -23, -23, -23, -22, -22,
			-22, -22, -21, -21, -21, -21, -20, -20, -20, -19, -19, -19, -19, -18, -18, -18, -17, -17, -17, -17, -16,
			-16, -16, -16, -15, -15, -15, -14, -14, -14, -14, -13, -13, -13, -13, -12, -12, -12, -11, -11, -11, -11,
			-10, -10, -10, -10, -9, -9, -9, -8, -8, -8, -8, -7, -7, -7, -6, -6, -6, -6, -5, -5, -5, -4, -4, -4, -4, -3,
			-3, -3, -3, -2, -2, -2, -1, -1, -1, -1, 0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 5, 6,
			6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 9, 9, 9, 9, 10, 10, 10, 10, 11, 11, 11, 11, 12, 12, 12, 13, 13, 13, 13, 14,
			14, 14, 14 };

	/**
	 * Gets the call sign WTC.
	 *
	 * @param stn the stn
	 * @return the call sign WTC
	 */
	public static String getCSOrRcs(final String stn) {
		String ret_value = "";
		String CS = "";

		String trkId = getTrkIdFromStn(stn);

		final Optional<IRawData> jTRACK = BlackBoardUtility.getDataOpt(DataType.TRACK.name(), trkId);
		if (jTRACK.isPresent()) {
			String RCS = jTRACK.get().getSafeString("RCS");

			final Optional<IRawData> jFLIGHT1 = BlackBoardUtility.getDataOpt(DataType.BB_TRKID_FLIGHTNUM.name(), trkId);
			if (jFLIGHT1.isPresent()) {
				final String flight_num = jFLIGHT1.get().getSafeString("FLIGHT_NUM");

				final Optional<IRawData> jsonFlight = BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(),
						flight_num);
				CS = jsonFlight.isPresent() ? jsonFlight.get().getSafeString("CALLSIGN") : "";
			}
			if (!CS.isEmpty()) {
				ret_value = CS;
			} else if (!RCS.isEmpty()) {
				ret_value = RCS;
			}
		}

		return ret_value;
	}

	/*
	 * @param time return conflict time data into expected format
	 */
	/**
	 * Gets the formatted HHMM.
	 *
	 * @param alarmTime the alarm time
	 * @return the formatted HHMM
	 */

	public static String getFormattedHHMM(final String alarmTime) {
		String dateFormatted = "";
		final double seconds;
		if (alarmTime != null && !alarmTime.isEmpty()) {
			seconds = Integer.parseInt(alarmTime);

			final int hour = (int) (seconds / 3600);
			final int minute = (int) (seconds % 3600 / 60);
			dateFormatted = Strings.concat(String.format("%02d", hour), ":", String.format("%02d", minute));
		}

		return dateFormatted;
	}

	/**
	 * Gets the formatted MMSS.
	 *
	 * @param alarmTime the alarm time
	 * @return the formatted MMSS
	 */
	public static String getFormattedMMSS(final String alarmTime) {
		String dateFormatted = "";
		final double seconds;
		if (alarmTime != null && !alarmTime.isEmpty()) {
			seconds = Double.parseDouble(alarmTime);

			final int minute = (int) (seconds / 60);
			final int second = (int) (seconds - (minute * 60));

			dateFormatted = Strings.concat(String.format("%02d", minute), ":", String.format("%02d", second));

		}
		return dateFormatted;
	}

	/**
	 * Gets the distance double to two digit.
	 *
	 * @param distance the distance
	 * @return the distance double to two digit
	 */
	public static String getDistanceDoubleToTwoDigit(final String distance) {
		final DecimalFormat df = new DecimalFormat("#.##");
		df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		String formattedString = "";
		final double distanceDouble;
		if (distance != null && !distance.isEmpty()) {
			distanceDouble = Double.parseDouble(distance);
			formattedString = df.format(distanceDouble);
		}
		return formattedString;
	}

	/**
	 * Gets the all stn conflict.
	 *
	 * @param stn the stn
	 * @return the all stn conflict
	 */
	public static Stream<IRawData> getAllScaConflict(final String stn) {
		final var selectedTCTList = BlackBoardUtility.getSelectedData(DataType.SCA.name(), new Pair<>("STN_1", stn));
		final var selectedTCTList2 = BlackBoardUtility.getSelectedData(DataType.SCA.name(), new Pair<>("STN_2", stn));
		final HashMap<String, IRawData> totalMap = new HashMap<>(selectedTCTList);
		totalMap.putAll(selectedTCTList2);
		return totalMap.values().stream().filter(it -> !it.getSafeBoolean("ACK_SCA")).distinct();
	}

	/**
	 * Gets the prioirty sca conflict.
	 *
	 * @param stn the stn
	 * @return the prioirty sca conflict
	 */
	public static String getPrioirtyScaConflict(String stn) {

		return getAllScaConflict(stn)
				.map(x -> new Pair<>(x.getSafeString("CONFLICT_NUMBER", ""), x.getSafeInt("ALERT_PRIORITY", 9999)))
				.min(Comparator.comparing(Pair::getY)).map(Pair::getX).orElse("");
	}

	/**
	 * Format seconds to MMSS.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static String formatSecondsToMMSS(final long value) {
		final var totalSeconds = value < 0 ? 0 : value;

		final long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds);
		final long seconds = TimeUnit.SECONDS.toSeconds(totalSeconds) - TimeUnit.MINUTES.toSeconds(minutes);

		return String.format("%02d:%02d", minutes, seconds);
	}

	/**
	 * Gets the track id from stn.
	 *
	 * @param stn the stn
	 * @return the track id from stn
	 */
	public static String getTrkIdFromStn(String stn) {
		Optional<IRawData> trkIdObj = BlackBoardUtility.getDataOpt(DataType.STN_TRKID.name(), stn);
		if (trkIdObj.isPresent()) {
			return trkIdObj.get().getSafeString("TRACK_ID");
		}
		return "";
	}

	/**
	 * Gets the track id from stn.
	 *
	 * @param fn the fn
	 * @return the track id from stn
	 */
	public static String getTrkIdFromFn(String fn) {
		Optional<IRawData> trkIdObj = BlackBoardUtility.getDataOpt(DataType.BB_FLIGHTNUM_TRKID.name(), fn);
		if (trkIdObj.isPresent()) {
			return trkIdObj.get().getSafeString("TRACK_ID");
		}
		return "";
	}

	/**
	 * Gets the track id from stn.
	 *
	 * @param flightNum the flight num
	 * @return the track id from stn
	 */
	public static Optional<IRawData> getTrkFromFn(String flightNum) {
		Optional<IRawData> trkID = BlackBoardUtility.getDataOpt(DataType.BB_FLIGHTNUM_TRKID.name(), flightNum);
		if (trkID.isPresent()) {
			String trkId = trkID.get().getSafeString("TRACK_ID");
			return BlackBoardUtility.getDataOpt(DataType.TRACK.name(), trkId);
		}
		return Optional.empty();
	}

	/**
	 * Gets the trk from cs.
	 *
	 * @param callsign the callsign
	 * @return the trk from cs
	 */
	public static Optional<IRawData> getTrkFromCs(String callsign) {
		Optional<IRawData> flightNumData = BlackBoardUtility.getDataOpt(DataType.BB_CALLSIGN_FLIGHT.name(), callsign);
		if (flightNumData.isPresent()) {
			String flightNum = flightNumData.get().getSafeString("FLIGHT_NUM");
			Optional<IRawData> trkID = BlackBoardUtility.getDataOpt(DataType.BB_FLIGHTNUM_TRKID.name(), flightNum);
			if (trkID.isPresent()) {
				String trkId = trkID.get().getSafeString("TRACK_ID");
				return BlackBoardUtility.getDataOpt(DataType.TRACK.name(), trkId);
			}
		}

		return Optional.empty();
	}

	/**
	 * Gets the flightNum from Callsign.
	 *
	 * @param callsign the callsign
	 * @return the flightNumber
	 */
	public static Optional<String> getFlightNumFromCallsign(String callsign) {

		Optional<IRawData> flightNumData = BlackBoardUtility.getDataOpt(DataType.BB_CALLSIGN_FLIGHT.name(), callsign);
		if (flightNumData.isPresent()) {
			String flightNum = flightNumData.get().getSafeString("FLIGHT_NUM");
			return Optional.of(flightNum);
		}

		return Optional.empty();
	}

	/**
	 * Gets the flight from cs.
	 *
	 * @param callsign the callsign
	 * @return the flight from cs
	 */
	public static Optional<IRawData> getFlightFromCs(String callsign) {
		Optional<IRawData> flightNumData = BlackBoardUtility.getDataOpt(DataType.BB_CALLSIGN_FLIGHT.name(), callsign);
		if (flightNumData.isPresent()) {
			String flightNum = flightNumData.get().getSafeString("FLIGHT_NUM");
			return BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(), flightNum);
		}

		return Optional.empty();
	}

	/**
	 * Gets the template color from cs.
	 *
	 * @param callsign the callsign
	 * @return the template color from cs
	 */
	public static String getTemplateColorFromCs(String callsign) {
		Optional<IRawData> flight = getFlightFromCs(callsign);
		if (flight.isPresent()) {
			return flight.get().getSafeString("TEMPLATE_COLOR");
		}

		return "";
	}

	/**
	 * Gets the track id from stn.
	 *
	 * @param stn the stn
	 * @return the track id from stn
	 */
	public static Optional<IRawData> getTrkFromStn(String stn) {
		Optional<IRawData> trkID = BlackBoardUtility.getDataOpt(DataType.STN_TRKID.name(), stn);
		if (trkID.isPresent()) {
			String trkId = trkID.get().getSafeString("TRACK_ID");
			return BlackBoardUtility.getDataOpt(DataType.TRACK.name(), trkId);
		}
		return Optional.empty();
	}

	/**
	 * Gets the track id from stn.
	 *
	 * @param flightNum the flight num
	 * @return the track id from stn
	 */
	public static String getStnFromFn(String flightNum) {
		Optional<IRawData> trkID = BlackBoardUtility.getDataOpt(DataType.BB_FLIGHTNUM_TRKID.name(), flightNum);
		if (trkID.isPresent()) {
			String trkId = trkID.get().getSafeString("TRACK_ID");
			Optional<IRawData> trk = BlackBoardUtility.getDataOpt(DataType.TRACK.name(), trkId);
			if (trk.isPresent()) {
				return trk.get().getSafeString("STN");
			}
		}
		return "";
	}

	/**
	 * @param callsign the callsign
	 * @return true, if is in tentative conflict
	 */
	public static boolean isInTentativeConflict(final String callsign) {
		final var selectedMTCDList = BlackBoardUtility.getSelectedData(DataType.MTCD_PROBE_ITEM_NOTIFY.name(),
				new Pair<>("FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_1", callsign));

		final var selectedMTCDList2 = BlackBoardUtility.getSelectedData(DataType.MTCD_PROBE_ITEM_NOTIFY.name(),
				new Pair<>("FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_2", callsign));

		Optional<IRawData> flight = BlackBoardUtility.getDataOpt(DataType.BB_CALLSIGN_FLIGHT.name(), callsign);

		String stn = "";
		if (flight.isPresent()) {
			String flightN = flight.get().getSafeString("FLIGHT_NUM");
			getStnFromFn(flightN);
		}

		final var selectedTCTList = BlackBoardUtility.getSelectedData(DataType.TCT_TENTATIVE.name(),
				new Pair<>("TRACK_NUMBER1", stn));
		final var selectedTCTList2 = BlackBoardUtility.getSelectedData(DataType.TCT_TENTATIVE.name(),
				new Pair<>("TRACK_NUMBER2", stn));

		if (!selectedMTCDList.isEmpty() || !selectedMTCDList2.isEmpty() || !selectedTCTList.isEmpty()
				|| !selectedTCTList2.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the flight from stn.
	 *
	 * @param stn the stn
	 * @return the flight from stn
	 */
	public static Optional<IRawData> getFlightFromStn(String stn) {
		String trkId = getTrkIdFromStn(stn);
		return getFlightFromTrkId(trkId);

	}

	/**
	 * Gets the flight from trk id.
	 *
	 * @param trkId the trk id
	 * @return the flight from trk id
	 */
	public static Optional<IRawData> getFlightFromTrkId(String trkId) {
		Optional<IRawData> flight = BlackBoardUtility.getDataOpt(DataType.BB_TRKID_FLIGHTNUM.name(), trkId);
		if (flight.isPresent()) {
			String fNum = flight.get().getSafeString("FLIGHT_NUM");
			return BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(), fNum);
		}
		return Optional.empty();
	}

	/**
	 * Gets the callsign from the flightNum.
	 *
	 * @param fNum
	 * @return the callsign from the flightNum
	 */
	public static String getCallsignFromFlightNum(String fNum) {
		String callsign = "";
		var optCallsign = BlackBoardUtility.getSelectedData(DataType.BB_CALLSIGN_FLIGHT.name(), "FLIGHT_NUM", fNum)
				.values().stream().findFirst();
		if (optCallsign.isPresent()) {
			callsign = optCallsign.get().getId();
		}
		return callsign;
	}

	/**
	 * Gets the flight from trk id.
	 *
	 * @param trkId the trk id
	 * @return the flight from trk id
	 */
	public static String getFnFromTrkId(String trkId) {
		Optional<IRawData> flight = BlackBoardUtility.getDataOpt(DataType.BB_TRKID_FLIGHTNUM.name(), trkId);
		if (flight.isPresent()) {
			return flight.get().getSafeString("FLIGHT_NUM");
		}
		return "";
	}

	/**
	 * Search searchSectName_FRomHDI_SCT_Level.
	 *
	 * @param sct the sct
	 * @return the string
	 */
	public static String searchSectName_FRomHDI_SCT_Level(String sct) {

		String sectName = "";
		var sectorTableJSon = BlackBoardUtility.getDataOpt(DataType.ENV_HDI_SECTOR_LEVEL.name());
		if (sectorTableJSon.isPresent()) {
			var jsonTable = sectorTableJSon.get().getSafeRawDataArray("SECTOR_ARRAY_LIST");

			for (IRawDataElement jsonElement : jsonTable) {
				if (sct != null && !sct.isEmpty() && jsonElement.getSafeInt("ROP") == Integer.parseInt(sct)) {
					return jsonElement.getSafeString("SECTOR_NAME");
				}
			}
		}
		return sectName;
	}

	/**
	 * Gets the alarm conflict time.
	 *
	 * @param alarmTime the alarm time
	 * @return the alarm conflict time
	 */
	public static String getAlarmConflictTime(final String alarmTime) {
		String hms = "";
		if (alarmTime != null && !alarmTime.isEmpty()) {
			final long maxTime = 6000;
			final long millis = Long.parseLong(alarmTime);
			if (millis < maxTime) {
				hms = String.format("%02d:%02d", TimeUnit.SECONDS.toMinutes(millis), TimeUnit.SECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(millis)));
			}
		}
		return hms;
	}

	/**
	 * Gets the alarm conflict type.
	 *
	 * @param alarmType the alarm type
	 * @return the alarm conflict type
	 */
	public static String getAlarmConflictType(final String alarmType) {
		String ret_type = "";
		switch (alarmType) {
		case "0":

			ret_type = "STCA";
			break;
		case "1":

			ret_type = "MSAW";
			break;
		case "2":

			ret_type = "APW";
			break;
		case "3":

			ret_type = "APM";
			break;

		case "EMG":
		case "HIJ":
		case "RCF":
			ret_type = alarmType;
			break;

		case "TCAS":
			ret_type = "ACAS";
			break;
		default:
			break;
		}
		return ret_type;
	}

	/**
	 * Gets the alarm urgency.
	 *
	 * @param alarmUrgency the alarm urgency
	 * @return the alarm urgency
	 */
	public static String getAlarmUrgency(final String alarmUrgency) {
		String urgency = "";
		switch (alarmUrgency) {
		case "0":

			urgency = "ACT";
			break;
		case "1":

			urgency = "LAT";
			break;
		case "2":

			urgency = "PRE";
			break;
		case "3":

			urgency = "SUS";
			break;
		default:
			break;
		}
		return urgency;
	}

	/**
	 * Gets the alarm code.
	 *
	 * @param stn the stn
	 * @return the alarm code
	 */
	public static String getAlarmCode(final String stn) {

		final String callsign = getFlightValue(stn, "CALLSIGN");
		String ret_code = callsign;
		if (callsign.isBlank()) {
			String rcs = getTrackValue(stn, "RCS");
			if (rcs.isBlank()) {
				String ssr = getTrackValue(stn, "MODE_3A");
				ret_code = ssr;
			} else {
				ret_code = rcs;
			}
		}
		return ret_code;
	}

	/**
	 * Gets the data from flight.
	 *
	 * @param stn  the stn
	 * @param data the data
	 * @return the data from flight
	 */
	public static String getFlightValue(final String stn, final String data) {
		Optional<IRawData> fl = getFlightFromStn(stn);
		if (fl.isPresent()) {
			return fl.get().getSafeString(data);
		}
		return "";

	}

	/**
	 * Gets the data from track.
	 *
	 * @param stn  the stn
	 * @param data the data
	 * @return the data from track
	 */
	public static String getTrackValue(final String stn, final String data) {
		String trkId = getTrkIdFromStn(stn);
		final Optional<IRawData> jsonTrack = BlackBoardUtility.getDataOpt(DataType.TRACK.name(), trkId);
		if (jsonTrack.isPresent()) {
			return jsonTrack.get().getSafeString(data);
		}
		return "";
	}

	/**
	 * Gets the sector ID.
	 *
	 * @return the sector ID
	 */
	public static int getSectorID() {
		final Optional<IRawData> json = BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name());
		return json.map(iRawData -> iRawData.getSafeInt("OWNP")).orElse(-1);

	}

	/**
	 * Gets the correlated vehicle track.
	 *
	 * @param vid the vid
	 * @return the correlated vehicle track
	 */
	public static Optional<IRawData> getCorrelatedVehicleTrack(final String vid) {
		return BlackBoardUtility.getDataOpt(DataType.BB_VID_STN.name(), vid);
	}

	/**
	 * Gets the correlated vehicle.
	 *
	 * @param stn the stn
	 * @return the correlated vehicle
	 */
	public static Optional<IRawData> getCorrelatedVehicle(final String stn) {
		return BlackBoardUtility.getDataOpt(DataType.BB_STN_VID.name(), stn);
	}

	/**
	 * Checks if is correlated flight.
	 *
	 * @param stn the stn
	 * @return true, if is correlated flight
	 */
	public static boolean isCorrelatedVehicle(final String stn) {
		return BlackBoardUtility.getDataOpt(DataType.BB_STN_VID.name(), stn).isPresent();
	}

	/**
	 * Gets the data from track.
	 *
	 * @param stn the stn
	 * @param fn  the fn
	 *
	 */
	public static void addCorrelationBB(final String stn, String fn) {

		String trkId = getTrkIdFromStn(stn);

		if (fn.isEmpty()) {
			Optional<IRawData> trk = BlackBoardUtility.getDataOpt(DataType.BB_TRKID_FLIGHTNUM.name(), trkId);
			if (trk.isPresent()) {
				fn = trk.get().getSafeString("FLIGHT_NUM");
			} else {
				fn = searchFNumByStn(stn);
			}
		}

		IRawData jsonFlight = RawDataFactory.create();
		jsonFlight.put("FLIGHT_NUM", fn);
		jsonFlight.put("TRACK_ID", trkId);

		if (!fn.isEmpty() && !trkId.isEmpty()) {
			boolean force = false;
			Optional<IRawData> elem = BlackBoardUtility.getDataOpt(DataType.BB_FLIGHTNUM_TRKID.name(), fn);
			if (!elem.isPresent()) {
				force = true;
			}
			BlackBoardUtility.addData(DataType.BB_FLIGHTNUM_TRKID.name(), jsonFlight);
			if (force) {
				ManagerBlackboard.forcedUpdate(DataType.FLIGHT_EXTFLIGHT.name(), fn);
				ManagerBlackboard.forcedUpdate(DataType.FLIGHT_TRJ.name(), fn);
			}
		}

		IRawData jsonTrack = RawDataFactory.create();
		jsonTrack.put("FLIGHT_NUM", fn);
		jsonTrack.put("TRACK_ID", trkId);

		if (!trkId.isEmpty() && !fn.isEmpty()) {
			BlackBoardUtility.addData(DataType.BB_TRKID_FLIGHTNUM.name(), jsonTrack);
		}

	}

	/**
	 * Gets the data from track.
	 *
	 * @param trkId the trk id
	 * @param fn    the fn
	 *
	 */
	public static void deleteCorrelationBB(String trkId, String fn) {
		if (fn.isEmpty()) {
			fn = getFnFromTrkId(trkId);
		}

		if (trkId.isEmpty()) {
			trkId = getTrkIdFromFn(fn);
		}

		BlackBoardUtility.removeData(DataType.BB_FLIGHTNUM_TRKID.name(), fn);
		BlackBoardUtility.removeData(DataType.BB_TRKID_FLIGHTNUM.name(), trkId);
	}

	/**
	 * Search F num by stn.
	 *
	 * @param stn the stn
	 * @return the string
	 */
	private static String searchFNumByStn(final String stn) {
		final var selectedFlightList = BlackBoardUtility.getSelectedData(DataType.FLIGHT_EXTFLIGHT.name(),
				new Pair<>("STN", stn));
		Optional<String> value = selectedFlightList.keySet().stream().findFirst();
		if (value.isPresent()) {
			return value.get();
		}
		return "";
	}

	/**
	 * Search F num by stn.
	 *
	 * @param stn the stn
	 * @return the string
	 */
	public static boolean isScaCtl(final String stn) {

		final var priorityScaId = Utils_LIS.getPrioirtyScaConflict(stn);
		final var rawdataSca = BlackBoardUtility.getDataOpt(BlackBoardConstants_LIS.DataType.SCA.name(), priorityScaId);
		if (rawdataSca.isPresent()) {
			int tyeP = rawdataSca.get().getSafeInt("ALARMTYP");
			int subType = rawdataSca.get().getSafeInt("SUB_TYPE");
			if (tyeP == 6 && subType == 1) {
				return true;
			}
			if (tyeP == 1 && (subType == 9 || subType == 10)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Search F num by stn.
	 *
	 * @param stn the stn
	 * @return the string
	 */
	public static boolean isScaCto(final String stn) {

		final var priorityScaId = Utils_LIS.getPrioirtyScaConflict(stn);
		final var rawdataSca = BlackBoardUtility.getDataOpt(BlackBoardConstants_LIS.DataType.SCA.name(), priorityScaId);
		if (rawdataSca.isPresent()) {
			int tyeP = rawdataSca.get().getSafeInt("ALARMTYP");
			int subType = rawdataSca.get().getSafeInt("SUB_TYPE");
			if (tyeP == 12 && (subType == 1 || subType == 2)) {
				return true;
			}
			if (tyeP == 6 && subType == 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param jsonTrack
	 * @return emergencyRpas
	 */
	public static String decodeEmergency(Optional<IRawData> jsonTrack) {
		String retValue = "";
		boolean c2Loss = false;
		boolean daaLoss = false;
		boolean c2Loss_fromMode3A = false;

		boolean rst_validity = jsonTrack.get().getSafeBoolean(RST_VALIDITY);
		if (rst_validity) {
			c2Loss = jsonTrack.get().getSafeBoolean(EMG_RPAS_C2L);
			daaLoss = jsonTrack.get().getSafeBoolean(EMG_RPAS_DAAL);
		}
		String track_mode_3A = jsonTrack.get().getSafeString("MODE_3A");
		if (track_mode_3A != null) {
			if (track_mode_3A.equals("A7400")) {
				c2Loss_fromMode3A = true;
			}
		}

		if (c2Loss || c2Loss_fromMode3A) {
			retValue = EMG_RPAS_C2L;
		} else {
			if (daaLoss) {
				retValue = EMG_RPAS_DAAL;
			}
		}

		return retValue;
	}

	/**
	 * @param trackLEV
	 * @return newCorrection
	 */
	public static int getCorrectionLevQnh(int trackLEV) {
		final var dev_qna_info = BlackBoardUtility.getDataOpt(DataType.ENV_QNAINFO.name());
		if (trackLEV != 32768) {
			if (dev_qna_info.isPresent()) {
				int envTransitionLevel = dev_qna_info.get().getSafeInt("TRANS_LEVEL", -1);
				int envQNHREc = dev_qna_info.get().getSafeInt("QNH", -1);
				if (envQNHREc != -1) {
					if (trackLEV <= envTransitionLevel) {
						if (envQNHREc >= 900 || envQNHREc <= 1065) {
							final int tableindex = envQNHREc - 900;
							int LEVCorrection = CORRECTION_TABLE_QNH[tableindex];
							trackLEV += LEVCorrection;
						}
					}
				}
			}
		}
		return trackLEV;

	}
}
