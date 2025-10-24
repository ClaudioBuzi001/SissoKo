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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.Strings;
import com.leonardo.infrastructure.log.ILogger;
import com.leonardo.infrastructure.plugins.framework.SimplePluginManager;

import application.pluginService.ServiceExecuter.ServiceExecuter;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import applicationLIS.extension.ListBaseExtension;
import auxliary.PluginListConstants;

/**
 * The Class AnalyzerList.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public class AnalyzerList implements IObserver {

	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateLogger(AnalyzerList.class);

	/** The Constant configuredDataType. */

	private final Map<String, Map<String, IRawData>> configuredDataType = new HashMap<>();

	/** The Constant nestedTag. */

	private static final String NESTED_TAG = "$";

	/** The Constant interrrogationTag. */
	private static final String INTERROGATION_TAG = "?";

	/** The Constant interrrogationSep. */
	private static final String INTERROGATION_SEP = ";";

	/** The Constant parameterTag. */
	private static final String PARAMETER_TAG = "@";

	/** The Constant idPrantesistOpen. */
	private static final String ID_PRANTESIST_OPEN = "<";

	/** The Constant idPrantesistClose. */
	private static final String ID_PRANTESIST_CLOSE = ">";

	/** The Constant subFiledDot. */
	private static final String SUB_FILE_DOT = ".";

	/** The Constant serviceConvertLongToEpochTime. */
	private static final String SERVICE_CONVERT_LONG_TO_EPOCH_TIME = "CONVERTEPOCH_";

	/** The last data type. */
	private static String LAST_DATA_TYPE = "";

//	/** The last list name. */
//	private static String LAST_LIST_NAME = "";

	/** The last in data. */
	private static String LAST_IN_DATA = "";

	/** The last out data. */
	private static String LAST_OUT_DATA = "";

	/** The last in data value. */
	private static String LAST_IN_DATA_VALUE = "";

	/** The Constant listConditionObj. */
	private static final Map<String, Boolean> LIST_CONDITION_OBJ = new HashMap<>();

	/** The Constant listMultipleConditionObj. */
	private static final Map<String, String> LIST_MULTIPLE_CONDITION_OBJ = new HashMap<>();

	/** The Constant listMultipleParam. */
	private static final Map<String, String> LIST_MULTIPLE_PARAM = new HashMap<>();

	/** The Constant listMtcdCurrentElements. */
	private static final Map<Integer, Double> LIST_MTCD_CURRENT_ELEMENTS = new HashMap<>();

//	/** The condition. */
//	private static boolean condition = true;

	/** The Constant m_colAttributes. */
//	private static final Map<String, HashMap<String, HashMap<String, String>>> M_COL_ATTRIBUTES = new HashMap<>();

