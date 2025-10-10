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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.data_exchange.model.RawStorage;
import com.leonardo.infrastructure.ArgumentManager;
import com.leonardo.infrastructure.Generics;
import com.leonardo.infrastructure.collections.HashMapEx;
import com.leonardo.infrastructure.collections.IReadOnlyMap;

// TODO: Auto-generated Javadoc
/**
 * The Class StorageManager.
 */
public class StorageManager {

	/** The Constant m_containerStorage. */
	private static final Map<String, RawStorage> m_containerStorage = new ConcurrentHashMap<>();

	/** The Constant m_observersList. */
	private static final HashMap<String, ArrayList<IObserver>> m_observersList = new HashMap<>();

//	/** The bb include. */
//	private static String bbInclude = ConfigurationFile.getProperties("BB_INCLUDE", "");

	/** The new trajectory. */
	public static boolean print = ArgumentManager.getBooleanOf("print");

	/**
	 * The Class SingletonLoader.
	 */
	private static class SingletonLoader {

		/** The Constant instance. */
		private static final StorageManager instance = new StorageManager();
	}

	/**
	 * Instantiates a new storage manager.
	 */
	private StorageManager() {
	}

	/**
	 * Gets the single instance of StorageManager.
	 *
	 * @return single instance of StorageManager
	 */
	public static StorageManager getInstance() {
		return SingletonLoader.instance;
	}

	/**
	 * Gets the container storage.
	 *
	 * @return the container storage
	 */
	public Map<String, RawStorage> getContainerStorage() {
		return m_containerStorage;
	}

	/**
	 * Manage storage.
	 *
	 * @param objectMsg the object msg
	 */
	public static void manageStorage(IRawData objectMsg) {
		Operation operation = objectMsg.getOperation(); // adesso non torna mai null
		String type = objectMsg.getType();
		if (!m_containerStorage.containsKey(type)) {
			if (Generics.isOneOf(operation, Operation.NONE, Operation.UPDATE, Operation.INSERT, Operation.APPEND)) {
				// ADD CONTAINER AND ROWS
				RawStorage storage = new RawStorage(objectMsg.getType());
				m_containerStorage.put(type, storage);
				storage.addItem(objectMsg);
			}
		} else {
			RawStorage storage = m_containerStorage.get(type);
			if (operation == Operation.DELETE) {
				// REMOVE ROWS
				storage.removeItem(objectMsg);
			} else if (operation != Operation.NONE) {
				// UPDATE ROWS
				storage.addItem(objectMsg);
			}
		}
	}

	/**
	 * Gets the storage.
	 *
	 * @param dataType the data type
	 * @return the storage
	 */
	public RawStorage getStorage(String dataType) {
		return m_containerStorage.get(dataType);
	}

	/**
	 * Gets the items storage.
	 *
	 * @param dataType the data type
	 * @return the items storage
	 */
	public static IReadOnlyMap<String, IRawData> getItemsStorage(String dataType) {
		RawStorage rawStorage = m_containerStorage.get(dataType);
		return rawStorage == null ? new HashMapEx<>() : new HashMapEx<>(rawStorage.getItemsMap());
	}

	/**
	 * Gets the items store.
	 *
	 * @param dataType the data type
	 * @return the items store
	 */
	public static IReadOnlyMap<String, String> getItemsStore(String dataType) {
		RawStorage rawStorage = m_containerStorage.get(dataType);
		if (rawStorage == null) {
			return new HashMapEx<>();
		}

		HashMapEx<String, String> rawStorageMap = new HashMapEx<>();
		for (IRawData element : rawStorage.getItemsList()) {
			rawStorageMap.put(element.getId(), element.toJsonString());
		}
		return rawStorageMap;
	}

	/**
	 * Gets the items storage list.
	 *
	 * @return the items storage list
	 */
	public List<RawStorage> getItemsStorageList() {
		return new ArrayList<>(m_containerStorage.values());
	}

	/**
	 * Gets the item storage opt.
	 *
	 * @param dataType the data type
	 * @param key      the key
	 * @return the item storage opt
	 */
	public static Optional<IRawData> getItemStorageOpt(String dataType, String key) {
		return Optional.ofNullable(m_containerStorage.get(dataType)).map(storage -> storage.getItemsMap().get(key));
	}

	/**
	 * Gets the clone item storage opt.
	 *
	 * @param dataType the data type
	 * @param key      the key
	 * @return the clone item storage opt
	 */
	public static Optional<IRawData> getCloneItemStorageOpt(String dataType, String key) {
		RawStorage rawStorage = m_containerStorage.get(dataType);
		IRawData rawData = null;
		if (rawStorage != null) {
			rawData = rawStorage.getItemsMap().get(key);
			if (rawData != null) {
				rawData = RawDataFactory.create(rawData);
			}
		}
		return Optional.ofNullable(rawData);
	}

	/**
	 * Gets the storage size.
	 *
	 * @param dataType the data type
	 * @return the storage size
	 */
	public int getStorageSize(String dataType) {
		RawStorage rawStorage = m_containerStorage.get(dataType);
		return rawStorage != null ? rawStorage.getSize() : 0;
	}

	/**
	 * registrazione osservatore.
	 *
	 * @param target   the target
	 * @param datatype the datatype
	 */
	public static synchronized void register(IObserver target, String datatype) {
		// registro l'osservatore
		var listObs = m_observersList.computeIfAbsent(datatype, k -> new ArrayList<>());
		listObs.add(target);
	}


	/**
	 * Notify change.
	 *
	 * @param item the item
	 * 
	 */
	public static synchronized void notifyChange(IRawData item) {
		long timestamp = System.currentTimeMillis();
		if (print) {
			System.out.println("Updating Time GF: " + timestamp + " ItemType: " + item.getType() + " Operation: "
					+ item.getOperation() + " Id: " + item.getId());
		}
		var list = m_observersList.get(item.getType());
		if (list != null) {
			list.forEach(o -> o.update(item));
		}
	}

}
