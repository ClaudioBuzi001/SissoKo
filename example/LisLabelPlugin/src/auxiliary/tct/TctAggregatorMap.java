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

import common.AggregatorMap;
import common.CommonConstants;
import service.tct.EMG_TCTAggregatorService;

/**
 * The Class TctAggregatorMap.
 */
public class TctAggregatorMap extends AggregatorMap {
	
		/**
		 * Instantiates a new tct aggregator map.
		 */
		
		public TctAggregatorMap()
		{
			
			put(CommonConstants.TCT, new EMG_TCTAggregatorService());
			
			
		}
}


