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
package auxiliary.flight;

import java.util.HashMap;

import com.gifork.commons.data.IRawData;

import auxiliary.correlation.CorrelationInputConstants;
import auxiliary.fpm.FpmInputConstants;
import common.BlackMap;
import common.CommonConstants;

/**
 * The Class FlightBlackMap.
 */
public class FlightBlackMap extends BlackMap {

	/**
	 * Instantiates a new flight black map.
	 */
	public FlightBlackMap() {
		this.map = new HashMap<>();

		this.map.put(FlightInputConstants.TEMPLATE, 0);
		this.map.put(FlightInputConstants.CFL, 0);
		this.map.put(FlightInputConstants.ARC, 0);
		this.map.put(FlightInputConstants.CALLSIGN, 0);
		this.map.put(FlightInputConstants.DLE, 0);
		this.map.put(FlightInputConstants.MES_TRA, 0);
		this.map.put(FlightInputConstants.MES_REV, 0);

		this.map.put(FlightInputConstants.EPP_WARNING_POSITION, 0);
		this.map.put(FlightInputConstants.EPP_WARNING_LEVEL, 0);
		this.map.put(FlightInputConstants.EPP_WARNING_TIME, 0);
		this.map.put(FlightInputConstants.LEV_COPY, 0);
		this.map.put(FlightInputConstants.EMERGENCY_SL, 0);

		this.map.put(FlightInputConstants.ISGROUND, 0);

		this.map.put(FlightInputConstants.PELDRAWING, 0);

		this.map.put(FlightInputConstants.ISMANASSNOTCOMPLIANT, 0);

		this.map.put(FlightOutputConstants.COO_REM, 0);
		this.map.put(FlightInputConstants.XFL, 0);
		this.map.put(FlightInputConstants.PEL, 0);

		this.map.put(FlightInputConstants.SCT, 0);
		this.map.put(FlightInputConstants.FRQ, 0);

		this.map.put(IRawData.KEY_OPERATION, 0);
		this.map.put(IRawData.KEY_DATATYPE, 0);

		this.map.put(FlightInputConstants.LOF_NAN_COLOR, 0);

		this.map.put(FpmInputConstants.CFL_INST_CONF, 0);

		this.map.put(CommonConstants.STN, 0);
		this.map.put(CorrelationInputConstants.CORLM_STATUS, 0);

		this.map.put(FlightInputConstants.NEXTORDER, 0);

		this.map.put(FlightInputConstants.DHRPN, 0);

		this.map.put(FlightInputConstants.TDR, 0);

		this.map.put(FlightInputConstants.IS_FPT, 0);

		this.map.put("SURV", 0);
		this.map.put("APAM_GLOB_FILT", 0);
		this.map.put("ISVFL", 0);

		this.map.put("OWNTRA", 0);
		this.map.put("RIOFEXT", 0);

		this.map.put("ATD_F", 0);

		this.map.put("EOME_POINT_OUT", 0);
		this.map.put("RIOF", 0);
		this.map.put("ACS", 0);
		this.map.put("EIME_POINT_OUT", 0);
		this.map.put("ISTCL", 0);
		this.map.put("XFLDRAWING", 0);
		this.map.put("ISABINOROUTE", 0);
		this.map.put("TOOVERFLYROUTE", 0);

		this.map.put("ISFLDI", 0);
		this.map.put("DAIW_GLOB_FILT", 0);
		this.map.put("CPDLC_NEXT_FREQ", 0);
		this.map.put("ISSCL", 0);
		this.map.put("SELTIME", 0);
		this.map.put("NPB", 0);
		this.map.put("EXITTIME", 0);
		this.map.put("MASK_UDS", 0);
		this.map.put("NEARTOEXIT", 0);
		this.map.put("ISSTARTUP", 0);
		this.map.put("CSSR", 0);

		this.map.put("CXUDS", 0);
		this.map.put("NRF_ETO", 0);
		this.map.put("MNV_COORD_STATUS", 0);
		this.map.put("ISSEL", 0);
		this.map.put("ISTAXI", 0);
		this.map.put("NRF", 0);
		// this.map.put("TACTIME", 0);
		this.map.put("NSC", 0);
		this.map.put("RONIDX", 0);
		this.map.put("EAT", 0);
		this.map.put("MAL", 0);
		this.map.put("ISPAL", 0);
		this.map.put("NTC", 0);
		this.map.put("NAN_OLM", 0);
		this.map.put("STARJUNCTPT", 0);
		this.map.put("NTS", 0);
		this.map.put("NAN_OLS", 0);
		this.map.put("MSE", 0);
		this.map.put("ISSIL", 0);
		this.map.put("TRANSPOINTLONGITUDE", 0);
		this.map.put("CPB", 0);

		this.map.put("ETN", 0);
		this.map.put("MTS", 0);
		this.map.put("SIDJUNCTPT", 0);
		this.map.put("MES_REV_COLOR", 0);
		this.map.put("RIIFEXT", 0);
		this.map.put("AMM", 0);
		this.map.put("STCA_GLOB_FILT", 0);
		this.map.put("IFIXID", 0);

		this.map.put("RIIF", 0);
		this.map.put("LOF_OIS", 0);
		this.map.put("LCI", 0);
		this.map.put("LOF_OIM", 0);

		this.map.put("ISCPDLCCONNECTED", 0);
		this.map.put("LTI", 0);
		this.map.put("ISFIREXITLIST", 0);
		this.map.put("LTO", 0);
		this.map.put("FSSR", 0);
		this.map.put("MNV_ISCOORDIN", 0);
		this.map.put("LOF_OLS", 0);
		this.map.put("OLDI_EST", 0);
		this.map.put("LOF_OLM", 0);
		this.map.put("TRANSPOINTLATITUDE", 0);
		this.map.put("CCA", 0);
		this.map.put("CS_DRAWING", 0);
		this.map.put("NAN_OIM", 0);
		this.map.put("MSAW_GLOB_FILT", 0);

		this.map.put("NAN_OIS", 0);
		this.map.put("WARNING_TIME", 0);
		this.map.put("WARNING_LEVEL", 0);
		this.map.put("EP_CL", 0);
		this.map.put("WARNING_POSITION", 0);
		this.map.put("CST", 0);
		this.map.put("ARC_TYPE", 0);
		this.map.put("IS_MTCD", 0);
		this.map.put("IS_SUA", 0);
		this.map.put("RATECLIMIND", 0);
		this.map.put("MES_REV_WARN", 0);
		this.map.put("MOOS", 0);
		this.map.put("MIOS", 0);
		this.map.put("MES_TRA_WARN", 0);
		this.map.put(FlightInputConstants.ARC_ARROW, 0);
	}
}
