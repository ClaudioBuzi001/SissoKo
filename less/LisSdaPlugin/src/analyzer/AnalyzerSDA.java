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
package analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.auxiliary.ConfigurationFile;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataArray;
import com.gifork.commons.data.IRawDataElement;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.Strings;
import com.leonardo.infrastructure.log.ILogger;
import com.leonardo.infrastructure.plugins.framework.SimplePluginManager;

import applicationLIS.BlackBoardConstants_LIS;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.BlackBoardUtility_LIS;
import applicationLIS.GiForkMain_LIS;
import applicationLIS.extension.SdaBaseExtension;

/**
 * The Class AnalyzerSDA.
 */
public class AnalyzerSDA implements IObserver {

	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateLogger(AnalyzerSDA.class);

	/** The env to sda data type list. */
	private final ArrayList<String> envToSdaDataTypeList = new ArrayList<>();

	/** The my sector name. */
	private static String mySectorName;

	/** The my resp list. */
	private static String myRespList;

	/** The my lp. */
	private static String myLp = "";

	/** The activate. */
	private boolean activate = false;

	/** The my oe name. */
	private String MY_OE_NAME;

	/** VIS_RADAR_DISS. */
	private static final boolean VIS_RADAR_DISS = ConfigurationFile.getBoolProperties("VIS_RADAR_DISS");

	/** NO_SHOW_LOGIN. */
	private static final boolean NO_SHOW_LOGIN = ConfigurationFile.getBoolProperties("NO_SHOW_LOGIN");

	/**
	 * Register.
	 */
	public void register() {
		envToSdaDataTypeList.add(DataType.ENV_CDBINFO.name());
		envToSdaDataTypeList.add(DataType.ENV_FUNCTIONFILTERNOTIFY.name());
		envToSdaDataTypeList.add(DataType.ENV_SECTOR_TABLE.name());
		envToSdaDataTypeList.add(DataType.ENV_PASSWORD.name());
		envToSdaDataTypeList.add(DataType.ENV_MSP.name());
		envToSdaDataTypeList.add(DataType.ENV_OWN.name());
		envToSdaDataTypeList.add(DataType.ENV_AERODROMEINFO.name());
		envToSdaDataTypeList.add(DataType.ENV_OES.name());
		envToSdaDataTypeList.add(DataType.ENV_SERVER_STATUS.name());
		envToSdaDataTypeList.add(DataType.ENV_RADAR_TABLE.name());
		envToSdaDataTypeList.add(DataType.ENV_PROBE.name());
		envToSdaDataTypeList.add(DataType.ENV_TIME.name());
		envToSdaDataTypeList.add(DataType.ENV_MOC.name());
		envToSdaDataTypeList.add(DataType.ENV_OLDI_LINE.name());
		envToSdaDataTypeList.add(DataType.ENV_HDI_SECTOR_LEVEL.name());
		envToSdaDataTypeList.add(DataType.XAI_HDI_STATE.name());
		envToSdaDataTypeList.add(DataType.BB_MTCD_ITEM_TH_PROB.name());
		envToSdaDataTypeList.add(DataType.BB_THRESHOLD_TCT.name());
		envToSdaDataTypeList.add(DataType.ENV_LDAP.name());
		envToSdaDataTypeList.add(DataType.ENV_LOGIN_DATA.name());
		envToSdaDataTypeList.add(DataType.ENV_AIR_CATEGORY.name());
		envToSdaDataTypeList.add(DataType.ENV_DPS.name());
		envToSdaDataTypeList.add(DataType.BB_STRIP.name());
		envToSdaDataTypeList.add(DataType.STATE_BYP.name());
		envToSdaDataTypeList.add(DataType.ENV_SYSTEM_CENTER.name());
		envToSdaDataTypeList.add(DataType.ENV_RWY_IN_USE.name());
		envToSdaDataTypeList.add(DataType.BB_OPENGIFORKPANEL.name());
		envToSdaDataTypeList.add(DataType.BB_ERROR_CONNECTION.name());

		envToSdaDataTypeList.add(DataType.ENV_GDB_LOAD.name());

		for (final String dataType : envToSdaDataTypeList) {
			StorageManager.register(this, dataType);
		}
	}

