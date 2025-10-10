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

import java.util.HashMap;

/**
 * The Class AliasMap.
 */
public class AliasMap 
{		
	
	/** The map. */
	protected HashMap <String, HashMap<String, Alias>> map;

	
	
/**
 * Gets the key data.
 *
 * @param key the key
 * @return the key data
 */
	public HashMap<String, Alias> getKeyData(final String key)
	{		
		return this.map.get(key);	
	}
	
	
	/**
	 * Sets the alias map name.
	 *
	 * @param name the new alias map name
	 */
	public void setAliasMapName(final String name)
	{
		/** The alias map name. */
	}
}


