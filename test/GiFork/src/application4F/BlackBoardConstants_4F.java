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
package application4F;

/**
 * The Class BlackBoardConstants_4F.
 */
public class BlackBoardConstants_4F {

	/**
	 * The Enum DataType.
	 */
	public enum DataType {

		/** The BB_CFL_STEPS. */
		BB_CFL_STEPS,

		/** The bb cpdlc history. */
		BB_CPDLC_HISTORY,

		/** The bb cpdlc freetext. */
		BB_CPDLC_FREETEXT,

		/** The bb cpdlc gi dialogues. */
		BB_CPDLC_GI_DIALOGUES,

		/** The bb flight free text. */
		BB_FLIGHT_FREE_TEXT,

		/** The bb flightnum stnlost. */
		BB_FLIGHTNUM_STNLOST,

		/** The bb track alert ack. */
		BB_TRACK_ALERT_ACK,

		/** The bb opengiforkpanel. */
		BB_OPENGIFORKPANEL,

		/** The bb order result. */
		BB_ORDER_RESULT,

		/** The bb preview room config. */
		BB_PREVIEWMAPPING,

		/** The current OPSUP privilege. */
		BB_PRIVILEGE,

		/** The bb qnh selected. */
		BB_QNH_SELECTED,

		/** The current sensors status. */
		BB_RADARS_STATUS,

		/** The bb rti rjcack. */
		BB_RTI_RJCACK,

		/** The current rwy in use. */
		BB_SHOW_RWY,

		/** The bb stn flightnum. */
		BB_STN_FLIGHTNUM,

		/** The bb stn amb. */
		BB_STN_AMB,

		/** The bb flightnum stn. */
		BB_FLIGHTNUM_STN,

		/** The bb callsign flightnum. */
		BB_CALLSIGN_FLIGHTNUM,

		/** The bb holding. */
		BB_HOLDING,

		/** The bb CUD. */
		BB_CUD,
		
		/** The contingency. */
		CONTINGENCY,

		/** The corlm data. */
		CORLM_DATA,

		/** The cpdlc datalink. */
		CPDLC_DATALINK,

		/** The cpdlc dialogues. */
		CPDLC_DIALOGUES,

		/** The cpdlc generic. */
		CPDLC_GENERIC,

		/** The cpdlc logon. */
		CPDLC_LOGON,

		/** The elig sid. */
		ELIG_SID,

		/** The elig star. */
		ELIG_STAR,

		/** The elig map. */
		ELIG_MAPP,

		/** The env aerodromeconfiguration. */
		ENV_AERODROMECONFIGURATION,

		/** The env all lp defcomresp. */
		ENV_ALL_LP_DEFCOMRESP,

		/** The env atsu currentfreqplanresp. */
		ENV_ATSU_CURRENTFREQPLANRESP,

		/** The env atsu lps. */
		ENV_ATSU_LPS,

		/** The env atsu responsibility. */
		ENV_ATSU_RESPONSIBILITY,

		/** The env adjacentatsus. */
		ENV_ADJACENTATSUS,

		/** The env atsucopinformation. */
		ENV_ATSUCOPINFORMATION,

		/** The env adjacent atsu resp. */
		ENV_ADJATSU_RESP,

		/** The env lp group mapping. */
		ENV_LPGROUPMAPPING,

		/** The env lp group configuration. */
		ENV_LPGROUPCONF,

		/** The env sector table. */
		ENV_SECTORTABLE,

		/** The env lp group shared volumes. */
		ENV_LPGROUPSHAREDVOLUMES,

		/** The env atsumonitor. */
		ENV_ATSUMONITOR,

		/** The env atsu respphone. */
		ENV_ATSU_RESPPHONE,

		/** The env familyofoperatorslist. */
		ENV_FAMILYOFOPERATORSLIST,

		/** The env freqplanlist. */
		ENV_FREQPLANLIST,

		/** The env gen. */
		ENV_GEN,

		/** The env operroomconf. */
		ENV_OPERROOMCONF,

		/** The env owninfo. */
		ENV_OWNINFO,

		/** The env tsa. */
		ENV_TSA,

		/** The env wpparameters. */
		ENV_WPPARAMETERS,

		/** The env start bulkservice. */
		ENV_START_BULKSERVICE,