	/**
	 * Update.
	 *
	 * @param jsonData the json data
	 */
	@Override
	public void update(final IRawData jsonData) {

		switch (DataType.valueOf(jsonData.getType())) {
		case ENV_PROBE:
			DataRoot rootData = DataRoot.createMsg();
			var serviceNode = rootData.addHeaderOfService("CONFIGURATION");
			serviceNode.addLine("PROBE_GROUP", jsonData.getSafeString("PROBE"));

			ManagerBlackboard.addJVOutputList(rootData);
			break;
		case ENV_GDB_LOAD:
			rootData = DataRoot.createMsg();
			serviceNode = rootData.addHeaderOfService("RELOAD_GDB_XML");
			serviceNode.addLine("STATE", "");
			ManagerBlackboard.addJVOutputList(rootData);
			break;
		case BB_MTCD_ITEM_TH_PROB:
			rootData = DataRoot.createMsg();
			serviceNode = rootData.addHeaderOfService("FILTER_MTCD_PROBABILITY");
			serviceNode.addLine("THRESHOLD", jsonData.getSafeString("THRESHOLD"));
			ManagerBlackboard.addJVOutputList(rootData);
			break;

		case BB_THRESHOLD_TCT:
			rootData = DataRoot.createMsg();
			serviceNode = rootData.addHeaderOfService("FILTER_TCT_THRESHOLD");
			serviceNode.addLine("TCT_DISTANCE", jsonData.getSafeString("DISTANCE"));
			serviceNode.addLine("TCT_TIME", jsonData.getSafeString("TIME_VIS"));
			ManagerBlackboard.addJVOutputList(rootData);
			break;
		case BB_OPENGIFORKPANEL:
			setGiForkPanelVisible();
			break;
		case BB_ERROR_CONNECTION:
			rootData = DataRoot.createMsg();
			serviceNode = rootData.addHeaderOfService("SHUTDOWN");
			String hostName = jsonData.getSafeString("HOST", "");
			serviceNode.addLine("TEXT", "ERROR CONNECTION " + hostName + ", RESTART APPLICATION?");
			ManagerBlackboard.addJVOutputList(rootData);
			break;
		case XAI_HDI_STATE:
			if (!NO_SHOW_LOGIN) {
				if (jsonData.getSafeString("XAI_HDI_STATE").equals("2")) {

					if (activate) {
						return;
					}
					activate = true;

					if (!BlackBoardUtility.getDataOpt(DataType.BB_SDA.name()).map(r -> r.getSafeBoolean("FIRSTLOGIN"))
							.orElse(false)) {
						BlackBoardUtility.createData(DataType.BB_SDA.name(), IRawData.NO_ID, "FIRSTLOGIN", "true");

						rootData = DataRoot.createMsg();
						serviceNode = rootData.addHeaderOfService("EXTERNAL_ORDER");
						serviceNode.addLine("GRAPHIC_ORDER", "PREVIEW");
						serviceNode.addLine("HOST_NAME", "BCV");
						serviceNode.addLine("ORDER_ID", "LGN");
						serviceNode.addLine("PREVIEW_NAME", "previewLGN");
						serviceNode.addLine("OBJECT_TYPE", "LGN");

						ManagerBlackboard.addJVOutputList(rootData);
					}
				}
			}
			break;
		case ENV_AERODROMEINFO:
			processENV_QNHINFO(jsonData);
			break;

		case ENV_RWY_IN_USE:
			processENV_RWY_INUSE(jsonData);
			break;

		case ENV_DPS:
			processENV_DSP(jsonData);
			break;

		case ENV_TIME:
			processENV_TIME(jsonData);
			break;
		case BB_STRIP:
			processStrip(jsonData);
			break;
		case ENV_MOC:
			processMoc(jsonData);
			break;
		case STATE_BYP:
			processBypState(jsonData);
			break;
		case ENV_SYSTEM_CENTER:
			processSystemCenter(jsonData);
			break;
		default:
			processAllEnvForSDA();
			break;
		}

	}

	/**
	 * Sets the gi fork panel visible.
	 */
	private static void setGiForkPanelVisible() {
		GiForkMain_LIS.setVisibility();
	}

	/**
	 * @param jsonData
	 */
	private static void processENV_RWY_INUSE(IRawData jsonData) {

		final DataRoot rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService("RWY_IN_USE");
		// TODO gestire solo il primo che arriva

		String rwyARRName = "";
		String rwyDEPName = "";
		String airNAME = "";

		IRawDataArray jsonAirportList = jsonData.getSafeRawDataArray("airpList");

		if (jsonAirportList != null && !jsonAirportList.isEmpty()) {
			for (IRawDataElement jsonAirpElement : jsonAirportList) {
				airNAME = jsonAirpElement.getSafeString("airpName");
				rwyARRName = jsonAirpElement.getSafeString("rwy_da");
				rwyDEPName = jsonAirpElement.getSafeString("rwy_dd");
				break;
			}
		}

		serviceNode.addLine("RWY_ARR", rwyARRName);
		serviceNode.addLine("RWY_DEP", rwyDEPName);
		serviceNode.addLine("AIRP_NAME", airNAME);

		ManagerBlackboard.addJVOutputList(rootData);

	}

	/**
	 * processBypState.
	 *
	 * @param json the json
	 */
	private static void processBypState(IRawData json) {

		final DataRoot rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService("BYPASS_STATE");
		int valueState = json.getSafeInt("BYP_STATE", -1);
		String radarName = json.getSafeString("RADAR_NAME");
		String radarSelect = json.getSafeString("RD_SEL");

		serviceNode.addLine("BYP_STATE", String.valueOf(valueState));
		serviceNode.addLine("RADAR_SELECTED_NAME", radarName);
		serviceNode.addLine("RADAR_INDEX", radarSelect);
		ManagerBlackboard.addJVOutputList(rootData);
	}

	/**
	 * Process system Center.
	 *
	 * @param json the json
	 */
	private static void processSystemCenter(IRawData json) {

		final DataRoot rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService("SYSTEM_CENTER");
		String latSystemCenter = json.getSafeString("SYS_CENTER_LAT");
		String lonSystemCenter = json.getSafeString("SYS_CENTER_LON");
		String radius = json.getSafeString("CONFORMANCE_RADIUS");

		serviceNode.addLine("LAT", latSystemCenter);
		serviceNode.addLine("LON", lonSystemCenter);
		serviceNode.addLine("EARTH_RADIUS_NM", radius);
		ManagerBlackboard.addJVOutputList(rootData);
	}

