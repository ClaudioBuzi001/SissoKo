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
package auxiliary.tct;

import common.BlackMap;

import java.util.HashMap;

/**
 * The Class TctBlackMap.
 */
public class TctBlackMap extends BlackMap
{
	
	/**
	 * Instantiates a new tct black map.
	 */
	public TctBlackMap()
	{
		this.map = new HashMap<>();
		this.map.put("STN",0);
		this.map.put("X2_AT_CONFLICT_TIME",0);
        this.map.put("LON1_C" ,0);
        this.map.put("X1_AT_CONFLICT_TIME" ,0);
        this.map.put("D_TO_MD1" ,0);
        this.map.put("CONFLICT_NUMBER" ,0);
        this.map.put("LAT1_AT_CONFLICT_TIME" ,0);
        this.map.put("D_TO_MD2" ,0);
        this.map.put("Y1_C" ,0);
        this.map.put("LAT1_MD" ,0);
        this.map.put("CLOSEST_APPROACH_TIME" ,0);
        this.map.put("LON1_MD" ,0);
        this.map.put("Y1_AT_CONFLICT_TIME" ,0);
        this.map.put("LAT2_AT_CONFLICT_TIME" ,0);
        this.map.put("ALARM_TYPE" ,0);
        this.map.put("TIME_TO_CONFLICT" ,0);
        this.map.put("TRACK_NUMBER2" ,0);
        this.map.put("TRACK_NUMBER1" ,0);
        this.map.put("EST_DISTANCE_TO_SEP_LOSS_1" ,0);
        this.map.put("EST_DISTANCE_TO_SEP_LOSS_2" ,0);
        this.map.put("ESTIMATED_MINIMUM_VERTICAL_DISTANCE" ,0);
        this.map.put("PRESENT_THREE_DIM_DISTANCE" ,0);
        this.map.put("PRESENT_VERTICAL_DISTANCE" ,0);
        this.map.put("X2_C" ,0);
        this.map.put("LON1_AT_CONFLICT_TIME" ,0);
        this.map.put("LAT1_C" ,0);
        this.map.put("PRESENT_PLANAR_DISTANCE" ,0);
        this.map.put("LAT2_MD" ,0);
        this.map.put("URGENCY" ,0);
        this.map.put("Y1_MD" ,0);
        this.map.put("Y2_AT_MINIMUM_DISTANCE" ,0);
        this.map.put("LON2_C" ,0);
        this.map.put("X1_AT_MINIMUM_DISTANCE" ,0);
        this.map.put("X1_MD" ,0);
        this.map.put("Y2_C" ,0);
        this.map.put("Y2_MD" ,0);
        this.map.put("LON2_AT_CONFLICT_TIME" ,0);
        this.map.put("LON2_MD" ,0);
        this.map.put("X2_AT_MINIMUM_DISTANCE" ,0);
        this.map.put("X2_MD" ,0);
        this.map.put("TIME_TO_CLOSEST_APPROACH" ,0);
        this.map.put("Y2_AT_CONFLICT_TIME" ,0);
        this.map.put("ISTCT5NM" ,0);
        this.map.put("ISTCT" ,0);
        this.map.put("ESTIMATED_MINIMUM_PLANAR_DISTANCE" ,0);
        this.map.put("X1_C" ,0);
        this.map.put("ESTIMATED_MINIMUM_THREE_DIM_DISTANCE" ,0);
        this.map.put("CONFLICT_TIME" ,0);
        this.map.put("Y1_AT_MINIMUM_DISTANCE" ,0);
        this.map.put("LAT2_C" ,0);
    	this.map.put("EMERGENCY_TCT", 0);
    	this.map.put("CONFLICT_ID_TCT",0);

	}
}