		/** The env end bulkservice. */
		ENV_END_BULKSERVICE,

		/** The env familysettings. */
		ENV_FAMILYSETTINGS,

		/** The env usersettings. */
		ENV_USERSETTINGS,

		/** The env playbackconfig. */
		ENV_PLAYBACKCONFIG,

		/** The env userloginstatus. */
		ENV_USERLOGINSTATUS,

		/** The env server. */
		ENV_SERVER,

		/** The env missingwind. */
		ENV_MISSINGWIND,

		/** The env qnhstandard. */
		ENV_QNHSTANDARD,

		/** The env automaticupdqnh. */
		ENV_AUTOMATICUPDQNH,

		/** The env atsusectorlevels. */
		ENV_ATSUSECTORLEVELS,

		/** The env holding areas. */
		ENV_HOLDING_AREAS,

		/** The env all lp. */
		ENV_ALL_LP,

		/** The env predefinedsid list. */
		ENV_PREDEFINEDSID_LIST,

		/** The env remotedesktop. */
		ENV_REMOTEDESKTOP,

		/** The env oes. */
		ENV_OES,

		/** The env adjatsu point. */
		ENV_ADJATSU_POINT,

		/** The env aerodrome qnh. */
		ENV_AERODROME_QNH,

		/** The env cpdlcairspacelist. */
		ENV_CPDLCAIRSPACELIST,

		/** The env datalinkpreformattedtext. */
		ENV_DATALINKPREFORMATTEDTEXT,

		/** The env dlrejectedaircraftcriteria. */
		ENV_DLREJECTEDAIRCRAFTCRITERIA,

		/** The env familiesofoperatorswp. */
		ENV_FAMILIESOFOPERATORSWP,

		/** The env monitorlist. */
		ENV_MONITORLIST,

		/** The env myrespaerodrome. */
		ENV_MYRESPAERODROME,

		/** The env responsibilities. */
		ENV_RESPONSIBILITIES,

		/** The env rwy sid list. */
		ENV_RWY_SID_LIST,

		/** The env rwy star list. */
		ENV_RWY_STAR_LIST,

		/** The env respphoneplan list. */
		ENV_RESPPHONEPLAN_LIST,

		/** The env lp cpdlc status. */
		ENV_LP_CPDLC_STATUS,

		/** The env automaticlogonrequestrejection. */
		ENV_AUTOMATICLOGONREQUESTREJECTION,

		/** The env lpmapping. */
		ENV_LPMAPPING,

		/** The env predefinediaps. */
		ENV_PREDEFINEDIAPS,

		/** The env iafs. */
		ENV_IAFS,

		/** The env predefinedstar list. */
		ENV_PREDEFINEDSTAR_LIST,

		/** The env apmzonelist. */
		ENV_APMZONELIST,

		/** The env freqplan. */
		ENV_FREQPLAN,

		/** The env myrespfix. */
		ENV_MYRESPFIX,

		/** The env atsu respfreqinuse. */
		ENV_ATSU_RESPFREQINUSE,

		/** The env application. */
		ENV_APPLICATION,

		/** The env closed runways. */
		ENV_CLOSEDRUNWAYS,

		/** The env wtc separations. */
		ENV_WTCSEPARATIONS,

		/** The env default track filters. */
		ENV_DEFAULTTRACKSFILTERS,

		/** The external order. */
		EXTERNAL_ORDER,

		/** The flight coordination. */
		FLIGHT_COORDINATION,

		/** The flight consultsfplreminders. */
		FLIGHT_CONSULTSFPLREMINDERS,

		/** The flight dep. */
		FLIGHT_DEP,

		/** The flight efl. */
		FLIGHT_EFL,

		/** The flight firentry. */
		FLIGHT_FIRENTRY,

		/** The flight firexit. */
		FLIGHT_FIREXIT,

		/** The flight fldi. */
		FLIGHT_FLDI,

		/** The flight hld. */
		FLIGHT_HLD,

		/** The flight iaf. */
		FLIGHT_IAF,

		/** The flight ifl. */
		FLIGHT_IFL,

		/** The flight lnd. */
		FLIGHT_LND,

		/** The flight relresp. */
		FLIGHT_RELRESP,

		/** The flight sel. */
		FLIGHT_SEL,

