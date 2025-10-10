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
import auxiliary.track.TrackOutputConstants;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import com.leonardo.infrastructure.Strings;

/**
 * The Class GNDAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class GNDAggregatorService implements IAggregatorService {

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = TrackOutputConstants.GND;
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
		
		final String value;
		final String valueEcho;
		value = "G" + Strings.right("000" + inputJson.getSafeString(TrackInputConstants.SPD_MOD), 3);
		
		valueEcho = "G" + Strings.right("0000" + inputJson.getSafeString(TrackInputConstants.SPD_MOD), 4);
		
		dataNode.addLine(TrackInputConstants.SPEED, value);
		
		dataNode.addLine("SPEED_ECHO", valueEcho);

    }
}
