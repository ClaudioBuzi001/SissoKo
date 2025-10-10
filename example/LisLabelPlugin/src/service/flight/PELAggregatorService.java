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

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import com.leonardo.infrastructure.Generics;

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import common.ColorConstants;
import common.CommonConstants;
import common.Utils;

/**
 * The Class PELAggregatorService.
 *
 * @author esegato
 */
public class PELAggregatorService implements IAggregatorService {

	/** The Constant callBackPEL. */
	private static final String callBackPEL = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=PEL,    OBJECT_TYPE=PEL,     PREVIEW_NAME=choicePEL   )";

	/** The Constant callBackCTP. */
	private static final String callBackCTP = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=CTPPEL, OBJECT_TYPE=CTPPEL,  PREVIEW_NAME=choiceCTPPEL,DATA={'ISHOOK':'track'})";

	/** The Constant callBackACP. */
	private static final String callBackACP = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPPEL, OBJECT_TYPE=PEL,     PREVIEW_NAME=choiceACPPEL,DATA={'ISHOOK':'track'})";

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		String pel = inputJson.getSafeString(FlightInputConstants.PEL);
		final String peldrawing = inputJson.getSafeString(FlightInputConstants.PELDRAWING_STATUS);
		final boolean warningTrans = inputJson.getSafeBoolean(FlightInputConstants.COORDTRANS);
		final String flightNum = inputJson.getSafeString(FlightInputConstants.FLIGHT_NUM);
		final String RJC = inputJson.getSafeString("RJC");
		final boolean flag_Coord = inputJson.getSafeBoolean("IS_COORD_PEL");

		String color = "";
		String show = "";

		String colorDefault = "";
		String callBack = "";

		final String INITCOORDSECTOR = Utils.getInitCoordSector(flightNum + "_" + "PEL");

		if (Generics.isOneOf(pel, "", FlightInputConstants.PEL_0, FlightInputConstants.PEL_NULL,
				FlightInputConstants.PEL_PEL)) {
			pel = FlightOutputConstants.PEL_PEL;
			callBack = callBackPEL;
		} else {
			show = EdmModelKeys.Attributes.Show.FORCED;

			if (peldrawing.equals(CommonConstants.COLOR_DEFAULT)) {
				color = colorDefault;
				callBack = callBackPEL;
			}
			if (peldrawing.equals(CommonConstants.COLOR_CYAN)) {

				color = ColorConstants.CYAN;
				if (warningTrans) {
					color = ColorConstants.ORANGE;
				}
				callBack = "";
			}
			if (peldrawing.equals(CommonConstants.COLOR_GREEN)) {
				color = ColorConstants.MAGENTA;
				if (warningTrans) {
					color = ColorConstants.ORANGE;
				}
				if (INITCOORDSECTOR.equals("DOWNSTREAM")) {
					callBack = callBackACP;
				} else if (INITCOORDSECTOR.equals("UPSTREAM")) {
					callBack = callBackCTP;
				}
			}

			if (peldrawing.equals(CommonConstants.COLOR_ORANGE)) {
				color = ColorConstants.ORANGE;

			}

			if (peldrawing.equals(CommonConstants.COLOR_YELLOW)) {
				color = ColorConstants.YELLOW;
			}
			if (RJC.equals("RJC") && flag_Coord) {
				color = ColorConstants.ORANGE;
			}
		}

		dataNode.addLine(FlightOutputConstants.PEL, pel)
				.setAttributeIf(!color.isEmpty(), EdmModelKeys.Attributes.COLOR, color)
				.setAttributeIf(!show.isEmpty(), EdmModelKeys.Attributes.SHOW, show)
				.setAttributeIf(!callBack.isEmpty(), EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, callBack);

		// Set the FRAME_PEL field based on the ASAP order status
		// TODO: fields marked as "_BV4" will be moved to a dedicated GiFork plugin in the future
		// TODO {VALERIO}: Make the pel field show a frame when the ASAP order is active
		String pelFrameState = "OFF";
		if (!pel.isEmpty() && false) {
			pelFrameState = "ON";
		}
		dataNode.addLine(FlightOutputConstants.FRAME_PEL_BV4, pelFrameState);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = FlightOutputConstants.PEL;
		return m_serviceName;
	}

}
