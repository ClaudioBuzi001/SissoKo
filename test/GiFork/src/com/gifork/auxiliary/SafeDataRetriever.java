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

package com.gifork.auxiliary;

import java.util.HashMap;

import com.gifork.blackboard.GiForkConstants;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.Generics;
import com.leonardo.infrastructure.Strings;
import com.leonardo.infrastructure.log.ILogger;

/**
 * *.
 *
 * @author HCI Questa classe implementa una serie di metodi statici per recuperare informazioni
 *         dalle hasmap object e service evitando di tornare valoi null ed eventuali eccezioni
 * @version
 */
public class SafeDataRetriever {

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(SafeDataRetriever.class);

	/** The Constant dummyMap. */
	private static final HashMap<String, String> dummyMap = new HashMap<>();

	/**
	 * ** Data la struttura dati 'newDataModel' torna il valore assunto dall'attributo 'attribName'
	 * dell'elemento 'key'; qualora uno dei parametri fosse 'null', la funzione torna il valory di
	 * default "" (stringa vuota).
	 *
	 * @param dataModel  the data model
	 * @param key        the key
	 * @param attribName the attrib name
	 * @return the string data
	 */
	public static String getStringData(HashMap<String, HashMap<String, String>> dataModel, String key,
			String attribName) {
		return dataModel.getOrDefault(key, dummyMap).getOrDefault(attribName, Strings.EMPTY);
	}

	/**
	 * ** Data la struttura dati 'newDataModel' torna il valore assunto dall'elemento 'key'; qualora uno
	 * dei parametri fosse 'null', la funzione torna il valory di default "" (stringa vuota).
	 *
	 * @param dataModel the data model
	 * @param key       the key
	 * @return the string data
	 */
	public static String getStringData(HashMap<String, String> dataModel, String key) {
		if (dataModel != null) {
			return dataModel.getOrDefault(key, Strings.EMPTY);
		}
		return Strings.EMPTY;
	}

	/**
	 * ** Data la struttura dati 'newDataModel' torna il valore assunto dall'elemento 'key'; qualora uno
	 * dei parametri fosse 'null', la funzione torna il valory di default "" (stringa vuota).
	 *
	 * @param dataModel    the data model
	 * @param key          the key
	 * @param defaultValue the default value
	 * @return the string data or default
	 */
	public static String getStringDataOrDefault(HashMap<String, String> dataModel, String key, String defaultValue) {
		if (dataModel != null) {
			return dataModel.getOrDefault(key, defaultValue);
		}
		return defaultValue;
	}

	/**
	 * Gets the string data.
	 *
	 * @param dataModel    the data model
	 * @param key          the key
	 * @param attribName   the attrib name
	 * @param defaultValue the default value
	 * @return the string data
	 */
	public static String getStringData(HashMap<String, HashMap<String, String>> dataModel, String key,
			String attribName, String defaultValue) {
		return dataModel.getOrDefault(key, dummyMap).getOrDefault(attribName, defaultValue);
	}

	/**
	 * **.
	 *
	 * @param dataModel  the data model
	 * @param key        the key
	 * @param attribName the attrib name
	 * @return the int data
	 */
	public static int getIntData(HashMap<String, HashMap<String, String>> dataModel, String key, String attribName) {
		return strToInteger(getStringData(dataModel, key, attribName, "0"));
	}

	/**
	 * Gets the int dataor default.
	 *
	 * @param newDataModel the new data model
	 * @param key          the key
	 * @param defaultValue the default value
	 * @return the int dataor default
	 */
	public static int getIntDataorDefault(HashMap<String, HashMap<String, String>> newDataModel, String key,
			String defaultValue) {
		return strToInteger(newDataModel.getOrDefault(key, dummyMap).getOrDefault(key, "0"));
	}

	/**
	 * **.
	 *
	 * @param newDataModel the new data model
	 * @param key          the key
	 * @return the int data
	 */
	public static int getIntData(HashMap<String, String> newDataModel, String key) {
		return strToInteger(newDataModel.getOrDefault(key, "0"));
	}

	/**
	 * **.
	 *
	 * @param dataModel  the data model
	 * @param key        the key
	 * @param attribName the attrib name
	 * @return the float data
	 */
	public static float getFloatData(HashMap<String, HashMap<String, String>> dataModel, String key,
			String attribName) {
		return strToFloat(dataModel.getOrDefault(key, dummyMap).getOrDefault(attribName, "0"));
	}

