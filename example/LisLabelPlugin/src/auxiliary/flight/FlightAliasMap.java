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
package auxiliary.flight;

import auxiliary.fpm.FpmInputConstants;
import auxiliary.fpm.FpmOutputConstants;
import common.Alias;
import common.AliasMap;

import java.util.HashMap;

/**
 * The Class FlightAliasMap.
 */
public class FlightAliasMap extends AliasMap {
	
	/**
	 * Instantiates a new flight alias map.
	 */
	
	public FlightAliasMap() {
		HashMap<String, Alias> app_map;

		this.map = new HashMap<>();

		
		
		app_map = new HashMap<>();
		app_map.put(FlightInputConstants.TEMPLATE_AIS,
				new Alias(FlightOutputConstants.TEMPLATE, FlightOutputConstants.TEMPLATE_AIS));
		app_map.put(FlightInputConstants.TEMPLATE_CONTROLLED,
				new Alias(FlightOutputConstants.TEMPLATE, FlightOutputConstants.TEMPLATE_CONTROLLED));
		app_map.put(FlightInputConstants.TEMPLATE_NEARBY,
				new Alias(FlightOutputConstants.TEMPLATE, FlightOutputConstants.TEMPLATE_NEARBY));
		app_map.put(FlightInputConstants.TEMPLATE_HANDOVER_EXITING,
				new Alias(FlightOutputConstants.TEMPLATE, FlightOutputConstants.TEMPLATE_HANDOVER_EXITING));
		app_map.put(FlightInputConstants.TEMPLATE_NOTIFIED,
				new Alias(FlightOutputConstants.TEMPLATE, FlightOutputConstants.TEMPLATE_NOTIFIED));
		app_map.put(FlightInputConstants.TEMPLATE_HANDOVER_ENTERING,
				new Alias(FlightOutputConstants.TEMPLATE, FlightOutputConstants.TEMPLATE_HANDOVER_ENTERING));
		app_map.put(FlightInputConstants.TEMPLATE_TENTATIVE,
				new Alias(FlightOutputConstants.TEMPLATE, FlightOutputConstants.TEMPLATE_TENTATIVE));
		this.map.put(FlightInputConstants.TEMPLATE, app_map);
		
		app_map = new HashMap<>();
		app_map.put(FlightInputConstants.STS_HEAD,
				new Alias(FlightOutputConstants.STS, FlightOutputConstants.STS_HEAD));
		app_map.put(FlightInputConstants.STS_HOSP,
				new Alias(FlightOutputConstants.STS, FlightOutputConstants.STS_HOSP));
		this.map.put(FlightInputConstants.STS, app_map);
		

		
		
		app_map = new HashMap<>();
		app_map.put(FpmInputConstants.GND_DEV_TRUE,
				new Alias(FpmOutputConstants.GND_DEV, FpmOutputConstants.GND_DEV_TRUE));
		app_map.put(FpmInputConstants.GND_DEV_FALSE,
				new Alias(FpmOutputConstants.GND_DEV, FpmOutputConstants.GND_DEV_FALSE));
		this.map.put(FpmInputConstants.GND_DEV, app_map);
	
		app_map = new HashMap<>();
		app_map.put(FpmInputConstants.LONG_DEV_FLAG_TRUE,
				new Alias(FpmOutputConstants.LONG_DEV_FLAG, FpmOutputConstants.LONG_DEV_FLAG_TRUE));
		app_map.put(FpmInputConstants.LONG_DEV_FLAG_FALSE,
				new Alias(FpmOutputConstants.LONG_DEV_FLAG, FpmOutputConstants.LONG_DEV_FLAG_FALSE));
		this.map.put(FpmInputConstants.LONG_DEV_FLAG, app_map);
		
		app_map = new HashMap<>();
		app_map.put(FpmInputConstants.SPD_DEV_TRUE,
				new Alias(FpmOutputConstants.SPD_DEV, FpmOutputConstants.SPD_DEV_TRUE));
		app_map.put(FpmInputConstants.SPD_DEV_FALSE,
				new Alias(FpmOutputConstants.SPD_DEV, FpmOutputConstants.SPD_DEV_FALSE));
		this.map.put(FpmInputConstants.SPD_DEV, app_map);
		
		app_map = new HashMap<>();
		app_map.put(FpmInputConstants.VERT_DEV_FLAG_TRUE,
				new Alias(FpmOutputConstants.VERT_DEV_FLAG, FpmOutputConstants.VERT_DEV_FLAG_TRUE));
		app_map.put(FpmInputConstants.VERT_DEV_FLAG_FALSE,
				new Alias(FpmOutputConstants.VERT_DEV_FLAG, FpmOutputConstants.VERT_DEV_FLAG_FALSE));
		this.map.put(FpmInputConstants.VERT_DEV_FLAG, app_map);
		
		app_map = new HashMap<>();
		app_map.put(FpmInputConstants.VR_RATE_DEV_TRUE,
				new Alias(FpmOutputConstants.VR_RATE_DEV, FpmOutputConstants.VR_RATE_DEV_TRUE));
		app_map.put(FpmInputConstants.VR_RATE_DEV_FALSE,
				new Alias(FpmOutputConstants.VR_RATE_DEV, FpmOutputConstants.VR_RATE_DEV_FALSE));
		this.map.put(FpmInputConstants.VR_RATE_DEV, app_map);
		
		app_map = new HashMap<>();
		app_map.put(FpmInputConstants.NON_RVSM_EQUIPPED_FLAG_TRUE,
				new Alias(FpmOutputConstants.NON_RVSM_EQUIPPED_FLAG, FpmOutputConstants.NON_RVSM_EQUIPPED_FLAG_TRUE));
		app_map.put(FpmInputConstants.NON_RVSM_EQUIPPED_FLAG_FALSE,
				new Alias(FpmOutputConstants.NON_RVSM_EQUIPPED_FLAG, FpmOutputConstants.NON_RVSM_EQUIPPED_FLAG_FALSE));
		this.map.put(FpmInputConstants.NON_RVSM_EQUIPPED_FLAG, app_map);
		

		this.setAliasMapName("FlightAliasMap");
	}
}
