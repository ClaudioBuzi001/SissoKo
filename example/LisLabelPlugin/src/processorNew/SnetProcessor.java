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

import java.util.HashMap;
import java.util.Optional;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.track.TrackOutputConstants;
import common.CommonConstants;
import common.Utils;

/**
 * The Class SnetProcessor.
 */
public enum SnetProcessor {
	;

	/** The Constant alarmedTracks. */

	private static final HashMap<String, HashMap<String, String>> alarmedListTracks = new HashMap<>();

	/**
	 * The Class Alarm.
	 */
	private static class Alarm {

		/** The stca. */
		private String stca = "";

		/** The msaw. */
		private String msaw = "";

		/** The daiw. */
		private String aiw = "";

		/** The apm. */
		private String apm = "";

		/** The apw. */
		private String apw = "";

		/** The rcf. */
		private String rcf = "";
		/** The hij. */
		private String hij = "";
		/** The emg. */
		private String emg = "";

		/**
		 * The Enum AlarmType.
		 */

		private enum AlarmType {
			/** The stca. */
			STCA,

			/** The msaw. */
			MSAW,

			/** The apm. */
			APM,

			/** The apw. */
			APW,

			/** The daiw. */
			AIW,

			/** The HIJ. */
			HIJ,

			/** The EMG. */
			EMG,

			/** The RCF. */
			RCF,

		}

		/**
		 * Gets the alarm.
		 *
		 * @param type the type
		 * @return the alarm
		 */
		public String getAlarm(final String type) {
			String alarmData = "";
			switch (type) {
			case "STCA":
				alarmData = stca;
				break;
			case "MSAW":
				alarmData = msaw;
				break;
			case "APM":
				alarmData = apm;
				break;
			case "APW":
				alarmData = apw;
				break;
			case "AIW":
				alarmData = aiw;
				break;
			case "HIJ":
				alarmData = hij;
				break;
			case "EMG":
				alarmData = emg;
				break;
			case "RCF":
				alarmData = rcf;
				break;
			}
			return alarmData;
		}

		/**
		 * Gets the alarm type.
		 *
		 * @param alarmType the alarm type
		 * @return the alarm type
		 */
		public static String getAlarmType(final String alarmType) {
			String ret_value;
			switch (alarmType) {
			case CommonConstants.ALARM_TYPE_STCA:
				ret_value = "STCA";
				break;
			case CommonConstants.ALARM_TYPE_MSAW:
				ret_value = "MSAW";
				break;
			case CommonConstants.ALARM_TYPE_APW:
				ret_value = "APW";
				break;
			case CommonConstants.ALARM_TYPE_APM:
				ret_value = "APM";
				break;
			case CommonConstants.ALARM_TYPE_AIW:
				ret_value = "AIW";
				break;
			case "HIJ":
				ret_value = "HIJ";
				break;
			case "RCF":
				ret_value = "RCF";
				break;
			case "EMG":
				ret_value = "EMG";
				break;
			default:
				ret_value = "";
			}

			return ret_value;
		}
	}

