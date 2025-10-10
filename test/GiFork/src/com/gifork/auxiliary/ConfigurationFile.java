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

import java.io.File;
import java.util.HashMap;

import com.gifork.blackboard.GiForkConstants;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.ArgumentManager;
import com.leonardo.infrastructure.JIniFile;
import com.leonardo.infrastructure.log.ILogger;
import com.leonardo.infrastructure.plugins.framework.SimplePluginManager;

/**
 * The Class ConfigurationFile.
 *
 */
public class ConfigurationFile {

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(ConfigurationFile.class);

	/** The Constant cfg. */
	private static final HashMap<String, String> cfg = new HashMap<>();

	/**
	 * m_cdbAddr
	 */
	private static String m_cdbAddr = ConfigurationFile.getProperties("CDB_ADDR", "127.0.0.1");
	/**
	 * m_cdbPort
	 */
	private static int m_cdbPort = ConfigurationFile.getIntegerProperties("CDB_PORT", 54333);

	/**
	 * m_tctAddr
	 */
	private static String m_tctAddr = ConfigurationFile.getProperties("TCT_ADDR", "224.9.1.251");
	/**
	 * m_tctPort
	 */
	private static int m_tctPort = ConfigurationFile.getIntegerProperties("TCT_PORT", 0);

	/**
	 * m_tctAddr
	 */
	private static String m_dbsAddr = ConfigurationFile.getProperties("DBS_ADDR", "224.9.1.251");
	/**
	 * m_tctPort
	 */
	private static int m_dbsPort = ConfigurationFile.getIntegerProperties("DBS_PORT", 0);

	/**
	 * m_snetAddr
	 */
	private static String m_snetAddr = ConfigurationFile.getProperties("SNET_ADDR", "224.9.1.251");
	/**
	 * m_snetPort
	 */
	private static int m_snetPort = ConfigurationFile.getIntegerProperties("SNET_PORT", 0);

	/**
	 * m_scaAddr
	 */
	private static String m_scaAddr = ConfigurationFile.getProperties("SCA_ADDR", "224.9.166.252");
	/**
	 * m_scaPort
	 */
	private static int m_scaPort = ConfigurationFile.getIntegerProperties("SCA_PORT", 0);

	/**
	 * m_scaAddr
	 */
	private static String m_hdiAddr = ConfigurationFile.getProperties("HDI_ADDR", "127.0.0.1");
	/**
	 * m_scaPort
	 */
	private static int m_hdiPort = ConfigurationFile.getIntegerProperties("HDI_PORT", 12573);

	static {
		String pathName = SimplePluginManager.CONFIG_EXTERNAL_FOLDER + "/WPCFG/" + "wpNet.ini";
		File f = new File(pathName);

		if (f.exists()) {
			JIniFile m_configFile = JIniFile.create(pathName);
			m_cdbAddr = m_configFile.get("GI-FORK", "CDB_ADDR").get();
			m_cdbPort = Integer.parseInt(m_configFile.get("GI-FORK", "CDB_PORT").get());

			m_tctAddr = m_configFile.get("GI-FORK", "TCT_ADDR").get();
			m_tctPort = Integer.parseInt(m_configFile.get("GI-FORK", "TCT_PORT").get());

			m_dbsAddr = m_configFile.get("GI-FORK", "DBS_ADDR").get();
			try {
				m_dbsPort = Integer.parseInt(m_configFile.get("GI-FORK", "DBS_PORT").get());
			} catch (Exception e) {
				logger.logWarn("Cannot find the dbs port", e.getMessage());
			}

			m_snetAddr = m_configFile.get("GI-FORK", "SNET_ADDR").get();
			m_snetPort = Integer.parseInt(m_configFile.get("GI-FORK", "SNET_PORT").get());

			m_scaAddr = m_configFile.get("GI-FORK", "SCA_ADDR").get();
			m_scaPort = Integer.parseInt(m_configFile.get("GI-FORK", "SCA_PORT").get());

			m_hdiAddr = m_configFile.get("GI-FORK", "HDI_ADDR").get();
			m_hdiPort = Integer.parseInt(m_configFile.get("GI-FORK", "HDI_PORT").get());
		}

	}

	/**
	 * Load from xml.
	 *
	 * @param xml the xml
	 */
	public static void loadFromXml(String xml) {
		if (xml.contains("</msg>")) {
			IRawData jsonObject = XmlToJsonObject.convertXMLToRawData(xml);
			for (String key : jsonObject.getKeys()) {
				ConfigurationFile.setProperties(key, jsonObject.get(key).toString());
			}
		}
	}

	/**
	 * *.
	 *
	 * @param PropertiesName String
	 * @param defaultValue   String
	 */
	public static void setProperties(String PropertiesName, String defaultValue) {
		cfg.put(PropertiesName, defaultValue);
	}

