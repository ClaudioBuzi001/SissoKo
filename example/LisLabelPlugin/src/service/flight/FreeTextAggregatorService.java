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
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.auxiliary.ColorGeneric;
import com.gifork.commons.data.IRawData;

/**
 * The Class FreeTextAggregatorService.
 */
public class FreeTextAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final String freeTxtLabel = inputJson.getSafeString("FREE_TEXT");
		final String freeTxtEsb = inputJson.getSafeString("NEW_TX");
		
		String color = "";
		String defaultTxt = ColorGeneric.getInstance().getColor("FREE_TEXT_DEFAULT");
		String genericColor = ColorGeneric.getInstance().getColor("FREE_TEXT_COLOR");
		
		
		dataNode.addLine("FREE_TEXT", freeTxtLabel.replace("\\", "\\\\"));
		dataNode.addLine("NEW_TX", freeTxtEsb.replace("\\", "\\\\"));
		
		if (freeTxtLabel.trim().isEmpty()) {
			dataNode.addLine("TXT_LBL", defaultTxt)
				.setAttribute(EdmModelKeys.Attributes.COLOR, color); 
		}else {
			dataNode.addLine("TXT_LBL", freeTxtLabel.replace("\\", "\\\\"))
			.setAttribute(EdmModelKeys.Attributes.COLOR, genericColor); 
		}
		
		if (freeTxtEsb.trim().isEmpty()) {
			dataNode.addLine("TXT_ESB", freeTxtEsb.replace("\\", "\\\\"))
			.setAttribute(EdmModelKeys.Attributes.COLOR, color); 
		}else {
			dataNode.addLine("TXT_ESB", freeTxtEsb.replace("\\", "\\\\"))
			.setAttribute(EdmModelKeys.Attributes.COLOR, genericColor); 
		}
		
    }

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = "FREE_TEXT";
		return service_name;
	}

}
