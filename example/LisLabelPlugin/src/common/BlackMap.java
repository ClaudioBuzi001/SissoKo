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
 * The Class BlackMap.
 */
public class BlackMap 
{
	
	/** The map. */
	protected HashMap <String, Integer> map;
	
	/**
	 * Contains key.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean containsKey(final String key)
	{
		return this.map.containsKey(key);
	}
	
	/**
	 * Put.
	 *
	 * @param key the key
	 */
	public void put(final String key)
	{
		this.map.put(key, 0);
	}
	
	
	/**
 * To string.
 *
 * @return the string
 */
@Override
	public String toString() 
	{
		final StringBuilder output = new StringBuilder();
		
		for (final String key : this.map.keySet())
			output.append("key: ").append(key);
		
		return output.toString();			
	}
}