		/** The flight sfplreminder. */
		FLIGHT_SFPLREMINDER,

		/** The flight vfl. */
		FLIGHT_VFL,

		/** The flight extflight. */
		FLIGHT_EXTFLIGHT,

		/** The flight whatifcontext. */
		FLIGHT_WHATIFCONTEXT,

		/** The flight whatifflight. */
		FLIGHT_WHATIFFLIGHT,

		/** The flight coordflight. */
		FLIGHT_COORDFLIGHT,

		/** The flight alternatve XFL/SKIP. */
		FLIGHT_XFLSKIPFLIGHT,
		
		/** The fpm data. */
		FPM_DATA,

		/** The interwp message. */
		INTERWP_MESSAGE,

		/** The interwp message response. */
		INTERWP_MESSAGE_RESPONSE,

		/** The interwp supervisor map. */
		INTERWP_SUPERVISOR_MAP,

		/** The order consultation. */
		ORDER_CONSULTATION,

		/** The order flightplanquery. */
		ORDER_FLIGHTPLANQUERY,

		/** The sensor status. */
		SENSOR_STATUS,

		/** The snet. */
		SNET,

		/** The stn lost. */
		STN_LOST,

		/** The tct. */
		TCT,

		/** The track data. */
		TRACK_DATA,

		/** The track flight. */
		TRACK_FLIGHT,

		/** The track modes. */
		TRACK_MODES,

		/** The apm zones. */
		APM_ZONES,

		/** The flight trj. */
		FLIGHT_TRJ,

		/** The TSNET map. */
		SNET_MAP,

		/** The snet map list. */
		SNET_MAP_LIST,

		/** The surveillance status. */
		SURV_STATUS,

		/** The bb show arp. */
		BB_SHOW_ARP,

		/** The bb hook. */
		BB_HOOK,

		/** The cora data. */
		CORA_DATA,

		/** The adsc report. */
		ADSC_REPORT,

		/** The epp report datatype. */
		EPP_REPORT_DATATYPE,

		/** The ecsp flight data. */
		ECSP_FLIGHT,

		/** The ecsp track data. */
		ECSP_TRACK,

		/** The sua violated. */
		SUA_VIOLATED,

		/** The env room configuration. */
		ENV_ROOMCONF,

		/** The env strategic constraints. */
		ENV_STRATEGICCONSTRAINTS,

		/** The env ATSU transmitted. */
		ENV_ATSU_TRANSMITTED,
		
		/** The map boundaries. */
		MAP_BOUNDARIES,

		/** The aman role data. */
		AMAN_ROLE, 
		
		/** The aman sequence data. */
		AMAN_SEQUENCE,
		
		/** The aman hook data. */
		BB_AMAN_HOOK,
		
		/** PROTO_FORCE_TEMPLATE. */
		PROTO_FORCE_TEMPLATE,

		/** PROTO_FORCE_FIELD_COLOR. */
		PROTO_FORCE_FIELD_COLOR,
		
		/** PROTO_FORCE_ALLFIELDS_COLOR. */
		PROTO_FORCE_ALLFIELDS_COLOR,
		
		/** PROTO_FORCE_FIELD_VALUE_PRE. */
		PROTO_FORCE_FIELD_VALUE_PRE,

		/** PROTO_FORCE_FIELD_VALUE_POST. */
		PROTO_FORCE_FIELD_VALUE_POST
	}

	/** The Constant SURVSYS_NAME. */

	public static final String SURVSYS_NAME = "SURVSYS_NAME";

	/** The Constant ADEP. */
	public static final String ADEP = "ADEP";

	/** The Constant ADES. */
	public static final String ADES = "ADES";

	/** The Constant RESPTRAV_ARRAY. */

	public static final String RESPTRAV_ARRAY = "RESPTRAV_ARRAY";

	/** The Constant DEFCOMRESP. */
	public static final String DEFCOMRESP = "DEFCOMRESP";

	/** The Constant RESPTRAV_ID. */
	public static final String RESPTRAV_ID = "RESPTRAV_ID";

	/** The Constant RESP. */
	public static final String RESP = "RESP";

	/** The Constant MY_PHYSICALCONSOLE. */
	public static final String MY_PHYSICALCONSOLE = "MY_PHYSICALCONSOLE";

