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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fourflight.WP.ECI.edm.HeaderNode;
import com.fourflight.WP.ECI.edm.Operation;
import com.fourflight.WP.ECI.edmplayback.GlobalSettings;
import com.gifork.auxiliary.ConfigurationFile;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.log.LoggerFactory;
import com.google.gson.Gson;
import com.leonardo.infrastructure.Generics;
import com.leonardo.infrastructure.log.ILogger;

import application.pluginService.ServiceExecuter.ServiceExecuter;
import applicationLIS.BlackBoardConstants_LIS;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import common.AggregatorMap;
import common.Alias;
import common.AliasMap;
import common.BlackMap;
import common.CommonConstants;
import common.Utils;
import processorNew.CPDLCProcessor;
import processorNew.DbsProcessor;
import processorNew.FlightProcessor;
import processorNew.FlightTrjProcessor;
import processorNew.MTCDProcessor;
import processorNew.ScaProcessor;
import processorNew.SnetProcessor;
import processorNew.TctProcessor;
import processorNew.TrackProcessor;

/**
 * The Class AnalyzerLabel.
 */
public class AnalyzerLabel implements IObserver {

	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateLogger(AnalyzerLabel.class);

	/** The Constant alarmedTct. */

	public static final Map<String, String> alarmedTct = new HashMap<>();

	/** The is by pass ord dard. */
	private static boolean isByPassOrdDard = false;

	/**
	 * Checks if is by pass ord dard.
	 *
	 * @return true, if is by pass ord dard
	 */
	public static boolean isByPassOrdDard() {
		return isByPassOrdDard;
	}

	/**
	 * Sets the by pass ord dard.
	 *
	 * @param _isByPassOrdDard
	 */
	public static void setByPassOrdDard(boolean _isByPassOrdDard) {
		isByPassOrdDard = _isByPassOrdDard;
	}

	/** The is by pass state. */
	private static byte isByPassState = 0;

	/**
	 * Gets the checks if is by pass state.
	 *
	 * @return the checks if is by pass state
	 */
	public static byte getIsByPassState() {
		return isByPassState;
	}

	/**
	 * Sets the checks if is by pass state.
	 *
	 * @param isByPassState the new checks if is by pass state
	 */
	public static void setIsByPassState(byte isByPassState) {
		AnalyzerLabel.isByPassState = isByPassState;
	}

	/** The check VIS FPT TRACK. */
	private static boolean check_NOT_VIS_FPT_TRACK = false;

	/** The M_GSON. */
	private static final Gson M_GSON = new Gson();

	/**
	 * Instantiates a new analyzer label.
	 */

	/**
	 * Analyze.
	 *
	 * @param blackMap      the black map
	 * @param aggregatorMap the aggregator map
	 * @param aliasMap      the alias map
	 * @param json          the json
	 * @param objectNode    the object node
	 */
	public static void analyze(final BlackMap blackMap, final AggregatorMap aggregatorMap, final AliasMap aliasMap,
			final IRawData json, final HeaderNode objectNode) {
		if (json.has(FlightInputConstants.SPX)) {
			json.put(FlightOutputConstants.FPT_LONGITUDE, json.remove(FlightInputConstants.SPX));
		}
		if (json.has(FlightInputConstants.SPY)) {
			json.put(FlightOutputConstants.FPT_LATITUDE, json.remove(FlightInputConstants.SPY));
		}

		json.getKeys().stream().filter(
				strKey -> !blackMap.containsKey(strKey) && !strKey.equals("VT_POINT") && !strKey.equals("GT_POINT"))
				.forEach(key -> {
					final Alias alias = Utils.getAlias(key, json, aliasMap);
					objectNode.addLine(alias.getAliasKey(), alias.getAliasData());
				});

		aggregatorMap.keySet().forEach(key -> ServiceExecuter.getInstance()
				.executeAggregator(aggregatorMap.getService(key).getServiceName(), json, objectNode));

	}

