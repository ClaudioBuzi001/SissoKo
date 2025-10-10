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
package auxiliary.track;

/**
 * The Class TrackInputConstants.
 */
public enum TrackInputConstants {
	;

	/** The Constant SPI. */
	public static final String SPI = "SPI";

	/** The Constant SPI_FALSE. */
	static final String SPI_FALSE = "0";

	/** The Constant RCS. */
	public static final String RCS = "RCS";

	/** The Constant RCS_NULL. */
	public static final String RCS_NULL = "null";

	/** The Constant RCS_EMPTY. */
	public static final String RCS_EMPTY = "        ";

	/** The Constant MGN. */
	static final String MGN = "MGN";

	/** The Constant IAS. */
	static final String IAS = "IAS";

	/** The Constant IAS_0. */
	public static final String IAS_0 = "0";

	/** The Constant SAL_OLD_VALUE. */
	static final String SAL_OLD_VALUE = "SAL_OLD_VALUE";

	/** The Constant SAL_OLD_VALUE_TRUE. */
	static final String SAL_OLD_VALUE_TRUE = "true";

	/** The Constant SAL_OLD_VALUE_FALSE. */
	static final String SAL_OLD_VALUE_FALSE = "false";

	/** The Constant SAL_VALIDITY_VALUE. */
	static final String SAL_VALIDITY_VALUE = "SAL_VALIDITY_VALUE";

	/** The Constant SAL_VALIDITY_VALUE_TRUE. */
	static final String SAL_VALIDITY_VALUE_TRUE = "true";

	/** The Constant SAL_VALIDITY_VALUE_FALSE. */
	static final String SAL_VALIDITY_VALUE_FALSE = "false";

	/** The Constant HDG_OLD. */
	static final String HDG_OLD = "HDG_OLD";

	/** The Constant HDG_OLD_TRUE. */
	static final String HDG_OLD_TRUE = "true";

	/** The Constant HDG_OLD_FALSE. */
	static final String HDG_OLD_FALSE = "false";

	/** The Constant HDG_VALIDITY. */
	static final String HDG_VALIDITY = "HDG_VALIDITY";

	/** The Constant HDG_VALIDITY_000. */
	static final String HDG_VALIDITY_000 = "000";

	/** The Constant HDG_VALIDITY_111. */
	static final String HDG_VALIDITY_111 = "111";

	/** The Constant SPD_MOD. */
	public static final String SPD_MOD = "SPD_MOD";

	/** The Constant FOM. */
	public static final String FOM = "FOM";

	/** The Constant SPEED. */
	public static final String SPEED = "SPEED";

	/** The Constant OVERSYMBOL. */
	public static final String OVERSYMBOL = "OVERSYMBOL";

	/** The Constant LEV. */
	public static final String LEV = "LEV";

	/** The Constant AFL. */
	public static final String AFL = "AFL";

	/** The Constant CMH. */
	public static final String CMH = "CMH";

	/** The Constant ABSENT_LEVEL. */
	public static final int ABSENT_LEVEL = 32768;

	/** The Constant ABSENT_LEVEL_PLOT. */
	public static final int ABSENT_LEVEL_PLOT = 8192;

	/** The Constant MODE_3A. */
	public static final String MODE_3A = "MODE_3A";

	/** The Constant ECAT. */
	public static final String ECAT = "ECAT";

	/** The Constant RATECLIMIND_NOT_MORE. */
	public static final String RATECLIMIND_NOT_MORE = "NOT_MORE";

	/** The Constant RATECLIMIND_NOT_LESS. */
	public static final String RATECLIMIND_NOT_LESS = "NOT_LESS";

	/** The Constant VR. */
	public static final String VR = "VR";

	/** The Constant VR_ARC. */
	public static final String VR_ARC = "VR_ARC";

	/** The Constant VR_0. */
	public static final String VR_0 = "0";

	/** The Constant VR_VALIDITY_VALUE. */
	public static final String VR_VALIDITY_VALUE = "VR_VALIDITY_VALUE";

	/** The Constant VR_VALIDITY_VALUE_FALSE. */
	public static final String VR_VALIDITY_VALUE_FALSE = "false";

	/** The Constant VR_ATD. */
	public static final String VR_ATD = "VR_ATD";

	/** The Constant VR_THRESHOLD. */
	public static final int VR_THRESHOLD = 2;

