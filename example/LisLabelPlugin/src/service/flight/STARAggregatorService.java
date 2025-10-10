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

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.auxiliary.ColorGeneric;
import com.gifork.commons.data.IRawData;

/**
 * The Class STARAggregatorService.
 *
 *
 */
public class STARAggregatorService implements IAggregatorService {

	/** The Constant callBackCSA. */
	private static final String callBackCSA = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=CSA, OBJECT_TYPE=CSA, PREVIEW_NAME=previewCSA)";

	/** The Constant callBackSAC. */
	private static final String callBackSAC = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=SAC, OBJECT_TYPE=SAC,	PREVIEW_NAME=previewSAC)";

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		String starData="";
		String color="";
		String callBackLeft="";
		String callBackRight="";

		boolean flagVisStar=inputJson.getSafeBoolean(FlightInputConstants.FLAG_VIS_STAR);
		boolean flagASCL =inputJson.getSafeBoolean(FlightInputConstants.FLAG_ASCL);

		/***if (flagVisStar && !inputJson.getSafeString(FlightOutputConstants.STAR).trim().isEmpty()) {
		 * per ora valutiamo di presentarla indipendentemente se vuota
		 * */

		if (flagVisStar) {
			starData = inputJson.getSafeString(FlightInputConstants.STAR).trim();

			callBackLeft=callBackCSA;
			callBackRight=callBackSAC;

			if (flagASCL) {
				color = ColorGeneric.getInstance().getColor("STAR_ASCL");
			}
		}

		dataNode.addLine(FlightOutputConstants.STAR_BV4, starData)
				.setAttribute(EdmModelKeys.Attributes.COLOR, color)
				.setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, callBackLeft)
				.setAttribute(EdmModelKeys.Attributes.RIGHT_MOUSE_CALLBACK, callBackRight);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = "STAR_BV4";
		return m_serviceName;
	}
}
