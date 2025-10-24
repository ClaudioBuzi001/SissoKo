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
import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxliary.PluginListConstants;

import java.util.Optional;

import com.gifork.auxiliary.SafeDataRetriever;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;
import com.leonardo.infrastructure.Strings;


/**
 * The Class ServiceSCLListUtility.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public class ServiceSCLListUtility implements IFunctionalService {
	
	/** The val 1. */
	private String VAL_1 = "";
	
	/** The val 2. */
	private String VAL_2 = "";
	
	/** The val 3. */
	private String VAL_3 = "";
	
	/** The val 4. */
	private String VAL_4 = "";
	
	/** The val 5. */
	private String VAL_5 = "";
	
	/** The val 6. */
	private String VAL_6 = "";

	/**
	 * Execute.
	 *
	 * @param input the input
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData input) {
		String result = "";
		final IRawDataElement parameters = input.getAuxiliaryData();
		/** The field to check. */
		
		String FIELD_TO_CHECK = parameters.getSafeString("FIELD_TO_CHECK").replace("..", "");
		this.VAL_1 = parameters.getSafeString("VAL_1");
		this.VAL_2 = parameters.getSafeString("VAL_2");
		this.VAL_3 = parameters.getSafeString("VAL_3");
		this.VAL_4 = parameters.getSafeString("VAL_4");
		this.VAL_5 = parameters.getSafeString("VAL_5");
		this.VAL_6=  Integer.toString(parameters.getSafeInt("VAL_6",0));
		/** The info type. */
		
		String INFO_TYPE = parameters.getSafeString("INFO_TYPE"); 

		if (INFO_TYPE.isEmpty()) {  
			
			if (FIELD_TO_CHECK.equals("DIRECTION"))
				result=calculateHeadingColor();
			if (FIELD_TO_CHECK.equals("PELCOLOR"))
			    result=calcutePELColor(input);
			else if(FIELD_TO_CHECK.equals("XFLCOLOR"))
			    result=calcuteXFLColor(input);
			if (FIELD_TO_CHECK.equals("IFIXCOLOR"))
				result=calculateIFIXColor();
			if (FIELD_TO_CHECK.equals("OFIXCOLOR"))
				 result=calculateOFIXColor();
		}
		
		
		if (INFO_TYPE.equals("IMAGE")) {
            if (FIELD_TO_CHECK.equals("DIRECTION")) {
                result = calculateHeadingDirection();
            }
		}
		

		return result;

	}

	/**
	 * Calculate OFIX color.
	 *
	 * @return the string
	 */
	private String calculateOFIXColor() {
		String ret_color = "#FFFFFF";
		if(!VAL_1.equals("0"))
			ret_color="#FFFF00";

		return ret_color;
	}

	/**
	 * Calculate IFIX color.
	 *
	 * @return the string
	 */
	private String calculateIFIXColor() {
		String ret_color = "#FFFFFF";
		if(VAL_1.equals("PENDING"))
			ret_color="#FFFF00";

		return ret_color;
	}

	/**
	 * Calcute PEL color.
	 * @param inputJson 
	 *
	 * @return the string
	 */
	private static String calcutePELColor(IRawData inputJson) {
		String template = "";
		String ret_color="";
		final String flightNum = inputJson.getSafeString("FLIGHT_NUM");
		boolean warningTrans =false;
		template = inputJson.getSafeString("TEMPLATE");
		String coordState="";
		
		String DEFAULT_CoordOut = "#00FFFF";
		String DEFAULT_CoordIn  = "#FF00FF";
		String DEFAULT_Color    = "#FFFFFF"; /** #fafafa*/
		
		boolean isCoordIN=false;
		boolean isCoordOUT=false;
		
		final Optional<IRawData> jsonflightCoord = BlackBoardUtility.getDataOpt(DataType.FLIGHT_COORDINATION.name(),
				flightNum+"_PEL");
		
		if (jsonflightCoord.isPresent()) {
			 warningTrans = jsonflightCoord.get().getSafeBoolean("COORDTRANS");
			 isCoordOUT = jsonflightCoord.get().getSafeBoolean("ISCOUT");
			 isCoordIN = jsonflightCoord.get().getSafeBoolean("ISCIN");
			 coordState = jsonflightCoord.get().getSafeString("COORDSTATE");
		}
		
		
		if (isCoordIN){
			if (coordState.equals("3")) {
				ret_color = "#FFA500";
			}

			if (template.equals(PluginListConstants.TEMPLATE_AIS)){	
				if (warningTrans) {
					ret_color = "#FFA500";
				}
						
			}
			
			if (ret_color.isEmpty()) {
				ret_color = DEFAULT_CoordIn;
			}
			
		}	

		if (isCoordOUT){
			if (coordState.equals("3")) {
				ret_color = "#FFA500";
			}
			
			if (template.equals(PluginListConstants.TEMPLATE_AIS)){	
				if (warningTrans) {
					ret_color = "#FFA500";
				}
						
			}
			if (ret_color.isEmpty()) {
				ret_color = DEFAULT_CoordOut;
			}
		}
		
		if (ret_color.isEmpty()) {
			ret_color = DEFAULT_Color;
		}
		
		return ret_color;

	}

	/**
	 * Calcute XFL color.
	 * @param inputJson 
	 *
	 * @return the string
	 */
	private static String calcuteXFLColor(IRawData inputJson) {
		
		String template = "";
		String ret_color="";
		final String flightNum = inputJson.getSafeString("FLIGHT_NUM");
		boolean warningTrans =false;
		template = inputJson.getSafeString("TEMPLATE");
		String coordState="";
		
		String DEFAULT_CoordOut = "#00FFFF";
		String DEFAULT_CoordIn  = "#FF00FF";
		String DEFAULT_Color    = "#FFFFFF"; /** #fafafa*/
		
		boolean isCoordIN=false;
		boolean isCoordOUT=false;
		
		final Optional<IRawData> jsonflightCoord = BlackBoardUtility.getDataOpt(DataType.FLIGHT_COORDINATION.name(),
				flightNum+"_XFL");
		
		if (jsonflightCoord.isPresent()) {
			 warningTrans = jsonflightCoord.get().getSafeBoolean("COORDTRANS");
			 isCoordOUT = jsonflightCoord.get().getSafeBoolean("ISCOUT");
			 isCoordIN = jsonflightCoord.get().getSafeBoolean("ISCIN");
			 coordState = jsonflightCoord.get().getSafeString("COORDSTATE");
		}
		
		
		if (isCoordIN){
			if (coordState.equals("3")) {
				ret_color = "#FFA500";
			}

			if (template.equals(PluginListConstants.TEMPLATE_CONTROLLED)){	
				if (warningTrans) {
					ret_color = "#FFA500";
				}
						
			}
			
			if (ret_color.isEmpty()) {
				ret_color = DEFAULT_CoordIn;
			}
			
		}	

		if (isCoordOUT){
			if (coordState.equals("3")) {
				ret_color = "#FFA500";
			}
			
			if (template.equals(PluginListConstants.TEMPLATE_CONTROLLED)){	
				if (warningTrans) {
					ret_color = "#FFA500";
				}
						
			}
			
			if (ret_color.isEmpty()) {
				ret_color = DEFAULT_CoordOut;
			}
		}
		if (ret_color.isEmpty()) {
			if (template.equals(PluginListConstants.TEMPLATE_CONTROLLED)){	
				ret_color = getXflColorDefault();
			}else {
				ret_color = DEFAULT_Color;	
			}
		}
		
		
		return ret_color;
	}

	/**
	 * Calculate heading color.
	 *
	 * @return the string
	 */
	private String calculateHeadingColor() {
		String ret_color = "#955F20"; 
		if (!this.VAL_1.isEmpty()&&Strings.isNumeric(this.VAL_1)) {

			final double hdg = SafeDataRetriever.strToDouble(this.VAL_1);
				if (hdg <= 89.0)
					ret_color = "#C6E2FF"; 
				else if (hdg <= 179.0)
					ret_color = "#0000FF"; 
				else if (hdg <= 269.0)
					ret_color = "#FFFF00"; 
				else if (hdg <= 359.0)
					ret_color = "#E5BE01"; 

		}
		return ret_color;
	}
	
	
	/**
	 * Calculate heading direction.
	 *
	 * @return the string
	 */
	private String calculateHeadingDirection() {
		String ret_direction = "ACK"; 
		if (!this.VAL_1.isEmpty() && Strings.isNumeric(this.VAL_1)) {
			
			final double hdg = SafeDataRetriever.strToDouble(this.VAL_1);
			if (hdg <= 89.0)
				ret_direction = "NE";  
			else if (hdg <= 179.0)
				ret_direction = "SE"; 
			else if (hdg <= 269.0)
				ret_direction = "SO"; 
			else if (hdg <= 359.0)
				ret_direction = "NO"; 

		}
		return ret_direction;
	}
	
	
	
	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The My name. */
		String myName = "SCL_LIST_UTILITY";
		return myName;
	}
	

	/**
	 * @return xflColor
	 */
	private static String getXflColorDefault() {
		Optional<IRawData> listColorGeneric =  Optional.empty();
		
		listColorGeneric = BlackBoardUtility.getDataOpt(DataType.PRELOADED_COLOR_SET.name(),
				"LIST_COLOR_GENERIC");
		
		String fieldColor = listColorGeneric.get().getSafeString("XFL_COLOR_COORD");
		if (!fieldColor.isEmpty()) {
			return fieldColor;
		}
		return "";
	}
	
	
}