	/** The Constant ROCD. */
	public static final String ROCD = "ROCD";

	/** The Constant SAL. */
	public static final String SAL = "SAL";

	/** The Constant SAL_0. */
	public static final String SAL_0 = "0";

	/** The Constant PSC. */
	public static final String PSC = "PSC";

	/** The Constant PSC_PSR. */
	public static final int PSC_PSR = 1;

	/** The Constant PSC_SSR. */
	public static final int PSC_SSR = 2;

	/** The Constant PSC_COMB. */
	public static final int PSC_COMB = 3;

	/** The Constant MODE_S. */
	public static final String MODE_S = "MODE_S";

	/** The Constant IS_ADSB_TRACKER. */
	public static final String IS_ADSB_TRACKER = "IS_ADSB_TRACKER";

	/** The Constant IS_ADS_TRACKERS. */
	public static final String IS_ADS_TRACKERS = "IS_ADS_TRACKERS";

	/** The Constant IS_MLAT_TRACKERS. */
	public static final String IS_MLAT_TRACKERS = "IS_MLAT_TRACKERS";

	/** The Constant ECAT_DRONE. */
	public static final int ECAT_DRONE = 13;
	
	/** The Constant ECAT_SUBORBIT. */
	public static final int ECAT_SUBORBIT = 14;

	/** The Constant ECAT_VEHIC. */
	public static final int ECAT_VEHIC = 21;

	/** The Constant COAST. */
	public static final String COAST = "COAST";

	/** The Constant IS_DUPLICATED. */
	public static final String IS_DUPLICATED = "IS_DUPLICATED";

	/** The Constant IS_NAVIGATED. */
	public static final String IS_NAVIGATED = "IS_NAVIGATED";

	/** The Constant LEV_UP. */
	public static final int LEV_DOWN = 290;

	/** The Constant LEV_UP. */
	public static final int LEV_UP = 410;

	/** The Constant IS_DUPLICATED_RCS. */
	public static final String IS_DUPLICATED_RCS = "IS_DUPLICATED_RCS";

	/** The Constant BPS. */
	public static final String BPS = "BPS";

	/** The Constant BYP. */
	public static final String BYP = "BYP";

	/** The Constant FIRSTQNH. */
	public static final int FIRSTQNH = 900;

	/** The Constant LASTQNH. */
	public static final int LASTQNH = 1065;

	/** The Constant MOD_CONSPICUITY. */
	public static final String MOD_CONSPICUITY = "A1000";

	/** The Constant TMM. */
	public static final String TMM = "TMM";

	/** The Constant TCT. */
	public static final String TCT = "TCT";

	/** The Constant MODE_4. */
	public static final String MODE_4 = "MODE_4";

	/** The Constant VX. */
	public static final String VX = "VX";

	/** The Constant VY. */
	public static final String VY = "VY";

	/** The Constant C2L. */
	public static final String C2L = "C2L";

	/** The Constant IS_FPT_TRACKER. */
	public static final String IS_FPT_TRACKER = "IS_FPT_TRACKER";

	/** The Constant IS_SMR_TRACKER. */
	public static final String IS_SMR_TRACKER = "IS_SMR_TRACKER";

	/** The Constant TCAS. */
	public static final String TCAS = "TCAS";

	/** The Constant BDS30_RAT. */
	public static final String BDS30_RAT = "BDS30_RAT";

	/** The Constant BDS30_ARA41. */
	public static final String BDS30_ARA41 = "BDS30_ARA41";

	/** The Constant BDS30_MTE. */
	public static final String BDS30_MTE = "BDS30_MTE";

	/** The Constant IS_TEST_TRANSPONDER_TRACK. */
	public static final String IS_TEST_TRANSPONDER_TRACK = "IS_TEST_TRANSPONDER_TRACK";

	/** The Constant TRANSP. */
	public static final String TRANSP = "TRANSP";

//	/** The Constant EMERGENCY_RPAS. */
//	public static final String EMERGENCY_RPAS = "EMERGENCY_RPAS";
	/** The Constant RST_VALIDITY. */
	public static final String RST_VALIDITY = "RST_VALIDITY";
	/** The Constant EMG_RPAS_DAAL. */
	public static final String EMG_RPAS_DAAL = "DAAL";
	/** The Constant EMG_RPAS_DAAL. */
	public static final String EMG_RPAS_C2L = "EMG_RPAS_C2L";
	
	
	
}
