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

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightInputConstants;
import auxiliary.track.TrackInputConstants;
import common.ColorConstants;
import common.CommonConstants;

/**
 * @author LATORREM
 *
 */
public class EMERGENCY_RPASAggregatorService implements IAggregatorService {

	@Override
	public String getServiceName() {
		/** The service name. */
		return FlightInputConstants.EMERGENCY_RPAS;
	}

	
	@Override
	public void aggregate(IRawData inputJson, HeaderNode dataNode) {
		String emergencyRPAS = "";
		String color = "";

		String FLIGHT_NUM = inputJson.getSafeString(CommonConstants.FLIGHT_NUM);
		final Optional<IRawData> jsonTrack = Utils_LIS.getTrkFromFn(FLIGHT_NUM);

		if (jsonTrack.isPresent()) {

			boolean c2Loss = false;
			boolean daaLoss = false;
			boolean c2Loss_fromMode3A = false;

			boolean rstValidity = jsonTrack.get().getSafeBoolean(TrackInputConstants.RST_VALIDITY);
			if (rstValidity) {
				c2Loss = rstValidity && jsonTrack.get().getSafeBoolean(TrackInputConstants.C2L);
				daaLoss = rstValidity && jsonTrack.get().getSafeBoolean(TrackInputConstants.EMG_RPAS_DAAL);
			}

			String track_mode_3A = jsonTrack.get().getSafeString(TrackInputConstants.MODE_3A);
			if (track_mode_3A != null) {
				if (track_mode_3A.equals("A7400")) {
					c2Loss_fromMode3A = true;
				}
			}

			if (c2Loss || c2Loss_fromMode3A) {
				emergencyRPAS = TrackInputConstants.C2L;
			} else if (daaLoss) {
				emergencyRPAS = TrackInputConstants.EMG_RPAS_DAAL;
				color = ColorConstants.CALLSIGN_WARNING_COLOR;
			}
		}

		dataNode.addLine("EMERGENCY_RPAS", emergencyRPAS)
			.setAttribute(EdmModelKeys.Attributes.COLOR, color);

	}
}