	/**
	 * Update.
	 *
	 * @param json the json
	 */
	@Override
	public void update(final IRawData json) {
		final String loggerCaller = "update()";

		if (json.getType().equals(DataType.WP_SETTINGS.name())) {
			final GlobalSettings set0 = M_GSON.fromJson(json.getSafeString("DATA", "{}"), GlobalSettings.class);
			final var settings = set0.getSettings(0);
			if (isByPassState != settings.getByp_status() && isByPassState == 1) {
				ManagerBlackboard.forcedAllUpdate(DataType.FLIGHT_EXTFLIGHT.name(), 500);
			}

			isByPassState = settings.getByp_status();
		}

		if (json.getType().equals(DataType.ENV_MOC.name())) {
			isByPassOrdDard = json.getSafeInt("MOC_SFN", 0) == 0;
		} else if (Generics.isOneOf(json.getOperation(), Operation.INSERT, Operation.UPDATE)) {
			switch (DataType.valueOf(json.getType())) {
			case TCT:
				TctProcessor.process(json);
				break;
			case BB_DBS_SWITCH_LABEL:

				var jsonOpt = BlackBoardUtility.getSelectedData(BlackBoardConstants_LIS.DataType.DBS.name(),
						CommonConstants.FOLLOWER, json.getId()).values().stream().findFirst();

				if (jsonOpt.isPresent()) {
					DbsProcessor.process(jsonOpt.get());
				}
				break;
			case DBS:
				DbsProcessor.process(json);
				break;

			case FLIGHT_EXTFLIGHT:
				if (BlackBoardUtility.getDataOpt(DataType.TRKID_LOST.name(), json.getId()).isPresent()) {
					FlightProcessor.processLostFlight(json);
					FlightProcessor.process(json);
				} else {
					FlightProcessor.process(json);
				}
				FlightProcessor.processEsb(json);
				break;

			case TRACK:
				if (!json.getSafeBoolean("IS_FPT_TRACKER") || check_NOT_VIS_FPT_TRACK) {
					TrackProcessor.process(json);
				}
				break;

			case FLIGHT_TRJ:
				FlightTrjProcessor.process(json);
				String flightNUM = json.getSafeString("FLIGHT_NUM");
				/** Per KotaBharu serve inserire sulle ESB le informazioni della TRAJECTORY **/
				Optional<IRawData> jsonFLight = BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(),
						flightNUM);
				if (jsonFLight.isPresent()) {
					FlightProcessor.processEsb(jsonFLight.get());
				}
				break;

			case MTCD_ITEM_NOTIFY:
				MTCDProcessor.process(json, true);
				break;

			case CPDLC:
				CPDLCProcessor.process(json);
				break;

			case TRKID_LOST:
				FlightProcessor.processLostFlight(json);
				break;
			case FLIGHT_PNT:
				FlightProcessor.processPntFlight(json);
				break;
			case SNET:
				SnetProcessor.process(json);
				break;
			case SCA:
				ScaProcessor.process(json);
				break;
			default:
				LOGGER.logDebug(loggerCaller, "In Update-Insert - Operation not recognized: " + json.getType());
				break;
			}

		} else if (json.getOperation() == Operation.DELETE) {

			switch (DataType.valueOf(json.getType())) {
			case TCT:
				TctProcessor.delete(json);
				break;

			case BB_DBS_SWITCH_LABEL:
				var jsonOpt = BlackBoardUtility.getDataOpt(BlackBoardConstants_LIS.DataType.DBS.name(), json.getId());
				if (!jsonOpt.isEmpty()) {
					DbsProcessor.process(jsonOpt.get());
				}
				break;
			case DBS:
				DbsProcessor.delete(json);
				break;

			case FLIGHT_EXTFLIGHT:
				if (BlackBoardUtility.getDataOpt(DataType.TRKID_LOST.name(), json.getId()).isPresent()) {
					FlightProcessor.deleteLostFlight(json);
				}
				FlightProcessor.delete(json);
				FlightProcessor.deleteEsb(json);
				break;

			case TRACK:
				TrackProcessor.delete(json);
				break;

			case MTCD_ITEM_NOTIFY:
				MTCDProcessor.delete(json);
				break;
			case FLIGHT_PNT:
				FlightProcessor.deletePntFlight(json);
				break;
			case TRKID_LOST:
				FlightProcessor.deleteLostFlight(json);
				break;
			case SNET:
				SnetProcessor.delete(json);
				break;
			case SCA:
				ScaProcessor.delete(json);
				break;
			default:
				LOGGER.logDebug(loggerCaller, "In Delete - Operation not recognized: " + json.getType());
				break;
			}
		}
	}

	/**
	 *
	 */
	public void register() {
		StorageManager.register(this, DataType.TRACK.name());
		StorageManager.register(this, DataType.FLIGHT_EXTFLIGHT.name());
		StorageManager.register(this, DataType.FLIGHT_TRJ.name());
		StorageManager.register(this, DataType.MTCD_ITEM_NOTIFY.name());
		StorageManager.register(this, DataType.CPDLC.name());
		StorageManager.register(this, DataType.TCT.name());
		StorageManager.register(this, DataType.WP_SETTINGS.name());
		StorageManager.register(this, DataType.ENV_MOC.name());
		StorageManager.register(this, DataType.TRKID_LOST.name());
		StorageManager.register(this, DataType.SNET.name());
		StorageManager.register(this, DataType.SCA.name());
		StorageManager.register(this, DataType.FLIGHT_PNT.name());
		StorageManager.register(this, DataType.DBS.name());
		StorageManager.register(this, DataType.BB_DBS_SWITCH_LABEL.name());

		if (!ConfigurationFile.getBoolProperties("IS_NOT_VIS_FPT_TRACK")) {
			AnalyzerLabel.check_NOT_VIS_FPT_TRACK = true;
		}

	}
}
