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
package common;

/**
 * The Class CommonConstants.
 */
public enum CommonConstants {
	;

	/**
	 * The Enum CoordType.
	 */
	public enum CoordType {

		/** The no type. */
		HDG("CHE");

		/** The text. */
		private final String text;

		/**
		 * Instantiates a new coord type.
		 *
		 * @param text the text
		 */
		CoordType(final String text) {
			this.text = text;
		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		@Override
		public String toString() {
			return text;
		}

	}

	/**
	 * The Enum CoordState.
	 */
	public enum CoordState {

		/** The no coord. */
		NO_COORD(0),

		/** The modified. */
		MODIFIED(1),

		/** The accepted. */
		ACCEPTED(2),

		/** The rejected. */
		REJECTED(3),

		/** The proposed. */
		PROPOSED(4);

		/** The value. */
		private final int value;

		/**
		 * Instantiates a new coord state.
		 *
		 * @param value the value
		 */
		CoordState(final int value) {
			this.value = value;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public int getValue() {
			return value;
		}

		/**
		 * From value.
		 *
		 * @param val the val
		 * @return the coord state
		 */
		public static CoordState fromValue(final int val) {
			for (final CoordState b : CoordState.values()) {
				if (b.value == val) {
					return b;
				}
			}
			return NO_COORD;
		}
	}

	/** The Constant SHOW_TCT_CONFLICT. */
	public static final String SHOW_TCT_CONFLICT = "SHOW_TCT_CONFLICT(CONFLICT_ID=";

	/** The Constant TCT_CALLBACK_CLOSE. */
	public static final String TCT_CALLBACK_CLOSE = ")";

	/** The Constant FLIGHT_NUM. */
	public static final String FLIGHT_NUM = "FLIGHT_NUM";

	/** The Constant COORDSTATE. */
	public static final String COORDSTATE = "COORDSTATE";

	/** The Constant CALLSIGN. */
	public static final String CALLSIGN = "CALLSIGN";

	/** The Constant OBJECTID. */

	public static final String OBJECTID = "OBJECTID";

	/** The Constant MTCD_FILTER. */

	public static final String MTCD_FILTER = "MTCD_FILTER";

	/** The Constant ID. */
	public static final String ID = "ID";

	/** The Constant TRACK_ID. */
	public static final String TRACK_ID = "TRACK_ID";

	/** The Constant COLOR_DEFAULT. */

	public static final String COLOR_DEFAULT = "0";

	/** The Constant COLOR_CYAN. */
	public static final String COLOR_CYAN = "1";

	/** The Constant COLOR_GREEN. */
	public static final String COLOR_GREEN = "2";

	/** The Constant COLOR_ORANGE. */
	public static final String COLOR_ORANGE = "3";

	/** The Constant COLOR_YELLOW. */
	public static final String COLOR_YELLOW = "4";

	/** The Constant COLOR_PINK. */
	public static final String COLOR_PINK = "5";

	/** The Constant COLOR_MAGENTA. */
	public static final String COLOR_MAGENTA = "6";

	/** The Constant STN. */
	public static final String STN = "STN";

	/** The Constant ABSENT_STN. */

	public static final String ABSENT_STN = "16777215";

	/** The Constant BB_FLIGHT_CPDLC_DOWNLINK. */
	public static final String BB_FLIGHT_CPDLC_DOWNLINK = "BB_FLIGHT_CPDLC_DOWNLINK";

	/** The Constant BB_FLIGHT_CPDLC_UPLINK. */
	public static final String BB_FLIGHT_CPDLC_UPLINK = "BB_FLIGHT_CPDLC_UPLINK";

	/** The Constant ADSB_EMG. */

	public static final String ADSB_EMG = "ADSB_EMG";

	/** The Constant TCT. */
	public static final String TCT = "TCT";

	/** The Constant TRACK_NUMBER1. */
	public static final String TRACK_NUMBER1 = "TRACK_NUMBER1";

	/** The Constant TRACK_NUMBER2. */
	public static final String TRACK_NUMBER2 = "TRACK_NUMBER2";

	/** The Constant DBS. */
	public static final String DBS = "DBS";

	/** The Constant LEADER. */
	public static final String LEADER = "LEADER";

	/** The Constant FOLLOWER. */
	public static final String FOLLOWER = "FOLLOWER";

	/** The DBS_SWITCH_LABEL state BB key */
	public static final String DBS_SWITCH_LABEL_STATE = "STATE";

