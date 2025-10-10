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
package service.cpdlc;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxiliary.cpdlc.CpdlcInputConstants;
import auxiliary.cpdlc.CpdlcOutputConstants;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;

/**
 * The Class UPLINK DATALINK SQUAWK AggregatorService.
 *
 */
public class UDLSQUAWKAggregatorService implements IAggregatorService {

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = CpdlcOutputConstants.UPLINK_CPDLC_SQUAWK;
		return m_serviceName;
	}

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		String RQ_CPDLC_SQUAWK_VALUE = "";
		String RQ_CPDLC_SQUAWK_COLOR = "";
		String show;

		final String CMUDS = inputJson.getSafeString(CpdlcInputConstants.CPDLC_UPLINK_SURV_DIALOGUE_STATUS);
		final String templateStr = inputJson.getSafeString(FlightInputConstants.TEMPLATE_STRING);

		final var cpdlc_map_UpLink = BlackBoardUtility.getDataOpt(DataType.BB_FLIGHT_CPDLC_UPLINK.name(),
				inputJson.getId());
		if (cpdlc_map_UpLink.isPresent()) {
			final var cpdlcArray = cpdlc_map_UpLink.get().getSafeRawDataArray("CPDLC_LIST");
			String valueOUT = "";
			String valueColor = "";
			show = "";
			for (int ind = 0; ind < cpdlcArray.size() && show.isEmpty(); ind++) {
				IRawDataElement jCpdlcUp = cpdlcArray.get(ind);
				final String fieldName = jCpdlcUp.getSafeString("CPDLC_RQ");

				if (fieldName.equals(CpdlcOutputConstants.UPLINK_CPDLC_SQUAWK)) {

					switch (CMUDS) {
					case "0":
					case "2":
						valueOUT = ">";
						valueColor = "#FFFFFF";
						break;

					case "1":
						valueOUT = "*";
						valueColor = "#00FFFF";
						break;

					case "3":
					case "13":
						valueOUT = "||";
						valueColor = "#FFFFFF";
						break;

					case "4":
					case "18":
					case "19":
						valueOUT = "x";
						valueColor = "#FFFF00";
						break;

					/*
					 * case "5": valueOUT= "*"; valueColor = "#00FFFF";
					 */

					case "6":
					case "8":
					case "9":
					case "16":
					case "20":
					case "7":
						valueOUT = "#";
						valueColor = "#FFA500";
						break;

					case "21":
						valueOUT = "?";
						valueColor = "#FFFFFF";
						break;
					default:
						break;
					}
				}
				RQ_CPDLC_SQUAWK_VALUE = valueOUT;
				RQ_CPDLC_SQUAWK_COLOR = valueColor;
			}
		}

		show = (!RQ_CPDLC_SQUAWK_VALUE.isEmpty() && templateStr.contains(FlightOutputConstants.TEMPLATE_CONTROLLED))
				? "forced"
				: "";

		dataNode.addLine(CpdlcOutputConstants.UPLINK_CPDLC_SQUAWK, RQ_CPDLC_SQUAWK_VALUE)
				.setAttribute(EdmModelKeys.Attributes.COLOR, RQ_CPDLC_SQUAWK_COLOR)
				.setAttribute(EdmModelKeys.Attributes.SHOW, show);
	}
}
