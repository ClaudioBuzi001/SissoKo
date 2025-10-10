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

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.track.TrackInputConstants;
import auxiliary.track.TrackOutputConstants;

/**
 * The Class SALAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class SALAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final int trackData = inputJson.getSafeInt(TrackInputConstants.SAL);
		String salFrame = "OFF";

		int flightCfl=0;

		Optional<IRawData> jsonFlight = Utils_LIS.getFlightFromTrkId(inputJson.getId());
		if (jsonFlight.isPresent()) {
			flightCfl = jsonFlight.get().getSafeInt("CFL");
		}
		
		final var deviation = BlackBoardUtility.getDataOpt(DataType.PRELOADED_DEVIATION_DOWNLINKED.name());
		int sogliaDeviation = 0;
		int maxNumeroBattute = 0;
		if (deviation.isPresent()) {
			sogliaDeviation = deviation.get().getSafeInt("SAL_CFL_DEVIATION");
			maxNumeroBattute = deviation.get().getSafeInt("SAL_CFL_MAX_TICK");
		}

		final IRawDataElement auxiliaryData = inputJson.getAuxiliaryData();
		int numBattute = auxiliaryData.getSafeInt("numBattuteSAL", 0);

		if (trackData != 0 && (trackData > flightCfl + sogliaDeviation || trackData < flightCfl - sogliaDeviation)) {
			if (numBattute > maxNumeroBattute) {
				salFrame = "ON";
				auxiliaryData.put("salFrame", "ON");
			}
			numBattute++;
		} else {
			numBattute = 0;
			auxiliaryData.put("salFrame", "OFF");
		}
		auxiliaryData.put("numBattuteSAL", numBattute);

		String strData;
		if(trackData==0)
			strData="";
		else {
			strData=String.valueOf(trackData);
		}
		
		dataNode.addLine("SAL", strData);
		int familyType = -1;
		final var env_OWN = BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name());
		if (env_OWN.isPresent()) {
			familyType = env_OWN.get().getSafeInt("FAMILY");
		}

		if (salFrame.equals("ON") && familyType == 0) {
			salFrame = "OFF";
		}

		dataNode.addLine("FRAME_SAL", salFrame).setAttribute(EdmModelKeys.Attributes.SHOW,
				(salFrame.equals("ON")) ? "forced" : "");

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
