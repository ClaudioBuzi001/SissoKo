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

import com.fourflight.WP.ECI.edm.EdmModelKeys.TrajectoryPoint;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataArray;
import com.gifork.commons.data.IRawDataElement;

import application.pluginService.ServiceExecuter.IFunctionalService;
import applicationLIS.BlackBoardConstants_LIS;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.BlackBoardUtility_LIS;
import applicationLIS.Utils_LIS;

/**
 * The Class ServiceAliasList.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public class ServiceAliasList implements IFunctionalService {

	/**
	 * Execute.
	 *
	 * @param inputJson the input json
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData inputJson) {
		String ret_value = "";

		String flc = "";

		/** The data type. */
		String DATA_TYPE = inputJson.getType();
		final IRawDataElement parameters = inputJson.getAuxiliaryData();
		/** The out data. */
		String OUT_DATA = parameters.getSafeString("OUT_DATA");
		/** The info type. */
		String INFO_TYPE = parameters.getSafeString("INFO_TYPE");

		/** The field to check. */
		String FIELD_TO_CHECK = parameters.getSafeString("FIELD_TO_CHECK").replace("..", "");
		/** The field to check1. */
		String FIELD_TO_CHECK1 = parameters.getSafeString("FIELD_TO_CHECK1").replace("..", "");

		final int OUTFIXID = inputJson.getSafeInt("OFIXID", 0);
		final int INFIXID = inputJson.getSafeInt("IFIXID", 0);
		if (INFO_TYPE.isEmpty()) {
			ret_value = "";
			if (FIELD_TO_CHECK.isEmpty()) {

				switch (FIELD_TO_CHECK1) {

				case "CONFLICT_TIME":
					if (DATA_TYPE.equals("SNET")) {

						ret_value = Utils_LIS.getAlarmConflictTime(inputJson.getSafeString(FIELD_TO_CHECK1));
					}
					break;

				case "ALARM_TYPE":
					ret_value = Utils_LIS.getAlarmConflictType(inputJson.getSafeString(FIELD_TO_CHECK1));
					break;

				case "ALARM_URGENCY":

					ret_value = Utils_LIS.getAlarmUrgency(inputJson.getSafeString(FIELD_TO_CHECK1));
					break;

				case "CS_SSR1":
					if (DATA_TYPE.equals("SNET")) {

						ret_value = Utils_LIS.getAlarmCode(inputJson.getSafeString("STN1"));
					}
					break;

				case "CS_SSR2":
					if (DATA_TYPE.equals("SNET")) {

						final String alarmType = inputJson.getSafeString("ALARM_TYPE_STRING");
						if (alarmType.equals("APW") || alarmType.equals("MSAW")) {
							ret_value = inputJson.getSafeString("MAP_ID");
						} else {
							ret_value = Utils_LIS.getAlarmCode(inputJson.getSafeString("STN2"));
						}
					}
					break;

				case "DISTANCE_AT_CONFLICT_TIME":
					ret_value = Utils_LIS
							.getDistanceDoubleToTwoDigit(inputJson.getSafeString("DISTANCE_AT_CONFLICT_TIME"));
					break;

				case "PRESENT_PLANAR_DISTANCE":
					ret_value = Utils_LIS
							.getDistanceDoubleToTwoDigit(inputJson.getSafeString("PRESENT_PLANAR_DISTANCE"));
					break;

				case "LEV1":
					if (DATA_TYPE.equals("SNET")) {

						ret_value = Utils_LIS.getTrackValue(inputJson.getSafeString("STN1"), "LEV");
						if (ret_value.equals("32768")) {
							ret_value = "--";
						}
					}
					break;

				case "ADSB_EMG1":
					if (DATA_TYPE.equals("SNET")) {

						ret_value = Utils_LIS.getTrackValue(inputJson.getSafeString("STN1"), "ADSB_EMG");
					}
					break;

				case "LEV2":
					if (DATA_TYPE.equals("SNET")) {

						ret_value = Utils_LIS.getTrackValue(inputJson.getSafeString("STN2"), "LEV");
						if (ret_value.equals("32768")) {
							ret_value = "--";
						}
					}
					break;

				case "ADSB_EMG2":
					if (DATA_TYPE.equals("SNET")) {

						ret_value = Utils_LIS.getTrackValue(inputJson.getSafeString("STN2"), "ADSB_EMG");
					}
					break;

				case "SCT1":

					final String sct1 = Utils_LIS.getFlightValue(inputJson.getSafeString("STN1"), "SCT");
					ret_value = BlackBoardUtility_LIS.searchSECTOR_NAME(sct1);
					break;

				case "SCT2":

					final String sct2 = Utils_LIS.getFlightValue(inputJson.getSafeString("STN2"), "SCT");
					ret_value = BlackBoardUtility_LIS.searchSECTOR_NAME(sct2);
					break;

				default:
					break;

				}

			} else {
				switch (FIELD_TO_CHECK) {
				case "SCT":
				case "GOW":

					final String SCT = inputJson.getSafeString(FIELD_TO_CHECK);
					ret_value = BlackBoardUtility_LIS.searchSECTOR_NAME(SCT);
					break;
				case "MSE":

					final int MSE = inputJson.getSafeInt(FIELD_TO_CHECK, 0);
					if (MSE == 0) {
						ret_value = "N";
					} else {
						ret_value = "Y";
					}
					break;

				case "ETOIFIXID":
					final int IFIXID = inputJson.getSafeInt("IFIXID", 0);
					String flightNUM = inputJson.getSafeString("FLIGHT_NUM");
					Optional<IRawData> jsonFlTrnew = BlackBoardUtility.getDataOpt(DataType.FLIGHT_TRJ.name(),
							flightNUM);
					if (jsonFlTrnew.isPresent()) {
						IRawDataArray rawTrjArra = jsonFlTrnew.get().getSafeRawDataArray("TRJ_ARRAY");
						if (rawTrjArra.size() > 0 && IFIXID != -1) {
							var elem = rawTrjArra.get(IFIXID);
							if (elem != null) {
								ret_value = elem.getSafeString(TrajectoryPoint.ETO);
							}
						}
					}

					break;

				case "ETOOUTFIXID":
					flightNUM = inputJson.getSafeString("FLIGHT_NUM");
					jsonFlTrnew = BlackBoardUtility.getDataOpt(DataType.FLIGHT_TRJ.name(), flightNUM);
					if (jsonFlTrnew.isPresent()) {
						IRawDataArray rawTrjArra = jsonFlTrnew.get().getSafeRawDataArray("TRJ_ARRAY");
						if (rawTrjArra.size() > 0 && OUTFIXID != -1 && rawTrjArra.size() < OUTFIXID) {
							var elem = rawTrjArra.get(OUTFIXID);
							if (elem != null) {
								ret_value = elem.getSafeString(TrajectoryPoint.ETO);
							}
						}
					}
					break;

				case "OFIXNAME":
					flightNUM = inputJson.getSafeString("FLIGHT_NUM");
					jsonFlTrnew = BlackBoardUtility.getDataOpt(DataType.FLIGHT_TRJ.name(), flightNUM);
					if (jsonFlTrnew.isPresent()) {
						IRawDataArray rawTrjArra = jsonFlTrnew.get().getSafeRawDataArray("TRJ_ARRAY");
						if (rawTrjArra.size() > 0 && OUTFIXID != -1 && rawTrjArra.size() < OUTFIXID) {
							var elem = rawTrjArra.get(OUTFIXID);
							if (elem != null) {
								ret_value = elem.getSafeString(TrajectoryPoint.NAME);
							}
						}
					}

					break;

				case "OFIXLEV":
					flightNUM = inputJson.getSafeString("FLIGHT_NUM");
					jsonFlTrnew = BlackBoardUtility.getDataOpt(DataType.FLIGHT_TRJ.name(), flightNUM);
					if (jsonFlTrnew.isPresent()) {
						IRawDataArray rawTrjArra = jsonFlTrnew.get().getSafeRawDataArray("TRJ_ARRAY");
						if (rawTrjArra.size() > 0 && OUTFIXID != -1 && rawTrjArra.size() < OUTFIXID) {
							var elem = rawTrjArra.get(OUTFIXID);
							if (elem != null) {
								ret_value = elem.getSafeString(TrajectoryPoint.LEV);
							}
						}
					}

					break;

				case "IFIXNAME":
					flightNUM = inputJson.getSafeString("FLIGHT_NUM");
					jsonFlTrnew = BlackBoardUtility.getDataOpt(DataType.FLIGHT_TRJ.name(), flightNUM);
					if (jsonFlTrnew.isPresent()) {
						IRawDataArray rawTrjArra = jsonFlTrnew.get().getSafeRawDataArray("TRJ_ARRAY");
						if (rawTrjArra.size() > 0 && INFIXID != -1) {
							var elem = rawTrjArra.get(INFIXID);
							if (elem != null) {
								ret_value = elem.getSafeString(TrajectoryPoint.NAME);
							}
						}
					}

					break;

				case "response":
					/****
					 *
					 * Presentazione LAbel UNA SBY e WLC
					 *
					 *
					 * UNABLE presente se Se request_speed and scDDS del volo = 0-1-2-3-13 Se request_dct and rmDDS del
					 * volo = 0-1-2-3-13 Se request_lv and vcDDS del volo = 0-1-2-3-13
					 *
					 *
					 * StandBy presente se Se request_speed and scDDS del volo = 0-1-2 Se request_dct and rmDDS del volo
					 * = 0-1-2 Se request_lv and vcDDS del volo = 0-1-2
					 *
					 *
					 * Occorre gestire colori su altri stati
					 *
					 */

					final String response = inputJson.getSafeString("response");
					if (response.equals("Y")) {
						ret_value = OUT_DATA;
					}
					break;

				case "CS_SSR1":
					final String STN1 = inputJson.getSafeString("STN1");
					String CS1 = "";
					String SSR1 = "";
					if (!STN1.equals("-1")) {

						SSR1 = Utils_LIS.getTrackValue(STN1, "MODE_3A");
						CS1 = Utils_LIS.getFlightValue(STN1, "CALLSIGN");
						if (!CS1.isEmpty()) {
							ret_value = CS1;
						} else if (!SSR1.isEmpty()) {
							ret_value = SSR1;
						}
					}
					break;
				case "CS_SSR2":

					final String STN2 = inputJson.getSafeString("STN2");
					String CS2 = "";
					String SSR2 = "";
					final String alarmType = inputJson.getSafeString("ALARM_TYPE_STRING");
					if (alarmType.equals("APW")) {
						ret_value = inputJson.getSafeString("MAP_ID");
					} else {
						if (!STN2.equals("-1")) {
							SSR2 = Utils_LIS.getTrackValue(STN2, "MODE_3A");
							CS2 = Utils_LIS.getFlightValue(STN2, "CALLSIGN");
							if (!CS2.isEmpty()) {
								ret_value = CS2;
							} else if (!SSR2.isEmpty()) {
								ret_value = SSR2;
							}
						}

					}
					break;
				case "SCT1":
					final String SCTSTN1 = inputJson.getSafeString(FIELD_TO_CHECK);
					if (!SCTSTN1.equals("-1")) {
						String SCT1 = Utils_LIS.getFlightValue(SCTSTN1, "SCT");
						ret_value = BlackBoardUtility_LIS.searchSECTOR_NAME(SCT1);
					}
					break;

				case "SCT2":
					final String SCTSTN2 = inputJson.getSafeString(FIELD_TO_CHECK);
					if (!SCTSTN2.equals("-1")) {
						String SCT2 = Utils_LIS.getFlightValue(SCTSTN2, "SCT");
						ret_value = BlackBoardUtility_LIS.searchSECTOR_NAME(SCT2);
					}
					break;

				case "ISAPW":
					String onlyApw = inputJson.getSafeString("onlyApw");
					String apwAndSua = inputJson.getSafeString("apwAndSua");
					ret_value = "OFF";
					if (onlyApw.equals("ON") || apwAndSua.equals("ON")) {
						ret_value = "ON";
					}
					break;

				case "ISSUA":
					String onlySua = inputJson.getSafeString("onlySua");
					apwAndSua = inputJson.getSafeString("apwAndSua");
					ret_value = "OFF";
					if (onlySua.equals("ON") || apwAndSua.equals("ON")) {
						ret_value = "ON";
					}
					break;

				case "statustt":
					final String ttStatus = inputJson.getSafeString("statustt");
					/**
					 * abilitazione ordine statustt se non e' vuoto
					 ***/
					ret_value = "";
					if (!ttStatus.isEmpty()) {
						ret_value = "EXTERNAL_ORDER(ORDER_ID=DBCS, OBJECT_TYPE=DBCS, DATA={'STATUS_TT':'Y'}, PREVIEW_NAME=quickDBCS)";
					}
					break;

				case "status":
					final String Status = inputJson.getSafeString("statustt");
					/**
					 * abilitazione ordine status standar ON-OFF solo se statustt presente e uguale a OFF se statustt
					 * non presente abilito ordine
					 ***/
					ret_value = "";
					if (!Status.isEmpty()) {
						if (Status.equals("OFF")) {
							ret_value = "EXTERNAL_ORDER(ORDER_ID=DBCS, OBJECT_TYPE=DBCS, PREVIEW_NAME=quickDBCS)";
						}
					} else {
						ret_value = "EXTERNAL_ORDER(ORDER_ID=DBCS, OBJECT_TYPE=DBCS, PREVIEW_NAME=quickDBCS)";
					}

					break;
					
				case "ACK_BV4":	
					final String ackBV4 = inputJson.getSafeString("ACK_BV4");
					/**
					 * abilitazione ordine BUFA se campo non e' vuoto
					 ***/
					ret_value = "";
					if (!ackBV4.isEmpty()) {
						ret_value = "EXTERNAL_ORDER(POPUP_ORDER=ACK, ORDER_ID=BUFA , PREVIEW_NAME=quickBUFA ,  OBJECT_TYPE=BUFA)";
					}
					break;		
				
				case "UNDO_DCL":	
					final boolean undoDCL = inputJson.getSafeBoolean("FDCL");
					final boolean isPending = inputJson.getSafeBoolean("ISPENDING");
					/**
					 * abilitazione ordine UNDO_DCL se campo non e' true
					 ***/
					ret_value = "";
					if (undoDCL && isPending) {
						ret_value = "EXTERNAL_ORDER(POPUP_ORDER=UNDO DCL, ORDER_ID=BPC  , PREVIEW_NAME=quickBPC  ,  OBJECT_TYPE=BPC)";
					}
					break;		
					
				case "TACSTATUS":	
					final String tacStatus = inputJson.getSafeString("TACSTATUS");
					if (tacStatus.equals("ST")) {
						ret_value = "PS";
					}else {
						ret_value = tacStatus;
					}
					break;	
					
				case "MTCD_ACK":
					final var sectorList = inputJson.getSafeRawDataArray("DISTRIBUTION_LIST_SECTORS");
					for (final IRawDataElement colItem : sectorList) {
						final int sectId = Utils_LIS.getSectorID();
						final int sop = colItem.getSafeInt("SOP");
						if (sop == sectId) {
							if (colItem.getSafeInt("ACK_WARNING_MASK") == 0) {
								ret_value = "N";
							} else {
								ret_value = "S";
							}
							break;
						}
					}
					break;
				case "OXF":
					ret_value = inputJson.getSafeInt("OXF") == 1 ? "TC" : "PC";
					break;
				case "MY_ROLE":
					final var own = BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name());
					if (own.isPresent()) {
						String role = own.get().getSafeString("MY_ROLES");
						if (role.equals("EXEC") || role.equals("EXECUTIVE")) {
							ret_value = "PC";
						} else if (role.equals("PLN") || role.equals("PLANNER")) {
							ret_value = "TC";
						} else {
							ret_value = "EP";
						}
					}

					break;
				case "SCA":
					String stn = Utils_LIS.getStnFromFn(inputJson.getId());
					final var priorityScaId = Utils_LIS.getPrioirtyScaConflict(stn);
					final var rawdataSca = BlackBoardUtility.getDataOpt(BlackBoardConstants_LIS.DataType.SCA.name(),
							priorityScaId);
					if (rawdataSca.isPresent()) {
						ret_value = rawdataSca.get().getSafeString("LABEL_STR");
					}

					break;
				case "DEPIFX":
					/***
					 * FLC 0 = overfly, 1 = inbound, 2 = outbound, 3 = domestic, 4 = local
					 */
					flc = inputJson.getSafeString("FLC");
					if (flc.equals("1")) {
						ret_value = inputJson.getSafeString("IFIXNAME");
					} else {
						ret_value = inputJson.getSafeString("ADEP");
					}

					break;
				case "ETDETI":
					flc = inputJson.getSafeString("FLC");
					if (flc.equals("1")) {
						ret_value = inputJson.getSafeString("ETOIFIXID");
					} else {
						ret_value = inputJson.getSafeString("ETD");
					}

					break;

				case "DESOFX":
					flc = inputJson.getSafeString("FLC");
					if (flc.equals("2")) {
						ret_value = inputJson.getSafeString("OFIXNAME");
					} else {
						ret_value = inputJson.getSafeString("ADES");
					}

					break;
				case "ETLETO":
					flc = inputJson.getSafeString("FLC");
					if (flc.equals("2")) {
						ret_value = inputJson.getSafeString("ETOOUTFIXID");
					} else {
						ret_value = inputJson.getSafeString("ETL");
					}

					break;

				case "CONFLICT_OR_RISK":
					final String conflitOrRisk = inputJson.getSafeString("CONFLICT_OR_RISK");
					if (conflitOrRisk.equals("CONFLICT")) {
						final int type = inputJson.getSafeInt("ENCOUNTER_TYPE");
						switch (type) {
						case 1:
							ret_value = "MERG";
							break;
						case 2:
							ret_value = "TRAIL";
							break;
						case 3:
							ret_value = "FACE";
							break;
						case 4:
							ret_value = "CROSS";
							break;
						default:
							ret_value = "CONFLICT";
							break;
						}
					} else {
						if (conflitOrRisk.contains("RISK")) {
							ret_value = "RISK";
						} else {
							ret_value = conflitOrRisk;
						}
					}
					break;
				default:
					break;
				}
			}
		}

		if (INFO_TYPE.equals("IMAGE")) {
			switch (FIELD_TO_CHECK) {

			case "IS_FPT":
				ret_value = inputJson.getSafeBoolean(FIELD_TO_CHECK) ? "TRUE" : "FALSE";
				break;

			case "ATD1":
				if (DATA_TYPE.equals("SNET")) {
					ret_value = Utils_LIS.getTrackValue(inputJson.getSafeString("STN1"), "ATD");
				}
				break;
			case "ATD2":
				if (DATA_TYPE.equals("SNET")) {

					ret_value = Utils_LIS.getTrackValue(inputJson.getSafeString("STN2"), "ATD");
				}
				break;

			case "ACK_TCAT":
				final Optional<IRawData> tcatAck = BlackBoardUtility.getDataOpt(DataType.BB_TCAT_ACK.name(),inputJson.getId());
				if (tcatAck.isPresent()) {
					ret_value = "TRUE";	
				}else {
					ret_value ="FALSE";
				}
				
				break;	
			
			case "FDCL":
				
				final boolean undoDCL = inputJson.getSafeBoolean("FDCL");
				final boolean isPending = inputJson.getSafeBoolean("ISPENDING");
				/**
				 * presentazione spunta DCL se volo pending
				 ***/
				ret_value = "";
				if (undoDCL && isPending) {
					ret_value = "FALSE";
				}else {
					ret_value = "TRUE";
				}
				break;	
				
			default:
				break;
			}
		}

		if (FIELD_TO_CHECK.equals("CALLBACK_SHOW_FPT")) {
			final boolean fptFlag = inputJson.getSafeBoolean("IS_FPT");
			if (fptFlag) {
				/** The Is FPT show. */
				String isFPTShow = "SHOW_FPT()";
				ret_value = isFPTShow;
			}
		}

		
		if (FIELD_TO_CHECK.equals("CALLBACK_ACK_TCAT")) {
			ret_value = "";
			final Optional<IRawData> tcatAck = BlackBoardUtility.getDataOpt(DataType.BB_TCAT_ACK.name(),inputJson.getId());
			if (!tcatAck.isPresent()) {
				ret_value = "EXTERNAL_ORDER(ORDER_ID=ACKTCAT, OBJECT_TYPE=ACKTCAT,  DATA={'FLIGHT_NUM': '"+ inputJson.getSafeString("FLIGHT_NUM") +"'}, PREVIEW_NAME=quickACKTCAT)";	
			}
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
		/** The My name. */
		String myName = "ALIAS_LIST";
		return myName;
	}

}
