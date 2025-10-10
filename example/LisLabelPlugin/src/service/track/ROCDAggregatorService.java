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
import com.gifork.auxiliary.SafeDataRetriever;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightInputConstants;

/**
 * The Class ROCDAggregatorService.
 */
public class ROCDAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		final String dataType = "r";
		String data;
		String ROCD_FRAME = "OFF";
		String FLIGHT_DATA = "";
		String dataArrow = "";
		String TRACK_DATA;
		String visible = "";

		if (inputJson.getSafeString("ROCD").trim().isEmpty() || inputJson.getSafeString("ROCD").equals("0")) {
			data = "00";
		} else {
			data = inputJson.getSafeString("ROCD");
			final int dataInt = Integer.parseInt(data);
			if (Math.negateExact(dataInt) > 0) {
				dataArrow = "2";
				data = String.valueOf(dataInt * -1);
			} else if (Math.abs(dataInt) > 0) {
				dataArrow = "1";
			}
			if (Math.abs(dataInt) < 10) {
				data = "0" + data;
			}
		}

		Optional<IRawData> jsonFlight = Utils_LIS.getFlightFromTrkId(inputJson.getId());

		
		if (inputJson.getSafeString("VR").trim().isEmpty() || inputJson.getSafeString("VR").equals("0")) {
			TRACK_DATA = "0";
		}else{
			TRACK_DATA = inputJson.getSafeString("VR");	
		}
		
		if (jsonFlight.isPresent()) {
			FLIGHT_DATA = jsonFlight.get().getSafeString("ARC");
			final IRawDataElement auxiliaryData = jsonFlight.get().getAuxiliaryData();

			/** The num battute. */
			int numBattute = 0;
			if (!FLIGHT_DATA.isEmpty()) {
				final Double flightData = SafeDataRetriever.strToDouble(FLIGHT_DATA);
				final double trackData = Double.parseDouble(TRACK_DATA);
				final var deviation = BlackBoardUtility.getDataOpt(DataType.PRELOADED_DEVIATION_DOWNLINKED.name());
				final Double sogliaDeviation = deviation.isPresent()
						? deviation.get().getSafeDouble("VR_ARC_DEVIATION", 0)
						: 0;
				final double maxNumeroBattute = deviation.isPresent()
						? deviation.get().getSafeDouble("VR_ARC_MAX_TICK", 0)
						: 0;

				numBattute = auxiliaryData.getSafeInt("numBattuteVR", 0);

				if (trackData > flightData + sogliaDeviation || trackData < flightData - sogliaDeviation) {

					if (numBattute > maxNumeroBattute) {
						ROCD_FRAME = "ON";
					}
					numBattute++;
				} else {
					numBattute = 0;
				}
			}
			auxiliaryData.put("numBattuteVR", numBattute);
		}
		if ((FLIGHT_DATA.isEmpty() || FLIGHT_DATA.equals("0")) && (!data.equals("00"))) {
			visible = "true";
		} else if ((FLIGHT_DATA.isEmpty() || FLIGHT_DATA.equals("0"))
				&& (TRACK_DATA.isEmpty() || TRACK_DATA.equals("0")) && (data.equals("00"))) {
			visible = "true";
		}

		dataNode.addLine(FlightInputConstants.ARC_ARROWR, dataArrow).setAttribute(EdmModelKeys.Attributes.VISIBLE,
				(!dataArrow.isEmpty()));

		dataNode.addLine("ROCD", dataType + data).setAttribute(EdmModelKeys.Attributes.VISIBLE, visible);

		String show = "";

		if (ROCD_FRAME.equals("ON")) {
			show = "forced";
		}

		dataNode.addLine("FRAME_VR", ROCD_FRAME).setAttribute(EdmModelKeys.Attributes.SHOW, show);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = "ROCD";
		return m_serviceName;
	}

}
