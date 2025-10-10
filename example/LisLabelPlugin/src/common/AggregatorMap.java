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
package common;

import application.pluginService.ServiceExecuter.IAggregatorService;
import application.pluginService.ServiceExecuter.ServiceExecuter;

import java.util.HashMap;
import java.util.Set;

/**
 * The Class AggregatorMap.
 */
public class AggregatorMap {
	
	/** The map. */
	private final HashMap<String, IAggregatorService> m_map = new HashMap<>();


	/**
 * Key set.
 *
 * @return the sets the
 */
public Set<String> keySet() {
		return this.m_map.keySet();
	}

	/**
	 * Gets the service.
	 *
	 * @param key the key
	 * @return the service
	 */
	public IAggregatorService getService(final String key) {
		return this.m_map.get(key);
	}
	
	/**
	 * Put.
	 *
	 * @param key the key
	 * @param value the value
	 */
	protected final void put(final String key, final IAggregatorService value) {
		this.m_map.put(key, value);
		ServiceExecuter.getInstance().RegisterService(value);
	}

}
