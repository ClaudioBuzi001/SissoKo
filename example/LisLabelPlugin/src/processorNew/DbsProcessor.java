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
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;

import applicationLIS.BlackBoardConstants_LIS;
import applicationLIS.Utils_LIS;
import common.CommonConstants;
import common.Utils;

/**
 * Collection of static methods to process and send the DBS data to the viewer.
 */
public final class DbsProcessor {

	/**
	 * Private constructor in order to prevent direct instantiation
	 */
	private DbsProcessor() {
	}

	/**
	 * Process the DBS data information in order to crate a XML for the viewer.
	 *
	 * @param rawdataDbs the input raw data object
	 */
	public static void process(final IRawData rawdataDbs) {
		String callsign = rawdataDbs.getSafeString(CommonConstants.FOLLOWER);

		final var rawdataTrack = Utils_LIS.getTrkFromCs(callsign);

		var stateData = BlackBoardUtility.getDataOpt(BlackBoardConstants_LIS.DataType.BB_DBS_SWITCH_LABEL.name(),
				callsign);

		boolean state = false;
		if (!stateData.isEmpty()) {
			state = stateData.get().getSafeBoolean(CommonConstants.DBS_SWITCH_LABEL_STATE);
		}
		rawdataDbs.put(CommonConstants.DBS,
				rawdataDbs.getSafeString(state ? CommonConstants.DBS_RNDIST : CommonConstants.DBS_DTL));

		rawdataTrack.ifPresent(iRawData -> updateDbsTrack(rawdataDbs, iRawData));

	}

	/**
	 * Delete the DBS information from the track
	 *
	 * @param rawdataDbs the input raw data object
	 */
	public static void delete(final IRawData rawdataDbs) {

		final String stnDbs = rawdataDbs.getSafeString(CommonConstants.FOLLOWER);

		rawdataDbs.put(CommonConstants.DBS, "");
		rawdataDbs.put(CommonConstants.DBS_LAT, "");
		rawdataDbs.put(CommonConstants.DBS_LON, "");
		rawdataDbs.put(CommonConstants.DBS_COLOR, "");
		rawdataDbs.put(CommonConstants.DBS_ANGLE, "");

		final var rawdataTrack = Utils_LIS.getTrkFromCs(stnDbs);

		rawdataTrack.ifPresent(iRawData -> updateDbsTrack(rawdataDbs, iRawData));

	}

	/**
	 * Adds the alarm track.
	 *
	 * @param rawdataDbs the input raw data object
	 * @param trackData  the track raw data object
	 */
	private static void updateDbsTrack(final IRawData rawdataDbs, final IRawData trackData) {
		XMLCreation(rawdataDbs, trackData);
	}

	/**
	 * Create and sent the XML object to the viewer.
	 *
	 * @param rawdataDbs the input raw data object
	 * @param trackData  the track raw data object
	 */
	private static void XMLCreation(final IRawData rawdataDbs, final IRawData trackData) {
		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
		Utils.addElementTrack(trackData, objectNode);

		if (!trackData.isEmpty()) {
			final Optional<IRawData> flight = Utils_LIS.getFlightFromTrkId(trackData.getId());
			Utils.addTemplate(Optional.of(trackData), flight, objectNode);

		}

		objectNode.addLine(CommonConstants.DBS, rawdataDbs.getSafeString(CommonConstants.DBS));
		objectNode.addLine(CommonConstants.DBS_LAT, rawdataDbs.getSafeString(CommonConstants.DBS_LAT));
		objectNode.addLine(CommonConstants.DBS_LON, rawdataDbs.getSafeString(CommonConstants.DBS_LON));
		objectNode.addLine(CommonConstants.DBS_COLOR, rawdataDbs.getSafeString(CommonConstants.DBS_COLOR));
		objectNode.addLine(CommonConstants.DBS_ANGLE, rawdataDbs.getSafeString(CommonConstants.DBS_ANGLE));
		Utils.doTCPClientSender(rootData);

	}

}
