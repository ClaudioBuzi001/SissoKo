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

import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

import application.pluginService.ServiceExecuter.IFunctionalService;
import applicationLIS.Utils_LIS;

/**
 * The Class ServiceTCTList.
 *
 * @author pcardoni
 * @version $Revision$
 */
public class ServiceTCTList implements IFunctionalService {

	/**
	 * Execute.
	 *
	 * @param inputJson the input json
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData inputJson) {
		String ret_value = "";
		final IRawDataElement parameters = inputJson.getAuxiliaryData();

		/** The field to check. */
		String FIELD_TO_CHECK = parameters.getSafeString("FIELD_TO_CHECK").replace("..", "");

		switch (FIELD_TO_CHECK) {
		case "TIME_TO_CONFLICT":

			ret_value = Utils_LIS.formatSecondsToMMSS(inputJson.getSafeInt(FIELD_TO_CHECK));
			break;
		case "CS_SSR1":
			ret_value = Utils_LIS.getCSOrRcsOrSsr(inputJson.getSafeString("TRACK_NUMBER1"));
			break;
		case "LEV1":
			ret_value = Utils_LIS.getTrackValue(inputJson.getSafeString("TRACK_NUMBER1"), "LEV");
			break;
		case "LEV2":
			ret_value = Utils_LIS.getTrackValue(inputJson.getSafeString("TRACK_NUMBER2"), "LEV");
			break;
		case "CS_SSR2":
			ret_value = Utils_LIS.getCSOrRcsOrSsr(inputJson.getSafeString("TRACK_NUMBER2"));
			break;
		case "CONFLICT_TIME":
			ret_value = Utils_LIS.getFormattedHHMM(inputJson.getSafeString("CONFLICT_TIME"));
			break;
		case "CLOSEST_APPROACH_TIME":
			ret_value = Utils_LIS.getFormattedHHMM(inputJson.getSafeString("CLOSEST_APPROACH_TIME"));
			break;
		case "PRESENT_PLANAR_DISTANCE":
			ret_value = Utils_LIS.getDistanceDoubleToTwoDigit(inputJson.getSafeString("PRESENT_PLANAR_DISTANCE"));
			break;
		case "EST_DISTANCE_TO_SEP_LOSS_1":
			ret_value = Utils_LIS.getDistanceDoubleToTwoDigit(inputJson.getSafeString("EST_DISTANCE_TO_SEP_LOSS_1"));
			break;
		case "EST_DISTANCE_TO_SEP_LOSS_2":
			ret_value = Utils_LIS.getDistanceDoubleToTwoDigit(inputJson.getSafeString("EST_DISTANCE_TO_SEP_LOSS_2"));
			break;
		case "TIME_TO_CLOSEST_APPROACH":
			ret_value = Utils_LIS.getFormattedMMSS(inputJson.getSafeString("TIME_TO_CLOSEST_APPROACH"));
			break;
		case "ESTIMATED_MINIMUM_PLANAR_DISTANCE":
			ret_value = Utils_LIS
					.getDistanceDoubleToTwoDigit(inputJson.getSafeString("ESTIMATED_MINIMUM_PLANAR_DISTANCE"));
			break;
		case "D_TO_MD1":
			ret_value = Utils_LIS.getDistanceDoubleToTwoDigit(inputJson.getSafeString("D_TO_MD1"));
			break;
		case "D_TO_MD2":
			ret_value = Utils_LIS.getDistanceDoubleToTwoDigit(inputJson.getSafeString("D_TO_MD2"));
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
		/** The My name. */
		String myName = "TCT_LIST";
		return myName;
	}

}
