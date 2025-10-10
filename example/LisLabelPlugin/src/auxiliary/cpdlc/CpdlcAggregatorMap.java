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

import application.pluginService.ServiceExecuter.IAggregatorService;
import application.pluginService.ServiceExecuter.ServiceExecuter;
import common.AggregatorMap;
import service.cpdlc.*;



/**
 * The Class CpdlcAggregatorMap.
 */
public class CpdlcAggregatorMap extends AggregatorMap
{
	
	/**
	 * Instantiates a new cpdlc aggregator map.
	 */
	
	public CpdlcAggregatorMap()
	{
		
		put(CpdlcOutputConstants.DOWNLINK_CPDLC_MSG, new DDLMSGAggregatorService());

		
		put(CpdlcOutputConstants.UPLINK_CPDLC_CFL, new UDLCFLAggregatorService());
		
		
		put(CpdlcOutputConstants.UPLINK_CPDLC_ARC, new UDLARCAggregatorService());
		
		
		put(CpdlcOutputConstants.UPLINK_CPDLC_HDG, new UDLHDGAggregatorService());
		
		
		put(CpdlcOutputConstants.UPLINK_CPDLC_SPEED, new UDLSPEEDAggregatorService());
		
		
		put(CpdlcOutputConstants.UPLINK_CPDLC_CONTACT, new UDLCONTACTAggregatorService());
		
		
		put(CpdlcOutputConstants.UPLINK_CPDLC_SQUAWK, new UDLSQUAWKAggregatorService());
		
	}
	
	/**
	 * Adds the service ext.
	 *
	 * @param key the key
	 * @param service the service
	 */
	public void addServiceExt(final String key, final IAggregatorService service) {
		put(key, service);
		ServiceExecuter.getInstance().RegisterService(service);
	}
}



