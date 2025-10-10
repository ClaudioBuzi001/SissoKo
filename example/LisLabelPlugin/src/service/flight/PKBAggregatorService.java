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
package service.flight;

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

/**
 * The Class PKBAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class PKBAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final boolean isArr = inputJson.getSafeBoolean(FlightInputConstants.ISARR);
		final boolean isDep = inputJson.getSafeBoolean(FlightInputConstants.ISDEP);
		final String arrPkb = inputJson.getSafeString(FlightInputConstants.ARR_PKBAY);
		final String depPkb = inputJson.getSafeString(FlightInputConstants.DEP_PKBAY);
		String data = FlightOutputConstants.PKB;

		if (isArr && !arrPkb.isEmpty()) {
			data = arrPkb;
		}

		if (isDep && !depPkb.isEmpty()) {
			data = depPkb;
		}
		dataNode.addLine(FlightOutputConstants.PKB, data);
    }

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = FlightOutputConstants.PKB;
		return m_serviceName;
	}

}