	/** The Constant DBS. */
	public static final String DBS_RNDIST = "DBS_RN_DIST";

	/** The Constant DBS. */
	public static final String DBS_DTL = "DBS_DTL";

	/** The DBS marker lon value. */
	public static final String DBS_LAT = "DBS_BRK_LAT";

	/** The DBS marker lon value. */
	public static final String DBS_LON = "DBS_BRK_LON";

	/** The distance to lose/gain */
	public static final String DBS_ANGLE = "DBS_BRK_ANGLE";

	/** The dbs marker color */
	public static final String DBS_COLOR = "DBS_COLOR";

	/** The Constant ALARM_TYPE. */
	public static final String ALARM_TYPE = "ALARM_TYPE";

	/** The Constant CATEGORY. */
	public static final String CATEGORY = "CATEGORY";

	/** The Constant CATEGORY. */
	public static final String STYLE_FIRST_LINE = "STYLE_FIRST_LINE";

	/** The Constant TCT_TIME. */
	public static final String TCT_TIME = "TCT_TIME";

	/** The Constant CONFLICT_TIME. */
	public static final String CONFLICT_TIME = "CONFLICT_TIME";

	/** The Constant CONFLICT_ID. */
	public static final String CONFLICT_ID = "ID";

	/** The Constant CONFLICT_NUMBER. */
	public static final String CONFLICT_NUMBER = "CONFLICT_NUMBER";

	/** The Constant ACT_DIST. */
	public static final String ACT_DIST = "ACT_DIST";

	/** The Constant PRESENT_PLANAR_DISTANCE. */
	public static final String PRESENT_PLANAR_DISTANCE = "PRESENT_PLANAR_DISTANCE";

	/** The Constant MIN_DIST. */
	public static final String MIN_DIST = "MIN_DIST";

	/** The Constant SEP_DIST. */
	public static final String SEP_DIST = "SEP_DIST";

	/** The Constant DIST. */
	public static final String DIST = "DIST";

	/** The Constant ESTIMATED_MINIMUM_PLANAR_DISTANCE. */
	public static final String ESTIMATED_MINIMUM_PLANAR_DISTANCE = "ESTIMATED_MINIMUM_PLANAR_DISTANCE";

	/** The Constant D_AT_CONFLICT_TIME. */
	public static final String D_AT_CONFLICT_TIME = "D_AT_CONFLICT_TIME";

	/** The Constant EST_DISTANCE_TO_SEP_LOSS_1. */
	public static final String EST_DISTANCE_TO_SEP_LOSS_1 = "EST_DISTANCE_TO_SEP_LOSS_1";

	/** The Constant EST_DISTANCE_TO_SEP_LOSS_2. */
	public static final String EST_DISTANCE_TO_SEP_LOSS_2 = "EST_DISTANCE_TO_SEP_LOSS_2";

	/** The Constant D_TO_MD1. */
	public static final String D_TO_MD1 = "D_TO_MD1";

	/** The Constant D_TO_MD2. */
	public static final String D_TO_MD2 = "D_TO_MD2";

	/** The Constant TIME_MIN_DIST. */
	public static final String TIME_MIN_DIST = "TIME_MIN_DIST";

	/** The Constant TIME_TO_CONFLICT. */
	public static final String TIME_TO_CONFLICT = "TIME_TO_CONFLICT";

	/** The Constant URGENCY. */
	public static final String URGENCY = "URGENCY";

	/** The Constant IS_TCT_TENTATIVE. */
	public static final String IS_TCT_TENTATIVE = "IS_TCT_TENTATIVE";

	/** The Constant ARROW. */
	public static final String ARROW = "ARROW";

	/** The Constant ALARM_TYPE_STCA. */
	public static final String ALARM_TYPE_STCA = "0";

	/** The Constant ALARM_TYPE_MSAW. */
	public static final String ALARM_TYPE_MSAW = "1";

	/** The Constant ALARM_TYPE_APW. */
	public static final String ALARM_TYPE_APW = "2";

	/** The Constant ALARM_TYPE_APM. */
	public static final String ALARM_TYPE_APM = "3";

	/** The Constant ALARM_TYPE_APM. */
	public static final String ALARM_TYPE_AIW = "4";

	/** The Constant STN_1. */
	public static final String STN_1 = "STN1";

	/** The Constant STN_2. */
	public static final String STN_2 = "STN2";

