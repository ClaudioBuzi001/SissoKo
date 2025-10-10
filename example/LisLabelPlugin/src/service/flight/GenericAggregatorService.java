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

import java.util.Optional;

import com.fourflight.WP.ECI.edm.HeaderNode;
import com.fourflight.WP.ECI.edm.EdmModelKeys.TrajectoryPoint;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataArray;
import com.leonardo.infrastructure.Generics;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxiliary.flight.FlightInputConstants;

/**
 * The Class GenericAggregatorService.
 * 
 */
public class GenericAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		final String fn = inputJson.getSafeString(FlightInputConstants.FLIGHT_NUM);
		
		String IFIXNEXT = "";
		String ETOIFIXNEXT = "";
		
		/** GESTIONE CAMPI TRAJETTORIA per ESB***/
		String flightNUM = inputJson.getSafeString("FLIGHT_NUM");
		Optional<IRawData> jsonFlTrnew = BlackBoardUtility.getDataOpt(DataType.FLIGHT_TRJ.name(), flightNUM);

		if (jsonFlTrnew.isPresent()) {
			IRawDataArray rawTrjArra = jsonFlTrnew.get().getSafeRawDataArray("TRJ_ARRAY");
			for (int i = 0; i < rawTrjArra.size(); i++) {
				var elemNext = rawTrjArra.get(i);
				if (elemNext != null) {
					String typePT = elemNext.getSafeString(TrajectoryPoint.PT);
					boolean ovrFly = elemNext.getSafeBoolean(TrajectoryPoint.OVF);
					if (!ovrFly && (Generics.isOneOf(typePT, "F", "G", "X", "I", "J", "K"))) {
						ETOIFIXNEXT = elemNext.getSafeString(TrajectoryPoint.ETO);
						IFIXNEXT = elemNext.getSafeString(TrajectoryPoint.NAME);
						break;
					}
				}
			}
		
		}
		
		dataNode.addLine("IFIXNEXT", IFIXNEXT);
		dataNode.addLine("ETOIFIXNEXT", ETOIFIXNEXT);
		
		dataNode.addLine(FlightInputConstants.FLIGHT_NUM, fn);
    }

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		return FlightInputConstants.FLIGHT_NUM;
	}
}
