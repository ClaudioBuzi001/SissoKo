/*
 * (c) Copyright Leonardo Company S.p.A.. All rights reserved.
 * 
 * Any right of industrial and intellectual property on this document,
 * and of technical Know-how herein contained, belongs to
 * Leonardo Company S.p.A. and/or third parties.
 * According to the law, it is forbidden to disclose, reproduce or however
 * use this document and any data herein contained for any use without
 * previous written authorization by Leonardo Company S.p.A.
 */
package auxiliary.cpdlc;

import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import common.Alias;
import common.AliasMap;

import java.util.HashMap;

/**
 * The Class CpdlcAliasMap.
 */
public class CpdlcAliasMap extends AliasMap
{
	
	/**
	 * Instantiates a new cpdlc alias map.
	 */
	public CpdlcAliasMap()
	{
		final HashMap<String, Alias> app_map;
		
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
		
		this.setAliasMapName("CpdlcAliasMap");
	}
}



