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
package services;

import application.pluginService.ServiceExecuter.IFunctionalService;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

/**
 * The Class ServiceAckColor.
 */
public class ServiceAckColor implements IFunctionalService {

	/** The Color. */
	private String Color= "#FF0000";


	/**
	 * Execute.
	 *
	 * @param parameters the parameters
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData parameters)
	{
		

		final IRawDataElement auxData = parameters.getAuxiliaryData();
		final boolean ackVal = auxData.getSafeBoolean("ACK" , false);
		if (ackVal)
			Color = "#CECECE";
		
		return Color;
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The My name. */
		String myName = "ACK_COLOR";
		return myName;
	}
}
