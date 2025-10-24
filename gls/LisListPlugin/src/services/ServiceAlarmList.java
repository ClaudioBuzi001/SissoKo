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
package services;

import java.util.Optional;

import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

import application.pluginService.ServiceExecuter.IFunctionalService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;

/**
 * The Class ServiceAlarmList.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public class ServiceAlarmList implements IFunctionalService {

	/**
	 * Execute.
	 *
	 * @param input the input
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData input) {
		String ret_value = "";
		final IRawDataElement parameters = input.getAuxiliaryData();
		/** The field to check. */
		String FIELD_TO_CHECK = parameters.getSafeString("FIELD_TO_CHECK").replace("..", "");
		/** The STN_Target . */
		String STN_Target = parameters.getSafeString("VAL_TO_CHECK");

		switch (FIELD_TO_CHECK) {
		case "CS_VID":
			String Callsign_Vid = "";
			String RCS_Track = "";
			String Mode3A_Track = "";
			if (!STN_Target.equals("-1")) {

				RCS_Track = Utils_LIS.getTrackValue(STN_Target, "RCS");
				if (RCS_Track.isBlank() || RCS_Track.isEmpty()) {
					Mode3A_Track = Utils_LIS.getTrackValue(STN_Target, "MODE_3A");
				}

				final Optional<IRawData> jBBStn_VID = BlackBoardUtility.getDataOpt(DataType.BB_STN_VID.name(),
						STN_Target);
				if (jBBStn_VID.isPresent()) {
					Callsign_Vid = jBBStn_VID.get().getSafeString("VID");
				}

				if (!Callsign_Vid.isEmpty()) {
					ret_value = Callsign_Vid;
				} else {
					Callsign_Vid = Utils_LIS.getFlightValue(STN_Target, "VID");
				}

				if (!Callsign_Vid.isEmpty()) {
					ret_value = Callsign_Vid;
				} else if (!RCS_Track.isBlank()) {
					ret_value = RCS_Track;
				} else if (!Mode3A_Track.isEmpty()) {
					ret_value = Mode3A_Track;
				}
			}
			break;
		case "ACK_SCA":
			final var sca = BlackBoardUtility.getDataOpt(DataType.SCA.name(), input.getId());
			ret_value = "FALSE";
			if (sca.isPresent()) {
				ret_value = sca.get().getSafeBoolean(FIELD_TO_CHECK) ? "TRUE" : "FALSE";
			}
			break;
		case "ACK_SNET":
			final var alarmAck = BlackBoardUtility.getDataOpt(DataType.SNET.name(), input.getId());
			ret_value = "SOUND_ON";
			if (alarmAck.isPresent()) {
				ret_value = alarmAck.get().getSafeBoolean("IS_SOUND_SNET") ? "SOUND_ON" : "SOUND_OFF";
			}
			break;

		case "ALARM_STATUS":
			Optional<IRawData> BBKScolor = BlackBoardUtility.getDataOpt(DataType.PRELOADED_COLOR_SET.name(),
					parameters.getSafeString("ID"));
			if (BBKScolor.isEmpty()) {
				BBKScolor = BlackBoardUtility.getDataOpt(DataType.PRELOADED_COLOR_SET.name(), "LIST_COLOR_GENERIC");
			}

			if (BBKScolor.isPresent()) {
				final IRawData listColorGeneric = BBKScolor.get();

				final var scaN = BlackBoardUtility.getDataOpt(DataType.SCA.name(), input.getId());
				String alarm_Status = "";
				if (scaN.isPresent()) {
					alarm_Status = scaN.get().getSafeString(FIELD_TO_CHECK);
				}

				final IRawDataElement fieldColorMap = listColorGeneric.getSafeElement(FIELD_TO_CHECK);
				final IRawDataElement valueParameters = fieldColorMap.getSafeElement(alarm_Status);
				ret_value = valueParameters.getSafeString("COLOR");
			}
			break;

		default:
			break;
		}

		return ret_value;
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String SERVICE_NAME = "ALARM_LIST";
		return SERVICE_NAME;
	}

}
