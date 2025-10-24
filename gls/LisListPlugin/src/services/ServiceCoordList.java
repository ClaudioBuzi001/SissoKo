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
import auxliary.PluginListConstants;

/**
 * The Class ServiceCoordList.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public class ServiceCoordList implements IFunctionalService {

	/**
	 * Execute.
	 *
	 * @param inputJson the input json
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData inputJson) {

		String ret_value = "";
		String ret_color = "";

		inputJson.getSafeString("ID");
		inputJson.getSafeString("DATA_TYPE");
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
		parameters.getSafeString("VAL_1");
		parameters.getSafeString("VAL_2");
		/** The val 3. */
		String VAL_3 = parameters.getSafeString("VAL_3");
		/** The val 4. */
		String VAL_4 = parameters.getSafeString("VAL_4");

		if (INFO_TYPE.equals("COLOR")) {

			String template = "";
			final String flightNum = inputJson.getSafeString("FLIGHT_NUM");
			boolean warningTrans = inputJson.getSafeBoolean("COORDTRANS");

			final Optional<IRawData> jsonflight = BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(),
					flightNum);
			if (jsonflight.isPresent()) {
				template = jsonflight.get().getSafeString("TEMPLATE");
			}

			if ("COORDIN".equals(LIST_NAME)) {
				if (FIELD_TO_CHECK1.equals("COORDINDATA")) {
					if (VAL_3.equals("COORD_STATUS") && (inputJson.getSafeString("COORDSTATE").equals("3"))) {
						ret_color = "#FFA500";
					}

					if (VAL_4.equals("COORD_TYPE")) {
						if ((inputJson.getSafeString("COORDTYPE").equals("XFL")
								&& template.equals(PluginListConstants.TEMPLATE_CONTROLLED))
								|| (inputJson.getSafeString("COORDTYPE").equals("PEL")
										&& template.equals(PluginListConstants.TEMPLATE_AIS))) {
							if (warningTrans) {
								ret_color = "#FFA500";
							}
						}
					}
				}

				if (ret_color.isEmpty()) {
					ret_color = DEFAULT;
				}
			}

			if ("COORDOUT".equals(LIST_NAME)) {
				if (FIELD_TO_CHECK1.equals("COORDOUTDATA")) {
					if (VAL_3.equals("COORD_STATUS") && (inputJson.getSafeString("COORDSTATE").equals("3"))) {
						ret_color = "#FFA500";
					}

					if (VAL_4.equals("COORD_TYPE")) {
						if ((inputJson.getSafeString("COORDTYPE").equals("XFL")
								&& template.equals(PluginListConstants.TEMPLATE_CONTROLLED))
								|| (inputJson.getSafeString("COORDTYPE").equals("PEL")
										&& template.equals(PluginListConstants.TEMPLATE_AIS))) {
							if (warningTrans) {
								ret_color = "#FFA500";
							}
						}
					}
				}

				if (ret_color.isEmpty()) {
					ret_color = DEFAULT;
				}
			}
			return ret_color;
		}

		if (INFO_TYPE.equals("CALLBCK")) {

			if ("COORDIN".equals(LIST_NAME)) {
				if (!inputJson.isEmpty()) {

					final String val_COORDTYPE_field1 = inputJson.getSafeString(FIELD_TO_CHECK1);
					final int val_COORDState_field2 = inputJson.getSafeInt(FIELD_TO_CHECK2, 0);

					/** The quick ACPXRQ. */
					String quickACPXRQ = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPXRQ, OBJECT_TYPE=XRQ,  PREVIEW_NAME=choiceACPXRQ, DATA={'PREVIEWINVISIBLE':'true','quickAccept':'true'})";
					/** The quick ACPCSP. */
					String quickACPCSP = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPCSP, OBJECT_TYPE=CSP,  PREVIEW_NAME=choiceACPCSP, DATA={'PREVIEWINVISIBLE':'true','quickAccept':'true'})";
					/** The quick ACPCHE. */
					String quickACPCHE = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPCHE, OBJECT_TYPE=CHE,  PREVIEW_NAME=choiceACPCHE, DATA={'PREVIEWINVISIBLE':'true','quickAccept':'true'})";
					/** The quick ACPDCT. */
					String quickACPDCT = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPDCT, OBJECT_TYPE=DCT,  PREVIEW_NAME=choiceACPDCT, DATA={'PREVIEWINVISIBLE':'true','quickAccept':'true'})";
					/** The quick ACPPEL. */
					String quickACPPEL = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPPEL, OBJECT_TYPE=PEL,  PREVIEW_NAME=choiceACPPEL, DATA={'PREVIEWINVISIBLE':'true','quickAccept':'true'})";
					/** The quick ACPXFL. */
					String quickACPXFL = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPXFL, OBJECT_TYPE=XFL,  PREVIEW_NAME=choiceACPXFL, DATA={'PREVIEWINVISIBLE':'true','quickAccept':'true'})";
					
					/** The quick ACPGEC. */
					String quickACPGEC = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPGEC, OBJECT_TYPE=GEC,  PREVIEW_NAME=choiceACPGEC, DATA={'PREVIEWINVISIBLE':'true','quickAccept':'true'})";
					
					switch (val_COORDState_field2) {
					case 1:
						switch (val_COORDTYPE_field1) {
						case "XFL":
							ret_value = quickACPXFL;
							break;

						case "PEL":
							ret_value = quickACPPEL;
							break;

						case "DCT":
							ret_value = quickACPDCT;
							break;
							
						case "CHE":
							ret_value = quickACPCHE;
							break;

						case "CSP":
							ret_value = quickACPCSP;
							break;

						case "XRQ":
							ret_value = quickACPXRQ;
							break;

						case "GEC":
							ret_value = quickACPGEC;
							break;	
							
						default:
							break;
						}
						break;

					case 4:

						/** The quick ACPRRQ. */
						String quickACPRRQ = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPRLS, OBJECT_TYPE=RLS,  PREVIEW_NAME=choiceACPRLS, DATA={'PREVIEWINVISIBLE':'true','quickAccept':'true'})";
						/** The quick ACPTAC. */
						String quickACPTAC = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPTAC, OBJECT_TYPE=TAC,  PREVIEW_NAME=choiceACPTAC, DATA={'PREVIEWINVISIBLE':'true','quickAccept':'true'})";
						/** The quick ACPRTI. */
						String quickACPRTI = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPRTI, OBJECT_TYPE=RTI,  PREVIEW_NAME=choiceACPRTI, DATA={'PREVIEWINVISIBLE':'true','quickAccept':'true'})";
						/** The quick ACPTIP. */
						String quickACPTIP = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPTIP, OBJECT_TYPE=TIP,  PREVIEW_NAME=choiceACPTIP, DATA={'PREVIEWINVISIBLE':'true','quickAccept':'true'})";
						
						
						switch (val_COORDTYPE_field1) {

						case "XFL":
							ret_value = quickACPXFL;
							break;

						case "PEL":
							ret_value = quickACPPEL;
							break;

						case "GEC":
							ret_value = quickACPGEC;
							break;		
							
						case "DCT":
							ret_value = quickACPDCT;
							break;

						case "CHE":
							ret_value = quickACPCHE;
							break;

						case "CSP":
							ret_value = quickACPCSP;
							break;

						case "XRQ":
							ret_value = quickACPXRQ;
							break;

						case "RTI":
							ret_value = quickACPRTI;
							break;

						case "TIP":
							ret_value = quickACPTIP;
							break;

						case "TAC":
							ret_value = quickACPTAC;
							break;

						case "RRQ":
							ret_value = quickACPRRQ;
							break;
						default:
							break;
						}
						break;

					default:
						break;
					}
				}
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
		String myName = "COORD_LIST";
		return myName;
	}
}