	/**
	 * Process.
	 *
	 * @param rawdataSnet the rawdata snet
	 */
	public static void process(final IRawData rawdataSnet) {

		if (!rawdataSnet.getId().contains("TCAS")) {
			final String stn1 = rawdataSnet.getSafeString(CommonConstants.STN_1);
			final String stn2 = rawdataSnet.getSafeString(CommonConstants.STN_2);

			final var rawdataTrack1 = Utils_LIS.getTrkFromStn(stn1);
			final var rawdataTrack2 = Utils_LIS.getTrkFromStn(stn2);
			final String alarmType = rawdataSnet.getSafeString(CommonConstants.ALARM_TYPE);
			final String isSound = rawdataSnet.getSafeString("IS_SOUND_SNET", "false");
			final Alarm alarm = setAlarm(alarmType);
			if (rawdataTrack1.isPresent()) {

				if (!alarmedListTracks.containsKey(stn1)) {
					alarmedListTracks.put(stn1, new HashMap<>());
					for (final Alarm.AlarmType ind : Alarm.AlarmType.values()) {
						alarmedListTracks.get(stn1).put(ind.name(), alarm.getAlarm(ind.name()));
					}
				}
				final String alarmUpdate = Alarm.getAlarmType(alarmType);
				alarmedListTracks.get(stn1).put(alarmUpdate, alarm.getAlarm(alarmUpdate));

				XMLCreation(rawdataTrack1.get(), isSound);
			}
			if (rawdataTrack2.isPresent()) {

				if (!alarmedListTracks.containsKey(stn2)) {
					alarmedListTracks.put(stn2, new HashMap<>());
					for (final Alarm.AlarmType ind : Alarm.AlarmType.values()) {
						alarmedListTracks.get(stn2).put(ind.name(), alarm.getAlarm(ind.name()));
					}
				}
				final String alarmUpdate = Alarm.getAlarmType(alarmType);
				alarmedListTracks.get(stn2).put(alarmUpdate, alarm.getAlarm(alarmUpdate));

				XMLCreation(rawdataTrack2.get(), isSound);
			}
			final String alarmUpdate = Alarm.getAlarmType(alarmType);
			if (alarmUpdate.equals(Alarm.AlarmType.STCA.name())) {
				sendSNETGraphicToViewer(rawdataSnet);
			}
		}
	}

	/**
	 * Send SNET graphic to viewer.
	 *
	 * @param snet the snet
	 */
	private static void sendSNETGraphicToViewer(final IRawData snet) {

		Optional<IRawData> trk1 = Utils_LIS.getTrkFromStn(snet.getSafeString(CommonConstants.STN_1));
		Optional<IRawData> trk2 = Utils_LIS.getTrkFromStn(snet.getSafeString(CommonConstants.STN_2));
		String objectId1="";
		String objectId2="";
		if(trk1.isPresent()) {
			 objectId1 = Utils.getTrackId(trk1.get().getId());
		}
		if(trk2.isPresent()) {
			objectId2 = Utils.getTrackId(trk2.get().getId());
		}

		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(snet.getOperation(), EdmModelKeys.HeaderType.STCA_CONFLICT);
		objectNode.addLine(CommonConstants.ID, snet.getSafeString("CONFLICT_NUMBER"));
		objectNode.addLine(CommonConstants.OBJECTID + "1", objectId1);
		objectNode.addLine(CommonConstants.OBJECTID + "2", objectId2);

		ManagerBlackboard.addJVOutputList(rootData);

	}

	/**
	 * XML creation.
	 *
	 * @param trackData the track data
	 * @param isSound   the is sound
	 */
	private static void XMLCreation(final IRawData trackData, final String isSound) {

		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
		Utils.addElementTrack(trackData, objectNode);
		addAlarm(trackData, objectNode, isSound);
		Utils.doTCPClientSender(rootData);
	}

