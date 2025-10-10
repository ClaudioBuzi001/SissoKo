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
package com.gifork.blackboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.commons.log.LoggerFactory;
import com.gifork.data_exchange.model.RawStorage;
import com.leonardo.infrastructure.Pair;
import com.leonardo.infrastructure.Strings;
import com.leonardo.infrastructure.collections.IReadOnlyMap;
import com.leonardo.infrastructure.log.ILogger;

/**
 * The Class BlackBoardUtility.
 */
public class BlackBoardUtility {

	/**
	 *
	 */
	private static final ILogger logger = LoggerFactory.CreateLogger(BlackBoardUtility.class);
	/**
	 *
	 */
	public static final Timer m_timeline = new Timer("BB Statistics");

	/**
	 * Instantiates a new black board utility.
	 */
	private BlackBoardUtility() {
	}

	/**
	 * Gets the data opt.
	 *
	 * @param dataType the data type
	 * @return the data opt
	 */
	public static Optional<IRawData> getDataOpt(String dataType) {
		return StorageManager.getItemStorageOpt(dataType, IRawData.NO_ID);
	}

	/**
	 * Gets the data opt.
	 *
	 * @param dataType the data type
	 * @param key      the key
	 * @return the data opt
	 */
	public static Optional<IRawData> getDataOpt(String dataType, String key) {
		return StorageManager.getItemStorageOpt(dataType, key);
	}

	/**
	 * Gets the clone data opt.
	 *
	 * @param dataType the data type
	 * @param key      the key
	 * @return the clone data opt
	 */
	public static Optional<IRawData> getCloneDataOpt(String dataType, String key) {
		return StorageManager.getCloneItemStorageOpt(dataType, key);
	}

	/**
	 * Gets the all data.
	 *
	 * @param dataType the data type
	 * @return the all data
	 */
	public static IReadOnlyMap<String, IRawData> getAllData(String dataType) {
		return StorageManager.getItemsStorage(dataType);
	}

	/**
	 * Gets the selected data.
	 *
	 * @param dataType the data type
	 * @param filter   the filter
	 * @return the selected data
	 */
	public static Map<String, IRawData> getSelectedData(String dataType, Pair<String, String> filter) {
		IReadOnlyMap<String, IRawData> items = StorageManager.getItemsStorage(dataType);

		HashMap<String, IRawData> selectedList = new HashMap<>();

		for (var entry : items.entrySet()) {
			IRawData json = entry.getValue();
			if (json.getSafeString(filter.getX()).equals(filter.getY())) {
				selectedList.put(entry.getKey(), json);
			}
		}
		return selectedList;
	}

	/**
	 * Restituisce un sottoinsieme della BB che soddisfa la condizione item.field==value
	 *
	 * @param dataType codice della blackboard
	 * @param field    nome del campo su cui applicare il filtro
	 * @param value    valore che deve avere il campo per soddisfare il filtro
	 * @return sottoinsieme di valori filtrati
	 */
	public static Map<String, IRawData> getSelectedData(String dataType, String field, String value) {
		var items = StorageManager.getItemsStorage(dataType);

		HashMap<String, IRawData> selectedList = new HashMap<>();
		for (var entry : items.keySet()) {
			IRawData json = items.get(entry);
			if (json != null && json.getSafeString(field).equals(value)) {
				selectedList.put(entry, json);
			}
		}
		return selectedList;
	}

	/**
	 * Gets the size.
	 *
	 * @param dataType the data type
	 * @return the size
	 */
	public static int getSize(String dataType) {
		return StorageManager.getInstance().getStorageSize(dataType);
	}

	/**
	 * Creates the data.
	 *
	 * @param dataType   the data type
	 * @param key        the key
	 * @param paramName  the param name
	 * @param paramValue the param value
	 */

	public static void createData(String dataType, String key, String paramName, String paramValue) {
		IRawData rawData = RawDataFactory.create();
		rawData.put(paramName, paramValue);

		ManagerBlackboard.addRawData(rawData, dataType, key);
	}

	/**
	 * Creates the data.
	 *
	 * @param dataType the data type
	 * @param key      the key
	 * @param params   the params
	 */
	public static void createData(String dataType, String key, HashMap<String, String> params) {
		IRawData rawData = RawDataFactory.create();
		for (var item : params.entrySet()) {
			rawData.put(item.getKey(), item.getValue());
		}

		ManagerBlackboard.addRawData(rawData, dataType, key);
	}

	/**
	 * Attenzione! Utilizzare il metodo forceUpdateData che notifica gli aggiornamenti.
	 *
	 * Attenzione! <br>
	 * Questo metodo altera il RawData passato Questo metodo, utilizzando la
	 * StorageManager.getItemStorage, se non trova in BB ne crea uno nuovo e lo aggiunge.
	 *
	 * @param dataType   the data type
	 * @param key        the key
	 * @param paramName  the param name
	 * @param paramValue the param value
	 * @return true, if successful
	 */
	public static boolean updateData(String dataType, String key, String paramName, String paramValue) {
		IRawData rawData = BlackBoardUtility.getDataOpt(dataType, key).orElse(RawDataFactory.create());
		rawData.setOperation(Operation.UPDATE);
		if (Strings.hasRelevantChars(paramName)) {
			rawData.put(paramName, paramValue);
		}

		return ManagerBlackboard.addRawData(rawData, dataType, key);
	}

