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
package applicationLIS;

// TODO: Auto-generated Javadoc
/**
 * The Class BlackBoardConstants_LIS.
 */
public class BlackBoardConstants_LIS {

	/**
	 * The Enum DataType.
	 */
	public enum DataType {

		/** The bb cpdlc freetext. */
		BB_CPDLC_FREETEXT,

		/** The vehicle. */
		VEHICLE,

		/** The rwy sca conflict. */
		// RWY_SCA_CONFLICT,

		/** The rwy sca alarm. */
		RWY_ALARM,

		/** The sca. */
		SCA,

		/** The env rwy in use. */
		ENV_RWY_IN_USE,

		/** The env aerodromeinfo. */
		ENV_AERODROMEINFO,

		/** The bb vif. */
		BB_VIF,

		/** The snet. */
		SNET,

		/** The track. */
		TRACK,

		/** The track modes. */
		TRACK_MODES,

		/** The flight extflight. */
		FLIGHT_EXTFLIGHT,

		/** The mtcd item notify. */
		MTCD_ITEM_NOTIFY,

		/** The cpdlc. */
		CPDLC,

		/** The tct. */
		TCT,

		/** The bb radar. */
		BB_RADAR,

		/** The bb wp settings. */
		WP_SETTINGS,

		/** The env moc. */
		ENV_MOC,

		/** The stn lost. */
		TRKID_LOST,

		/** The plot. */
		PLOT,

		/** The probe. */
		PROBE,

		/** The mtcd oxf time notify. */
		MTCD_OXF_TIME_NOTIFY,

		/** The env own. */
		ENV_OWN,

		/** The systemCenter. */
		ENV_SYSTEM_CENTER,

		/** The mtcd interaction item notify. */
		MTCD_INTERACTION_ITEM_NOTIFY,

		/** The bb callsign flight. */
		BB_CALLSIGN_FLIGHT,

		/** The flight coordination. */
		FLIGHT_COORDINATION,

		/** The preloaded color set. */
		PRELOADED_COLOR_SET,

		/** The route plan. */
		ROUTE_PLAN,

		/** The bb flight cpdlc downlink. */
		BB_FLIGHT_CPDLC_DOWNLINK,

		/** The bb flight cpdlc uplink. */
		BB_FLIGHT_CPDLC_UPLINK,

		/** The bb stn flightnum. */
		BB_TRKID_FLIGHTNUM,

		/** The bb flightnum stn. */
		BB_FLIGHTNUM_TRKID,

		/** The snet map list. */
		SNET_MAP_LIST,

		/** The preloaded deviation downlinked. */
		PRELOADED_DEVIATION_DOWNLINKED,

		/** The gi fork configuration. */
		GI_FORK_CONFIGURATION,

		/** The flight adsc report. */
		FLIGHT_ADSC_REPORT,

		/** The function filter. */
		FUNCTION_FILTER,

		/** The env qnainfo. */
		ENV_QNAINFO,

		/** The env oes. */
		ENV_OES,

		/** The env cdbinfo. */
		ENV_CDBINFO,

		/** The env functionfilternotify. */
		ENV_FUNCTIONFILTERNOTIFY,

		/** The env sector table. */
		ENV_SECTOR_TABLE,

		/** The env password. */
		ENV_PASSWORD,

		/** The env msp. */
		ENV_MSP,

		/** The env server status. */
		ENV_SERVER_STATUS,

		/** The env radar table. */
		ENV_RADAR_TABLE,

		/** The env probe. */
		ENV_PROBE,

		/** The env gdb load. */
		ENV_GDB_LOAD,

		/** The env time. */
		ENV_TIME,

		/** The env oldi line. */
		ENV_OLDI_LINE,

		/** The env hdi sector level. */
		ENV_HDI_SECTOR_LEVEL,

		/** The bb mtcd item th prob. */
		BB_MTCD_ITEM_TH_PROB,

		/** The env ldap. */
		ENV_LDAP,

		/** The env login data. */
		ENV_LOGIN_DATA,

		/** The xai hdi state. */
		XAI_HDI_STATE,

		/** The bb sda. */
		BB_SDA,

		/** The bb mtcd label. */
		BB_MTCD_LABEL,

		/** The bb stn vid. */
		BB_STN_VID,

		/** The apw map. */
		APW_MAP,

		/** The env supervisor message. */
		ENV_SUPERVISOR_MESSAGE,

		/** The meteo ais data. */
		METEO_AIS_DATA,

		/** The fpm line data. */
		FPM_LINE_DATA,

		/** The tct diag notify. */
		TCT_DIAG_NOTIFY,

		/** The mtcd diag notify. */
		MTCD_DIAG_NOTIFY,

		/** The external order. */
		EXTERNAL_ORDER,

		/** The supervisor map. */
		SUPERVISOR_MAP,

		/** The GENERI C D B user settings. */
		GENERIC_DB_UserSettings,

		/** The xai state. */
		XAI_STATE,

		/** The cpdlc history. */
		CPDLC_HISTORY,

		/** The env failed logon notify. */
		ENV_FAILED_LOGON_NOTIFY,

		/** The bb mtcd item th time. */
		BB_MTCD_ITEM_TH_TIME,

		/** The bb time or distance. */
		BB_TIME_OR_DISTANCE,

		/** The bb snet alarm ack. */
		BB_SNET_ALARM_ACK,

		/** The bb str ack. */
		BB_STR_ACK,

		/** The bb pot ack. */
		BB_POT_ACK,

		/** The bb mis ack. */
		BB_MIS_ACK,

		/** The bb threshold tct. */
		BB_THRESHOLD_TCT,

