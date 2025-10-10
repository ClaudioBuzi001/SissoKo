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

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;

import applicationLIS.BlackBoardConstants_LIS;
import applicationLIS.Utils_LIS;
import common.CommonConstants;
import common.Utils;

/**
 * The Class scaProcessor.
 */
public enum ScaProcessor {
	;

	/**
	 * Analyze sca.
	 *
	 * @param rawdatasca the rawdatasca
	 */

	/**
	 * Process.
	 *
	 * @param rawdatasca the rawdata sca
	 */
	public static void process(final IRawData rawdatasca) {

		final var rawdataTrack1 = Utils_LIS.getTrkFromStn(rawdatasca.getSafeString("STN_1"));
		final var rawdataTrack2 = Utils_LIS.getTrkFromStn(rawdatasca.getSafeString("STN_2"));

		rawdataTrack1.ifPresent(iRawData -> addAlarmTrack(rawdatasca, iRawData));
		rawdataTrack2.ifPresent(iRawData -> addAlarmTrack(rawdatasca, iRawData));

	}

	/**
	 * Adds the alarm track.
	 *
	 * @param rawdatasca the rawdata sca
	 * @param trackData  the track data
	 */
	private static void addAlarmTrack(final IRawData rawdatasca, final IRawData trackData) {
		rawdatasca.put(CommonConstants.STN, trackData.getSafeString(CommonConstants.STN));
		XMLCreation(rawdatasca, trackData);
	}

	/**
	 * XML creation.
	 *
	 * @param rawdatasca the rawdata sca
	 * @param trackData  the track data
	 */
	private static void XMLCreation(final IRawData rawdatasca, final IRawData trackData) {
		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
		Utils.addElementTrack(trackData, objectNode);

		final String stn = trackData.getSafeString(CommonConstants.STN);
		String almStr = "";

		final var listSca = Utils_LIS.getAllScaConflict(stn);

		StringBuilder str = new StringBuilder();
		listSca.forEach(el -> str.append(el.getSafeString("LABEL_STR")).append(" "));

		final var priorityScaId = Utils_LIS.getPrioirtyScaConflict(stn);
		String COLOR = "";
		objectNode.addLine("IS_SOUND_SCA", "false");
		final var rawdataSca = BlackBoardUtility.getDataOpt(BlackBoardConstants_LIS.DataType.SCA.name(), priorityScaId);
		if (rawdataSca.isPresent()) {
			almStr = rawdataSca.get().getSafeString("LABEL_STR");

			if (rawdataSca.get().getSafeInt("ALARM_STATUS") == 1) {
				COLOR = "#FF0000";
				objectNode.addLine("IS_SOUND_SCA", "true");
			} else if (rawdataSca.get().getSafeInt("ALARM_STATUS") == 0) {
				COLOR = "#FFFF00";
			} else {
				COLOR = "#FFFFFF";
			}

		}
		objectNode.addLine("ALM_LIST", str.toString());
		objectNode.addLine("ALM", almStr).setAttribute(EdmModelKeys.Attributes.COLOR, COLOR);
		objectNode.addLine("ISCTO", Utils_LIS.isScaCto(stn));
		objectNode.addLine("ISCTL", Utils_LIS.isScaCtl(stn));
		Utils.doTCPClientSender(rootData);
	}

	/**
	 * Delete.
	 *
	 * @param rawdatasca the rawdata sca
	 */
	public static void delete(final IRawData rawdatasca) {

		final var rawdataTrack1 = Utils_LIS.getTrkFromStn(rawdatasca.getSafeString("STN_1"));
		rawdataTrack1.ifPresent(iRawData -> deleteConflictOnTrack(rawdatasca, iRawData));

		final var rawdataTrack2 = Utils_LIS.getTrkFromStn(rawdatasca.getSafeString("STN_2"));
		rawdataTrack2.ifPresent(iRawData -> deleteConflictOnTrack(rawdatasca, iRawData));

	}

	/**
	 * Delete conflict on track.
	 *
	 * @param rawdatasca the rawdata sca
	 * @param trackData  the track data
	 */
	private static void deleteConflictOnTrack(final IRawData rawdatasca, final IRawData trackData) {

		XMLCreation(rawdatasca, trackData);
	}

}