	/** The Constant MY_LP. */
	public static final String MY_LP = "MY_LP";

	/** The Constant FAMILY. */
	public static final String FAMILY = "FAMILY";

	/** The Constant FREQPLANLIST. */
	public static final String FREQPLANLIST = "JSonArray";

	/** The Constant IAFLIST. */
	public static final String IAFLIST = "JSonArray";

	/** The Constant IAF. */
	public static final String IAF = "IAF";

	/** The Constant LAT. */
	public static final String LAT = "LAT";

	/** The Constant LON. */
	public static final String LON = "LON";

	/** The Constant LIST. */
	public static final String LIST = "LIST";

	/** The Constant PARENT_FLIGHT_NUM. */
	public static final String PARENT_FLIGHT_NUM = "PARENT_FLIGHT_NUM";

	/** The Constant ISWIF. */
	public static final String ISWIF = "ISWIF";

	/** The Constant SCTTRAV_ARRAY. */
	public static final String SCTTRAV_ARRAY = "SCTTRAV_ARRAY";

	/** The Constant WIF_SCTTRAV_ARRAY. */
	public static final String WIF_SCTTRAV_ARRAY = "WIF_SCTTRAV_ARRAY";

	/** The Constant HASWIF. */
	public static final String HASWIF = "HASWIF";

	/** The Constant WIFVISIBLE. */
	public static final String WIFVISIBLE = "WIFVISIBLE";

	/** The Constant ISWIFOWNER. */
	public static final String ISWIFOWNER = "ISWIFOWNER";

	/** The Constant ALT_FLIGHT_NUM. */
	public static final String ALT_FLIGHT_NUM = "WIF_FLIGHT_NUM";

	/** The Constant WIF_CONTEXT_ID. */
	public static final String WIF_CONTEXT_ID = "WIF_CONTEXT_ID";

	/** The Constant IS_WIF_DL. */
	public static final String IS_WIF_DL = "IS_WIF_DL";

	/** The Constant WIF_TIME. */
	public static final String WIF_TIME = "WIF_TIME";

	/** The Constant WIF_ORIGINATOR. */
	public static final String WIF_ORIGINATOR = "WIF_ORIGINATOR";

//	/** The Constant WIF_TYPE. */
//	public static final String WIF_TYPE = "WIF_TYPE";

	/** The Constant WIF_INPUTDATA. */
	public static final String WIF_INPUTDATA = "WIF_INPUTDATA";

	/** The Constant WIF_STATUS. */
	public static final String WIF_STATUS = "WIF_STATUS";

	/** The Constant ISCOORD. */
	public static final String ISCOORD = "ISCOORD";

	/** The Constant HASCOORD. */
	public static final String HASCOORD = "HASCOORD";

	/** The Constant COORD_FLIGHT_NUM. */
	public static final String COORD_FLIGHT_NUM = "COORD_FLIGHT_NUM";

	/** The Constant CALLSIGN. */
	public static final String CALLSIGN = "CALLSIGN";

	/** The Constant LCFS_CPDLC_SECTOR. */
	public static final String LCFS_CPDLC_SECTOR = "LCFS_CPDLC_SECTOR";

	/** The Constant BOTH. */
	public static final String BOTH = "BOTH";

	/** The Constant ONLY_DISABLE. */
	public static final String ONLY_DISABLE = "ONLY_DISABLE";

	/** The Constant ONLY_ENABLE. */
	public static final String ONLY_ENABLE = "ONLY_ENABLE";

	/** The Constant CPDLCENABLEDLP_LIST. */
	public static final String CPDLCENABLEDLP_LIST = "CPDLCENABLEDLP_LIST";

	/** The Constant CPDLCDISABLEDLP_LIST. */
	public static final String CPDLCDISABLEDLP_LIST = "CPDLCDISABLEDLP_LIST";

	/** The Constant RTI. */
	public static final String RTI = "RTI";

	/** The Constant RTI_DATA. */
	public static final String RTI_DATA = "RTI_DATA";

	/** The Constant VISIBLE. */
	public static final String VISIBLE = "VISIBLE";

	/** The Constant LEV_EXT. */
	public static final String LEV_EXT = "LEV_EXT";

