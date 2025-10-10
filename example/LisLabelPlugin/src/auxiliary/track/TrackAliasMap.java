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
package auxiliary.track;

import common.Alias;
import common.AliasMap;

import java.util.HashMap;

/**
 * The Class TrackAliasMap.
 */
public class TrackAliasMap extends AliasMap
{		
	
	/**
	 * Instantiates a new track alias map.
	 */
	
	public TrackAliasMap () 
	{				
		HashMap<String, Alias> app_map;
		
		this.map = new HashMap<>();		
				
		
		
		app_map = new HashMap<>();
		app_map.put(TrackInputConstants.SPI_FALSE, new Alias(TrackOutputConstants.SPI, TrackOutputConstants.SPI_FALSE));		
		this.map.put(TrackInputConstants.SPI, app_map);
		
		app_map = new HashMap<>();
		app_map.put(TrackInputConstants.RCS_NULL, new Alias(TrackOutputConstants.RCS, TrackOutputConstants.RCS_NULL));		
		this.map.put(TrackInputConstants.RCS, app_map);
		
		app_map = new HashMap<>();
		app_map.put(TrackInputConstants.IAS_0, new Alias(TrackOutputConstants.IAS, TrackOutputConstants.IAS_NULL));
		this.map.put(TrackInputConstants.IAS, app_map);
		
		app_map = new HashMap<>();
		app_map.put(TrackInputConstants.SAL_OLD_VALUE_TRUE, new Alias(TrackOutputConstants.SAL_OLD_VALUE, TrackOutputConstants.SAL_OLD_VALUE_TRUE));
		app_map.put(TrackInputConstants.SAL_OLD_VALUE_FALSE, new Alias(TrackOutputConstants.SAL_OLD_VALUE, TrackOutputConstants.SAL_OLD_VALUE_FALSE));
		this.map.put(TrackInputConstants.SAL_OLD_VALUE, app_map);
		
		app_map = new HashMap<>();
		app_map.put(TrackInputConstants.SAL_VALIDITY_VALUE_TRUE, new Alias(TrackOutputConstants.SAL_VALIDITY_VALUE, TrackOutputConstants.SAL_VALIDITY_VALUE_TRUE));
		app_map.put(TrackInputConstants.SAL_VALIDITY_VALUE_FALSE, new Alias(TrackOutputConstants.SAL_VALIDITY_VALUE, TrackOutputConstants.SAL_VALIDITY_VALUE_FALSE));
		this.map.put(TrackInputConstants.SAL_VALIDITY_VALUE, app_map);		
		
		app_map = new HashMap<>();
		app_map.put(TrackInputConstants.HDG_OLD_TRUE, new Alias(TrackOutputConstants.HDG_OLD, TrackOutputConstants.HDG_OLD_TRUE));
		app_map.put(TrackInputConstants.HDG_OLD_FALSE, new Alias(TrackOutputConstants.HDG_OLD, TrackOutputConstants.HDG_OLD_FALSE));
		this.map.put(TrackInputConstants.HDG_OLD, app_map);			
		
		app_map = new HashMap<>();
		app_map.put(TrackInputConstants.HDG_VALIDITY_000, new Alias(TrackOutputConstants.HDG_VALIDITY, TrackOutputConstants.HDG_VALIDITY_000));
		app_map.put(TrackInputConstants.HDG_VALIDITY_111, new Alias(TrackOutputConstants.HDG_VALIDITY, TrackOutputConstants.HDG_VALIDITY_111));
		this.map.put(TrackInputConstants.HDG_VALIDITY, app_map);	
		
		
		this.setAliasMapName("TrackAliasMap");
	}
}


