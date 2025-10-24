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
 * The Class ServiceQNHList.
 */
public class ServiceQNHList implements IFunctionalService
{


	/**
	 * Execute.
	 *
	 * @param input the input
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData input)
	{
		String ret_value= "";
		final IRawDataElement parameters = input.getAuxiliaryData();
		/** The field to check1. */
		String FIELD_TO_CHECK1 = parameters.getSafeString("FIELD_TO_CHECK1").replace("..", "");
		/** The val 1. */
		String VAL_1 = parameters.getSafeString("VAL_1");
		/** The val 2. */
		String VAL_2 = parameters.getSafeString("VAL_2");
		/** The val 3. */
		String VAL_3 = parameters.getSafeString("VAL_3");
		/** The val 4. */
		String VAL_4 = parameters.getSafeString("VAL_4");
		/** The val 5. */
		String VAL_5 = parameters.getSafeString("VAL_5");


		/** The ret callback accept reject. */
		String ret_callback_accept_reject = "CONTEXT_MENU(MENU_FILE=QNHAcceptRejectList.xml)";
		/** The ret callback default. */
		String ret_callback_default = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW , ORDER_ID=QNA, OBJECT_TYPE=QNA,  DATA={'IS_LIST':'true'}, PREVIEW_NAME=previewQNA)";
		/** The ret color orange. */
		String ret_color_orange = "#FFA500";
		/** The ret color yellow. */
		String ret_color_yellow = "#FFFF00";
		/** The ret color red. */
		String ret_color_red = "#FF0000";
		/** The ret color default. */
		String ret_color_default = "#00FF00";
		switch (FIELD_TO_CHECK1) {
			case "QNH_VALUE":
				
				
				
				
				final short qnhState = Short.parseShort(VAL_5);
				
				if (qnhState == 3  ||
				    qnhState == 8  ||
					qnhState == 13 )
					ret_value = VAL_3 + '.' + VAL_4;
				else
					ret_value = VAL_1 + '.' + VAL_2;
				break;
				
			case "ISMAIN":
				
				if (Boolean.parseBoolean(VAL_1))
					ret_value="TRUE";
				else
					ret_value="FALSE";
				
				break;
			case "QNH_COLOR":
				
				final short qnhStateColor = Short.parseShort(VAL_1);
				
				if (qnhStateColor == 2  ||
					qnhStateColor == 7  ||
					qnhStateColor == 12 ||
					qnhStateColor == 9  ||
					qnhStateColor == 14 )
					
					ret_value= ret_color_red;
				
				else if (qnhStateColor == 3 ||
						 qnhStateColor == 8 ||
						 qnhStateColor == 13 )
					ret_value= ret_color_yellow;
				
				else if (qnhStateColor == 16 )
				
					ret_value= ret_color_orange;
				else
					ret_value= ret_color_default;
				break;
				

			
				
			case "QNH_CALLBACK":
				
				
				
				
				final short qnhStateCall = Short.parseShort(VAL_1);
				
				if (qnhStateCall == 3  ||
					qnhStateCall == 8  ||    
					qnhStateCall == 13 )
					ret_value = ret_callback_accept_reject;
				else
					ret_value = ret_callback_default;
				break;
				
			default:
				break;
		}
		
		return ret_value;
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName()
	{
		/** The my service. */
		String myService = "QNH_LIST";
		return myService;
	}
	
	
	
	



	
	

	
	
	
}