	/** The Constant DLCONNECTED. */
	public static final String DLCONNECTED = "DLCONNECTED";

	/** The Constant SUPERVISOR. */
	public static final String SUPERVISOR = "SUPERVISOR";

	/** The Constant MESSAGE_ID. */
	public static final String MESSAGE_ID = "MIN";

	/** The Constant DIALOGUE_TYPE. */
	public static final String DIALOGUE_TYPE = "DIALOGUE_TYPE";

	/** The Constant MIN. */
	public static final String MIN = "MIN";

	/** The Constant MAX. */
	public static final String MAX = "MAX";

	/** The Constant TYPE. */
	public static final String TYPE = "TYPE";

	/** The Constant IS_DL. */
	public static final String IS_DL = "IS_DL";

	/** The Constant ATN_FANS_TYPE. */
	public static final String ATN_FANS_TYPE = "ATN_FANS_TYPE";

	/** The Constant STEP. */
	public static final String STEP = "STEP";

	/** The Constant STEPMIN. */
	public static final String STEPMIN = "STEPMIN";

	/** The Constant STEPMAX. */
	public static final String STEPMAX = "STEPMAX";

	/** The Constant TARGETVALUE. */
	public static final String TARGETVALUE = "TARGETVALUE";

	/** The Constant VALUES. */
	public static final String VALUES = "VALUES";

	/** The Constant PARAMETER_RANGE_LIST. */
	public static final String PARAMETER_RANGE_LIST = "PARAMETER_RANGE_LIST";

	/** The Constant OVF. */
	public static final String OVF = "OVF";

	/** The Constant LP_LIST. */
	public static final String LP_LIST = "LP_LIST";

	/** The Constant RESP_LIST. */
	public static final String RESP_LIST = "RESP_LIST";

	/** The Constant RESPNAME. */
	public static final String RESPNAME = "RESPNAME";

	/** The Constant OENAME. */
	public static final String OENAME = "OENAME";

	/** The Constant EMPTYCONFIG_KEY. */
	public static final String EMPTYCONFIG_KEY = "#EMPTYCONFIG#";
	
	/** The Constant ISXFLSKIP. */
	public static final String ISXFLSKIP = "ISXFLSKIP";

	/** The Constant SKIPCOORDREQ. */
	public static final String SKIPCOORDREQ = "SKIPCOORDREQ";
	
	/** The Constant ECL_IN_FORCE. */
	public static final String ECL_IN_FORCE = "ECL_IN_FORCE";

	/** The Constant ISCONSULTSTARTED. */
	public static final String ISCONSULTSTARTED = "ISCONSULTSTARTED";
	
	/** The Constant XFL_ANSWER. */
	public static final String XFL_ANSWER = "XFL_ANSWER";
	
	/** The Constant SKIP_ANSWER. */
	public static final String SKIP_ANSWER = "SKIP_ANSWER";
	
	/**
	 * The Enum DialogueType.
	 */
	public enum DialogueType {

		/** ROUTE. */
		ROUTE,

		/** VERTICAL. */
		VERTICAL,

		/** SPEED. */
		SPEED,

		/** CROSS. */
		CROSS,

		/** NO_TYPE. */
		NO_TYPE;

		/**
		 * From string.
		 *
		 * @param text the text
		 * @return the Dialogue type
		 */
		public static DialogueType fromString(String text) {
			for (DialogueType b : DialogueType.values()) {
				if (b.name().equals(text)) {
					return b;
				}
			}
			return NO_TYPE;
		}
	}

	/**
	 * The Enum DialogueId.
	 */
	public enum DialogueId {

		/** ROUTE_GI_ID. */
		ROUTE_GI_ID,

		/** VERT_GI_ID. */
		VERT_GI_ID,

		/** SPEED_GI_ID. */
		SPEED_GI_ID,

		/** CROSS_GI_ID. */
		CROSS_GI_ID,

		/** NDA_GI_ID. */
		NDA_GI_ID,

		/** AMC_GI_ID. */
		AMC_GI_ID,

		/** SURVEILLANCE_GI_ID. */
		SURV_GI_ID,

		/** TRANSFER_GI_ID. */
		TRANSFER_GI_ID,

		/** FREETEXT_GI_ID. */
		FREETEXT_GI_ID
	}

}
