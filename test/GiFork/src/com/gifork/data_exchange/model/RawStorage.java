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
package com.gifork.data_exchange.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;


/**
 * The Class RawStorage.
 */
public class RawStorage {

	/**
	 * The Enum NotifyMsg.
	 */
	public enum NotifyMsg {

		/** The onrun. */
		ONRUN,
		/** The updated. */
		UPDATED,
		/** The removed. */
		REMOVED
	}

	/** The items. */
	private final ConcurrentHashMap<String, IRawData> m_items = new ConcurrentHashMap<>();

	/**
	 *
	 */
	private final ConcurrentHashMap<String, Integer> storageMaxSize = new ConcurrentHashMap<>();

	/** The type. */
	private final String m_type;

	/** The type. */
	private boolean m_enable = true;

	/**
	 * Instantiates a new raw storage.
	 *
	 * @param datatype the datatype
	 */
	public RawStorage(String datatype) {
		m_type = datatype;
	}

	/**
	 * Attenzione, se il dato che si vuole inserire non e' cambiato rispetto al precedente allora non
	 * esegue nessuna operazione.
	 *
	 * @param rawItem the raw item
	 */
	public void addItem(IRawData rawItem) {
		Objects.requireNonNull(rawItem, "rawItem must not be null");
		if (m_enable && !rawItem.isEmpty()) {
			IRawData prev = m_items.get(rawItem.getId());
			if (!rawItem.equals(prev)) {
				m_items.put(rawItem.getId(), rawItem);
				if (prev != null) {
					prev.getAuxiliaryDataOpt().ifPresent(aux -> rawItem.setAuxiliaryData(aux));
				}
				StorageManager.notifyChange(rawItem);
			}
		}
		calculatemaxstorage(m_type);
	}

	/**
	 * Gets the item.
	 *
	 * @param id the id
	 * @return the item
	 */
	public Optional<IRawData> getItem(String id) {
		return Optional.ofNullable(m_items.get(id));
	}

	/**
	 * Removes the item.
	 *
	 * @param data the data
	 * @return true, if successful
	 */
	public boolean removeItem(IRawData data) {
		IRawData prev = null;
		if (!data.isEmpty()) {
			prev = m_items.remove(data.getId());
			StorageManager.notifyChange(data);
		}
		return prev != null;
	}

	/**
	 * Gets the items map.
	 *
	 * @return the items map
	 */
	public ConcurrentHashMap<String, IRawData> getItemsMap() {
		return m_items;
	}

	/**
	 * Gets the items list.
	 *
	 * @return the items list
	 */
	public List<IRawData> getItemsList() {
		ArrayList<IRawData> retList = new ArrayList<>(m_items.values());
		return retList;
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return m_items.size();
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return m_type;
	}

	/**
	 * Removes the all.
	 */
	public void removeAll() {
		m_items.values().forEach(it -> removeItem(it));
	}

	/**
	 * Sets the enable.
	 *
	 * @param m_enable the new enable
	 */
	public void setEnable(boolean m_enable) {
		this.m_enable = m_enable;
	}

	/**
	 * Checks if is enable.
	 *
	 * @return true, if is enable
	 */
	public boolean isEnable() {
		return m_enable;
	}

	/**
	 * @param key
	 */
	/**
	 * @param key
	 */
	private void calculatemaxstorage(String key) {
		int max = Math.max(this.storageMaxSize.getOrDefault(key, 0), this.getSize());
		this.storageMaxSize.put(key, max);
	}

	/**
	 * Gets the storage max size.
	 *
	 * @return the storage max size
	 */
	public ConcurrentHashMap<String, Integer> getStorageMaxSize() {
		return storageMaxSize;
	}

}