	/**
	 * Adds the alarm.
	 *
	 * @param track      the track
	 * @param objectNode the object node
	 * @param isSound    the is sound
	 */
	private static void addAlarm(final IRawData track, final HeaderNode objectNode, String isSound) {
		final String trkId = track.getId();
		final String trkstn = track.getSafeString("STN");
		if (!track.isEmpty()) {
			Optional<IRawData> flight = Utils_LIS.getFlightFromTrkId(trkId);
			Utils.addTemplate(Optional.of(track), flight, objectNode);
		}
		final HashMap<String, String> alarmsList = alarmedListTracks.get(trkstn);
		if (alarmsList != null && !alarmsList.isEmpty()) {
			final var listAlarmGeneric = BlackBoardUtility.getDataOpt(DataType.PRELOADED_COLOR_SET.name(),
					"LIST_COLOR_GENERIC");

			if (listAlarmGeneric.isPresent()) {
				final IRawDataElement fieldAlarmJson = listAlarmGeneric.get().getSafeElement("SNET_ALARM");
				String alarmStca = "";
				if (!alarmsList.get("STCA").isEmpty()) {
					alarmStca = fieldAlarmJson.getSafeString(TrackOutputConstants.STCA);
				}
				objectNode.addLine(TrackOutputConstants.STCA, alarmStca);

				String alarmMsaw = "";
				if (!alarmsList.get("MSAW").isEmpty()) {
					alarmMsaw = fieldAlarmJson.getSafeString(TrackOutputConstants.MSAW);
				}
				objectNode.addLine(TrackOutputConstants.MSAW, alarmMsaw);

				String alarmDaiw = "";
				if (!alarmsList.get("APW").isEmpty()) {
					alarmDaiw = fieldAlarmJson.getSafeString(TrackOutputConstants.APW);
				}
				objectNode.addLine(TrackOutputConstants.APW, alarmDaiw);

				String alarmApam = "";
				if (!alarmsList.get("APM").isEmpty()) {
					alarmApam = fieldAlarmJson.getSafeString(TrackOutputConstants.APM);
				}
				objectNode.addLine(TrackOutputConstants.APM, alarmApam);

				String alarmAiw = "";
				if (!alarmsList.get("AIW").isEmpty()) {
					alarmAiw = fieldAlarmJson.getSafeString(TrackOutputConstants.AIW);
				}
				objectNode.addLine(TrackOutputConstants.AIW, alarmAiw);
			}
		} else {
			objectNode.addLine(TrackOutputConstants.STCA, "");
			objectNode.addLine(TrackOutputConstants.MSAW, "");
			objectNode.addLine(TrackOutputConstants.APW, "");
			objectNode.addLine(TrackOutputConstants.APM, "");
			objectNode.addLine(TrackOutputConstants.AIW, "");
		}

		objectNode.addLine("IS_SOUND_SNET", isSound);
	}

	/**
	 * Delete.
	 *
	 * @param rawdataSnet the rawdata snet
	 */
	public static void delete(final IRawData rawdataSnet) {
		if (!rawdataSnet.getId().contains("TCAS")) {
			final String stn1 = rawdataSnet.getSafeString(CommonConstants.STN_1);
			final String stn2 = rawdataSnet.getSafeString(CommonConstants.STN_2);
			if (!rawdataSnet.getSafeString(CommonConstants.CONFLICT_ID).startsWith("EMG")) {
				alarmedListTracks.remove(stn1);
				alarmedListTracks.remove(stn2);
			}
			final var rawdataTrack1 = Utils_LIS.getTrkFromStn(stn1);
			final var rawdataTrack2 = Utils_LIS.getTrkFromStn(stn2);
			rawdataTrack1.ifPresent(iRawData -> XMLCreation(iRawData, "false"));
			rawdataTrack2.ifPresent(iRawData -> XMLCreation(iRawData, "false"));

			sendSNETGraphicToViewer(rawdataSnet);
		}
	}

	/**
	 * Sets the alarm.
	 *
	 * @param alarmType the alarm type
	 * @return the alarm
	 */
	private static Alarm setAlarm(final String alarmType) {

		final Alarm alarm = new Alarm();

		switch (alarmType) {
		case CommonConstants.ALARM_TYPE_STCA:
			alarm.stca = "STCA";
			break;
		case CommonConstants.ALARM_TYPE_MSAW:
			alarm.msaw = "MSAW";
			break;
		case CommonConstants.ALARM_TYPE_APW:
			alarm.apw = "DAIW";
			break;
		case CommonConstants.ALARM_TYPE_APM:
			alarm.apm = "APAM";
			break;
		case CommonConstants.ALARM_TYPE_AIW:
			alarm.aiw = "AIW";
			break;
		case "EMG":
			alarm.emg = "EMG";
			break;
		case "HIJ":
			alarm.hij = "HIJ";
			break;
		case "RCF":
			alarm.rcf = "RCF";
			break;
		default:
		}

		return alarm;
	}

}
