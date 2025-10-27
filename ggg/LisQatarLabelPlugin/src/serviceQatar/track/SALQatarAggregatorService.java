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
package serviceQatar.track;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.Utils_LIS;
import auxiliary.track.TrackInputConstants;
import auxiliary.track.TrackOutputConstants;

/**
 * The Class SALAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class SALQatarAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		String salKey = TrackInputConstants.SAL;
		String salData = inputJson.getSafeString(TrackInputConstants.SAL);
		String visible = "";
		String show = "";

		String FLIGHT_CFL = "";

		if (salData.equals(TrackInputConstants.SAL_0)) {
			salKey = TrackOutputConstants.SAL;
			salData = TrackOutputConstants.SAL_0;
		}

		final var jsonFlight = Utils_LIS.getFlightFromTrkId(inputJson.getId());
		if (jsonFlight.isPresent()) {
			FLIGHT_CFL = jsonFlight.get().getSafeString("CFL");
		}

		if (!salData.isEmpty() && FLIGHT_CFL.isEmpty()) {
			visible = "true";
			show = "forced";
		}

		dataNode.addLine(salKey, salData).setAttribute(EdmModelKeys.Attributes.VISIBLE, visible)
				.setAttribute(EdmModelKeys.Attributes.SHOW, show);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = TrackOutputConstants.SAL;
		return m_serviceName;
	}

}
