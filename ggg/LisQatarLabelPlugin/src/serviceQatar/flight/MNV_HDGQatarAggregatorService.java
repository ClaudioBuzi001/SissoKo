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

import java.util.Optional;

import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.auxiliary.SafeDataRetriever;
import com.gifork.commons.data.IRawData;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightOutputConstants;

/**
 * The Class MNV_HDGQatarAggregatorService.
 */
public class MNV_HDGQatarAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		String data = "AHDG";

		String FLIGHT_DATA_HDG = inputJson.getSafeString("MNV_HDG");
		final String AHDG = inputJson.getSafeString("AHDG");
		final int MNV_TYPE = inputJson.getSafeInt("MNV_TYPE", 0);
		final int HDM = inputJson.getSafeInt("HDM", 0);

		double flightData = SafeDataRetriever.strToDouble(FLIGHT_DATA_HDG);
		final double flightAhdg = SafeDataRetriever.strToDouble(AHDG);
		final String FLIGHT_DATA_DPN = inputJson.getSafeString("MNV_DPN");

		if ((MNV_TYPE == 2 || MNV_TYPE == 4) && (HDM == 4 || HDM == 5)) {
			Optional<IRawData> trk = Utils_LIS.getTrkFromFn(inputJson.getId());
			if (trk.isPresent()) {
				final int MGN_TRACK = trk.get().getSafeInt("MGN", 0);
				final double MaxHdg = 359.0;
				if (HDM == 4) { // right Heading Modality
					flightData = (flightData + MGN_TRACK) % MaxHdg;
				} else if (HDM == 5) { // left Heading Modality
					flightData = (flightData - MGN_TRACK) % MaxHdg;
					if (flightData != Math.abs(flightData)) {
						flightData = (flightData * -1);
					}
				}
				FLIGHT_DATA_HDG = String.valueOf((int) flightData);
			}
		}

		if (MNV_TYPE != 0) {
			// Manouvre Type: No manouvre = 0; DCT = 1; OHD = 2; CHD = 3; RHD = 4
			if (MNV_TYPE == 1) {
				data = FLIGHT_DATA_DPN;
			} else if (MNV_TYPE == 2 || MNV_TYPE == 4) {
				if (flightData == 0.00) {
					data = FLIGHT_DATA_HDG;
				} else if (flightData < 10) {
					data = "H00" + FLIGHT_DATA_HDG;
				} else if (flightData < 100) {
					data = "H0" + FLIGHT_DATA_HDG;
				} else {
					data = "H" + FLIGHT_DATA_HDG;
				}
			} else if (MNV_TYPE == 3) {
				data = "CLHDG";
			}
		} else if (!AHDG.equals("0")) {
			if (flightAhdg < 10) {
				data = "H00" + AHDG;
			} else if (flightAhdg < 100) {
				data = "H0" + AHDG;
			} else {
				data = "H" + AHDG;
			}

		} else {
			data = "AHDG";
		}

		dataNode.addLine(FlightOutputConstants.MVN_SECTION_VALUE, data);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = "MNV_HDG";
		return service_name;
	}

}
