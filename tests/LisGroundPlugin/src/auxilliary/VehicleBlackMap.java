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
package auxilliary;

import common.BlackMap;

import java.util.HashMap;

/**
 * The Class VehicleBlackMap.
 */
public class VehicleBlackMap extends BlackMap {

	/**
	 * Instantiates a new vehicle black map.
	 */
	public VehicleBlackMap() {
		this.map = new HashMap<>();
		this.map.put("TEMPLATE", 0);
	}
}