	/**
	 * Update data with insert/update event notification.
	 *
	 * @param dataType   the data type
	 * @param key        the key
	 * @param paramName  the param name
	 * @param paramValue the param value
	 * @return true, if successful
	 */
	public static boolean forceUpdateData(String dataType, String key, String paramName, String paramValue) {
		IRawData rawData = BlackBoardUtility.getCloneDataOpt(dataType, key).orElse(RawDataFactory.create());
		rawData.setOperation(rawData.isEmpty() ? Operation.INSERT : Operation.UPDATE);

		if (Strings.hasRelevantChars(paramName)) {
			rawData.put(paramName, paramValue);
		}

		return ManagerBlackboard.addRawData(rawData, dataType, key);
	}

	/**
	 * Attenzione! Utilizzare il metodo forceUpdateData che notifica gli aggiornamenti.
	 *
	 * Attenzione! <br>
	 * Questo metodo altera il RawData passato * Questo metodo, utilizzando la
	 * StorageManager.getItemStorage, se non trova in BB ne crea uno nuovo e lo aggiunge.
	 *
	 * @param dataType the data type
	 * @param key      the key
	 * @param params   the params
	 * @return true, if successful
	 */
	public static boolean updateData(String dataType, String key, HashMap<String, String> params) {
		IRawData rawData = BlackBoardUtility.getDataOpt(dataType, key).orElse(RawDataFactory.create());
		rawData.setOperation(Operation.UPDATE);
		for (var item : params.entrySet()) {
			rawData.put(item.getKey(), item.getValue());
		}

		return ManagerBlackboard.addRawData(rawData, dataType, key);
	}

	/**
	 * Update data with insert/update event notification.
	 *
	 * @param dataType the data type
	 * @param key      the key
	 * @param params   the params
	 * @return true, if successful
	 */
	public static boolean forceUpdateData(String dataType, String key, HashMap<String, String> params) {
		IRawData rawData = BlackBoardUtility.getCloneDataOpt(dataType, key).orElse(RawDataFactory.create());
		rawData.setOperation(rawData.isEmpty() ? Operation.INSERT : Operation.UPDATE);

		for (var item : params.entrySet()) {
			rawData.put(item.getKey(), item.getValue());
		}

		return ManagerBlackboard.addRawData(rawData, dataType, key);
	}

	/**
	 * Add data. rawData has to be a new object or a cloned object, in order to have insert/update event
	 * notification.
	 *
	 * @param dataType the data type
	 * @param key      the key
	 * @param rawData  the raw data
	 * @return true, if successful
	 */
	public static boolean addData(String dataType, String key, IRawData rawData) {
		var rawDataClone = RawDataFactory.create(rawData);
		rawDataClone.setOperation(Operation.INSERT);
		return ManagerBlackboard.addRawData(rawDataClone, dataType, key);
	}

	/**
	 * Add data. rawData has to be a new object or a cloned object, in order to have insert/update event
	 * notification.
	 *
	 * @param dataType the data type
	 * @param rawData  the raw data
	 * @return true, if successful
	 */
	public static boolean addData(String dataType, IRawData rawData) {
		var rawDataClone = RawDataFactory.create(rawData);
		rawDataClone.setOperation(Operation.INSERT);
		return ManagerBlackboard.addRawData(rawDataClone, dataType);
	}

	/**
	 * Add data. rawData has to be a new object or a cloned object, in order to have insert/update event
	 * notification.
	 *
	 * @param dataType the data type
	 * @param rawData  the raw data
	 * @param oper     the oper
	 * @return true, if successful
	 */
	public static boolean addData(String dataType, IRawData rawData, Operation oper) {
		var rawDataClone = RawDataFactory.create(rawData);
		rawDataClone.setOperation(oper);
		return ManagerBlackboard.addRawData(rawDataClone, dataType);
	}

	/**
	 * Removes the data.
	 *
	 * @param dataType the data type
	 * @param json     the json
	 * @return true, if successful
	 */
	public static boolean removeData(String dataType, IRawData json) {
		return ManagerBlackboard.removeRawData(dataType, json.getId());
	}

	/**
	 * Removes the data.
	 *
	 * @param dataType the data type
	 * @param key      the key
	 * @return true, if successful
	 */
	public static boolean removeData(String dataType, String key) {
		return ManagerBlackboard.removeRawData(dataType, key);
	}

	/**
	 * Removes the all data.
	 *
	 * @param dataType the data type
	 */
	public static void removeAllData(String dataType) {
		RawStorage storage = StorageManager.getInstance().getStorage(dataType);
		if (storage != null) {
			storage.getItemsList().forEach(it -> ManagerBlackboard.removeRawData(dataType, it.getId()));
		}
	}

	/**
	 *
	 */
	private static void printStatistics() {

		logger.logError("", "############ STATISTICS BB ############");
		List<RawStorage> blackBoardList = StorageManager.getInstance().getItemsStorageList();
		List<RawStorage> statisticsList = blackBoardList.stream().filter(storage -> ManagerBlackboard.extractStatistics().getOrDefault(storage.getType(), false)).collect(Collectors.toList());

		for (RawStorage rawStorage : statisticsList) {
			String message = rawStorage.getType() + "--->\t\t(" + rawStorage.getSize() + ")";
			logger.logError("", message);
		}

		logger.logError("", "#####################################");
	}

	/**
	 * @param time
	 */
	public static void start(int time) {
		int delayMilliseconds = time * 60000;
		m_timeline.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				printStatistics();
			}

		}, delayMilliseconds, delayMilliseconds);
	}

//
//	/**
//	 *
//	 */
//	public static void stop() {
//		if (isRunning) {
//			m_timeline.cancel();
//		}
//	}

}