	/**
	 * Gets the properties.
	 *
	 * @param PropertiesName String
	 * @param defaultValue   the default value
	 * @return String
	 */
	public static String getProperties(String PropertiesName, String defaultValue) {
		String propertiesValue = SafeDataRetriever.getStringData(cfg, PropertiesName.toUpperCase());
		if (propertiesValue.isEmpty()) {
			propertiesValue = defaultValue;
		}
		return propertiesValue;
	}

	/**
	 * Gets the properties.
	 *
	 * @param PropertiesName String
	 * @return String
	 */
	public static String getProperties(String PropertiesName) {
		return getProperties(PropertiesName, "");
	}

	/**
	 * *.
	 *
	 * @param PropertiesName String
	 * @return boolean
	 */
	public static boolean getBoolProperties(String PropertiesName) {
		boolean ret = false;
		String propertiesValue = getProperties(PropertiesName);
		if (propertiesValue.equalsIgnoreCase(GiForkConstants.TRUE)
				|| propertiesValue.equalsIgnoreCase(GiForkConstants.YES) || propertiesValue.equals("1")) {
			ret = true;
		}
		return ret;
	}

	/**
	 * *.
	 *
	 * @param PropertiesName String
	 * @return double
	 */
	public static double getDoubleProperties(String PropertiesName) {
		double ret = 0;
		String propertiesValue = null;

		propertiesValue = getProperties(PropertiesName);

		if (propertiesValue != null && !propertiesValue.isEmpty()) {
			ret = Double.parseDouble(propertiesValue);
		}
		return ret;
	}

	/**
	 * *.
	 *
	 * @param PropertiesName String
	 * @param defaultValue   double
	 * @return double
	 */
	public static double getDoubleProperties(String PropertiesName, double defaultValue) {
		double ret = defaultValue;

		String propertiesValue = getProperties(PropertiesName);

		if (propertiesValue != null && !propertiesValue.isEmpty()) {
			try {
				ret = Double.parseDouble(propertiesValue);
			} catch (NumberFormatException ex) {
				logger.logError("getDoubleProperties", ex);
			}
		}
		return ret;
	}

	/**
	 * *.
	 *
	 * @param PropertiesName String
	 * @param defaultValue   int
	 * @return int
	 */
	public static int getIntegerProperties(String PropertiesName, int defaultValue) {
		String propertiesValue = null;
		int ret = defaultValue;

		propertiesValue = getProperties(PropertiesName);

		if (propertiesValue != null && !propertiesValue.isEmpty()) {
			try {
				ret = Integer.parseInt(propertiesValue);
			} catch (NumberFormatException ex) {
				logger.logError("getIntegerProperties", ex);
			}
		}
		return ret;
	}

	/**
	 * Gets the cdb addr.
	 *
	 * @return the cdb addr
	 */
	public static String getCdbAddr() {
		return m_cdbAddr;
	}

	/**
	 * Gets the cdb port.
	 *
	 * @return the cdb port
	 */
	public static int getCdbPort() {
		return m_cdbPort;
	}

	/**
	 * Gets the tct addr.
	 *
	 * @return the tct addr
	 */
	public static String getTctAddr() {
		return m_tctAddr;
	}

	/**
	 * Gets the tct port.
	 *
	 * @return the tct port
	 */
	public static int getTctPort() {
		return m_tctPort;
	}

	/**
	 * Gets the dbs addr.
	 *
	 * @return the dbs addr
	 */
	public static String getDbsAddr() {
		return m_dbsAddr;
	}

	/**
	 * Gets the dbs port.
	 *
	 * @return the dbs port
	 */
	public static int getDbsPort() {
		return m_dbsPort;
	}

	/**
	 * Gets the snet addr.
	 *
	 * @return the snet addr
	 */
	public static String getSnetAddr() {
		return m_snetAddr;
	}

	/**
	 * Gets the snet port.
	 *
	 * @return the snet port
	 */
	public static int getSnetPort() {
		return m_snetPort;
	}

	/**
	 * Gets the sca addr.
	 *
	 * @return the sca addr
	 */
	public static String getScaAddr() {
		return m_scaAddr;
	}

	/**
	 * Gets the sca port.
	 *
	 * @return the sca port
	 */
	public static int getScaPort() {
		return m_scaPort;
	}

	/**
	 * Gets the hdi addr.
	 *
	 * @return the hdi addr
	 */
	public static String getHdiAddr() {
		return m_hdiAddr;
	}

	/**
	 * Gets the hdi port.
	 *
	 * @return the hdi port
	 */
	public static int getHdiPort() {
		return m_hdiPort;
	}
		
	/**
	 * Checks if Prototyping Test is enabled.
	 *
	 * @return true if Prototyping Test is enabled.
	 */
	public static boolean isPrototypingTest() {
		return getBoolProperties("IS_PROTO_TEST") || ArgumentManager.getBooleanOf("IS_PROTO_TEST");
	}

}
