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

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxiliary.cpdlc.CpdlcInputConstants;
import auxiliary.cpdlc.CpdlcOutputConstants;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import common.ColorConstants;

/**
 * The Class DOWNLINK DATALINK MESSAGE AggregatorService.
 *
 */
public class DDLMSGAggregatorService implements IAggregatorService {

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = CpdlcOutputConstants.DOWNLINK_CPDLC_MSG;
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
		String CPDLC_RQ_value = "";
		String CPDLC_RQ_color = "";
		String CPDLC_RQ_blink = "";
		String CPDLC_RQ_DD_value = "";
		String CPDLC_RQ_DD_color = "";

		final String SCDDS = inputJson.getSafeString(CpdlcInputConstants.CPDLC_DOWNLINK_SPEED_DIALOGUE_STATUS);
		final String RMDDS = inputJson.getSafeString(CpdlcInputConstants.CPDLC_DOWNLINK_ROUTE_DIALOGUE_STATUS);
		final String VCDDS = inputJson.getSafeString(CpdlcInputConstants.CPDLC_DOWNLINK_VERTICAL_DIALOGUE_STATUS);
		final String templateStr = inputJson.getSafeString(FlightInputConstants.TEMPLATE_STRING);

		final var cpdlc_map_downlink = BlackBoardUtility.getDataOpt(DataType.BB_FLIGHT_CPDLC_DOWNLINK.name(),
				inputJson.getId());
		if (cpdlc_map_downlink.isPresent()) {
			String fieldValue = "";
			String valueOUT = "";
			String valueColor = "";
			String modeBlink = "OFF";
			final var cpdlcArray = cpdlc_map_downlink.get().getSafeRawDataArray("CPDLC_LIST");

			for (final com.gifork.commons.data.IRawDataElement jCpdlcDown : cpdlcArray) {
				final String fieldName = jCpdlcDown.getSafeString("CPDLC_RQ");

				switch (fieldName) {

				case "LEVEL":
					fieldValue = VCDDS;
					break;
				case "SPD":
					fieldValue = SCDDS;
					break;
				case "DCT":
					fieldValue = RMDDS;
					break;
				default:
					break;
				}

				switch (fieldValue) {
				case "1":
					valueOUT = "*";
					valueColor = "#00FFFF";
					break;

				case "2":
					modeBlink = "ON";
					break;

				case "3":
					valueOUT = "||";
					valueColor = "#FFFFFF";
					break;

				case "13":
					valueOUT = "||";
					valueColor = "#FFFFFF";
					modeBlink = "ON";
					break;
				default:
					break;
				}

				switch (fieldName) {
				case "LEVEL":
				case "SPD":
				case "DCT":
					CPDLC_RQ_value = jCpdlcDown.getSafeString("LABEL");
					CPDLC_RQ_color = ColorConstants.GREEN;
					CPDLC_RQ_DD_value = valueOUT;
					CPDLC_RQ_DD_color = valueColor;
					CPDLC_RQ_blink = modeBlink;
					break;

				default:
					break;
				}
			}
		}

		String show = (!CPDLC_RQ_value.isEmpty() && templateStr.contains(FlightOutputConstants.TEMPLATE_CONTROLLED))
				? "forced"
				: "";

		dataNode.addLine(CpdlcOutputConstants.DOWNLINK_CPDLC_SYMBOL, CPDLC_RQ_DD_value)
				.setAttribute(EdmModelKeys.Attributes.COLOR, CPDLC_RQ_DD_color)
				.setAttribute(EdmModelKeys.Attributes.SHOW, show);

		dataNode.addLine(CpdlcOutputConstants.DOWNLINK_CPDLC_MSG, CPDLC_RQ_value)
				.setAttribute(EdmModelKeys.Attributes.COLOR, CPDLC_RQ_color)
				.setAttribute(EdmModelKeys.Attributes.MOD_BLINK, CPDLC_RQ_blink)
				.setAttribute(EdmModelKeys.Attributes.SHOW, show);

	}
}
