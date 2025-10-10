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
package service.track;

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.track.TrackInputConstants;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

/**
 * The Class VYAggregatorService.
 *
 * @version $Revision$
 */
public class VYAggregatorService implements IAggregatorService {

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = TrackInputConstants.VY;
		return m_serviceName;
	}

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		dataNode.addLine(TrackInputConstants.VY, ""+inputJson.getSafeDouble(TrackInputConstants.VY, 0)* 3600);
	}

}