	/**
	 * processMoc.
	 *
	 * @param json the json data
	 */
	private void processMoc(IRawData json) {

		final DataRoot rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService("MOC_STATUS");
		final int valueMoc = json.getSafeInt("MOC_SFN");

		final StringJoiner survJoiner = new StringJoiner(",");
		String mocValue = "1";

		if (valueMoc == 0) {
			survJoiner.add("DARD" + ":" + "DEGRADED");
			mocValue = "0";
		} else if (valueMoc == 1) {
			survJoiner.add("RFB" + ":" + "FULL");
		} else if (valueMoc == 2) {
			survJoiner.add("RDP" + ":" + "FULL");
		} else if (valueMoc == 3) {
			survJoiner.add("MRT" + ":" + "FULL");
		} else if (valueMoc == 4) {
			survJoiner.add("BYPASS" + ":" + "FULL");
		} else if (valueMoc == 5) {
			survJoiner.add("SDARD" + ":" + "STOPPED");
		} else if (valueMoc == 6) {
			survJoiner.add("RDP2" + ":" + "FULL");
		}

		serviceNode.addLine("SURVEILLANCE_STATUS", survJoiner.toString());
		serviceNode.addLine("BYP_ENABLE", mocValue);

		ManagerBlackboard.addJVOutputList(rootData);

		
		
		
		
		final var jsonRadarTable = BlackBoardUtility.getDataOpt(DataType.ENV_RADAR_TABLE.name());
		if (jsonRadarTable.isPresent()) {
			processAllEnvForSDA();
		}
		
		
	}

	/**
	 * @param jsonData
	 */
	private static void processStrip(final IRawData jsonData) {
		final DataRoot rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService("PRINT_STRIP");
		serviceNode.addLine("FLIGHT_NUM", jsonData.getSafeString("FLIGHT_NUM"));
		serviceNode.addLine("LAYOUT", jsonData.getSafeString("LAYOUT"));
		ManagerBlackboard.addJVOutputList(rootData);

	}

	/**
	 * Send preview login.
	 *
	 * @param json the json
	 */
	private static void sendPreviewLogin(final IRawData json) {

		if (!NO_SHOW_LOGIN) {
			if (!BlackBoardUtility.getDataOpt(DataType.XAI_HDI_STATE.name()).map(r -> r.getSafeString("XAI_HDI_STATE"))
					.orElse("").equals("2")) {
				return;
			}

			if (json.getSafeInt("LDAP_LOG_STATUS") != 0) {
				BlackBoardUtility.updateData(DataType.BB_SDA.name(), IRawData.NO_ID, "LOGINREQUIRED", "false");
				return;
			}

			if (BlackBoardUtility.getDataOpt(DataType.BB_SDA.name()).map(r -> r.getSafeBoolean("LOGINREQUIRED"))
					.orElse(false)) {
				return;
			}

			BlackBoardUtility.updateData(DataType.BB_SDA.name(), IRawData.NO_ID, "LOGINREQUIRED", "true");

			final DataRoot rootData = DataRoot.createMsg();
			final var serviceNode = rootData.addHeaderOfService("EXTERNAL_ORDER");
			serviceNode.addLine("GRAPHIC_ORDER", "PREVIEW");
			serviceNode.addLine("HOST_NAME", "BCV");
			serviceNode.addLine("ORDER_ID", "LGN");
			serviceNode.addLine("PREVIEW_NAME", "previewLGN");
			serviceNode.addLine("OBJECT_TYPE", "LGN");
			serviceNode.addLine("DATA", "{'FORCEDORDER':'true'}");
			ManagerBlackboard.addJVOutputList(rootData);
		}
	}

	/**
	 * Process all env for SDA.
	 */
	private void processAllEnvForSDA() {
		final String loggerCaller = "processAllEnvForSDA()";
		final DataRoot rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService("SYSTEM_STATUS");

		for (final String dataType : envToSdaDataTypeList) {
			final var rawdata = BlackBoardUtility.getDataOpt(dataType);
			rawdata.ifPresent(js -> rawdataToXml(js, serviceNode));
		}

		SimplePluginManager.getInstance().getExtensions(SdaBaseExtension.class).forEach(ex -> ex.update(serviceNode));

		ManagerBlackboard.addJVOutputList(rootData);
		LOGGER.logDebug(loggerCaller, rootData.toXml());
	}

