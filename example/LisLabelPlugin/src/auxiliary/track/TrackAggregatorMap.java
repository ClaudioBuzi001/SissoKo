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

import application.pluginService.ServiceExecuter.IAggregatorService;
import application.pluginService.ServiceExecuter.ServiceExecuter;
import common.AggregatorMap;
import service.track.*;

/**
 * The Class TrackAggregatorMap.
 */
public class TrackAggregatorMap extends AggregatorMap {

	/**
	 * Instantiates a new track aggregator map.
	 */
	
	public TrackAggregatorMap() {
		
		put(TrackOutputConstants.IS_ADSC_TRACKER, new ADSCAggregatorService());

		
		put(TrackOutputConstants.GND, new GNDAggregatorService());

		
		put(TrackOutputConstants.LEV, new LEVAggregatorService());

		
		put(TrackOutputConstants.SAL, new SALAggregatorService());

		
		put(TrackInputConstants.VR, new VRAggregatorService());

		
		put(TrackInputConstants.MGN, new MGNAggregatorService());

		
		put(TrackInputConstants.IAS, new IASAggregatorService());

		
		put(TrackInputConstants.MODE_4, new MODE4AggregatorService());

		
		put(TrackInputConstants.ROCD, new ROCDAggregatorService());

		
		put(TrackInputConstants.VX, new VXAggregatorService());

		
		put(TrackInputConstants.VY, new VYAggregatorService());

		
		put(TrackOutputConstants.IS_EMG, new EMGAggregatorService());

		
		put(TrackInputConstants.TCAS, new TCASAggregatorService());
		
		
		put(TrackInputConstants.RCS, new RCSAggregatorService());

	}

	/**
	 * Adds the service ext.
	 *
	 * @param key     the key
	 * @param service the service
	 */
	public void addServiceExt(final String key, final IAggregatorService service) {
		put(key, service);
		ServiceExecuter.getInstance().RegisterService(service);
	}

}