//	/** The Constant m_ExtInfoAttributes. */
//	private static final Map<String, HashMap<String, HashMap<String, String>>> M_EXT_INFO_ATTRIBUTES = new HashMap<>();

	/** The map CPDLC flight dialogues. */
	private final Map<String, ArrayList<String>> mapCPDLCFlightDialogues = new HashMap<>();

	/** The map sil. */
	private final Map<String, String> mapSil = new HashMap<>();
	/** The map sil. */
	private final Map<String, String> mapdep = new HashMap<>();
	/** The map sil. */
	private final Map<String, String> maparr = new HashMap<>();
	/** The RST_VALIDITY. */
	public static final String RST_VALIDITY = "RST_VALIDITY";
	/** The EMG_RPAS_C2L. */
	public static final String EMG_RPAS_C2L = "C2L";
	/** The EMG_RPAS_DAAL. */
	public static final String EMG_RPAS_DAAL = "DAAL";

	/** The queue. */
	private final BlockingQueue<IRawData> queue = new LinkedBlockingQueue<>();

	/** The running. */
	private volatile boolean running = true;

	/** The consumer thread. */
	private Thread consumerThread;

	/**
	 * Instantiates a new analyzer list.
	 *
	 * @param configuredListArray HashMap<String, ArrayList<RawData>>
	 */
	public AnalyzerList(final Map<String, Map<String, IRawData>> configuredListArray) {

		StorageManager.register(this, DataType.BB_TRKID_FLIGHTNUM.name());

		configuredDataType.putAll(configuredListArray);

		consumerThread = new Thread(this::consumeList);
		consumerThread.setName("AnalyzerList");
		consumerThread.start();

		for (final var datatypeForListCfg : configuredDataType.entrySet()) {
			StorageManager.register(this, datatypeForListCfg.getKey());
		}
		LIST_MULTIPLE_PARAM.put("SIL", "IFIXNAME");
		LIST_MULTIPLE_PARAM.put("HLD", "HLDF");
		LIST_MULTIPLE_PARAM.put("DEP", "ADEP");
		LIST_MULTIPLE_PARAM.put("ARR", "ADES");

		addEmptyList("DIAG", "DIAL");

	}

	/**
	 * Update.
	 *
	 * @param bbKsoure the bb ksoure
	 */
	@Override
	public void update(final IRawData bbKsoure) {
		if (bbKsoure != null) {
			queue.offer(bbKsoure); // Inserisce i dati nella coda senza bloccare
		}
	}

	/**
	 * Consume orders.
	 */
	private void consumeList() {
		while (running) {
			try {
				IRawData json = queue.take(); // Attende e preleva un elemento dalla coda
				processList(json);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.logError("consumeOrders() Thread interrotto", e);
			}
		}
	}

	/**
	 * Update.
	 *
	 * @param bbKsoure
	 */
	public void processList(final IRawData bbKsoure) {
		LAST_DATA_TYPE = bbKsoure.getType();
		final String dataType = bbKsoure.getType();
		switch (DataType.valueOf(bbKsoure.getType())) {
		case ENV_RWY_IN_USE:
			processingOnly_ENV_RWY_IN_USE(bbKsoure);
			break;
		case ENV_HISTORY_LOGON_NOTIFY:
			processingOnly_ENV_HISTORY_LOGON_NOTIFY(bbKsoure);
			break;
		case ENV_FAILED_LOGON_NOTIFY:
			processingOnly_ENV_FAILED_LOGON_NOTIFY(bbKsoure);
			break;
		case CPDLC:
			processingOnly_CPDLC(bbKsoure);
			break;
		case CPDLC_HISTORY:
			processingOnly_CPDLC_HISTORY(bbKsoure);
			break;
		case ENV_MYRESPONSIBILITIES:
		case ENV_ATSO_COP:
			processingOnly_ENV_ATSU_SECTOR_COP_List(bbKsoure);
			break;
		case ENV_LOGIN_DATA:
			processingOnly_ENV_LOGIN_DATA(bbKsoure);
			break;
		case MTCD_ITEM_NOTIFY:
			if (!bbKsoure.getSafeString("CONFLICT_OR_RISK").equals("COP")) {
				processingOnly_MTCD_ITEM_NOTIFY(bbKsoure);
			}
			break;
		case FLIGHT_COORDINATION:
			processingOnly_FLIGHT_COORDINATION(bbKsoure);
			break;
		case ENV_AERODROMEINFO:
			processingOnly_ENV_AERODROMEINFO(bbKsoure);
			break;
		case FLIGHT_EXTFLIGHT:
			processingAllFlightList(bbKsoure);
			break;
		case TCT:
			processingTctAlm(bbKsoure);
			break;

		default:
			final List<ListBaseExtension> list = SimplePluginManager.getInstance()
					.getExtensions(ListBaseExtension.class).stream().filter(ex -> ex.getDataType().contains(dataType))
					.distinct().collect(Collectors.toList());
			if (!list.isEmpty()) {
				list.forEach(proc -> proc.update(bbKsoure));
			} else {
				processingAllListForDataType(bbKsoure);
			}
			break;
		}
	}

	/**
	 * Processing TCT alm.
	 *
	 * @param bbKsoure the bb ksoure
	 */
	private void processingTctAlm(final IRawData bbKsoure) {

		processingAllListForDataType(bbKsoure);
	}

	/**
	 * Processing snet alm ack.
	 *
	 * @param bbKsoure the bb ksoure
	 */

	/**
	 * Processing only browse list.
	 *
	 * @param json the json
	 */

	/**
	 * Processing only FLIGH T COORDINATION.
	 *
	 * @param json the json
	 */
	private void processingOnly_FLIGHT_COORDINATION(final IRawData json) {

		final IRawData deleteJson = RawDataFactory.createFromJson(json.toString());
		final boolean iscin = json.getSafeBoolean("ISCIN");
		final boolean iscout = json.getSafeBoolean("ISCOUT");
		final String flightNUM = json.getSafeString("FLIGHT_NUM");
		final String COORDTYPE = json.getSafeString("COORDTYPE");

		if (iscin) {
			deleteJson.setOperation(Operation.DELETE);
			deleteJson.put("ISCIN", "false");
			deleteJson.put("ISCOUT", "true");
			deleteJson.put("FLIGHT_NUM", flightNUM);
			deleteJson.put("COORDTYPE", COORDTYPE);
			deleteJson.setId(deleteJson.getId());
			deleteJson.setType("FLIGHT_COORDINATION");
			processingAllListForDataType(deleteJson);
		}
		if (iscout) {
			deleteJson.setOperation(Operation.DELETE);
			deleteJson.put("ISCIN", "true");
			deleteJson.put("ISCOUT", "false");
			deleteJson.put("FLIGHT_NUM", flightNUM);
			deleteJson.put("COORDTYPE", COORDTYPE);
			deleteJson.setId(deleteJson.getId());
			deleteJson.setType("FLIGHT_COORDINATION");
			processingAllListForDataType(deleteJson);
		}

		processingAllListForDataType(json);

	}

	/**
	 * Processing all flight list.
	 *
	 * @param bbKsource the bb ksource
	 */
	private void processingAllFlightList(final IRawData bbKsource) {
		final String dataTYpe = bbKsource.getType();

		final var listArrayForDataType = configuredDataType.get(dataTYpe);
		for (final String listName : listArrayForDataType.keySet()) {
//			LAST_LIST_NAME = listName;
			final IRawData listCfg = listArrayForDataType.get(listName);
			if (listName.equals("SIL")) {
				final String flightNUM = bbKsource.getSafeString("FLIGHT_NUM");
				final var silElemJson = RawDataFactory.create();

				String pointName = bbKsource.getSafeString("IFIXNAME");
				String pointETO = bbKsource.getSafeString("ETOIFIXID");
				String pointFixLev = bbKsource.getSafeString("IFIXILEV");
				String pointFixLat = bbKsource.getSafeString("IFIX_LATITUDE");
				String pointFixLon = bbKsource.getSafeString("IFIX_LONGITUDE");

				if (mapSil.get(flightNUM) != null) {
					final String ifix = mapSil.get(flightNUM);
					if (!ifix.equals(pointName)) {
						silElemJson.setOperation(Operation.DELETE);
						silElemJson.put("IFIXNAME", ifix);
						silElemJson.put("FLIGHT_NUM", flightNUM);
						silElemJson.put("ROW_ID", flightNUM);
						silElemJson.put("DATA_TYPE", "FLIGHT_EXTFLIGHT");
						silElemJson.setId(flightNUM);
						ProcessingList(listCfg, silElemJson, listName);
					}
				}

				mapSil.put(flightNUM, pointName);
				final String template = bbKsource.getSafeString("TEMPLATE");
				final String templateColor = bbKsource.getSafeString("TEMPLATE_COLOR");
				final String copName = bbKsource.getSafeString("COPNAME");

				final String callSign = bbKsource.getSafeString("CALLSIGN");
				final String aty = bbKsource.getSafeString("ATY");

				final String rvsm = bbKsource.getSafeString("RVSM");

				final String pel = bbKsource.getSafeString("PEL");

				silElemJson.put("ISSIL", bbKsource.getSafeString("ISSIL"));
				silElemJson.put("COPNAME", copName);
				silElemJson.setOperation(bbKsource.getOperation());
				silElemJson.put("IFIXNAME", pointName);
				silElemJson.put("ETOIFIXID", pointETO);
				silElemJson.put("CALLSIGN", callSign);
				silElemJson.put("ATY", aty);
				silElemJson.put("RVSM", rvsm);
				silElemJson.put("IFIXLEV", pointFixLev);

				silElemJson.put("IFIX_LATITUDE", pointFixLat);
				silElemJson.put("IFIX_LONGITUDE", pointFixLon);

				silElemJson.put("FLIGHT_NUM", flightNUM);
				silElemJson.put("TEMPLATE", template);
				silElemJson.put("TEMPLATE_COLOR", templateColor);
				silElemJson.put(EdmModelKeys.List.ROW_ID, flightNUM);
				silElemJson.put("PEL", pel);
				silElemJson.setType(dataTYpe);
				silElemJson.setId(flightNUM);
				ProcessingList(listCfg, silElemJson, listName);

			} else if (listName.equals("DEP")) {
				final String flightNUM = bbKsource.getSafeString("FLIGHT_NUM");
				final var silElemJson = RawDataFactory.create();

				String pointName = bbKsource.getSafeString("ADEP");

				if (mapdep.get(flightNUM) != null) {
					final String ifix = mapdep.get(flightNUM);
					if (!ifix.equals(pointName)) {
						silElemJson.setOperation(Operation.DELETE);
						silElemJson.put("ADEP", ifix);
						silElemJson.put("FLIGHT_NUM", flightNUM);
						silElemJson.put("ROW_ID", flightNUM);
						silElemJson.put("DATA_TYPE", "FLIGHT_EXTFLIGHT");
						silElemJson.setId(flightNUM);
						ProcessingList(listCfg, silElemJson, listName);
					}
				}

				mapdep.put(flightNUM, pointName);
				silElemJson.setOperation(bbKsource.getOperation());
				silElemJson.setType(dataTYpe);
				silElemJson.setId(flightNUM);
				ProcessingList(listCfg, bbKsource, listName);

			} else if (listName.equals("ARR")) {
				final String flightNUM = bbKsource.getSafeString("FLIGHT_NUM");
				final var silElemJson = RawDataFactory.create();

				String pointName = bbKsource.getSafeString("ADES");

				if (maparr.get(flightNUM) != null) {
					final String ifix = maparr.get(flightNUM);
					if (!ifix.equals(pointName)) {
						silElemJson.setOperation(Operation.DELETE);
						silElemJson.put("ADES", ifix);
						silElemJson.put("FLIGHT_NUM", flightNUM);
						silElemJson.put("ROW_ID", flightNUM);
						silElemJson.put("DATA_TYPE", "FLIGHT_EXTFLIGHT");
						silElemJson.setId(flightNUM);
						ProcessingList(listCfg, silElemJson, listName);
					}
				}

				maparr.put(flightNUM, pointName);
				silElemJson.setOperation(bbKsource.getOperation());
				silElemJson.setType(dataTYpe);
				silElemJson.setId(flightNUM);
				ProcessingList(listCfg, bbKsource, listName);

			} else if (listName.equals("RPAS")) {
				final String flightNUM = bbKsource.getSafeString("FLIGHT_NUM");
				final var rpasElemJson = RawDataFactory.create();
				final Optional<IRawData> jsonTrack = Utils_LIS.getTrkFromFn(flightNUM);

				boolean isRpas = bbKsource.getSafeBoolean("IS_RPAS");
				Operation operLis = Operation.INSERT;
				if (!isRpas || bbKsource.getOperation().equals(Operation.DELETE)) {
					operLis = Operation.DELETE;
				}

				String callsign = bbKsource.getSafeString("CALLSIGN");
				String procedure = bbKsource.getSafeString("PROCEDURE").trim();
				String payload = bbKsource.getSafeString("PAYLOAD").trim();
				String clearance = bbKsource.getSafeString("CLEARANCE").trim();
				String pilotcontact = bbKsource.getSafeString("PILOTCONTACT").trim();
				String pilotid = bbKsource.getSafeString("PILOTID").trim();
				String rpascode = bbKsource.getSafeString("RPAS_CODE").trim();
				String route = bbKsource.getSafeString("ROUTE");
				String destination = bbKsource.getSafeString("ADES");
				String template = bbKsource.getSafeString("TEMPLATE");
				String templateColor = bbKsource.getSafeString("TEMPLATE_COLOR");

				rpasElemJson.put("IS_RPAS", isRpas);
				rpasElemJson.setType(dataTYpe);
				rpasElemJson.put("CALLSIGN", callsign);
				rpasElemJson.setOperation(operLis);
				rpasElemJson.put("PAYLOAD", payload);
				rpasElemJson.put("CLEARANCE", clearance);
				rpasElemJson.put("PILOTCONTACT", pilotcontact);
				rpasElemJson.put("PILOTID", pilotid);
				rpasElemJson.put("RPAS_CODE", rpascode);
				rpasElemJson.put("PROCEDURE", procedure);
				rpasElemJson.put("ROUTE", route);
				rpasElemJson.put("DESTINATION", destination);
				if (!jsonTrack.isEmpty()) {
					rpasElemJson.put("EMERGENCY_RPAS", decodeEmergency(jsonTrack));
					rpasElemJson.put("SSR_CODE", jsonTrack.get().getSafeString("MODE_3A").trim());
				}

				rpasElemJson.put("FLIGHT_NUM", flightNUM);
				rpasElemJson.put("TEMPLATE_COLOR", templateColor);
				rpasElemJson.put("TEMPLATE", template);
				rpasElemJson.put(EdmModelKeys.List.ROW_ID, flightNUM);

				rpasElemJson.setType(dataTYpe);

				rpasElemJson.setId(flightNUM);

				ProcessingList(listCfg, rpasElemJson, listName);

			}
			/*
			 * else if (listName.equals("CSL")) { final IRawData coastJson =
			 * RawDataFactory.createFromJson(bbKsource.toString()); final int csDrawing =
			 * coastJson.getSafeInt("CS_DRAWING"); final String template = coastJson.getSafeString("TEMPLATE");
			 * coastJson.setId(bbKsource.getId()); coastJson.setType(bbKsource.getType()); if
			 * (!(template.equals(PluginListConstants.TEMPLATE_CONTROLLED) || (csDrawing == 11 &&
			 * (template.equals(PluginListConstants.TEMPLATE_TENTATIVE) ||
			 * template.equals(PluginListConstants.TEMPLATE_HANDOVER_ENTERING))))) {
			 * coastJson.setOperation(Operation.DELETE); } else {
			 * coastJson.setOperation(bbKsource.getOperation()); } ProcessingList(listCfg, coastJson);
			 *
			 * }
			 */
			else {
				ProcessingList(listCfg, bbKsource, listName);
			}
		}

		final String flightNum = bbKsource.getSafeString("FLIGHT_NUM");
		if (mapCPDLCFlightDialogues.get(flightNum) != null) {
			final ArrayList<String> listDialogues = mapCPDLCFlightDialogues.get(flightNum);
			final ArrayList<String> listDialoguesToRemove = new ArrayList<>();
			for (final String cpdlcId : listDialogues) {
				final var bbkSource = BlackBoardUtility.getDataOpt(DataType.CPDLC.name(), cpdlcId);
				if (bbkSource.isPresent()) {
					LAST_DATA_TYPE = DataType.CPDLC.name();
					processingOnly_CPDLC(bbkSource.get());
					if (!bbkSource.get().getSafeString("STATUS_DIALOG").isEmpty()) {
						listDialoguesToRemove.add(cpdlcId);
					}
				}
			}
			if (!listDialoguesToRemove.isEmpty()) {
				for (final String cpdlcDialogue : listDialoguesToRemove) {
					mapCPDLCFlightDialogues.get(flightNum).remove(cpdlcDialogue);
				}
			}
			if (mapCPDLCFlightDialogues.get(flightNum).isEmpty()) {
				mapCPDLCFlightDialogues.remove(flightNum);
			}
		}
	}

	/**
	 * Processing only EN V RW Y I N USE.
	 *
	 * @param json the json
	 */
	private void processingOnly_ENV_RWY_IN_USE(final IRawData json) {
		final String dataTYpe = json.getType();
		final var airpList = json.getSafeRawDataArray("airpList");
		for (final IRawDataElement airpElem : airpList) {
			final var listArrayForDataType = configuredDataType.get(dataTYpe);
			if (listArrayForDataType != null) {
				for (final String listName : listArrayForDataType.keySet()) {
					final IRawData RWYINUSE = listArrayForDataType.get(listName);
					if (RWYINUSE != null) {
//						LAST_LIST_NAME = listName;
						ProcessingList(RWYINUSE, airpElem, listName);
					}
				}

			}
		}
	}

	/**
	 * Processing only EN V AERODROMEINFO.
	 *
	 * @param json the json
	 */
	private void processingOnly_ENV_AERODROMEINFO(final IRawData json) {
		final String dataTYpe = json.getType();
		final var aerodromeList = json.getSafeRawDataArray("aerodromeList");
		for (final IRawDataElement itemAirQnhData : aerodromeList) {
			final var listArrayForDataType = configuredDataType.get(dataTYpe);
			if (listArrayForDataType != null) {
				for (final String listName : listArrayForDataType.keySet()) {
					final IRawData AIRP_DATA = listArrayForDataType.get(listName);
					if (AIRP_DATA != null) {
//						LAST_LIST_NAME = listName;
						ProcessingList(AIRP_DATA, itemAirQnhData, listName);
					}
				}
			}
		}
	}

	/**
	 * Processing only MTC D ITE M NOTIFY.
	 *
	 * @param receivedMsgData the received msg data
	 */
	private void processingOnly_MTCD_ITEM_NOTIFY(final IRawData receivedMsgData) {
		final String dataTYpe = receivedMsgData.getType();
		final double probabilityEnc = receivedMsgData.getSafeDouble("ENCOUNTER_PROBABILITY");
		final var listArrayForDataType = configuredDataType.get(dataTYpe);
		if (listArrayForDataType != null) {
			for (final String listName : listArrayForDataType.keySet()) {
				final IRawData AIRP_DATA = listArrayForDataType.get(listName);
				if (AIRP_DATA != null) {
					// LAST_LIST_NAME = listName;
					LIST_MTCD_CURRENT_ELEMENTS.put(receivedMsgData.getSafeInt("CONFLICT_ID", -1), probabilityEnc);
					ProcessingList(AIRP_DATA, receivedMsgData, listName);
				}
			}
		}
	}

	/**
	 * Processing only EN V LOGI N DATA.
	 *
	 * @param receivedMsgData the received msg data
	 */
	private void processingOnly_ENV_LOGIN_DATA(final IRawData receivedMsgData) {
		final String dataTYpe = receivedMsgData.getType();
		final var physConsoleList = receivedMsgData.getSafeRawDataArray("physConsoleList");
		for (final IRawDataElement physConsole : physConsoleList) {
			final var listArrayForDataType = configuredDataType.get(dataTYpe);
			if (listArrayForDataType != null) {
				for (final String listName : listArrayForDataType.keySet()) {
					final IRawData LOGIN_DATA = listArrayForDataType.get(listName);
					if (LOGIN_DATA != null) {
						// LAST_LIST_NAME = listName;
						ProcessingList(LOGIN_DATA, physConsole, listName);
					}
				}
			}
		}
	}

	/**
	 * Processing only EN V HISTOR Y LOGO N NOTIFY.
	 *
	 * @param json the json
	 */
	private void processingOnly_ENV_HISTORY_LOGON_NOTIFY(final IRawData json) {
		final String dataTYpe = json.getType();
		final var historyLogonList = json.getSafeRawDataArray("historyLogonList");

		for (final IRawDataElement element : historyLogonList) {
			final var itemLogon = RawDataFactory.create(element);

			final var listArrayForDataType = configuredDataType.get(dataTYpe);
			if (listArrayForDataType != null) {
				for (final String listName : listArrayForDataType.keySet()) {
					final IRawData LOGON_DATA = listArrayForDataType.get(listName);
					if (LOGON_DATA != null) {
						// LAST_LIST_NAME = listName;

						itemLogon.setOperation(json.getOperation());

						ProcessingList(LOGON_DATA, itemLogon, listName);
					}
				}
			}
		}
	}

	/**
	 * Processing only ENV FAILED LOGON NOTIFY.
	 *
	 * @param receivedMsgData the received msg data
	 */
	private void processingOnly_ENV_FAILED_LOGON_NOTIFY(final IRawData receivedMsgData) {

		final String dataTYpe = receivedMsgData.getType();
		final var historyLogonList = receivedMsgData.getSafeRawDataArray("historyLogonList");
		for (final IRawDataElement element : historyLogonList) {
			final var itemLogon = RawDataFactory.create(element);
			final var listArrayForDataType = configuredDataType.get(dataTYpe);
			if (listArrayForDataType != null) {
				for (final String listName : listArrayForDataType.keySet()) {
					final IRawData LOGON_DATA = listArrayForDataType.get(listName);
					if (LOGON_DATA != null) {
						// LAST_LIST_NAME = listName;
						itemLogon.setOperation(receivedMsgData.getOperation());
						ProcessingList(LOGON_DATA, itemLogon, listName);
					}
				}
			}
		}
	}

	/**
	 * Gets the tagged parameters.
	 *
	 * @param value     String
	 * @param paramList the param list
	 */
	private static void getTaggedParameters(final String value, final List<String> paramList) {
		try {

			String fieldVal = value;

			while (true) {
				int startIdx = fieldVal.indexOf(PluginListConstants.TAG);
				if (startIdx == -1) {
					break;
				}
				startIdx += 1;
				int endidx = fieldVal.indexOf(PluginListConstants.TAG, startIdx);
				if (endidx == -1) {
					break;
				}
				paramList.add(fieldVal.substring(startIdx, endidx));

				if (endidx + 1 < fieldVal.length()) {
					fieldVal = fieldVal.substring(endidx + 1);
				} else {
					break;
				}
			}
		} catch (final Exception e) {
			LOGGER.logError("getTaggedParameters", e);
		}
	}

	/**
	 * Processing all list for data type.
	 *
	 * @param receivedMsgData RawData
	 */
	private void processingAllListForDataType(final IRawData receivedMsgData) {
		final String _dataType = receivedMsgData.getType();
		final var listArrayForDataType = configuredDataType.get(_dataType);
		if (listArrayForDataType != null) {

			for (final var listCfgMap : listArrayForDataType.entrySet()) {
				// LAST_LIST_NAME = listCfgMap.getKey();
				final IRawData listJsonCfg = listCfgMap.getValue();
				ProcessingList(listJsonCfg, receivedMsgData, listCfgMap.getKey());
			}
		}
	}

	/**
	 * Processing list.
	 *
	 * @param listConfig the list config
	 * @param source     the source
	 * @param listName   the list name
	 */
	private static void ProcessingList(final IRawData listConfig, final IRawDataElement source, final String listName) {
		ProcessingList(listConfig, RawDataFactory.create(source), listName);
	}

	/**
	 * Processing list.
	 *
	 * @param listConfig RawData
	 * @param source     RawData
	 * @param listName
	 */
	private static void ProcessingList(final IRawData listConfig, final IRawData source, final String listName) {

		/**
		 * Represents a simple key-value pair with optional attributes.
		 * <p>
		 * This class is used to store a data record consisting of a key, a value, and an optional map of
		 * additional attributes.
		 */
		class RecordData {
			private final String Key;
			private final String Value;
			private Map<String, String> Attributes;

			private RecordData(final String key, final String value) {
				Key = key;
				Value = value;
				Attributes = null;
			}

			private RecordData(final String key, final String value, final Map<String, String> attributes) {
				this(key, value);
				Attributes = attributes;
			}
		}

		final String sourceID = source.getId();

		final String taggedParamater = listConfig.getSafeString(PluginListConstants.CONDITION);

		final List<String> conditionParamters = new ArrayList<>();
		getTaggedParameters(taggedParamater, conditionParamters);
		boolean condition = true;

		// conditionParamters.stream().findFirst().ifPresent(item -> condition = source.getSafeBoolean(item,
		// false));
		Optional<String> elem = conditionParamters.stream().findFirst();
		if (elem.isPresent()) {
			condition = source.getSafeBoolean(elem.get(), false);
		}

		final DataRoot rootListData = DataRoot.createMsg();

		final var l_ArrayExtInfo = listConfig.getSafeRawDataArray(EdmModelKeys.List.EXTINFO);

		final var l_Array = listConfig.getSafeRawDataArray(EdmModelKeys.List.COL);

		final var behaviourParamJson = listConfig.getSafeElement("attributes");
		final String idLista = behaviourParamJson.getSafeString("ID");

		Operation oper = source.getOperation();

		final String rowIDField = listConfig.getSafeString(EdmModelKeys.List.ROW_ID);
		final String rowIDVal = parseTaggedParamters(rowIDField, source, sourceID, listName);

		final String multipleListParam = LIST_MULTIPLE_PARAM.get(listName);

		StringBuilder multipleListRowId = new StringBuilder(listName).append('_').append(multipleListParam).append('_')
				.append(rowIDVal);

		StringBuilder ident = new StringBuilder(listName).append(rowIDVal);
		// Map<String, Boolean> LIST_CONDITION_OBJ = new HashMap<>();
		if (condition) {
			if (oper == Operation.DELETE) {
				LIST_CONDITION_OBJ.remove(ident.toString());
			} else {
				LIST_CONDITION_OBJ.put(ident.toString(), true);
			}

			if (LIST_MULTIPLE_PARAM.containsKey(listName)) {
				if (oper == Operation.DELETE) {
					LIST_MULTIPLE_CONDITION_OBJ.remove(multipleListRowId.toString(),
							source.getSafeString(multipleListParam));
				} else {
					LIST_MULTIPLE_CONDITION_OBJ.put(multipleListRowId.toString(),
							source.getSafeString(multipleListParam));
				}
			}
		} else {
			final Boolean isLastListName = LIST_CONDITION_OBJ.get(ident.toString());
			final String multipleListRowIdValue = LIST_MULTIPLE_CONDITION_OBJ.get(multipleListRowId.toString());
			if (isLastListName != null && isLastListName) {
				oper = Operation.DELETE;
				LIST_CONDITION_OBJ.remove(ident.toString());

				if (multipleListRowIdValue != null) {
					if (LIST_MULTIPLE_PARAM.containsKey(listName)) {
						source.put(multipleListParam, multipleListRowIdValue);
					}
					LIST_MULTIPLE_CONDITION_OBJ.remove(multipleListRowId.toString());

				}
			} else if (multipleListRowIdValue != null) {
				if (LIST_MULTIPLE_PARAM.containsKey(listName)) {
					source.put(multipleListParam, multipleListRowIdValue);
				}
				LIST_MULTIPLE_CONDITION_OBJ.remove(multipleListRowId.toString());
				oper = Operation.DELETE;

			} else {
				return;
			}
		}

		final var listNode = rootListData.addHeaderOfList(oper);

		if (oper != Operation.DELETE) {

			for (final String item : behaviourParamJson.getKeys()) {
				final String behaviourParamVal = behaviourParamJson.getSafeString(item);
				final String behaviourParamResult = parseTaggedParamters(behaviourParamVal, source, sourceID, listName);
				listNode.addLine(item.toUpperCase(), behaviourParamResult);
			}

			listNode.addLine(EdmModelKeys.List.COL_NUM, String.valueOf(l_Array.size()));

			final List<RecordData> xmlListRow = new ArrayList<>();

			if (!rowIDVal.isEmpty()) {
				xmlListRow.add(new RecordData(EdmModelKeys.List.ROW_ID, rowIDVal));
			}
			final Map<String, HashMap<String, HashMap<String, String>>> M_EXT_INFO_ATTRIBUTES = new HashMap<>();
			final var listExtInfoAttributes = M_EXT_INFO_ATTRIBUTES.computeIfAbsent(idLista, k -> new HashMap<>());
			for (final IRawDataElement extInfoItem : l_ArrayExtInfo) {
				final String idExtInfo = extInfoItem.getSafeString("key");
				final var extInfoAttributesMap = listExtInfoAttributes.computeIfAbsent(idExtInfo,
						k -> getAttribute(extInfoItem, "", source, sourceID, listName));
				listNode.addLine(idExtInfo, extInfoItem.getSafeString("data"), extInfoAttributesMap);
			}
			final Map<String, HashMap<String, HashMap<String, String>>> M_COL_ATTRIBUTES = new HashMap<>();
			final var listColAttributes = M_COL_ATTRIBUTES.computeIfAbsent(idLista, k -> new HashMap<>());
			for (int i = 0; i < l_Array.size(); i++) {
				final int j = i + 1;

				final IRawDataElement colItem = l_Array.get(i);
				final String indata = colItem.getSafeString(PluginListConstants.INDATA);
				final String outdata = colItem.getSafeString(PluginListConstants.OUTDATA);

				final String idCol = colItem.getSafeString("key");

				LAST_IN_DATA = indata;
				LAST_OUT_DATA = outdata;

				final var colAttributesMap = listColAttributes.computeIfAbsent(idCol,
						k -> getAttribute(colItem, EdmModelKeys.List.COL_, source, sourceID, listName));

				String colNum = Strings.concat(EdmModelKeys.List.COL, String.valueOf(j));
				listNode.addLine(colNum, outdata, colAttributesMap);

				if (!rowIDVal.isEmpty()) {
					final var rowAttributesMap = getAttribute(colItem, EdmModelKeys.List.ROW_, source, sourceID,
							listName);

					final String result = parseTaggedParamters(indata, source, sourceID, listName);
					LAST_IN_DATA_VALUE = result;
					xmlListRow.add(new RecordData(outdata, result, rowAttributesMap));
				}
			}

			xmlListRow.forEach(r -> listNode.addLine(r.Key, r.Value, r.Attributes));
		} else {

			if (!rowIDVal.isEmpty()) {

				final String behaviourParamId = parseTaggedParamters(idLista, source, sourceID, listName);
				listNode.addLine(IRawData.KEY_ID, behaviourParamId);
				listNode.addLine(EdmModelKeys.List.ROW_ID, rowIDVal);
			}
		}

		ManagerBlackboard.addJVOutputList(rootListData);
	}

	/**
	 * Parses the tagged paramters.
	 *
	 * @param _indata   the indata
	 * @param inputJson the input json
	 * @param id        the id
	 * @param listName  the list name
	 * @return the string
	 */
	private static String parseTaggedParamters(final String _indata, final IRawData inputJson, final String id,
			final String listName) {
		String indata = _indata;

		final ArrayList<String> paramList = new ArrayList<>();
		getTaggedParameters(indata, paramList);
		for (String param : paramList) {
			StringBuilder ele = new StringBuilder(PARAMETER_TAG).append(param).append(PARAMETER_TAG);
			param = param.replace(" ", "");
			/* MMA 22 Jan 2021 BEGIN field$jsonField ES: payload$auto */
			if (param.contains(SERVICE_CONVERT_LONG_TO_EPOCH_TIME)) {

				final int initIdx = param.indexOf(SERVICE_CONVERT_LONG_TO_EPOCH_TIME);
				try {
					String valueToConvert = param.substring(initIdx + SERVICE_CONVERT_LONG_TO_EPOCH_TIME.length());
					valueToConvert = parseCompositeParam(id, valueToConvert, inputJson, listName);
					final String valueToInsert = convertEpochToLocalTime(Long.valueOf(valueToConvert));
					indata = indata.replace(ele.toString(), valueToInsert);
				} catch (final Exception e) {
					LOGGER.logError("parseTaggedParamters", e);
				}

			} else if (param.contains(NESTED_TAG)) {
				final String EMPTY_CONVERSION = "";

				final int nestedIdx = param.indexOf(NESTED_TAG);
				final String fatherParam = param.substring(0, nestedIdx);
				final String childParam = param.substring(nestedIdx + 1);
				try {
					String fatherString = inputJson.getSafeString(fatherParam);
					fatherString = fatherString.replace("=", ":");
					fatherString = fatherString.replace(":,", ":'',");
					final IRawData fatherJson = RawDataFactory.createFromJson(fatherString);
					final String childString = fatherJson.getSafeString(childParam);
					indata = indata.replace(ele.toString(), childString);
				} catch (final Exception e) {
					e.printStackTrace();
					indata = EMPTY_CONVERSION;
				}

			}

			if (param.contains(INTERROGATION_TAG)) {
				final int interrogationIdx = param.indexOf(INTERROGATION_TAG);
				final int separatorIdx = param.indexOf(INTERROGATION_SEP);

				final String contitionalParam = param.substring(0, interrogationIdx);
				final String xmloutcondition = parseCompositeParam(id, contitionalParam, inputJson, listName);

				if (!xmloutcondition.isEmpty()) {

					final String contitionalParamResult1 = param.substring(interrogationIdx + 1, separatorIdx);
					final String xmloutValue1 = parseCompositeParam(id, contitionalParamResult1, inputJson, listName);
					indata = indata.replace(ele.toString(), xmloutValue1);
				} else {

					final String contitionalParamResult2 = param.substring(separatorIdx + 1);
					final String xmloutValue2 = parseCompositeParam(id, contitionalParamResult2, inputJson, listName);
					indata = indata.replace(ele.toString(), xmloutValue2);
				}
			} else {

				String xmloutValue = parseCompositeParam(id, param, inputJson, listName);
				indata = indata.replace(ele.toString(), xmloutValue);
			}
		}
		return indata;
	}

	/**
	 * Parses the composite param.
	 *
	 * @param _id        String
	 * @param _param     private
	 * @param _inputJson RawData
	 * @param listName   String
	 * @return String
	 */
	private static String parseCompositeParam(final String _id, final String _param, final IRawData _inputJson,
			final String listName) {
		return parseCompositeParam(_id, _param, _inputJson, "", listName);
	}

	/**
	 * Service call.
	 *
	 * @param _id          the id
	 * @param param        the param
	 * @param _inputJs     the input json
	 * @param DefaultValue the default value
	 * @param listName
	 * @return the string
	 */
	/*
	 *
	 *
	 */
	private static String serviceCall(final String _id, final String param, final IRawData _inputJs,
			String DefaultValue, final String listName) {
		String xmloutValue = "";

		final var _inputJson = RawDataFactory.create(_inputJs);
		_inputJson.setId(_inputJs.getId());
		_inputJson.setType(_inputJs.getType());

		final int startIndexField = param.indexOf("(");
		final int stopIndexField = param.lastIndexOf(")");
		final String ServiceName = param.substring(0, startIndexField);
		String parametersStr = param.substring(startIndexField + 1, stopIndexField);
		parametersStr = parametersStr.replace(" ", "");
		final String[] parametersList = parametersStr.split(Pattern.quote(","));
		final IRawData jsonParams = RawDataFactory.create();

		jsonParams.put("IN_DATA_VALUE", LAST_IN_DATA_VALUE);
		jsonParams.put("IN_DATA", LAST_IN_DATA);
		jsonParams.put("OUT_DATA", LAST_OUT_DATA);
		jsonParams.put("LIST_NAME", listName);
		jsonParams.setType(LAST_DATA_TYPE);
		jsonParams.setId(_id);

		for (final String paramkeyDataStr : parametersList) {
			final String[] paramkeyDataList = paramkeyDataStr.split(Pattern.quote("="));
			final String paramName = paramkeyDataList[0];
			String paramValue = paramkeyDataList[1];

			paramValue = parseCompositeParam(_id, paramValue, _inputJson, paramValue, listName);

			jsonParams.put(paramName, paramValue);

			if (paramName.equalsIgnoreCase("DEFAULT")) {
				DefaultValue = paramValue;
			}
		}

		_inputJson.setAuxiliaryData(jsonParams);
		if (ServiceExecuter.getInstance().isAvailable(ServiceName)) {
			final Object obj = ServiceExecuter.getInstance().ExecuteService(ServiceName, _inputJson);
			if (obj instanceof String) {
				xmloutValue = (String) obj;
			} else {
				xmloutValue = DefaultValue;
			}
		} else {
			xmloutValue = param;
		}
		return xmloutValue;
	}

	/**
	 * Expression 1 parsing.
	 *
	 * @param _id          String
	 * @param param        String
	 * @param _inputJson   RawData
	 * @param DefaultValue String
	 * @param listName
	 * @return String
	 */
	private static String expression_1_Parsing(final String _id, final String param, IRawData _inputJson,
			final String DefaultValue, final String listName) {
		String xmloutValue = "";
		String id = _id;
		String dataType = "";
		String field = "";
		String idName = "";

		final int startIndexField = param.indexOf(ID_PRANTESIST_OPEN);
		final int stopIndexField = param.lastIndexOf(ID_PRANTESIST_CLOSE);
		final int tIndexDot = param.lastIndexOf(SUB_FILE_DOT);
		dataType = param.substring(0, startIndexField);
		idName = param.substring(startIndexField + 1, stopIndexField);
		field = param.substring(tIndexDot + 1);

		final String extracted_id = parseCompositeParam(_id, idName, _inputJson, listName);
		if (!extracted_id.isEmpty()) {
			id = extracted_id;
		} else {
			id = _inputJson.getSafeString(idName, idName);
		}
		final var bbkSource = BlackBoardUtility.getDataOpt(dataType, id);
		if (bbkSource.isPresent()) {

			_inputJson = bbkSource.get();
		}
		xmloutValue = _inputJson.getSafeString(field, DefaultValue);

		return xmloutValue;
	}

	/**
	 * {@literal}Questo metodo elabora i parametri dinamici dei file di configurazione delle liste.
	 *
	 * @param _id          String
	 * @param _param       private
	 * @param _inputJson   RawData
	 * @param DefaultValue the default value
	 * @param listName
	 * @return String
	 */
	private static String parseCompositeParam(final String _id, final String _param, final IRawData _inputJson,
			final String DefaultValue, final String listName) {
		String xmloutValue = "";

		if (_param.contains("(") && _param.contains(")")) {

			xmloutValue = serviceCall(_id, _param, _inputJson, DefaultValue, listName);
			LAST_IN_DATA_VALUE = xmloutValue;
		} else if (_param.contains(SUB_FILE_DOT) && _param.contains(ID_PRANTESIST_OPEN)
				&& _param.contains(ID_PRANTESIST_CLOSE)) {

			xmloutValue = expression_1_Parsing(_id, _param, _inputJson, DefaultValue, listName);
			LAST_IN_DATA_VALUE = xmloutValue;

		}
		if (xmloutValue.isEmpty()) {

			xmloutValue = _inputJson.getSafeString(_param, DefaultValue);
			LAST_IN_DATA_VALUE = xmloutValue;
		}
		return xmloutValue;
	}

	/**
	 * Gets the attribute.
	 *
	 * @param keyDataVal   the key data val
	 * @param prefixFilter the prefix filter
	 * @param inputJson    the input json
	 * @param sourceID     the source ID
	 * @param listName
	 * @return the attribute
	 */
	private static HashMap<String, String> getAttribute(final IRawDataElement keyDataVal, final String prefixFilter,
			final IRawData inputJson, final String sourceID, final String listName) {
		final HashMap<String, String> attributes = new HashMap<>();

		keyDataVal.getKeys().stream().filter(strKey -> strKey.startsWith(prefixFilter)).forEach(key -> {
			String value = keyDataVal.getSafeString(key);
			if (!value.isEmpty()) {
				key = key.replace(prefixFilter, "");
				value = parseTaggedParamters(value, inputJson, sourceID, listName);
				attributes.put(key, value);

			}
		});
		return attributes;
	}

	/**
	 * Processing only EN V ATS U SECTO R CO P list.
	 *
	 * @param json     the json
	 * @param listName
	 */
	private void processingOnly_ENV_ATSU_SECTOR_COP_List(final IRawData json) {
		final var atsuList = json.getSafeRawDataArray("ATSULIST");
		final var listCfg = configuredDataType.get(json.getType()).get("ASI");
		for (final IRawDataElement itemAtsuData : atsuList) {
			final IRawData rawData = RawDataFactory.create(itemAtsuData);
			rawData.setType(json.getType());
			StringBuilder str = new StringBuilder(itemAtsuData.getSafeString("ATSU_NAME")).append("_")
					.append(itemAtsuData.getSafeString("SECTOR_ID"));
			if (json.getType().equals(DataType.ENV_ATSO_COP.name())) {
				str.append("_").append(itemAtsuData.getSafeString("CPDLC_LOWER_LEVEL"));
				rawData.setId(str.toString());
			} else {
				rawData.setId(str.toString());
			}

			ProcessingList(listCfg, rawData, listCfg.getId());
		}
	}

	/**
	 * Processing only CPDLC.
	 *
	 * @param json the json
	 */

	private void processingOnly_CPDLC(final IRawData json) {
		final String dataTYpe = json.getType();
		final String flightId = json.getSafeString("FLIGHT_NUM");
		final String dialogueId = json.getSafeString("CPDLC_ID");
		final Map<String, IRawData> listArrayForDataType = configuredDataType.get(dataTYpe);
		if (listArrayForDataType != null) {
			StringBuilder rowId = new StringBuilder(dialogueId).append('@').append(flightId);
			for (final String listName : listArrayForDataType.keySet()) {
				final IRawData cpdlcData = listArrayForDataType.get(listName);
				if (cpdlcData != null) {
					// LAST_LIST_NAME = listName;
					json.put("CPDLC_ROW_ID", rowId.toString());
					ProcessingList(cpdlcData, json, listName);
				}
			}
		}
		if (json.getSafeString("STATUS_DIALOG").isEmpty()) {
			if (!mapCPDLCFlightDialogues.containsKey(flightId)) {
				mapCPDLCFlightDialogues.put(flightId, new ArrayList<>());
			}
			if (!mapCPDLCFlightDialogues.get(flightId).contains(dialogueId)) {
				mapCPDLCFlightDialogues.get(flightId).add(dialogueId);
			}
		}
	}

	/**
	 * Processing only CPDL C HISTORY.
	 *
	 * @param json the json
	 */
	private void processingOnly_CPDLC_HISTORY(final IRawData json) {
		final String dataTYpe = json.getType();
		final var historyCPDLCList = json.getSafeRawDataArray("LIST_HISTORY");
		for (int i = historyCPDLCList.size() - 1; i >= 0; i--) {
			final var itemLogon = historyCPDLCList.get(i);
			final Map<String, IRawData> listArrayForDataType = configuredDataType.get(dataTYpe);
			if (listArrayForDataType != null) {
				for (final String listName : listArrayForDataType.keySet()) {
					final IRawData history_Data = listArrayForDataType.get(listName);
					if (history_Data != null) {
						// LAST_LIST_NAME = listName;
						final String flightNum = json.getSafeString("FLIGHT_NUM");
						itemLogon.put("FLIGHT_NUM", flightNum);
						IRawData jsonHistory = RawDataFactory.create(itemLogon);
						jsonHistory.setOperation(json.getOperation());
						jsonHistory.setId(json.getId());
						jsonHistory.setType(dataTYpe);
						ProcessingList(history_Data, jsonHistory, listName);
					}
				}

			}
		}

	}

	/**
	 * Convert epoch to local time.
	 *
	 * @param epochTime the epoch time
	 * @return the string
	 */
	private static String convertEpochToLocalTime(final Long epochTime) {
		final Date epochDate = new Date(epochTime * 1000);
		final SimpleDateFormat jdf = new SimpleDateFormat("yyyy/MM/dd'-'HH:mm");
		return jdf.format(epochDate);
	}

	/**
	 * Adds the empty list.
	 *
	 * @param dataType the data type
	 * @param listName the list name
	 */
	private void addEmptyList(final String dataType, final String listName) {
		if (configuredDataType.get(dataType) != null && configuredDataType.get(dataType).get(listName) != null) {
			final IRawData listCfg = configuredDataType.get(dataType).get(listName);
			final var json = RawDataFactory.create();
			json.setOperation(Operation.INSERT);
			AnalyzerList.ProcessingList(listCfg, json, listName);
		}

	}

	/**
	 * @param jsonTrack
	 * @return emergencyRpas
	 */
	private static String decodeEmergency(Optional<IRawData> jsonTrack) {
		String retValue = "";
		boolean c2Loss = false;
		boolean daaLoss = false;
		boolean c2Loss_fromMode3A = false;

		boolean rst_validity = jsonTrack.get().getSafeBoolean(RST_VALIDITY);
		if (rst_validity) {
			c2Loss = jsonTrack.get().getSafeBoolean(EMG_RPAS_C2L);
			daaLoss = jsonTrack.get().getSafeBoolean(EMG_RPAS_DAAL);
		}
		String track_mode_3A = jsonTrack.get().getSafeString("MODE_3A");
		if (track_mode_3A != null) {
			if (track_mode_3A.equals("A7400")) {
				c2Loss_fromMode3A = true;
			}
		}

		if (c2Loss || c2Loss_fromMode3A) {
			retValue = EMG_RPAS_C2L;
		} else {
			if (daaLoss) {
				retValue = EMG_RPAS_DAAL;
			}
		}

		return retValue;
	}

}