	/** The Constant X1_AT_MINIMUM_DISTANCE. */
	public static final String X1_AT_MINIMUM_DISTANCE = "X1_AT_MINIMUM_DISTANCE";

	/** The Constant Y1_AT_MINIMUM_DISTANCE. */
	public static final String Y1_AT_MINIMUM_DISTANCE = "Y1_AT_MINIMUM_DISTANCE";

	/** The Constant X2_AT_MINIMUM_DISTANCE. */
	public static final String X2_AT_MINIMUM_DISTANCE = "X2_AT_MINIMUM_DISTANCE";

	/** The Constant Y2_AT_MINIMUM_DISTANCE. */
	public static final String Y2_AT_MINIMUM_DISTANCE = "Y2_AT_MINIMUM_DISTANCE";

	/** The Constant X1_AT_CONFLICT_TIME. */
	public static final String X1_AT_CONFLICT_TIME = "X1_AT_CONFLICT_TIME";

	/** The Constant Y1_AT_CONFLICT_TIME. */
	public static final String Y1_AT_CONFLICT_TIME = "Y1_AT_CONFLICT_TIME";

	/** The Constant X2_AT_CONFLICT_TIME. */
	public static final String X2_AT_CONFLICT_TIME = "X2_AT_CONFLICT_TIME";

	/** The Constant Y2_AT_CONFLICT_TIME. */
	public static final String Y2_AT_CONFLICT_TIME = "Y2_AT_CONFLICT_TIME";

	/** The Constant IS_SOUND_MTCD. */
	public static final String IS_SOUND_MTCD = "IS_SOUND_MTCD";

	/** The Constant IS_SOUND_MTCD_Value. */
	public static final String IS_SOUND_MTCD_Value = "Y";

	/** The Constant RESET_CALLBACK. */
	public static final String RESET_CALLBACK = "EXTERNAL_ORDER()";

	/** The Constant callbackOHD. */
	public static final String callbackOHD = "EXTERNAL_ORDER(GRAPHIC_ORDER=OPEN_HEADING, ORDER_ID=OHD, PREVIEW_NAME=quickOHD,  OBJECT_TYPE=OHD)";

	/** The Constant callbackCHE_INIT. */
	public static final String callbackCHE_INIT = "EXTERNAL_ORDER( GRAPHIC_ORDER=OPEN_HEADING_COORDINATION , ORDER_ID=CHE , OBJECT_TYPE=CHE ,  PREVIEW_NAME=quickCHE )";

	/** The Constant callbackCHE_ACPRJC. */
	public static final String callbackCHE_ACPRJC = "CONTEXT_MENU(MENU_FILE=HDGCoordAcpRjcContextMenu.xml)";

	/** The Constant callbackCHE_CTP. */
	public static final String callbackCHE_CTP = "CONTEXT_MENU(MENU_FILE=HDGCoordContextMenu.xml)";

	/** The Constant TRJ_ARRAY. */
	public static final String TRJ_ARRAY = "TRJ_ARRAY";

	/** The Constant IS_RNP. */
	public static final String IS_RNP = "IS_RNP";

	/** The Constant IS_RCP. */
	public static final String IS_RCP = "IS_RCP";

	/** The Constant IS_RSP. */
	public static final String IS_RSP = "IS_RSP";

	/** The Constant FLIGHT_VALUE_RSP. */
	public static final String FLIGHT_VALUE_RSP = "VALUE_RSP";

	/** The Constant FLIGHT_VALUE_RCP. */
	public static final String FLIGHT_VALUE_RCP = "VALUE_RCP";

	/** The Constant FLIGHT_FLAG_IS_RNP10. */
	public static final String FLIGHT_FLAG_IS_RNP10 = "IS_RNP10";

	/** The Constant FLIGHT_FLAG_IS_RNP4. */
	public static final String FLIGHT_FLAG_IS_RNP4 = "IS_RNP4";

	/** The Constant FLIGHT_FLAG_IS_RNP2. */
	public static final String FLIGHT_FLAG_IS_RNP2 = "IS_RNP2";

	/** The Constant FLIGHT_FLAG_IS_RNP1. */
	public static final String FLIGHT_FLAG_IS_RNP1 = "IS_RNP1";

	/** The Constant FLIGHT_FLAG_IS_RNPACHP. */
	public static final String FLIGHT_FLAG_IS_RNPACHP = "IS_RNPACHP";

	/** The Constant FLIGHT_ISIOAT. */
	public static final String FLIGHT_ISIOAT = "ISIOAT";

}