	/**
	 * Rawdata to xml.
	 *
	 * @param json        the json
	 * @param serviceNode the service node
	 */
	private void rawdataToXml(final IRawData json, final HeaderNode serviceNode) {

		switch (DataType.valueOf(json.getType())) {

		case ENV_MSP:
			serviceNode.addLine("MSP_TYPE", json.getSafeString("MSP_TYPE"));
			if (!json.getSafeString("MSP_TYPE").isEmpty()) {
				serviceNode.addLine("MSP", "1");
			} else {
				serviceNode.addLine("MSP", "0");
			}
			serviceNode.addLine("MSP_MY_SECTOR", json.getSafeString("MSP_MY_SECTOR"));
			break;
		case ENV_OWN:
			final String myRESP_LIST = json.getSafeString("MY_RESPLIST");
			String myRESP_LIST_NEW = "";
			final String mySector_Own = json.getSafeString("SUITE");
			final String mySector_OwnP = json.getSafeString("OWNP");
			String stateInfoWp = "SBY";
			if (!isStandby(mySector_Own)) {
				if (myRESP_LIST.contains(mySector_Own)) {
					myRESP_LIST_NEW = myRESP_LIST;
				}
				stateInfoWp = "OPS";
			}

			String rolePlanner = "false";
			final String myRoles = json.getSafeString("MY_ROLES");

			if (myRoles.contains("PLANNER") || myRoles.contains("MSP")) {
				rolePlanner = "true";
			}

			serviceNode.addLine("ROLE_PLN", rolePlanner);
			serviceNode.addLine("MY_ROLES", json.getSafeString("MY_ROLES"));
			serviceNode.addLine("MY_RESPLIST", myRESP_LIST_NEW);
			serviceNode.addLine("MY_LP", json.getSafeString("MY_LP"));
			serviceNode.addLine("MY_HOSTNAME", json.getSafeString("MY_HOSTNAME"));
			serviceNode.addLine("STATE_INFO_WP", stateInfoWp);
			serviceNode.addLine("PLB_MODE", json.getSafeString("PLB_MODE"));
			serviceNode.addLine("CPDLC_ENABLED", json.getSafeString("CPDLC_ENABLED"));
			serviceNode.addLine("DEF_COM_RESP", json.getSafeString("DEFAULT_COM_RESP"));
			serviceNode.addLine("OWNINFO_EXER_SIM", json.getSafeString("EXER_SIM"));
			final var data = BlackBoardUtility.getDataOpt(DataType.ENV_LDAP.name());
			String logstatus = "0";
			if (data.isPresent()) {
				logstatus = data.get().getSafeString("LDAP_LOG_STATUS", "0");
			}
			serviceNode.addLine("LOG_STATUS", logstatus);

			final String family = json.getSafeString("FAMILY");
			String familySDA;
			switch (family) {
			case "0":
				familySDA = "TWR";
				break;
			case "1":
				familySDA = "APP";
				break;
			case "2":
				familySDA = "ACC";
				break;
			case "3":
				familySDA = "TWRMIL";
				break;
			case "4":
				familySDA = "APPMIL";
				break;
			case "5":
				familySDA = "ACCMIL";
				break;
			case "6":
				familySDA = "FIS";
				break;
			case "7":
				familySDA = "NIMA";
				break;
			case "9":
				familySDA = "AOI";
				break;
			case "10":
				familySDA = "AOIJOIN";
				break;
			case "11":
				familySDA = "CDC";
				break;
			case "12":
				familySDA = "APR";
				break;
			case "13":
				familySDA = "GND";
				break;
			default:
				familySDA = "---";
				break;
			}

			serviceNode.addLine("FAMILY", familySDA);
			serviceNode.addLine("MY_SUITE", mySector_Own);

			// Main frequency per la Suite (logical sector) corrente
			String mainFreq = "";
			Optional<IRawData> atsuListOpt = BlackBoardUtility
					.getDataOpt(BlackBoardConstants_LIS.DataType.ENV_MYRESPONSIBILITIES.name(), IRawData.NO_ID);

			if (atsuListOpt.isPresent()) {
				IRawDataArray atsuList = atsuListOpt.get().getSafeRawDataArray("ATSULIST");
				var myAtsu = atsuList.stream().filter(atsu -> {
					String sectorId = atsu.getSafeString("SECTOR_ID");
					return !sectorId.isEmpty() && sectorId.equals(mySector_OwnP);
				}).findFirst();
				if (myAtsu.isPresent()) {
					mainFreq = myAtsu.get().getSafeString("FREQ833");
				}
			}
			serviceNode.addLine("MAIN_FREQ", mainFreq);

			// Main frequencies per le suites (logical sector) assorbite dalla suite corrente
			String mainFreqList = "";
			List<String> freqList = new ArrayList<>();
			if (atsuListOpt.isPresent()) {
				Optional<IRawData> sectorTable = BlackBoardUtility
						.getDataOpt(BlackBoardConstants_LIS.DataType.ENV_SECTOR_TABLE.name(), IRawData.NO_ID);
				if (sectorTable.isPresent()) {
					var sectorsList = sectorTable.get().getSafeRawDataArray("SECTOR_TABLE");
					var myRespList = new ArrayList<>(Arrays.asList(myRESP_LIST_NEW.split(",")));
					var atsuList = atsuListOpt.get().getSafeRawDataArray("ATSULIST");
					for (var s : myRespList) {
						//String freq = atsu.getSafeString("FREQ833");
						String secId = sectorsList.stream().filter(sector -> {
							String secName = sector.getSafeString("SECTOR_NAME");
							return !secName.isEmpty() && secName.equals(s);
						}).map(sector -> sector.getSafeString("SECTOR_ID"))
								.findFirst().orElse("");
						String freq = atsuList.stream().filter(atsu -> {
							String sectorId = atsu.getSafeString("SECTOR_ID");
							return !sectorId.isEmpty() && sectorId.equals(secId);
						}).map(atsu -> atsu.getSafeString("FREQ833")).findFirst().orElse("");
						if (!freq.isEmpty() && !freqList.contains(freq) &&
								!freq.equals(mainFreq)) {
							freqList.add(freq);
						}
					}

				}
			}
			Collections.sort(freqList);
			if (mainFreq != null && !mainFreq.isBlank()) {
				freqList.add(0, mainFreq);
			}
			mainFreqList = String.join(",", freqList);
			serviceNode.addLine("MAIN_FREQ_LIST", mainFreqList);

			final String worldEnvironment = json.getSafeString("OWN_WORLD");
			String worldEnvironmenSDA;

			String buttonPlayBackEnable;

			switch (worldEnvironment) {
			case "2":
				worldEnvironmenSDA = "ONL";
//				buttonPlayBack = "PASSIVE";
				buttonPlayBackEnable = "true";
				break;
			case "3":
				worldEnvironmenSDA = "TRN";
//				buttonPlayBack = "PASSIVE";
				buttonPlayBackEnable = "true";
				break;
			case "4":
				worldEnvironmenSDA = "PLB";
//				buttonPlayBack = "PASSIVE";
				buttonPlayBackEnable = "false";
				break;
			case "5":
				worldEnvironmenSDA = "EX" + json.getSafeString("EXER_SIM");
//				buttonPlayBack = "PASSIVE";
				buttonPlayBackEnable = "true";
				break;
			case "6":
				worldEnvironmenSDA = "PEX";
//				buttonPlayBack = "PASSIVE";
				buttonPlayBackEnable = "true";
				break;
			default:
				worldEnvironmenSDA = "---";
//				buttonPlayBack = "---";
				buttonPlayBackEnable = "true";
				break;
			}

			serviceNode.addLine("OWN_WORLD", worldEnvironmenSDA);
			// serviceNode.addLine("BUTTON_PLAY", buttonPlayBack);
			serviceNode.addLine("BUTTON_PLAY_DISABLE", buttonPlayBackEnable);

			
			if (json.getSafeBoolean("MONITORING"))
			{
				serviceNode.addLine("SUPERVISOR", "MONITORING");
			}
			
			updateLevelMinMaxSector(serviceNode);

			break;
		case ENV_AIR_CATEGORY:
			serviceNode.addLine("AIRP_CAT", json.getSafeString("AIRP_CAT"));
			break;
		case ENV_OES:
			processENV_OES(json, serviceNode);
			break;
		case ENV_SERVER_STATUS:
			processENV_SERVER_STATUS(json, serviceNode);
			break;
		case ENV_RADAR_TABLE:
			processENV_RADAR_TABLE(json, serviceNode);
			break;
		case ENV_PROBE:
			processENV_PROBE(json, serviceNode);
			break;
		case ENV_PASSWORD:
			processENV_PASSWORD(json, serviceNode);
			break;
		case ENV_OLDI_LINE:
			processENV_OLDI_LINE(json, serviceNode);
			processENV_OLDI_LINE_STATUS(json, serviceNode);
			break;
		case ENV_FUNCTIONFILTERNOTIFY:
			processENV_FUNCTION_FILTER(json, serviceNode);
			break;
		case ENV_CDBINFO:
			processENV_CDBINFO(json, serviceNode);
			break;
		case ENV_SECTOR_TABLE:
			processENV_SECTOR_TABLE(json, serviceNode);
			break;
		case ENV_LDAP:

			if (!json.isNull("isPresent")) {
				return;
			}

			final String accessTime = json.getSafeString("LDAP_ACCESS_TIME");
			String loginTime = "";
			String user = "";
			final String logStatus = json.getSafeString("LDAP_LOG_STATUS");

			if (accessTime != null && !accessTime.isEmpty()) {
				loginTime = accessTime.substring(11);
				user = json.getSafeString("LDAP_USERNAME");
			}
			serviceNode.addLine("USER", user);
			serviceNode.addLine("ACCESS_TIME", loginTime);
			serviceNode.addLine("LOG_STATUS", logStatus);

			final String exitTime = json.getSafeString("LDAP_EXIT_TIME");
			String logoutTime = "";
			if (exitTime != null && !exitTime.isEmpty()) {
				logoutTime = exitTime.substring(11);
			}

			serviceNode.addLine("EXIT_TIME", "LOGOUT   " + logoutTime);
			serviceNode.addLine("GROUP", "GROUP    " + json.getSafeString("LDAP_GROUP_BELONGING"));

			serviceNode.addLine("NAME", "NAME     " + json.getSafeString("LDAP_NAME"));
			serviceNode.addLine("SURNAME", "SURNAME  " + json.getSafeString("LDAP_NOTIFY_SURNAME"));

			sendPreviewLogin(json);
			break;
		case ENV_HDI_SECTOR_LEVEL:
			updateLevelMinMaxSector(serviceNode);
			break;

		default:
			break;
		}
	}

