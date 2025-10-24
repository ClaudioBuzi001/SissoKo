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

import java.util.HashMap;

import common.Alias;
import common.AliasMap;

/**
 * The Class VehicleAliasMap.
 */
public class VehicleAliasMap extends AliasMap {

	/**
	 * Instantiates a new vehicle alias map.
	 */
	public VehicleAliasMap() {
		final HashMap<String, Alias> app_map;

		this.map = new HashMap<>();

		app_map = new HashMap<>();
		app_map.put(VehicleInputValuesConstants.TEMPLATE_CONTROLLED,
				new Alias(VehicleOutputValuesConstants.TEMPLATE, VehicleOutputValuesConstants.TEMPLATE_CONTROLLED));
		app_map.put(VehicleInputValuesConstants.TEMPLATE_TENTATIVE,
				new Alias(VehicleOutputValuesConstants.TEMPLATE, VehicleOutputValuesConstants.TEMPLATE_TENTATIVE));
		app_map.put(VehicleInputValuesConstants.TEMPLATE_WAITING,
				new Alias(VehicleOutputValuesConstants.TEMPLATE, VehicleOutputValuesConstants.TEMPLATE_WAITING));
		app_map.put(VehicleInputValuesConstants.TEMPLATE_HANDOVER,
				new Alias(VehicleOutputValuesConstants.TEMPLATE, VehicleOutputValuesConstants.TEMPLATE_HANDOVER));
		this.map.put(VehicleOutputValuesConstants.TEMPLATE, app_map);

		this.setAliasMapName("VehicleAliasMap");
	}
}
