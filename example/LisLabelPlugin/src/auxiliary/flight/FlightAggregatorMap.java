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

import application.pluginService.ServiceExecuter.IAggregatorService;
import application.pluginService.ServiceExecuter.ServiceExecuter;
import auxiliary.correlation.CorrelationOutputConstants;
import auxiliary.fpm.FpmInputConstants;
import common.AggregatorMap;
import common.CommonConstants;
import service.correlation.CORLMSTATAggregatorService;
import service.cpdlc.LATERALDEVAggregatorService;
import service.flight.ARCAggregatorService;
import service.flight.ASSIGNEDSPEEDINKNOTSAggregatorService;
import service.flight.CALLSIGNAggregatorService;
import service.flight.CFLAggregatorService;
import service.flight.CLKAggregatorService;
import service.flight.COOAggregatorService;
import service.flight.CSTAggregatorService;
import service.flight.DLAggregatorService;
import service.flight.EMERGENCY_RPASAggregatorService;
import service.flight.EMGSLAggregatorService;
import service.flight.EMGSSRAggregatorService;
import service.flight.EPCLAggregatorService;
import service.flight.FRQAggregatorService;
import service.flight.FreeTextAggregatorService;
import service.flight.GenericAggregatorService;
import service.flight.IRFAggregatorService;
import service.flight.ISMTCDAggregatorService;
import service.flight.LOFNANAggregatorService;
import service.flight.LSVAggregatorService;
import service.flight.MCEAggregatorService;
import service.flight.MCPAggregatorService;
import service.flight.MESREVAggregatorService;
import service.flight.MESREVWRNAggregatorService;
import service.flight.MESTRAAggregatorService;
import service.flight.MESTRAWRNAggregatorService;
import service.flight.MIOSAggregatorService;
import service.flight.MISAggregatorService;
import service.flight.MNV_HDGAggregatorService;
import service.flight.MOOSAggregatorService;
import service.flight.NEXTORDERAggregatorService;
import service.flight.NXSAggregatorService;
import service.flight.PBCSAggregatorService;
import service.flight.PELAggregatorService;
import service.flight.PKBAggregatorService;
import service.flight.RWYAggregatorService;
import service.flight.SCTAggregatorService;
import service.flight.SNCColorAggregatorService;
import service.flight.STAMAggregatorService;
import service.flight.STARAggregatorService;
import service.flight.TODAggregatorService;
import service.flight.TYPEAggregatorService;
import service.flight.UNDOORDERAggregatorService;
import service.flight.XFLAggregatorService;
import service.fpm.CFLFLAGAggregatorService;

/**
 * The Class FlightAggregatorMap.
 */
public class FlightAggregatorMap extends AggregatorMap {

	/**
	 * Instantiates a new flight aggregator map.
	 */

	public FlightAggregatorMap() {

		put(FlightOutputConstants.COO_REM, new COOAggregatorService());

		put(FlightOutputConstants.EMERGENCY_SSR, new EMGSSRAggregatorService());

		put(FlightOutputConstants.ATYPE, new TYPEAggregatorService());

		put(FlightOutputConstants.DL, new DLAggregatorService());

		put(FlightOutputConstants.RWY, new RWYAggregatorService());

		put(FlightOutputConstants.PKB, new PKBAggregatorService());

		put(FlightOutputConstants.SCT, new SCTAggregatorService());

		put(FlightOutputConstants.FRQ, new FRQAggregatorService());

		put(FlightOutputConstants.NXS, new NXSAggregatorService());

		put(FlightOutputConstants.CLK, new CLKAggregatorService());

		put(FlightOutputConstants.CST, new CSTAggregatorService());

		put(FlightOutputConstants.EP_CL, new EPCLAggregatorService());

		put(FlightOutputConstants.XFL, new XFLAggregatorService());

		put(FlightOutputConstants.PEL, new PELAggregatorService());

		put(FlightOutputConstants.MES_TRA, new MESTRAAggregatorService());

		put(FlightOutputConstants.MES_TRA_WARN, new MESTRAWRNAggregatorService());

		put(FlightOutputConstants.MES_REV, new MESREVAggregatorService());

		put(FlightOutputConstants.MES_REV_WARN, new MESREVWRNAggregatorService());

		put(FlightOutputConstants.MIOS, new MIOSAggregatorService());

		put(FlightOutputConstants.MOOS, new MOOSAggregatorService());

		put(FlightOutputConstants.CALLSIGN, new CALLSIGNAggregatorService());

		put(FlightOutputConstants.EMERGENCY_SL, new EMGSLAggregatorService());

		put(FlightOutputConstants.TOD, new TODAggregatorService());

		put(FlightOutputConstants.CFL, new CFLAggregatorService());

		put(FlightOutputConstants.MNV_HDG, new MNV_HDGAggregatorService());

		put(FlightOutputConstants.ARC, new ARCAggregatorService());

		put(FlightOutputConstants.ASSIGNEDSPEEDINKNOTS, new ASSIGNEDSPEEDINKNOTSAggregatorService());

		put(FlightOutputConstants.LOF_NAN, new LOFNANAggregatorService());

		put(FpmInputConstants.CFL_INST_CONF, new CFLFLAGAggregatorService());

		put(CorrelationOutputConstants.CORLM_STATUS, new CORLMSTATAggregatorService());

		put(FlightInputConstants.NEXTORDER, new NEXTORDERAggregatorService());

		put(FlightOutputConstants.IS_MTCD, new ISMTCDAggregatorService());

		put(FlightInputConstants.UNDOORDER, new UNDOORDERAggregatorService());

		put(FlightInputConstants.IRF, new IRFAggregatorService());

		put(FlightOutputConstants.MISSED_APPROACH, new MISAggregatorService());

		put(FlightOutputConstants.DLE_1, new MCEAggregatorService());

		put(FlightOutputConstants.SNC, new SNCColorAggregatorService());

		put(FlightInputConstants.FLIGHT_NUM, new GenericAggregatorService());

		put(CommonConstants.IS_RNP, new PBCSAggregatorService());
		put ("LSV",                 new LSVAggregatorService());
		
		put(FlightInputConstants.MCP_CODE, new MCPAggregatorService());
		
		put("FREE_TEXT", new FreeTextAggregatorService());
		
		put(FlightInputConstants.EMERGENCY_RPAS, new EMERGENCY_RPASAggregatorService());
		
		put(FlightOutputConstants.STAR_BV4, new STARAggregatorService());
		
		put("STAM", new STAMAggregatorService());

		put(FlightOutputConstants.LAT, new LATERALDEVAggregatorService());

	}

	/**
	 * Adds the service ext.
	 *
	 * @param key     the key
	 * @param service the service
	 */
	public void addServiceExt(final String key, final IAggregatorService service) {
		put(key, service);
		ServiceExecuter.getInstance().RegisterService(service);
	}

}