	/**
	 * Process processENV_TIME
	 *
	 *
	 * @param json the json
	 */

	private static void processENV_TIME(final IRawData json) {
		final DataRoot rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService("TIME_INFO_STATUS");
		serviceNode.addLine("SYSTEM_TIME", json.getSafeString("TIME"));
		serviceNode.addLine("SYSTEM_DATE", json.getSafeString("DATE"));
		ManagerBlackboard.addJVOutputList(rootData);
	}

	/**
	 * Process EN V CDBINFO.
	 *
	 * @param json        the json
	 * @param serviceNode the service node
	 */
	private static void processENV_CDBINFO(final IRawData json, final HeaderNode serviceNode) {

		final String[] SPEED_FACTOR = { "1", "2", "4", "8" };
		serviceNode.addLine("CDBINFO_SPEED_SIM", SPEED_FACTOR[json.getSafeInt("CDBINFO_SPEED")]);

		serviceNode.addLine("CDBINFO_MODALITY", json.getSafeString("CDBINFO_MODALITY"));

		serviceNode.addLine("CDBINFO_FREEZE_STATUS", json.getSafeString("CDBINFO_FREEZE_STATUS"));
	}

	/**
	 * Process EN V PASSWORD.
	 *
	 * @param json        the json
	 * @param serviceNode the service node
	 */

