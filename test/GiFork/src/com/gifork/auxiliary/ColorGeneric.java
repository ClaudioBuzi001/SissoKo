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
package com.gifork.auxiliary;

import java.util.List;
import java.util.Optional;

import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

/**
 * The Class ColorGeneric.
 */

public final class ColorGeneric {

	/** The Constant mapFlightMessages. */
	private Optional<IRawData> listColorGeneric = Optional.empty();

	/** The Constant CPDLC_FRAME_GI_ERR. */
	public static final String CPDLC_FRAME_GI_ERR = "CPDLC_FRAME_GI_ERR";

	/** The Constant CPDLC_FRAME_GI_STA. */
	public static final String CPDLC_FRAME_GI_STA = "CPDLC_FRAME_GI_STA";

	/** The Constant CPDLC_FRAME_AI_STA. */
	public static final String CPDLC_FRAME_AI_STA = "CPDLC_FRAME_AI_STA";

	/** The Constant CPDLC_FRAME_AI_ERR. */
	public static final String CPDLC_FRAME_AI_ERR = "CPDLC_FRAME_AI_ERR";

	/** The Constant FLIGHT_RWY_LIRA. */
	public static final String FLIGHT_RWY_LIRA = "FLIGHT_RWY_LIRA";

	/** The Constant FLIGHT_RWY_LIRF. */
	public static final String FLIGHT_RWY_LIRF = "FLIGHT_RWY_LIRF";

	/** The Constant RWY_CLOSED. */
	public static final String RWY_CLOSED = "RWY_CLOSED";

	/** The Constant WARNING. */
	public static final String WARNING = "WARNING";

	/** The Constant WRN_NOT_CONFORMANCE. */
	public static final String WRN_NOT_CONFORMANCE = "WRN_NOT_CONFORMANCE";

	/** The Constant ALLARM. */
	public static final String ALLARM = "ALLARM";

	/** The Constant SFPL_EXTRAPOLATED. */
	public static final String SFPL_EXTRAPOLATED = "SFPL_EXTRAPOLATED";

	/** The Constant CALL_TRANSF. */
	public static final String CALL_TRANSF = "CALL_TRANSF";

	/** The Constant DEFAULT. */
	public static final String DEFAULT = "DEFAULT";

	/** The Constant ENABLE_STATE. */
	public static final String ENABLE_STATE = "ENABLE_STATE";

	/** The Constant OPEN_STATE. */
	public static final String OPEN_STATE = "OPEN_STATE";

	/** The Constant CPDLC_GI_ERR. */
	public static final String CPDLC_GI_ERR = "CPDLC_GI_ERR";

	/** The Constant CPDLC_GI_STA. */
	public static final String CPDLC_GI_STA = "CPDLC_GI_STA";

	/** The Constant CPDLC_AI_DEF. */
	public static final String CPDLC_AI_DEF = "CPDLC_AI_DEF";

	/** The Constant CPDLC_AI_STA. */
	public static final String CPDLC_AI_STA = "CPDLC_AI_STA";

	/** The Constant CPDLC_AI_ERR. */
	public static final String CPDLC_AI_ERR = "CPDLC_AI_ERR";

	/** The Constant TRACK_SLB_DEVIATION. */
	public static final String TRACK_SLB_DEVIATION = "TRACK_SLB_DEVIATION";

	/** The Constant COAST_STATE. */
	public static final String COAST_STATE = "COAST_STATE";

	/** The Constant MISMATCH. */
	public static final String MISMATCH = "MISMATCH";

	/** The Constant COORD_PEL_XFL_DM. */
	public static final String COORD_PEL_XFL_DM = "COORD_PEL_XFL_DM";

	/** The Constant COORD_XFL_PEL_MD. */
	public static final String COORD_XFL_PEL_MD = "COORD_XFL_PEL_MD";

	/** The Constant COORD_REJECTED. */
	public static final String COORD_REJECTED = "COORD_REJECTED";

	/** The Constant COORD_INVALID. */
	public static final String COORD_INVALID = "COORD_INVALID";

	/** The Constant COORD_PEL_AGREED. */
	public static final String COORD_PEL_AGREED = "COORD_PEL_AGREED";

	/** The Constant COORD_XFL_AGREED. */
	public static final String COORD_XFL_AGREED = "COORD_XFL_AGREED";

	/** The Constant AIS_PEL_AGREED. */
	public static final String AIS_PEL_AGREED = "AIS_PEL_AGREED";

	/** The Constant AIS_XFL_AGREED. */
	public static final String AIS_XFL_AGREED = "AIS_XFL_AGREED";

