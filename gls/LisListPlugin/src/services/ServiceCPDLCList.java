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

/**
 * The Class ServiceCPDLCList.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public class ServiceCPDLCList implements IFunctionalService {

	/**
	 * Execute.
	 *
	 * @param inputJson the input json
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData inputJson) {
		String ret_value = "";
		String ret_color = "#ffffff";
		final IRawDataElement parameters = inputJson.getAuxiliaryData();
		/** The list name. */
		String LIST_NAME = parameters.getSafeString("LIST_NAME");
		/** The info type. */
		String INFO_TYPE = parameters.getSafeString("INFO_TYPE");

		/** The default. */
		String DEFAULT = parameters.getSafeString("DEFAULT");
		/** The field to check1. */
		String FIELD_TO_CHECK1 = parameters.getSafeString("FIELD_TO_CHECK1").replace("..", "");
		/** The field to check2. */

		String FIELD_TO_CHECK2 = parameters.getSafeString("FIELD_TO_CHECK2").replace("..", "");

		/** The val to check. */
		String VAL_TO_CHECK = parameters.getSafeString("VAL_TO_CHECK");
		/** The out data. */

		String OUT_DATA = parameters.getSafeString("OUT_DATA");

		if (INFO_TYPE.isEmpty()) {
			ret_value = "";
			switch (FIELD_TO_CHECK1) {
			case "CCS":
				final int CCS = inputJson.getSafeInt(FIELD_TO_CHECK1, 0);
				if (CCS == 0) {
					ret_value = "NOTCONNECTED";
				}
				if (CCS == 1) {
					ret_value = "CONNECTED";
				}
				if ((CCS == 4) || (CCS == 9)) {
					ret_value = "DISCBYUSER";
				}
				if (CCS == 10) {
					ret_value = "DISCERROR";
				}
				if (CCS == 11) {
					return "CDACONNECTED";
				}
				if (CCS == 12) {
					ret_value = "DISCREQUEST";
				}
				if (CCS == 13) {
					ret_value = "CONNREQUEST";
				}
				break;
			case "ACS":
				final int ACS = inputJson.getSafeInt(FIELD_TO_CHECK1, 0);
				if (ACS == 0) {
					ret_value = "NOTCONNECTED";
				}
				if (ACS == 1) {
					ret_value = "CONNECTED";
				}
				if (ACS == 2) {
					ret_value = "TERMINATED";
				}
				if (ACS == 3) {
					ret_value = "DISCONNECTED";
				}
				if (ACS == 4) {
					ret_value = "FAULTGWY";
				}
				break;
			case "CFS":
				final int CFS = inputJson.getSafeInt(FIELD_TO_CHECK1, 0);
				if (CFS == 0) {
					ret_value = "ENABLED";
				}
				if (CFS == 1) {
					ret_value = "TRANSFERRING";
				}

				if (CFS == 2 || CFS == 3) {
					ret_value = "DISABLED";
				}
				break;
			case "NDA":
				final int NDA = inputJson.getSafeInt(FIELD_TO_CHECK1, 0);
				if ((NDA == 0) || (NDA == 1) || (NDA == 2)) {
					ret_value = "NOTSENT";
				}
				if (NDA == 3) {
					ret_value = "SENDING";
				}
				if (NDA == 4) {
					ret_value = "SENT";
				}
				if (NDA == 5) {
					ret_value = "ERROR";
				}
				if (NDA == 6) {
					ret_value = "NO LACK";
				}
				break;
			case "CTS":
				final int CTS = inputJson.getSafeInt(FIELD_TO_CHECK1, 0);
				if (CTS == 0) {
					ret_value = "NOTSENT";
				}
				if (CTS == 1) {
					ret_value = "SENDING";
				}
				if (CTS == 2) {
					ret_value = "SENT";
				}
				if (CTS == 3) {
					ret_value = "ERROR";
				}
				break;

			case "answer":
				if ("LISCPDLCOUT".equals(LIST_NAME)) {
					ret_value = inputJson.getSafeString(FIELD_TO_CHECK1);
					if (ret_value.isEmpty()) {
						final String flightNum = inputJson.getSafeString("FLIGHT_NUM");
						final var jsonflightOpt = BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(),
								flightNum);
						if (jsonflightOpt.isPresent()) {
							final IRawData jsonflight = jsonflightOpt.get();
							CPLDC_STATUS uplinkStatus = CPLDC_STATUS.NoDialogue;
							switch (VAL_TO_CHECK) {
							case "2":

								uplinkStatus = CPLDC_STATUS.fromValue(jsonflight.getSafeInt("VCUDS"));
								break;
							case "6":

								uplinkStatus = CPLDC_STATUS.fromValue(jsonflight.getSafeInt("SCUDS"));
								break;
							case "5":

								uplinkStatus = CPLDC_STATUS.fromValue(jsonflight.getSafeInt("RMUDS"));
								break;
							case "7":

								uplinkStatus = CPLDC_STATUS.fromValue(jsonflight.getSafeInt("CMUDS"));
								break;
							case "13":

								uplinkStatus = CPLDC_STATUS.fromValue(jsonflight.getSafeInt("TCUDS"));
								break;
							default:
								break;
							}
							if (uplinkStatus == CPLDC_STATUS.Close_Error) {
								ret_value = "ERR";
							}
							if (uplinkStatus == CPLDC_STATUS.Close_timeout) {
								ret_value = "TOUT";
							}
							if (uplinkStatus == CPLDC_STATUS.Close_delivery_Error) {
								ret_value = "DELIVERR";
							}
							if (uplinkStatus == CPLDC_STATUS.Not_Current_Data_Authority) {
								ret_value = "NOTCDA";
							}
							if (uplinkStatus == CPLDC_STATUS.Unable_due_to_weather) {
								ret_value = "UNW";
							}
							if (uplinkStatus == CPLDC_STATUS.Unable_due_to_performance) {
								ret_value = "UNP";
							}
							if (uplinkStatus == CPLDC_STATUS.Close_Flight_Out_of_CPDLC_Airspace) {
								ret_value = "CPDLCOUT";
							}
						}
					}
				}
				break;

			default:
				break;
			}
		}

		if (INFO_TYPE.equals("COLOR")) {
			if ("LISCPDLCFLIGHT".equals(LIST_NAME)) {

				Optional<IRawData> BBKScolor = BlackBoardUtility.getDataOpt(DataType.PRELOADED_COLOR_SET.name(),
						"LIST_COLOR_" + LIST_NAME);
				if (BBKScolor.isEmpty()) {
					BBKScolor = BlackBoardUtility.getDataOpt(DataType.PRELOADED_COLOR_SET.name(), "LIST_COLOR_GENERIC");
				}

				final IRawData listColorGeneric = BBKScolor.get();

				if (FIELD_TO_CHECK2.isEmpty()) {

					if (FIELD_TO_CHECK1.equals("UDS")) {
						int status = inputJson.getSafeInt("CMUDS", -1);
						if (checkStatusValueUplink(status)) {
							FIELD_TO_CHECK1 = "CMUDS";
						} else {
							status = inputJson.getSafeInt("TCUDS", -1);
							if (checkStatusValueUplink(status)) {
								FIELD_TO_CHECK1 = "TCUDS";
								/*
								 * else { status = BBobject.getSafeInt("CXUDS",-1); if (checkStatusValueUplink(status))
								 * { this.FIELD_TO_CHECK1 = "CXUDS"; }
								 */
							}
						}
					}
					if (!listColorGeneric.isNull(FIELD_TO_CHECK1)) {
						/** The field to check value1. */
						String FIELD_TO_CHECK_VALUE1 = inputJson.getSafeString(FIELD_TO_CHECK1);
						final IRawDataElement fieldColorMap = listColorGeneric.getSafeElement(FIELD_TO_CHECK1);
						final IRawDataElement valueParameters = fieldColorMap.getSafeElement(FIELD_TO_CHECK_VALUE1);
						ret_color = valueParameters.getSafeString("COLOR");
					}

					if (ret_color.isEmpty()) {
						ret_color = DEFAULT;
					}
					return ret_color;
				}
				int upLink = -1;
				int downLink = -1;

				if (FIELD_TO_CHECK1.equals("RMUDS") && FIELD_TO_CHECK2.equals("RMDDS")) {
					upLink = inputJson.getSafeInt(FIELD_TO_CHECK1, -1);
					downLink = inputJson.getSafeInt(FIELD_TO_CHECK2, -1);
				}

				if (FIELD_TO_CHECK1.equals("VCUDS") && FIELD_TO_CHECK2.equals("VCDDS")) {
					upLink = inputJson.getSafeInt(FIELD_TO_CHECK1, -1);
					downLink = inputJson.getSafeInt(FIELD_TO_CHECK2, -1);
				}

				if (FIELD_TO_CHECK1.equals("SCUDS") && FIELD_TO_CHECK2.equals("SCDDS")) {
					upLink = inputJson.getSafeInt(FIELD_TO_CHECK1, -1);
					downLink = inputJson.getSafeInt(FIELD_TO_CHECK2, -1);
				}
				if (FIELD_TO_CHECK1.equals("UMDS") && FIELD_TO_CHECK2.equals("DMDS")) {
					upLink = inputJson.getSafeInt(FIELD_TO_CHECK1, -1);
					downLink = inputJson.getSafeInt(FIELD_TO_CHECK2, -1);
				}

				String field = "";
				String valueField = "";

				if (CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.NoDialogue
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Unable
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Close
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Close_delivery_Error
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Close_timeout
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Close_Flight_Out_of_CPDLC_Airspace
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Not_Current_Data_Authority
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Close_RT
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Unable_due_to_weather
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Unable_due_to_performance) {

					field = FIELD_TO_CHECK1;
					valueField = upLink + "";

				}
				if (CPLDC_STATUS.fromValue(upLink) == CPLDC_STATUS.NoDialogue
						|| CPLDC_STATUS.fromValue(upLink) == CPLDC_STATUS.Close
						|| CPLDC_STATUS.fromValue(upLink) == CPLDC_STATUS.Close_RT) {

					field = FIELD_TO_CHECK2;
					valueField = downLink + "";
				}
				if (CPLDC_STATUS.fromValue(upLink) == CPLDC_STATUS.Open_waiting_LACK
						&& CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Open_waiting_LACK) {

					field = FIELD_TO_CHECK1;
					valueField = upLink + "";
				}

				if (!listColorGeneric.isNull(field)) {
					final IRawDataElement fieldColorMap = listColorGeneric.getSafeElement(field);
					final IRawDataElement valueParameters = fieldColorMap.getSafeElement(valueField);
					ret_color = valueParameters.getSafeString("COLOR");
				}
			}

			if ("LISCPDLCIN".equals(LIST_NAME)) {
				final String response = inputJson.getSafeString(FIELD_TO_CHECK1);
				if (response.equals("Y")) {

					CPLDC_STATUS downlinkStatus = getDownlinkStatus(inputJson.getSafeString("FLIGHT_NUM"),
							VAL_TO_CHECK);

					if ((OUT_DATA.equals("SBY")
							&& (downlinkStatus == CPLDC_STATUS.Standby || downlinkStatus == CPLDC_STATUS.STANDBY_ALERT))
							|| (OUT_DATA.equals("UNA") && downlinkStatus == CPLDC_STATUS.Unable)
							|| (OUT_DATA.equals("WLC") && (downlinkStatus == CPLDC_STATUS.Close
									|| downlinkStatus == CPLDC_STATUS.Close_RT))) {
						ret_color = "#FFFF00";
					}
					if (OUT_DATA.equals("WLC") && (downlinkStatus == CPLDC_STATUS.Close_timeout
							|| downlinkStatus == CPLDC_STATUS.Close_delivery_Error
							|| downlinkStatus == CPLDC_STATUS.Close_Error)) {
						ret_color = "#FFA500";
					}

					if (OUT_DATA.equals("UNA") && inputJson.getSafeString("answer").equals("UNABLE")) {
						ret_color = "#FFFF00";
					}

				}
			}

			if (ret_color.isEmpty()) {
				ret_color = DEFAULT;
			}
			return ret_color;

		}

		if (INFO_TYPE.equals("IMAGE")) {
			ret_value = "";

			if (FIELD_TO_CHECK2.isEmpty()) {
				if (FIELD_TO_CHECK1.equals("UDS")) {
					int status = inputJson.getSafeInt("CMUDS", -1);
					if (checkStatusValueUplink(status)) {
						ret_value = "UP";
					} else {
						status = inputJson.getSafeInt("TCUDS", -1);
						if (checkStatusValueUplink(status)) {
							ret_value = "UP";
						}
					}
				}

			} else {
				int upLink = -1;
				int downLink = -1;

				if (FIELD_TO_CHECK1.equals("RMUDS") && FIELD_TO_CHECK2.equals("RMDDS")) {
					upLink = inputJson.getSafeInt(FIELD_TO_CHECK1, -1);
					downLink = inputJson.getSafeInt(FIELD_TO_CHECK2, -1);
				}

				if (FIELD_TO_CHECK1.equals("VCUDS") && FIELD_TO_CHECK2.equals("VCDDS")) {
					upLink = inputJson.getSafeInt(FIELD_TO_CHECK1, -1);
					downLink = inputJson.getSafeInt(FIELD_TO_CHECK2, -1);
				}

				if (FIELD_TO_CHECK1.equals("SCUDS") && FIELD_TO_CHECK2.equals("SCDDS")) {
					upLink = inputJson.getSafeInt(FIELD_TO_CHECK1, -1);
					downLink = inputJson.getSafeInt(FIELD_TO_CHECK2, -1);
				}

				if (FIELD_TO_CHECK1.equals("UMDS") && FIELD_TO_CHECK2.equals("DMDS")) {
					upLink = inputJson.getSafeInt(FIELD_TO_CHECK1, -1);
					downLink = inputJson.getSafeInt(FIELD_TO_CHECK2, -1);
				}

				if (CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.NoDialogue
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Unable
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Close
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Close_delivery_Error
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Close_timeout
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Close_Flight_Out_of_CPDLC_Airspace
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Not_Current_Data_Authority
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Close_RT
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Unable_due_to_weather
						|| CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Unable_due_to_performance) {

					int valore = upLink;
					if ((valore == 0) || (valore == 1) || (valore == 2) || (valore == 3) || (valore == 4)
							|| (valore == 6) || (valore == 7) || (valore == 8) || (valore == 9) || (valore == 13)
							|| (valore == 16) || (valore == 18) || (valore == 19) || (valore == 20) || (valore == 21)) {
						ret_value = "UP";
					}
				}

				if (CPLDC_STATUS.fromValue(upLink) == CPLDC_STATUS.NoDialogue
						|| CPLDC_STATUS.fromValue(upLink) == CPLDC_STATUS.Close
						|| CPLDC_STATUS.fromValue(upLink) == CPLDC_STATUS.Close_RT) {

					int valore = downLink;

					if ((valore == 0) || (valore == 1) || (valore == 2) || (valore == 3) || (valore == 13)) {
						ret_value = "DOWN";
					}
				}

				if (CPLDC_STATUS.fromValue(upLink) == CPLDC_STATUS.Open_waiting_LACK
						&& CPLDC_STATUS.fromValue(downLink) == CPLDC_STATUS.Open_waiting_LACK) {

					int valore = upLink;
					if ((valore == 0) || (valore == 1) || (valore == 2) || (valore == 3) || (valore == 4)
							|| (valore == 6) || (valore == 7) || (valore == 8) || (valore == 9) || (valore == 13)
							|| (valore == 16) || (valore == 18) || (valore == 19) || (valore == 20) || (valore == 21)) {
						ret_value = "UP";
					}
				}

			}

		}

		if (INFO_TYPE.equals("BLINK")) {
			ret_value = "";
			if (FIELD_TO_CHECK2.isEmpty()) {
				if (FIELD_TO_CHECK1.equals("UDS")) {
					int valore = inputJson.getSafeInt("CMUDS", -1);
					if (valore == 2 || valore == 13) {
						ret_value = "ON";
					} else {
						valore = inputJson.getSafeInt("TCUDS", -1);
						if (valore == 2 || valore == 13) {
							ret_value = "ON";
							/*
							 * else { valore = BBobject.getSafeInt("CXUDS",-1); if (valore == 2 || valore == 13)
							 * ret_value = "ON"; }
							 */
						}
					}
					if (ret_value.isEmpty()) {
						ret_value = "OFF";
					}
				}
			} else {
				final int valore = -1;

				if (valore == 2) {
					ret_value = "ON";
				}

				if (valore == 13) {
					ret_value = "ON";
				}

				if (ret_value.isEmpty()) {
					ret_value = "OFF";
				}

			}

		}

		if (FIELD_TO_CHECK1.equals("CALLBACK_WLC")) {
			ret_value = "";
			final String response = inputJson.getSafeString("response");

			final CPLDC_STATUS status = getDownlinkStatus(inputJson.getSafeString("FLIGHT_NUM"), VAL_TO_CHECK);
			final boolean dialogueOpen = checkOpenDownlinkDialogue(status);
			if (response.equals("Y") && dialogueOpen)

			{
				/** The enable assign speed order. */
				String enableAssignSpeedOrder = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=SPD, OBJECT_TYPE=SPD,  PREVIEW_NAME=choiceSPD)";
				/** The enable CFL order. */
				String enableCFLOrder = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ALV, OBJECT_TYPE=ALV,  PREVIEW_NAME=choiceALV)";

				/** The enable DCT order. */
				String enablePDTOrder = "EXTERNAL_ORDER(GRAPHIC_ORDER=DIRECT_TO , WITHCHOICE=TRUE, ORDER_ID=DCT, PREVIEW_NAME=quickDCT,  OBJECT_TYPE=DCT)";
				switch (VAL_TO_CHECK) {
				case "2":

					ret_value = enableCFLOrder;
					break;
				case "6":

					ret_value = enableAssignSpeedOrder;
					break;
				case "5":

					ret_value = enablePDTOrder;
					break;
				default:
					break;
				}
			}
		}

		if (FIELD_TO_CHECK1.equals("CALLBACK_SBY") || FIELD_TO_CHECK1.equals("CALLBACK_UNA")) {
			ret_value = "";
			final StringBuilder result = new StringBuilder();
			final CPLDC_STATUS status = getDownlinkStatus(inputJson.getSafeString("FLIGHT_NUM"), VAL_TO_CHECK);
			final boolean dialogueOpen = checkOpenDownlinkDialogue(status);
			if (inputJson.getSafeString("response").equals("Y") && dialogueOpen) {
				if (FIELD_TO_CHECK1.equals("CALLBACK_SBY")) {
					result.append(
							"EXTERNAL_ORDER(ORDER_ID=SBY, OBJECT_TYPE=SBY,PREVIEW_NAME=quickSBY, DATA={'ISLIST':'true', 'CPDLC_ID' :'")
							.append(inputJson.getSafeString("CPDLC_ID")).append("'})");
				} else {
					result.append(
							"EXTERNAL_ORDER(ORDER_ID=UNA, OBJECT_TYPE=UNA,PREVIEW_NAME=quickUNA, DATA={'ISLIST':'true', 'CPDLC_ID' :'")
							.append(inputJson.getSafeString("CPDLC_ID")).append("'})");
				}
				ret_value = result.toString();
			}
		}

		return ret_value;
	}

	/**
	 * Get the status of current downlink dialogue
	 *
	 * @param flightNum
	 * @param classId
	 * @return status
	 */
	private static CPLDC_STATUS getDownlinkStatus(final String flightNum, final String classId) {
		CPLDC_STATUS status = CPLDC_STATUS.NoDialogue;
		final var jsonflight = BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(), flightNum);
		if (jsonflight.isPresent()) {
			switch (classId) {
			case "2":

				status = CPLDC_STATUS.fromValue(jsonflight.get().getSafeInt("VCDDS"));
				break;
			case "6":

				status = CPLDC_STATUS.fromValue(jsonflight.get().getSafeInt("SCDDS"));
				break;
			case "5":

				status = CPLDC_STATUS.fromValue(jsonflight.get().getSafeInt("RMDDS"));
				break;
			default:
				break;
			}
		}
		return status;
	}

	/**
	 * check the current downlink dialogue is open
	 *
	 * @param status
	 * @return check
	 */
	private static boolean checkOpenDownlinkDialogue(final CPLDC_STATUS status) {
		boolean check = status.equals(CPLDC_STATUS.Open) || status.equals(CPLDC_STATUS.Open_Alert)
				|| status.equals(CPLDC_STATUS.Open_waiting_LACK) || status.equals(CPLDC_STATUS.Standby)
				|| status.equals(CPLDC_STATUS.STANDBY_ALERT);
		return check;
	}

	/**
	 * Check status value uplink.
	 *
	 * @param valore the valore
	 * @return true, if successful
	 */
	private static boolean checkStatusValueUplink(final int valore) {
		return (valore == 0) || (valore == 1) || (valore == 2) || (valore == 3) || (valore == 4) || (valore == 6)
				|| (valore == 7) || (valore == 8) || (valore == 9) || (valore == 13) || (valore == 16) || (valore == 18)
				|| (valore == 19) || (valore == 20) || (valore == 21);
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The My name. */
		String myName = "CPDLC_ALIAS";
		return myName;
	}

	/**
	 * The Enum CPLDC_STATUS.
	 */
	private enum CPLDC_STATUS {

		/** The Open. */
		Open(0),

		/** The Open waiting LACK. */
		Open_waiting_LACK(1),

		/** The Open alert. */
		Open_Alert(2),

		/** The Standby. */
		Standby(3),

		/** The Unable. */
		Unable(4),

		/** The Close. */
		Close(5),

		/** The Close error. */
		Close_Error(6),

		/** The Close delivery error. */
		Close_delivery_Error(7),

		/** The Close timeout. */
		Close_timeout(8),

		/** The Close flight out of CPDL C airspace. */
		Close_Flight_Out_of_CPDLC_Airspace(9),

		/** The Open disregard. */
		Open_disregard(10),

		/** The Close disregard. */
		Close_disregard(11),

		/** The Roger. */
		Roger(12),

		/** The standby alert. */
		STANDBY_ALERT(13),

		/** The Affirm. */
		Affirm(14),

		/** The Negative. */
		Negative(15),

		/** The Not current data authority. */
		Not_Current_Data_Authority(16),

		/** The Close RT. */
		Close_RT(17),

		/** The Unable due to weather. */
		Unable_due_to_weather(18),

		/** The Unable due to performance. */
		Unable_due_to_performance(19),

		/** The CPDL C message not sent. */
		CPDLC_Message_Not_Sent(20),

		/** The Close waiting LACK. */
		Close_waiting_LACK(21),
		/** The No dialogue. */

		NoDialogue(-1);

		/** The code. */
		private final int code;

		/**
		 * Instantiates a new cpldc status.
		 *
		 * @param value the value
		 */
		CPLDC_STATUS(final int value) {
			this.code = value;
		}

		/**
		 * From value.
		 *
		 * @param value the value
		 * @return the cpldc status
		 */
		public static CPLDC_STATUS fromValue(final int value) {
			for (final CPLDC_STATUS status : CPLDC_STATUS.values()) {
				if (status.code == value) {
					return status;
				}
			}
			return NoDialogue;
		}

	}

}
