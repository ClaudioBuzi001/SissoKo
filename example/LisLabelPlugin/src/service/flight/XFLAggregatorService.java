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
 * The Class XFLAggregatorService.
 *
 * @author esegato
 */
public class XFLAggregatorService implements IAggregatorService {

	/** The Constant callBackXFL. */
	private static final String callBackXFL = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=XFL,    OBJECT_TYPE=XFL,     PREVIEW_NAME=choiceXFL   )";

	/** The Constant callBackCTP. */
	private static final String callBackCTP = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=CTPXFL, OBJECT_TYPE=CTPXFL,  PREVIEW_NAME=choiceCTPXFL, DATA={'ISHOOK':'track'})";

	/** The Constant callBackACP. */
	private static final String callBackACP = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=ACPXFL, OBJECT_TYPE=XFL,     PREVIEW_NAME=choiceACPXFL, DATA={'ISHOOK':'track'})";

	/** The Constant callBackPCO. */

	private static final String callBackPCO = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=PCO,    OBJECT_TYPE=PCO,     PREVIEW_NAME=previewPCO,   DATA={'ISHOOK':'track'})";

	/** The Constant callBackFEL. */
	private static final String callBackFEL = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=FEL,    OBJECT_TYPE=FEL,     PREVIEW_NAME=choiceFEL,     DATA={'ISHOOK':'track'})";

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		String xfl = inputJson.getSafeString(FlightInputConstants.XFL);
		final String xfldrawing = inputJson.getSafeString(FlightInputConstants.XFLDRAWING);
		final String flightNum = inputJson.getSafeString(FlightInputConstants.FLIGHT_NUM);
		final boolean warningTrans = inputJson.getSafeBoolean(FlightInputConstants.COORDTRANS);
		String color = "";
		String show = "";
		final String RJC = inputJson.getSafeString("RJC");
		final String mesTraWarning = inputJson.getSafeString("MES_TRA_WARN");
		final String mesTraColor = inputJson.getSafeString("MES_TRA_COLOR");
		final String mesTra = inputJson.getSafeString("MES_TRA");
		final boolean flag_Coord_XFL = inputJson.getSafeBoolean("IS_COORD_XFL");
		String callBack = "";		
		String colorDefault = "";

		final String imLastSector = inputJson.getSafeString("IAMLASTSECTOR");
		String cfl = inputJson.getSafeString(FlightInputConstants.CFL);
		final String INITCOORDSECTOR = Utils.getInitCoordSector(flightNum + "_" + "XFL");

		if (Generics.isOneOf(xfl, "", FlightInputConstants.XFL_0, FlightInputConstants.XFL_NULL,
				FlightInputConstants.XFL_XFL)) {

			xfl = FlightOutputConstants.XFL_XFL;
			callBack = callBackXFL;
		} else {

			if (!xfl.equals(cfl) || mesTraWarning.equals("PCO")) {
				show = EdmModelKeys.Attributes.Show.FORCED;
			}
			if (mesTraWarning.equals("PCO") && imLastSector.equalsIgnoreCase("true")) {
				if (mesTraColor.equals("1")) {
					color = ColorConstants.CYAN;
					callBack = callBackFEL;
				} else {
					color = ColorConstants.ORANGE;
					callBack = callBackPCO;
				}
			} else {
				switch (xfldrawing) {
				case CommonConstants.COLOR_DEFAULT:
					color = colorDefault;
					callBack = callBackXFL;
					break;
				case CommonConstants.COLOR_CYAN:
					color = ColorConstants.CYAN;
					if (warningTrans) {
						color = ColorConstants.ORANGE;
					}
					callBack = "";
					break;
				case CommonConstants.COLOR_GREEN:
					color = ColorConstants.MAGENTA;
					if (INITCOORDSECTOR.equals("DOWNSTREAM")) {
						callBack = callBackCTP;
					} else if (INITCOORDSECTOR.equals("UPSTREAM")) {
						callBack = callBackACP;
					}
					break;
				case CommonConstants.COLOR_ORANGE:
					color = ColorConstants.ORANGE;
					callBack = callBackXFL;
					break;
				case CommonConstants.COLOR_YELLOW:
					color = ColorConstants.YELLOW;
					callBack = "";
					break;
				}

			}
		}

		if (!xfl.isEmpty() && !xfl.equals(FlightInputConstants.XFL_0) && !xfl.equals(FlightInputConstants.XFL_NULL)
				&& !xfl.equals(FlightInputConstants.XFL_XFL)) {
			if (mesTra.equals("CDN")) {
				if (mesTraColor.equals("2")) {
					color = ColorConstants.GREEN;
					callBack = callBackACP;
				}
			}
			if (mesTra.equals("RJC")) {
				if (mesTraColor.equals("3")) {
					color = ColorConstants.ORANGE;
				}
			}
			if (RJC.equals("RJC") && flag_Coord_XFL) {
				color = ColorConstants.ORANGE;
			}
		}

		dataNode.addLine(FlightOutputConstants.XFL, xfl)
				.setAttributeIf(!color.isEmpty(), EdmModelKeys.Attributes.COLOR, color)
				.setAttributeIf(!show.isEmpty(), EdmModelKeys.Attributes.SHOW, show)
				.setAttributeIf(!callBack.isEmpty(), EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, callBack);

		// Set the FRAME_XFL field based on the ASAP order status
		// TODO: fields marked as "_BV4" will be moved to a dedicated GiFork plugin in the future
		// TODO {VALERIO}: Make the XFL field show a frame when the ASAP XFL order is active
		String xflFrameState = "OFF";
		if (!xfl.isEmpty() && inputJson.getSafeBoolean(FlightInputConstants.ASAP_XFL_ORDER)) {
			xflFrameState = "ON";
		}
		dataNode.addLine(FlightOutputConstants.FRAME_XFL_BV4, xflFrameState);
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = FlightOutputConstants.XFL;
		return m_serviceName;
	}

}