		/** The env myresponsibilities. */
		ENV_MYRESPONSIBILITIES,

		/** The env atso cop. */
		ENV_ATSO_COP,
		/** The flight probe. */
		FLIGHT_PROBE,
		/** The env history logon notify. */
		ENV_HISTORY_LOGON_NOTIFY,
		/** The flight inactive. */
		FLIGHT_INACTIVE,
		/** The bb order dialogue. */
		BB_ORDER_DIALOGUE,

		/** Direction Finder (HDI). */
		DIRECTION_FINDER,

		/** Direction Finder (JV). */
		BB_DIRECTION_FINDER_JV,
		/** The env datalinkpreformattedtext. */
		ENV_DATALINKPREFORMATTEDTEXT,
		/** The ads area. */
		ADS_AREA,
		/** The single flight area contract. */
		FLIGHT_CONTRACT,

		/** The bb sca ack. */
		BB_SCA_ACK,
		/** The bb vehicle cpdlc downlink. */
		BB_VEHICLE_CPDLC_DOWNLINK,

		/** The weather maps. */
		WEATHER_MAPS,

		/** The env conspicuity table. */
		ENV_CONSPICUITY_TABLE,

		/** The unk. */
		UNK,

		/** The env aif. */
		ENV_AIF,

		/** The env dps. */
		ENV_DPS,

		/** The env gdn. */
		ENV_GDN,

		/** The env gdn. */
		ENV_STOPBAR,

		/** The env path run. */
		ENV_PATH_RUN,

		/** The env ldap c2. */
		ENV_LDAP_C2,

		/** The env usi vers. */
		ENV_USI_VERS,

		/** The env level sector. */
		ENV_LEVEL_SECTOR,

		/** The env change world. */
		ENV_CHANGE_WORLD,

		/** The env physical. */
		ENV_PHYSICAL,

		/** The env qnhinfo. */
		ENV_QNHINFO,

		/** The env sid. */
		ENV_SID,

		/** The env star. */
		ENV_STAR,

		/** The env diag message. */
		DIAG,

		/** The order probe. */
		ORDER_PROBE,

		/** The env hdi table freq. */
		ENV_HDI_TABLE_FREQ,

		/** The flight fpm. */
		FLIGHT_FPM,

		/** The mtcd probe item notify. */
		MTCD_PROBE_ITEM_NOTIFY,

		/** The mtcd minseparation notify. */
		MTCD_MINSEPARATION_NOTIFY,

		/** The mtcd env aif. */
		MTCD_ENV_AIF,

		/** The tct tentative. */
		TCT_TENTATIVE,

		/** The env qna info. */
		ENV_QNA_INFO,

		/** The env rwy airp control sec. */
		ENV_RWY_AIRP_CONTROL_SEC,
		/** The preloaded check template. */
		PRELOADED_CHECK_TEMPLATE,
		/** The preloaded check template. */
		PRELOADED_CHECK_AIRPORT,
		/** The env air category. */
		ENV_AIR_CATEGORY,
		/** The snt trackid. */
		STN_TRKID,
		/** The cpdlc vif. */
		CPDLC_VIF,
		/** The bb vehicle cpdlc uplink. */
		BB_VEHICLE_CPDLC_UPLINK,
		/** The bb vid stn. */
		BB_VID_STN,

		/** the bb Strip *. */
		BB_STRIP,

		/** the Automatic Strip Print *. */
		BB_AUTOMATIC_STRIP_PRINT,

		/** the Automatic Strip Print *. */
		PRELOADED_CDB_SUBSCRIPTION,

		/** the STATE_BYP *. */
		STATE_BYP,
		/** The ENV_DISS_CHANGE. */
		ENV_DISS_CHANGE,
		/** The bb show arp. */
		BB_SHOW_ARP,

		/** The bb hook. */
		BB_HOOK,

		/** The flj trj. */
		FLIGHT_TRJ,

		/** The flj trj. */
		FLIGHT_TRJ_PROBE,

		/** The HDI_CHANGE_WORLD. */
		HDI_CHANGE_WORLD,

		/** The flight pnt. */
		FLIGHT_PNT,

		/** The bb opengiforkpanel. */
		BB_OPENGIFORKPANEL,
		/** The HDI_CHANGE_WORLD. */
		HDI_DELETE_TRACKS,

		/** The DBS */
		DBS,
		/** The bb DBS Switch label */
		BB_DBS_SWITCH_LABEL,

		/** The Preloaded ADES_ZONES */
		PRELOADED_ADES_ZONES,

		/** The Preloaded ADES_ZONES */
		AMAN_NSL_ITEM_NOTIFY,
		/** The dump gif. */
		DUMP_GIF,

		/** The error connection. */
		BB_ERROR_CONNECTION,

		/** The BB_ORDER_METAIS. */
		BB_ORDER_METAIS,

		/** PROTO_FORCE_TEMPLATE. */
		PROTO_FORCE_TEMPLATE,

		/** PROTO_FORCE_FIELD_COLOR. */
		PROTO_FORCE_FIELD_COLOR,

		/** PROTO_FORCE_ALLFIELDS_COLOR. */
		PROTO_FORCE_ALLFIELDS_COLOR,

		/** PROTO_FORCE_FIELD_VALUE_PRE. */
		PROTO_FORCE_FIELD_VALUE_PRE,

		/** PROTO_FORCE_FIELD_VALUE_POST. */
		PROTO_FORCE_FIELD_VALUE_POST,

		/** TCAT_MESSAGE. */
		TCAT_MESSAGE,

		/** TCAT_MESSAGE. */
		BB_TCAT_ACK,

		/** AIW_MAP */
		AIW_MAP

	}

}
