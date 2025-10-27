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
package serviceQatar.flight;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardUtility_LIS;
import auxiliaryQatar.QatarInputConstants;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;



/**
 * The Class NXSQatarAggregatorService.
 *
 * @author latorrem
 */
public class NXSQatarAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final int nextSector = inputJson.getSafeInt(QatarInputConstants.NXS, 0);
		final String visible = "true";
		String dataNxs;

		if (nextSector > 2) {
			dataNxs = BlackBoardUtility_LIS.searchSECTOR_NAME(nextSector + "");
		} else {
			dataNxs = inputJson.getSafeString("NEXT_FIR");
		}

		if (dataNxs.isEmpty()) {
			dataNxs = QatarInputConstants.NXS;
		}
		dataNode.addLine(QatarInputConstants.NXSNAME, dataNxs).setAttribute(EdmModelKeys.Attributes.VISIBLE, visible);
	}

	

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = QatarInputConstants.NXSNAME;
		return service_name;
	}

}
