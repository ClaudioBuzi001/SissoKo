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

import java.util.Optional;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;
import com.leonardo.infrastructure.Pair;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightInputConstants;
import service.flight.MNV_HDGAggregatorService;

/**
 * The Class MGNAggregatorService.
 */
public class MGNAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final int trackData = inputJson.getSafeInt("MGN");
		String MGN_FRAME = "OFF";
		String callback;
		int flighhtData =0;
		Pair<String, String> callCoord = new Pair<>("", "");

		Optional<IRawData> jsonFlight = Utils_LIS.getFlightFromTrkId(inputJson.getId());
		if (jsonFlight.isPresent()) {
			flighhtData = jsonFlight.get().getSafeInt("MNV_HDG");
			callCoord = MNV_HDGAggregatorService.getHdgCallback(jsonFlight.get().getId(),
					jsonFlight.get().getSafeString(FlightInputConstants.TEMPLATE));

		}

		final var deviation = BlackBoardUtility.getDataOpt(DataType.PRELOADED_DEVIATION_DOWNLINKED.name());
		int sogliaDeviation = 0;
		int maxNumeroBattute = 0;
		if (deviation.isPresent()) {
			sogliaDeviation = deviation.get().getSafeInt("MNV_HDG_MGN_DEVIATION");
			maxNumeroBattute = deviation.get().getSafeInt("MNV_HDG_MGN_MAX_TICK");
		}

		final IRawDataElement auxiliaryData = inputJson.getAuxiliaryData();
		int numBattute = auxiliaryData.getSafeInt("numBattuteMGN", 0);

		if (flighhtData > 0.0) {

			if (trackData != 0
					&& (trackData > flighhtData + sogliaDeviation || trackData < flighhtData - sogliaDeviation)) {
				if (numBattute > maxNumeroBattute) {
					MGN_FRAME = "ON";
				}
				numBattute++;
			} else {
				numBattute = 0;
			}
			auxiliaryData.put("numBattuteMGN", numBattute);
		}

		callback = callCoord.getX();

		String strData;
		if(trackData==0)
			strData="";
		else {
			strData=String.valueOf(trackData);
		}
		dataNode.addLine("MGN", strData).setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, callback);
		dataNode.addLine("FRAME_MGN", MGN_FRAME).setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, callback);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = "MGN";
		return m_serviceName;
	}

}