	/**
	 * **.
	 *
	 * @param dataModel the data model
	 * @param key       the key
	 * @return the float data
	 */
	public static float getFloatData(HashMap<String, String> dataModel, String key) {
		return strToFloat(dataModel.getOrDefault(key, "0"));
	}

	/**
	 * **.
	 *
	 * @param dataModel  the data model
	 * @param key        the key
	 * @param attribName the attrib name
	 * @return the bool data
	 */
	public static boolean getBoolData(HashMap<String, HashMap<String, String>> dataModel, String key,
			String attribName) {
		return getStringData(dataModel, key, attribName).equalsIgnoreCase(GiForkConstants.TRUE);
	}

	/**
	 * **.
	 *
	 * @param dataModel the data model
	 * @param key       the key
	 * @return the bool data
	 */
	public static boolean getBoolData(HashMap<String, String> dataModel, String key) {
		return Generics.isOneOf(getStringData(dataModel, key).toUpperCase(), GiForkConstants.TRUE, "ON");
	}

	/**
	 * **.
	 *
	 * @param dataModel  the data model
	 * @param key        the key
	 * @param attribName the attrib name
	 * @return the double data
	 */
	public static double getDoubleData(HashMap<String, HashMap<String, String>> dataModel, String key,
			String attribName) {
		return strToDouble(getStringData(dataModel, key, attribName, "0"));
	}

	/**
	 * **.
	 *
	 * @param dataModel the data model
	 * @param key       the key
	 * @return the double data
	 */
	public static double getDoubleData(HashMap<String, String> dataModel, String key) {
		return strToDouble(getStringDataOrDefault(dataModel, key, "0"));
	}

	/**
	 * **.
	 *
	 * @param value the value
	 * @return the double
	 */
	public static double strToDouble(String value) {
		return strToDouble(value, 0.0);
	}

	/**
	 * Str to double.
	 *
	 * @param value        the value
	 * @param defaultValue the default value
	 * @return the double
	 */
	public static double strToDouble(String value, double defaultValue) {
		final String loggerCaller = "strToDouble(String, double )";
		double doubleValue = defaultValue;
		if (!Strings.isNullOrEmpty(value)) {
			try {
				doubleValue = Double.parseDouble(value);
			} catch (NumberFormatException e) {
				logger.logError(loggerCaller, e);
			}
		}
		return doubleValue;
	}

	/**
	 * **.
	 *
	 * @param value the value
	 * @return the int
	 */
	public static int strToInteger(String value) {
		final String loggerCaller = "strToInteger(String)";
		int intValue = 0;
		if (!Strings.isNullOrEmpty(value) && Strings.isNumeric(value)) {
			try {
				intValue = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				logger.logError(loggerCaller, e);
				intValue = 0;
			}
		}
		return intValue;
	}

	/**
	 * Str to integer.
	 *
	 * @param value        the value
	 * @param defaultValue the default value
	 * @return the int
	 */
	public static int strToInteger(String value, int defaultValue) {
		final String loggerCaller = "strToInteger(String, int)";
		int intValue = defaultValue;
		if (!Strings.isNullOrEmpty(value) && Strings.isNumeric(value)) {
			try {
				intValue = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				logger.logError(loggerCaller, e);
				intValue = defaultValue;
			}
		}
		return intValue;
	}

	/**
	 * **.
	 *
	 * @param value the value
	 * @return the float
	 */
	public static float strToFloat(String value) {
		final String loggerCaller = "strToFloat(String)";
		float floatValue = 0;
		try {
			floatValue = Float.parseFloat(value);
		} catch (NumberFormatException e) {
			logger.logError(loggerCaller, e);
			floatValue = 0;
		}
		return floatValue;
	}

	/**
	 * **.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public static boolean strToBool(String value) {
		final String loggerCaller = "strToBool(String)";
		boolean booleanValue = false;
		try {
			booleanValue = Boolean.parseBoolean(value);
		} catch (NumberFormatException e) {
			logger.logError(loggerCaller, e);
		}
		return booleanValue;
	}

	/**
	 * Gets the env.
	 *
	 * @param value the value
	 * @return the env
	 */
	public static String getEnv(String value) {
		String envVariableVaue = "";
		String sys = System.getenv(value);
		if (sys != null) {
			envVariableVaue = sys;
		}
		return envVariableVaue;
	}

}
