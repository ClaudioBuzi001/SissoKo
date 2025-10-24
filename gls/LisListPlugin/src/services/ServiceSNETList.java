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
import com.leonardo.infrastructure.Strings;

import application.pluginService.ServiceExecuter.IFunctionalService;

/**
 * The Class ServiceSNETList.
 */
public class ServiceSNETList implements IFunctionalService {

	/**
	 * Execute.
	 *
	 * @param inputJson the input json
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData inputJson) {
		final IRawDataElement parameters = inputJson.getAuxiliaryData();

		/** The field to check. */
		String FIELD_TO_CHECK = parameters.getSafeString("FIELD_TO_CHECK").replace("..", "");
		String result = setValue(FIELD_TO_CHECK, inputJson);

		return result;
	}

	/**
	 * Sets the value.
	 *
	 * @param field     the field
	 * @param inputJson the input json
	 * @return the string
	 */
	private static String setValue(final String field, final IRawData inputJson) {

		String retValue = "FALSE";

		if (field.equals("FILTERS")) {

			if (inputJson.getSafeString("pdvAsf").equals("ON") || inputJson.getSafeString("pdvPsf").equals("ON")) {
				return "TRUE";
			}

			if (inputJson.getSafeString("vfrAsf").equals("ON") || inputJson.getSafeString("vfrPsf").equals("ON")) {
				return "TRUE";
			}

			if (inputJson.getSafeString("ifrAsf").equals("ON") || inputJson.getSafeString("ifrPsf").equals("ON")) {
				return "TRUE";
			}

			if (inputJson.getSafeString("milAsf").equals("ON") || inputJson.getSafeString("milPsf").equals("ON")) {
				return "TRUE";
			}

			if (inputJson.getSafeString("supAsf").equals("ON") || inputJson.getSafeString("supPsf").equals("ON")) {
				return "TRUE";
			}

			if (inputJson.getSafeString("gatAsf").equals("ON") || inputJson.getSafeString("gatPsf").equals("ON")) {
				return "TRUE";
			}

			if (inputJson.getSafeString("oatAsf").equals("ON") || inputJson.getSafeString("oatPsf").equals("ON")) {
				return "TRUE";
			}

			if (inputJson.getSafeString("monAsf").equals("ON") || inputJson.getSafeString("monPsf").equals("ON")) {
				return "TRUE";
			}

			if (inputJson.getSafeString("psrAsf").equals("ON") || inputJson.getSafeString("psrPsf").equals("ON")) {
				return "TRUE";
			}

			if (inputJson.getSafeString("macAsf").equals("ON") || inputJson.getSafeString("macPsf").equals("ON")) {
				return "TRUE";
			}

			if (inputJson.getSafeString("rxhAsf").equals("ON") || inputJson.getSafeString("rxhPsf").equals("ON")) {
				return "TRUE";
			}

			if (inputJson.getSafeString("losAsf").equals("ON") || inputJson.getSafeString("losPsf").equals("ON")) {
				return "TRUE";
			}

			if (inputJson.getSafeString("rahAsf").equals("ON") || inputJson.getSafeString("rahPsf").equals("ON")) {
				return "TRUE";
			}
			if (inputJson.getSafeString("holAsf").equals("ON") || inputJson.getSafeString("holPsf").equals("ON")) {
				return "TRUE";
			}
		}

		if (field.equals("CALLSIGN")) {
			for (int i = 1; i < 16; i++) {

				String it = Strings.concat("css", String.valueOf(i));
				if (!inputJson.getSafeString(it).trim().isEmpty()) {
					retValue = "TRUE";
					break;
				}

			}
		}

		if (field.equals("SSR")) {
			for (int i = 1; i < 16; i++) {
				String it = Strings.concat("mcs", String.valueOf(i));
				if (!inputJson.getSafeString(it).trim().isEmpty()) {
					retValue = "TRUE";
					break;
				}

			}
		}

		return retValue;
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {

		/** The My name. */
		String myName = "SNET_LIST";
		return myName;

	}

}
