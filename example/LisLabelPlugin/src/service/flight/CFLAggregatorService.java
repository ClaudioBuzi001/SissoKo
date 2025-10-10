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

/**
 * The Class CFLAggregatorService.
 */
public class CFLAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		final int cfl = inputJson.getSafeInt("CFL", -1);

		final String template = inputJson.getSafeString("TEMPLATE_STRING", "");

		final boolean cflWarningFlag = inputJson.getSafeBoolean(FlightInputConstants.CFL_WARNING_FLAG);
		boolean cflWarColor = false;
		int lev = -1;
		int roc = -1;
		String color = "";
		String numBatSal = "OFF";
		final Optional<IRawData> jsonTrack = Utils_LIS.getTrkFromFn(inputJson.getId());
		if (jsonTrack.isPresent()) {
			lev = jsonTrack.get().getSafeInt(TrackInputConstants.LEV, -1);
			roc = jsonTrack.get().getSafeInt(TrackInputConstants.ROCD, -1);
			numBatSal = jsonTrack.get().getAuxiliaryData().getSafeString("salFrame", "OFF");
		}
		if (lev != cfl) {

			if (cflWarningFlag) {

				if (roc != -1) {
					final int cmh = lev;
					final int cflDeviationThreshold = inputJson.getSafeInt("CFL_DEV_THRESHOLD", 0);
					if (cflDeviationThreshold != 65535) {
						if (roc < -4) {
							if (cmh < cfl - cflDeviationThreshold) {
								cflWarColor = true;
							}
						} else if (roc > 4) {
							if (cmh > cfl + cflDeviationThreshold) {
								cflWarColor = true;
							}
						} else {
							if ((cmh < cfl - cflDeviationThreshold) || (cmh > cfl + cflDeviationThreshold)) {
								cflWarColor = true;
							}
						}
					}

					if (cflWarColor && template.contains("CONTROLLED")) {

						color = ColorConstants.CFL_DEVIATION_COLOR;
					}
				}
			}
		}

		String data = "";
		String dataLabel = "";
		String visible = "";

		if (cfl != -1) {
			data = cfl + "";
			dataLabel = cfl + "";
			if (lev != cfl || numBatSal.equals("ON")) {

				visible = "true";
			}
		}

		dataNode.addLine("CFL", data);

		dataNode.addLine("CFL_LABEL", dataLabel).setAttribute(EdmModelKeys.Attributes.VISIBLE, visible)
				.setAttribute(EdmModelKeys.Attributes.SHOW, (lev != cfl || numBatSal.equals("ON")) ? "" : "optional")
				.setAttribute(EdmModelKeys.Attributes.COLOR, color);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = "CFL";
		return m_serviceName;
	}

}