	/** The Constant TCT_URGENCY_. */
	public static final String TCT_URGENCY_ = "TCT_URGENCY_";

	/** The Constant TCT_URGENCY_A. RED */
	public static final String TCT_URGENCY_A = "TCT_URGENCY_A";

	/** The Constant TCT_URGENCY_C. YELLOW */
	public static final String TCT_URGENCY_C = "TCT_URGENCY_C";

	/** The Constant TCT_URGENCY_C. YELLOW */
	public static final String TCT_WHAT_IF_TRY = "TCT_WHAT_IF_TRY";

	/** The Constant WRN_A. WHITE */
	public static final String WRN_A = "WRN_A";

	/** The Constant WRN_B. MAGENTA */
	public static final String WRN_B = "WRN_B";

	/** The Constant EMERGENCY_HJ ORANGE */
	public static final String EMERGENCY_HJ = "EMERGENCY_HJ";

	/** The Constant TCT_URGENCY_AB ORANGE */
	public static final String TCT_URGENCY_AB = "TCT_URGENCY_AB";

	/** The Constant TCT_EXC GREY */
	public static final String TCT_EXC = "TCT_EXC";

	/** The Constant CMH_WARNING */
	public static final String CMH_WARNING = "CMH_WARNING";
	
	/** The Constant IAS_MODE_S */
	public static final String IAS_MODE_S = "IAS_MODE_S";

	/** The Constant STATUS_ON */
	public static final String STATUS_ON = "STATUS_ON";
	
	/** The Constant STATUS_OFF */
	public static final String STATUS_OFF = "STATUS_OFF";

	/** The Constant TTL */
	public static final String TTL = "TTL";
	
	/** The Constant TTG */
	public static final String TTG = "TTG";

	/** The Constant EXCLUSION_CRITERIA_ON */
	public static final String EXCLUSION_CRITERIA_ON = "EXCLUSION_CRITERIA_ON";
	
	/** The Constant EXCLUSION_CRITERIA_OFF */
	public static final String EXCLUSION_CRITERIA_OFF = "EXCLUSION_CRITERIA_OFF";

	/** The Constant STATUS_OFF */
	public static final String UNSENSITIVE_COLOR = "UNSENSITIVE_COLOR";

	/**
	 * The Class SingletonLoader.
	 */
	private static class SingletonLoader {
		/** The instance. */
		private static final ColorGeneric instance = new ColorGeneric();
	}

	/**
	 * ColorGeneric.
	 */
	private ColorGeneric() {

		init();

	}

	/**
	 * Gets the single instance of ColorGeneric.
	 *
	 * @return Singleton
	 */
	public static ColorGeneric getInstance() {
		return SingletonLoader.instance;
	}

	/**
	 * Process.
	 */
	private void init() {

		/** The Constant listColorGeneric. */
		listColorGeneric = BlackBoardUtility.getDataOpt("PRELOADED_COLOR_SET", "LIST_COLOR_GENERIC");

	}

	/**
	 * Gets color.
	 *
	 * @param keyElement the field Key
	 * @return color
	 */
	public String getColor(String keyElement) {
		String fieldColor = listColorGeneric.get().getSafeString(keyElement);
		if (!fieldColor.isEmpty()) {
			return fieldColor;
		}
		return "";
	}

	/**
	 * Gets color.
	 *
	 * @param keyElement   the field Key
	 * @param valueElement the field value
	 * @param keyAttribute the field attribute (ALIAS, COLOR)
	 * @return color
	 */
	public String getColorParameter(String keyElement, String valueElement, String keyAttribute) {

		IRawDataElement fieldColorMap = listColorGeneric.get().getSafeElement(keyElement);
		if (!fieldColorMap.isEmpty()) {
			IRawDataElement valueParameters = fieldColorMap.getSafeElement(valueElement);
			return valueParameters.getSafeString(keyAttribute);
		}

		return "";
	}

	/**
	 * Gets color list.
	 *
	 * @param keyElement   the field Key
	 * @param valueElement the field value
	 * @return color
	 */
	public List<String> getColorList(String keyElement, String valueElement) {

		IRawDataElement fieldColorMap = listColorGeneric.get().getSafeElement(keyElement);
		if (!fieldColorMap.isEmpty()) {
			return fieldColorMap.getListOfString(valueElement);
		}

		return null;
	}

	/**
	 * Gets color map.
	 *
	 * @param keyElement the field Key
	 * @return color
	 */
	public IRawDataElement getColorMap(String keyElement) {
		return listColorGeneric.get().getSafeElement(keyElement);
	}

	/**
	 * ONLY FOR TEST USE
	 */
	public void reload() {
		init();
	}
}
