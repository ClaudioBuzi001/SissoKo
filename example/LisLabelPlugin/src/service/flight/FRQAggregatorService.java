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
import com.gifork.commons.data.IRawData;
import common.ColorConstants;
import common.Utils;

/**
 * The Class FRQAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class FRQAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
	    final boolean dataWcf = inputJson.getSafeBoolean(FlightInputConstants.WCF, false);
	    final String warningTraWarn = inputJson.getSafeString(FlightInputConstants.MES_TRA_WARN);
        final String warningRev = inputJson.getSafeString(FlightInputConstants.MES_REV);
        final String warningTra = inputJson.getSafeString(FlightInputConstants.MES_TRA);
        String visible = ""; 
        final String dataNff = inputJson.getSafeString(FlightInputConstants.NEXT_FIR_FRQ);
        
        
        final String stringTemplate = inputJson.getSafeString(FlightInputConstants.TEMPLATE_STRING);
        String colorFrq = "";
        

		if (!dataNff.isEmpty()&& !dataNff.equals("0")) {
			
			
			if (dataWcf) { 
				if(Utils.isMySectorByFreq(dataNff)) {
					if(!stringTemplate.contains(FlightOutputConstants.TEMPLATE_CONTROLLED))
						colorFrq = ColorConstants.ORANGE;
				}
				else if(stringTemplate.contains(FlightOutputConstants.TEMPLATE_TENTATIVE))
					colorFrq = ColorConstants.ORANGE;
			}
			else if (stringTemplate.contains(FlightOutputConstants.TEMPLATE_CONTROLLED)) {
				if ((warningTraWarn.equals("ROF") || warningTra.equals("SCO") || warningRev.equals("SDM"))) {
					colorFrq = ColorConstants.GREEN;
				}
			}
		}
		
        if (!colorFrq.isEmpty() || colorFrq.equals(ColorConstants.GREEN) || colorFrq.equals(ColorConstants.ORANGE)) { 
			
			visible = "true";
		} 
		else if  (stringTemplate.contains(FlightOutputConstants.TEMPLATE_HANDOVER_EXITING)) {
			visible = "true";
    	} 
		
		dataNode.addLine(FlightOutputConstants.NFRQ, dataNff)
		.setAttributeIf(!colorFrq.isEmpty(), EdmModelKeys.Attributes.COLOR, colorFrq)
		.setAttribute(EdmModelKeys.Attributes.VISIBLE, visible);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = FlightOutputConstants.FRQ;
		return service_name;
	}

}
