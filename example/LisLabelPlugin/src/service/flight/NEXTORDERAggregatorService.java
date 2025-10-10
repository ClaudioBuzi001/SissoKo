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
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

/**
 * The Class NEXTORDERAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class NEXTORDERAggregatorService implements IAggregatorService {

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = FlightInputConstants.NEXTORDER;
		return m_serviceName;
	}

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final var nextOrder = inputJson.getSafeString(FlightInputConstants.NEXTORDER);
		String data = nextOrder;
		String leftMouseCallback = "";

		switch (nextOrder) {
		case "UPB":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=UPB, OBJECT_TYPE=UPB,PREVIEW_NAME=quickUPB)";
			break;
		case "UST":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=UST, OBJECT_TYPE=UST,PREVIEW_NAME=quickUST)";
			break;
		case "ULG":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=ULG, OBJECT_TYPE=ULG,PREVIEW_NAME=quickULG)";
			break;
		case "CTL":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=CTL, OBJECT_TYPE=CTL,PREVIEW_NAME=quickCTL)";
			break;
		case "UCL":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=UCL, OBJECT_TYPE=UCL,PREVIEW_NAME=quickUCL)";
			break;
		case "UCT":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=UCT, OBJECT_TYPE=UCT,PREVIEW_NAME=quickUCT)";
			break;
		case "CFR":
			leftMouseCallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=CFR, OBJECT_TYPE=CFR,PREVIEW_NAME=previewCFR)";
			break;
		case "RPR":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=RPR, OBJECT_TYPE=RPR,PREVIEW_NAME=quickRPR)";
			break;
		case "UIR":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=UIR, OBJECT_TYPE=UIR,PREVIEW_NAME=quickUIR)";
			break;
		case "UEP":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=UEP, OBJECT_TYPE=UEP,PREVIEW_NAME=quickUEP)";
			break;
		case "EPB":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=EPB, OBJECT_TYPE=EPB,PREVIEW_NAME=quickEPB)";
			break;
		case "FCN":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=FCN, OBJECT_TYPE=FCN,PREVIEW_NAME=quickFCN)";
			break;
		case "ICA":
			leftMouseCallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW,ORDER_ID=ICA , OBJECT_TYPE=ICA, PREVIEW_NAME=previewICA)";
			break;
		case "CSR":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=CSR, OBJECT_TYPE=CSR,PREVIEW_NAME=quickCSR)";
			break;
		case "AOC":
			data = nextOrder;
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=AOC, OBJECT_TYPE=AOC,PREVIEW_NAME=quickAOC)";
			break;
		case "DCL":
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=DCL, OBJECT_TYPE=DCL,PREVIEW_NAME=quickDCL)";
			break;
		case "LND":
			leftMouseCallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW,ORDER_ID=LND, OBJECT_TYPE=LND,PREVIEW_NAME=previewLND)";
			break;
		case "SCA":
			data = "SUP";
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=SCA, OBJECT_TYPE=SCA,PREVIEW_NAME=quickSCA)";
			break;
		case "PBC":
			data = "PSH";
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=PBC, OBJECT_TYPE=PBC,PREVIEW_NAME=quickPBC)";
			break;
		case "LUG":
			data = "LUP";
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=LUG, OBJECT_TYPE=LUG,PREVIEW_NAME=quickLUG)";
			break;
		case "CTO":
			data = "TOC";
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=CTO, OBJECT_TYPE=CTO,PREVIEW_NAME=quickCTO)";
			break;
		case "TRC":
			data = "TAX";
			leftMouseCallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=GROUND_TAXI_TO_CLEARANCE, ORDER_ID=TRC, OBJECT_TYPE=TRC,PREVIEW_NAME=quickTRC)";
			break;
		case "CRC":
			data = "CRS";
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=CRC ,OBJECT_TYPE=CRC,PREVIEW_NAME=quickCRC)";
			break;
		case "TOF":
			data = "TRF";
			leftMouseCallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW, ORDER_ID=TOF, OBJECT_TYPE=TOF,PREVIEW_NAME=choiceTOF)";
			break;
		case "TKF":
			data = "TOR";
			leftMouseCallback = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW,ORDER_ID=TKF, OBJECT_TYPE=TKF,PREVIEW_NAME=previewTKF)";
			break;
		case "CRS":
			data = "CRS";
			leftMouseCallback = "EXTERNAL_ORDER(ORDER_ID=CRC, OBJECT_TYPE=CRC,PREVIEW_NAME=quickCRC)";
			break;
		default:
			data = "";	
		}
		
		dataNode.addLine("NEXT", data).setAttributeIf(!data.isEmpty(), EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, leftMouseCallback);
    }
}
