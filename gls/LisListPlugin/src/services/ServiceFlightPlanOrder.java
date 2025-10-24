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

import com.gifork.auxiliary.SafeDataRetriever;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

import application.pluginService.ServiceExecuter.IFunctionalService;

/**
 * The Class ServiceFlightPlanOrder.
 */
public class ServiceFlightPlanOrder implements IFunctionalService {

	/**
	 * Execute.
	 *
	 * @param inputJson the input json
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData inputJson) {
		final IRawDataElement parameters = inputJson.getAuxiliaryData();
		/** The val 1. */
		String VAL_1 = parameters.getSafeString("VAL_1");
		/** The field to check. */
		String FIELD_TO_CHECK = parameters.getSafeString("FIELD_TO_CHECK").replace("..", "");
		String result = formatorder(VAL_1, FIELD_TO_CHECK);

		return result;
	}

	/**
	 * Formatorder.
	 *
	 * @param value           the value
	 * @param fIELD_TO_CHECK2 the f IEL D T O CHECK 2
	 * @return the string
	 */
	private String formatorder(final String value, final String fIELD_TO_CHECK2) {
		final int value_int = SafeDataRetriever.strToInteger(value);

		switch (fIELD_TO_CHECK2) {
		case "FLIGHT_CAT":

			/** The DC rcallback. */
			String DCRcallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW , ORDER_ID=DCR , OBJECT_TYPE=DCR , PREVIEW_NAME=previewDCR ,  )";
			if (value_int == 2) {
				return DCRcallback;
			}
			/** The NRC rcallback. */
			String NRCRcallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW ,ORDER_ID=NRCR, OBJECT_TYPE=NRCR,  PREVIEW_NAME=previewNRCR)";
			return NRCRcallback;

		case "LANDING_LOCAL":
			/** The NNAC rcallback. */
			String NNACRcallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW ,ORDER_ID=NNACR, OBJECT_TYPE=NNACR,  PREVIEW_NAME=previewNNACR)";
			if (value_int == 3 || value_int == 4) {
				return NNACRcallback;
			}
			return "";

		case "COORD_STATUS_PEL":

			/** The CTPPE lcallback. */
			String CTPPELcallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW ,ORDER_ID=CTPPEL , OBJECT_TYPE=PEL ,  PREVIEW_NAME=choicePEL)";
			/** The ACKPE lcallback. */
			String ACKPELcallback = "EXTERNAL_ORDER(ORDER_ID=ACKPEL , OBJECT_TYPE=PEL ,  PREVIEW_NAME=quickPEL , DATA={'COORDSTATE':'5'} )";
			/** The ACPPE lcallback. */
			String ACPPELcallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW , ORDER_ID=ACPPEL , PREVIEW_NAME=choicePEL,  OBJECT_TYPE=PEL)";
			/** The PE lcallback. */
			String PELcallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW ,ORDER_ID=PEL, OBJECT_TYPE=PEL, PREVIEW_NAME=choicePEL)";
			switch (value_int) {
			case 0:
				return PELcallback;
			case 1:
				return ACPPELcallback;
			case 2:
				return "";
			case 3:
				return ACKPELcallback;
			case 4:
				return CTPPELcallback;
			default:
				return "";
			}

		case "COORD_STATUS_XFL":
			/** The CTPXF lcallback. */
			String CTPXFLcallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW ,ORDER_ID=CTPXFL , OBJECT_TYPE=XFL ,  PREVIEW_NAME=choiceXFL)";
			/** The ACKXF lcallback. */
			String ACKXFLcallback = "EXTERNAL_ORDER(ORDER_ID=ACKXFL , OBJECT_TYPE=XFL ,  PREVIEW_NAME=quickXFL , DATA={'COORDSTATE':'5'} )";
			/** The ACPXF lcallback. */
			String ACPXFLcallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW ,ORDER_ID=ACPXFL, OBJECT_TYPE=XFL,  PREVIEW_NAME=choiceXFL)";
			/** The XF lcallback. */
			String XFLcallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW ,ORDER_ID=XFL, OBJECT_TYPE=XFL,  PREVIEW_NAME=choiceXFL)";
			switch (value_int) {
			case 0:
				return XFLcallback;
			case 1:
				return ACPXFLcallback;
			case 3:
				return ACKXFLcallback;
			case 4:
				return CTPXFLcallback;
			default:
				return "";
			}

		}
		return "";
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {

		/** The My name. */
		String myName = "FLIGHT_PLAN_ORDER";
		return myName;

	}

}