	private static void processENV_PASSWORD(final IRawData json, final HeaderNode serviceNode) {
		final var env_OWN = BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name());
		if (env_OWN.isPresent() && env_OWN.get().getSafeBoolean("MONITORING")) {
			serviceNode.addLine("SUPERVISOR", "MONITORING");
		}else {
			
			final boolean PASSWORD_SPV_ENABLE = json.getSafeBoolean("CIVIL_SPV_ENABLE");
			
			if (PASSWORD_SPV_ENABLE) {
				serviceNode.addLine("SUPERVISOR", "SPV");
			} else {
				serviceNode.addLine("SUPERVISOR", "NO SPV");
			}
		}

	}

	/**
	 * Process EN V PROBE.
	 *
	 * @param json        the json
	 * @param serviceNode the service node
	 */
	private static void processENV_PROBE(final IRawData json, final HeaderNode serviceNode) {
		serviceNode.addLine("PROBE_GROUP", json.getSafeString("PROBE"));
	}

	/**
	 * Process EN V RADA R TABLE.
	 *
	 * @param json        the json
	 * @param serviceNode the service node
	 */
	private static void processENV_RADAR_TABLE(final IRawData json, final HeaderNode serviceNode) {
		final String loggerCaller = "processENV_RADAR_TABLE()";

		final StringJoiner radarStatusList = new StringJoiner(",");
		Optional<IRawData> itemMoc = BlackBoardUtility.getDataOpt(DataType.ENV_MOC.name());
		int valueMoc = -1;
		if (itemMoc.isPresent()) {
			valueMoc = itemMoc.get().getSafeInt("MOC_SFN", -1);
		}
		try {
			if (json != null) {

				String status;
				final var radarListArray = json.getSafeRawDataArray("RADAR_LIST");
				String valueSele = json.getSafeString("RADAR_SELECTED");
				String radarNameSel = "RAD";
				for (final IRawDataElement radarArrayElement : radarListArray) {
					final String IS_STATUS_ON = radarArrayElement.getSafeString("RADAR_STATUS");
					if (IS_STATUS_ON.equals("true")) {
						status = "ON";
					} else {
						status = "OFF";
					}

					final String radarName = radarArrayElement.getSafeString("RADAR_NAME");
					String radarDiss = radarArrayElement.getSafeString("RPU").trim();
					if (valueSele.equals(radarArrayElement.getSafeString("RADAR_ID"))) {
						radarNameSel = radarName;
					}
					StringBuilder radarfullName = new StringBuilder();

					if (VIS_RADAR_DISS && valueMoc == 0) {
						radarfullName.append(radarName).append("[").append(radarDiss).append("]").append(":")
								.append(status);
					} else {
						radarfullName.append(radarName).append(":").append(status);
					}
					radarStatusList.add(radarfullName.toString());

				}
				serviceNode.addLine("RADAR_SELECTED_NAME", radarNameSel);
			} else {
				LOGGER.logDebug(loggerCaller, "updateSensorsRadarStatus - json is null!");
			}
		} catch (final Exception exc) {
			LOGGER.logError(loggerCaller, Strings.concat("ENV - EXC in updateSensorsRadarStatus: ", exc.toString()));
		}
		serviceNode.addLine("RADAR_STATUS", radarStatusList.toString());

	}

	/**
	 * Process EN V SERVE R STATUS.
	 *
	 * @param json        the json
	 * @param serviceNode the service node
	 */
	private static void processENV_SERVER_STATUS(final IRawData json, final HeaderNode serviceNode) {
		final StringJoiner servJoiner = new StringJoiner(",");

		final var array = json.getSafeRawDataArray("CP12_EQPMT_LIST");

		for (final IRawDataElement elem : array) {
			final String key = elem.getSafeString("CP12_NAME");
			final String value = elem.getSafeString("CP12_STATUS");
			if (!key.isEmpty() && !value.isEmpty()) {
				servJoiner.add(Strings.concat(key, ":", value));
			}
		}
		serviceNode.addLine("SERVER_STATUS", servJoiner.toString());
	}

	/**
	 * Process EN V QNHINFO.
	 *
	 * @param jsonAero the json aero
	 */
	private static void processENV_QNHINFO(final IRawData jsonAero) {

		final var jsonArray = jsonAero.getSafeRawDataArray("aerodromeList");
		for (final IRawDataElement elem : jsonArray) {
			if (elem.getSafeBoolean("ISMAIN")) {
				final String ret_callback_accept_reject = "Menu";

				final String qnh_type = elem.getSafeString("AERODROME_NAME");
				final String trans_level = elem.getSafeString("TRANSITION_LEVEL");
				final String qnh = Strings.concat(elem.getSafeString("QNH_INT"), ".", elem.getSafeString("QNH_DEC"));

				final String temperature = elem.getSafeString("TEMPERATURE");

				String qnh_VIS;
				String qnh_SYMBOL = " ";
				String qnh_VIS_COLOR = "rgba(0, 255, 0, 1.0)";
				String qnh_SYMBOL_COLOR = "rgba(0, 255, 0, 1.0)";
				String temp_COLOR = "rgba(0, 255, 0, 1.0)";

				String qnh_Callback = "Default";

				final String qnh_proposed = Strings.concat(elem.getSafeString("QNH_INT_PROPOSED"), ".",
						elem.getSafeString("QNH_DEC_PROPOSED"));
				final int qnh_state = elem.getSafeInt("QNH_STATE", 0);

				final String QNH_SYMBOL = elem.getSafeString("QNH_SYMBOL");
				qnh_VIS = qnh;

				if (qnh_state == 16) {

					qnh_VIS_COLOR = "orange";
				}

				if (qnh_state == 8 || qnh_state == 13 || qnh_state == 3) {

					qnh_VIS_COLOR = "yellow";
					qnh_VIS = qnh_proposed;
					qnh_Callback = ret_callback_accept_reject;
				}

				if (qnh_state == 2 || qnh_state == 7 || qnh_state == 12 || qnh_state == 9 || qnh_state == 14) {
					qnh_VIS_COLOR = "red";
				}

				if (QNH_SYMBOL.equals("PROPOSED_GEN")) {

					qnh_SYMBOL = "*";
					qnh_SYMBOL_COLOR = "yellow";
				} else if (QNH_SYMBOL.equals("REFRESH_GEN")) {

					qnh_SYMBOL = "*";
					qnh_SYMBOL_COLOR = "orange";
				}

				final DataRoot rootData = DataRoot.createMsg();

				final var serviceNode = rootData.addHeaderOfService("QNH_INFO");

				serviceNode.addLine("TL_DEF", trans_level);
				serviceNode.addLine("QNH_TYPE", qnh_type);
				serviceNode.addLine("QNH", qnh_VIS);
				serviceNode.addLine("QNH_COLOR", qnh_VIS_COLOR);
				serviceNode.addLine("QNH_SYMBOL", qnh_SYMBOL);
				serviceNode.addLine("QNH_SYMBOL_COLOR", qnh_SYMBOL_COLOR);
				serviceNode.addLine("QNH_CALLBACK", qnh_Callback);
				serviceNode.addLine("TEMPERATURE", temperature);
				serviceNode.addLine("TEMP_COLOR", temp_COLOR);

				ManagerBlackboard.addJVOutputList(rootData);
			}
		}

	}

	/**
	 * Process EN V OES.
	 *
	 * @param json        the json
	 * @param serviceNode the service node
	 */
	private void processENV_OES(final IRawData json, final HeaderNode serviceNode) {
		final String loggerCaller = "processENV_OES()";
		setMyLp();
		final StringJoiner joiner = new StringJoiner(",");
		MY_OE_NAME = "";

		if (!myLp.isEmpty()) {
			json.getKeys().forEach(key -> {
				if (json.isArray(key)) {
					final var array = json.getSafeRawDataArray(key);
					for (final IRawDataElement elem : array) {
						if (elem.has("LP") && elem.has("OE")) {
							final String lp = elem.getSafeString("LP");
							final String oe = elem.getSafeString("OE");
							if (!lp.equals(myLp) && oe != null) {
								joiner.add(oe);
							} else if (myLp.equals(lp)) {
								json.put("MY_OE_NAME", oe);
								MY_OE_NAME = oe;
							}
						} else {
							LOGGER.logDebug(loggerCaller, "WARNING :: LP o OE NON SONO PRESENTI");
						}
					}
				}
			});
			json.put("OTHER_OEs", joiner.toString());
		}

		serviceNode.addLine("OTHER_OEs", joiner.toString());
		serviceNode.addLine("MY_OE_NAME", MY_OE_NAME);
	}

	/**
	 * Sets the my lp.
	 */
	private static void setMyLp() {
		myLp = "";
		final var data = BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name());
		data.ifPresent(js -> myLp = js.getSafeString("MY_LP"));
	}

	/**
	 * Process EN V OLD I LINE.
	 *
	 * @param json        the json
	 * @param serviceNode the service node
	 */
	private static void processENV_OLDI_LINE(final IRawData json, final HeaderNode serviceNode) {
		final String loggerCaller = "processENV_OLDI_LINE()";
		final StringJoiner lineStatusList = new StringJoiner(",");

		try {
			String status;
			final var jsonArray = json.getSafeRawDataArray("LINE_ARRAY");
			for (final IRawDataElement elem : jsonArray) {
				final boolean IS_STATUS_ON = elem.getSafeBoolean("LINE_STATUS");
				if (IS_STATUS_ON) {
					status = "ON";
				} else {
					status = "OFF";
				}

				final String lineName = elem.getSafeString("LINE_NAME");
				lineStatusList.add(Strings.concat(lineName.trim(), ":", status.trim()));
			}
		} catch (final Exception exc) {
			LOGGER.logError(loggerCaller, "ENV - EXC in processENV_OLDI_LINE: ");
		}
		serviceNode.addLine("LINE_STATUS", lineStatusList.toString());

	}

	/**
	 * Process processENV_OLDI_LINE_STATUS.
	 *
	 * @param json        the json
	 * @param serviceNode the service node
	 */
	private static void processENV_OLDI_LINE_STATUS(final IRawData json, final HeaderNode serviceNode) {
		final String loggerCaller = "processENV_OLDI_LINE_STATUS()";
		String style = "STOPPED";

		try {
			final var jsonArray = json.getSafeRawDataArray("LINE_ARRAY");
			final IRawDataArray arrayElementAFTN = RawDataFactory.createArray();

			for (final IRawDataElement elem : jsonArray) {
				if (elem.getSafeString("LINE_TYPE").equals("AFTN")) {
					IRawDataElement jEl = RawDataFactory.createElem();
					jEl.put("LINE_STATUS", elem.getSafeInt("LINE_STATUS"));
					jEl.put("LINE_TYPE", elem.getSafeString("LINE_TYPE"));
					arrayElementAFTN.put(jEl);
				}
			}

			if (!arrayElementAFTN.isEmpty()) {
				if (arrayElementAFTN.stream().allMatch(el -> el.getSafeInt("LINE_STATUS") == 1)) {
					style = "FULL";
				} else if (arrayElementAFTN.stream().allMatch(el -> el.getSafeInt("LINE_STATUS") == 0)) {
					style = "STOPPED";
				} else {
					style = "FULL";
				}
			}
		} catch (final Exception exc) {
			LOGGER.logError(loggerCaller, "ENV - EXC in processENV_OLDI_LINE_STATUS: ");
		}
		serviceNode.addLine("LINE_STATUS_AFTN", style);
	}

	/**
	 * Process EN V FUNCTIO N FILTER.
	 *
	 * @param json        the json
	 * @param serviceNode the service node
	 */
	private static void processENV_FUNCTION_FILTER(final IRawData json, final HeaderNode serviceNode) {
		final String loggerCaller = "processENV_FUNCTION_FILTER()";
		final StringJoiner filterFunList = new StringJoiner(",");
		try {
			if (json != null) {
				final var filterListArray = json.getSafeRawDataArray("FUNCTIONFILTER_NOTIFY_LIST");
				for (final IRawDataElement filterArrayElement : filterListArray) {
					String funName = filterArrayElement.getSafeString("FUNCTIONFILTER_NOTIFY_FUNCTIONNAME");
					final boolean funGlobal = filterArrayElement
							.getSafeBoolean("FUNCTIONFILTER_FUNCTIONGLOBAL_DISABLED", false);
					final boolean funSuite = filterArrayElement.getSafeBoolean("FUNCTIONFILTER_FUNCTIONSUITE_DISABLED",
							false);
					final boolean funLocal = filterArrayElement.getSafeBoolean("FUNCTIONFILTER_FUNCTIONLOCAL_DISABLED",
							false)
							|| filterArrayElement.getSafeBoolean("FUNCTIONFILTER_FUNCTIONLOCALSUP_DISABLED", false);

					final boolean buzzAuralGlobal = filterArrayElement
							.getSafeBoolean("FUNCTIONFILTER_BUZZGLOBAL_DISABLED", false);

					String statusColor = "Green";
					String statusUnderlined = "OFF";

					if (funGlobal) {
						statusColor = "Red";
					} else if (funSuite) {
						statusColor = "Orange";
					} else if (funLocal) {
						statusColor = "Yellow";
					} else {
						funName = "    ";
					}

					if (buzzAuralGlobal) {
						statusUnderlined = "ON";
					}

					if (funName.trim().isEmpty() && statusColor.equals("Green") && statusUnderlined.equals("ON")) {
						funName = filterArrayElement.getSafeString("FUNCTIONFILTER_NOTIFY_FUNCTIONNAME");
					}

					if (funName != null && !funName.isEmpty()) {
						filterFunList.add(
								Strings.concat(funName.trim(), ":", statusColor.trim(), ":", statusUnderlined.trim()));
					}
				}
			}
		} catch (final Exception exc) {
			LOGGER.logError(loggerCaller, "ENV - EXC in processENV_FUNCTION_FILTER: " + exc);
		}
		serviceNode.addLine("APPLICATION_STATUS", filterFunList.toString());

	}

	/**
	 * Update level min max sector.
	 *
	 * @param serviceNode the service node
	 */
	private static void updateLevelMinMaxSector(final HeaderNode serviceNode) {

		final ArrayList<Integer> listSectMin = new ArrayList<>();
		final ArrayList<Integer> listSectMax = new ArrayList<>();
		int my_lev_min = 0;
		int my_lev_max = 0;

		mySectorName = "";
		myRespList = "";

		try {

			final var jsonOW = BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name());

			jsonOW.ifPresent(jsonOWN -> {
				myRespList = jsonOWN.getSafeString("MY_RESPLIST");
				mySectorName = jsonOWN.getSafeString("SUITE");
				List<String> elemList = Arrays.asList(myRespList.split(","));
				final var jsonSct = BlackBoardUtility.getDataOpt(DataType.ENV_HDI_SECTOR_LEVEL.name());
				jsonSct.ifPresent(jsonSect -> {
					final var jsonArray = jsonSect.getSafeRawDataArray("SECTOR_ARRAY_LIST");
					for (final IRawDataElement jsonElement : jsonArray) {
						final String sectName = jsonElement.getSafeString("SECTOR_NAME");
						final int levMin = jsonElement.getSafeInt("LEVEL_MIN");
						final int levMax = jsonElement.getSafeInt("LEVEL_MAX");

						if (mySectorName.equals(sectName) || elemList.contains(sectName)) {
							listSectMin.add(levMin);
							listSectMax.add(levMax);
						}
					}

				});
			});

		} catch (final Exception e) {
			LOGGER.logError("Error :", e);
		}

		if (listSectMin.size() > 0) {
			Collections.sort(listSectMin);
			my_lev_min = listSectMin.get(0);
		}

		if (listSectMax.size() > 0) {
			listSectMax.sort(Collections.reverseOrder());
			my_lev_max = listSectMax.get(0);
		}

		serviceNode.addLine("FL_MIN", "" + my_lev_min);
		serviceNode.addLine("FL_MAX", "" + my_lev_max);
		boolean updateHeightFilter = ConfigurationFile.getBoolProperties(
				"UPDATE_HEIGHT_FILTER_ON_SECTORS_LEVEL_MIN_MAX_CHANGE");
		serviceNode.addLine("UPDATE_HEIGHTFILTER", updateHeightFilter);

	}

	/**
	 * Checks if is standby.
	 *
	 * @param sctName the sct name
	 * @return true, if is standby
	 */
	private static boolean isStandby(final String sctName) {
		return BlackBoardUtility_LIS.searchSECTOR_ID(sctName) < 3;
	}

	/**
	 * Process EN V SECTO R TABLE.
	 *
	 * @param json        the json
	 * @param serviceNode the service node
	 */
	private static void processENV_SECTOR_TABLE(final IRawData json, final HeaderNode serviceNode) {

		StringBuilder sectList = new StringBuilder();
		final var jsonArray = json.getSafeRawDataArray("SECTOR_TABLE");
		for (final IRawDataElement jsonElement : jsonArray) {
			final String sectorName = jsonElement.getSafeString("SECTOR_NAME");
			if (sectorName.contains("-")) {
				continue;
			}

			final int sectorId = jsonElement.getSafeInt("SECTOR_ID");
			if (sectorId == 2) {
				continue;
			}

			sectList.append(sectorName).append(",");
		}
		serviceNode.addLine("OTHER_RESPS", sectList.toString());

	}

	/**
	 * Process ENV DSP.
	 *
	 * @param json the json
	 */
	private static void processENV_DSP(final IRawData json) {

		final String magneticCorr = json.getSafeString("DPS_MAGNETIC_CORRECTION");
		final DataRoot rootData = DataRoot.createMsg();

		final var serviceNode = rootData.addHeaderOfService("MAGNETIC_INFO");
		serviceNode.addLine("CORRECTION", magneticCorr);
		ManagerBlackboard.addJVOutputList(rootData);

	}

}
