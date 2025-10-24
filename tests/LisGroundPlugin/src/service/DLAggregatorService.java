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
package service;

import analyzer.AnalyzerDirectionFinder;
import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxilliary.VehicleInputValuesConstants;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

import java.util.Optional;

/**
 * The Class DLAggregatorService.
 */
public class DLAggregatorService implements IAggregatorService {

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(AnalyzerDirectionFinder.class);

	/**
	 * <pre>
	 * umds                Uplink Mono Dialogue status.
	 *                     Campo compilato da VIF
	 *                     Di seguito i possibili valori di stato del dialogo uplink:
	 *                     0 = Open
	 *                     1 = Open waiting LACK
	 *                     2 = Open Alert
	 *                     3 = Standby
	 *                     4 = Unable
	 *                     5 = Close
	 *                     6 = Close Error
	 *                     7 = Close delivery Error
	 *                     8 = Close timeout
	 *                     17= Close RT
	 *                     0xFF = No Dialogue (valore di default)
	 *
	 * dmds                Downlink Mono Dialogue status.
	 *                     Campo compilato da VIF
	 *                     Di seguito i possibili valori di stato del dialogo downlink:
	 *                     0 = Open
	 *                     1 = Open waiting LACK
	 *                     2 = Open Alert
	 *                     3 = Standby
	 *                     4 = Unable
	 *                     5 = Close
	 *                     6 = Close Error
	 *                     7 = Close delivery Error
	 *                     8 = Close timeout
	 *                     17= Close RT
	 *                     0xFF = No Dialogue (valore di default)
	 *
	 *   ccs                 CPDLC Connection Status
	 *                     Campo compilato da VIF,
	 *                     Indica lo stato della connessione del servizio CPDLC.
	 *                     0 = Non connesso - stato iniziale
	 *                     1 = Connesso
	 *                     2 = Mancata risposta
	 *                     3 = Terminato
	 *                     4 = Disconnesso
	 *                    13 = Richiesta di connessione inviata al veicolo
	 * </pre>
	 *
	 * @param vehicleJson the vehicle json
	 * @param dataNode    the data node
	 */

	@Override
	public void aggregate(final IRawData vehicleJson, final HeaderNode dataNode) {

		
		final String UMDS_KEY = "UMDS";
		final String DMDS_KEY = "DMDS";
		final String CCS_KEY = "CCS";
		final String LOGONTYPE_KEY = "LOGON_TYPE";

		String modBlink = "OFF";
		String depFrame = "OFF";
		String desFrame = "OFF";

		String callBack = "";
		String reqReceived = "";
		String reqReceivedDmds = "";

		String COLOR = VehicleInputValuesConstants.WHITE;
		int dialogSatus = 255;
		String data = "";
		final String loggerCaller = "datalink()";
		final String vid = vehicleJson.getSafeString("VID");

		if (vehicleJson.getSafeInt(LOGONTYPE_KEY) == 1) {
			data = "V";

			if (vehicleJson.getSafeInt(CCS_KEY) == 1) {
				data = "N";



				final Optional<IRawData> vehicle_cpdlc_map_DownLink = BlackBoardUtility.getDataOpt(DataType.BB_VEHICLE_CPDLC_DOWNLINK.name(),
						vid);
				if (vehicle_cpdlc_map_DownLink.isPresent()) {
					reqReceivedDmds = vehicle_cpdlc_map_DownLink.get()
								.getSafeString(VehicleInputValuesConstants.CPDLC_RQ_VIF, "");
				}

				if (vehicleJson.getSafeInt(UMDS_KEY) == 0 || vehicleJson.getSafeInt(UMDS_KEY) == 3
				|| vehicleJson.getSafeInt(UMDS_KEY) == 4 || vehicleJson.getSafeInt(UMDS_KEY) == 8) {
					dialogSatus = vehicleJson.getSafeInt(UMDS_KEY);
					data = "1"; 
					depFrame = "ON";
					desFrame = "ON";
					reqReceived = "";

				} else if (vehicleJson.getSafeInt(UMDS_KEY) == 5 || vehicleJson.getSafeInt(UMDS_KEY) == 17) {
					dialogSatus = vehicleJson.getSafeInt(UMDS_KEY);
					data = "N";
					depFrame = "OFF";
					desFrame = "OFF";
					reqReceived = "";

				} else if (vehicleJson.getSafeInt(UMDS_KEY) == 6) {
					dialogSatus = vehicleJson.getSafeInt(UMDS_KEY);
					data = "V";
					depFrame = "OFF";
					desFrame = "OFF";
					reqReceived = "";
					
				} else if (vehicleJson.getSafeInt(UMDS_KEY) == 8) {
					dialogSatus = vehicleJson.getSafeInt(UMDS_KEY);
					data = "1";
					depFrame = "ON";
					desFrame = "ON";
					reqReceived = "";
				}
				
				
				
				if ((vehicleJson.getSafeInt(DMDS_KEY) == 0 || vehicleJson.getSafeInt(DMDS_KEY) == 2)
						&& (vehicleJson.getSafeInt(UMDS_KEY) == 255 || vehicleJson.getSafeInt(UMDS_KEY) == 5
								|| vehicleJson.getSafeInt(UMDS_KEY) == 8 || vehicleJson.getSafeInt(UMDS_KEY) == 17)) {
					data = "-1";
					dialogSatus = vehicleJson.getSafeInt(DMDS_KEY);
					depFrame = "ON";
					desFrame = "ON";
					reqReceived = reqReceivedDmds;
					if (reqReceived.contains("PROC")) {
						callBack = "CONTEXT_MENU(MENU_FILE=VIF_Unable_Standby_Proced.xml)";
					} else {
						callBack = "CONTEXT_MENU(MENU_FILE=VIF_Unable_Standby_Tow.xml)";
					}
					
				} else if (vehicleJson.getSafeInt(DMDS_KEY) == 3
						&& (vehicleJson.getSafeInt(UMDS_KEY) == 255 || vehicleJson.getSafeInt(UMDS_KEY) == 5
								|| vehicleJson.getSafeInt(UMDS_KEY) == 8 || vehicleJson.getSafeInt(UMDS_KEY) == 17)) {

					data = "-1";
					dialogSatus = vehicleJson.getSafeInt(DMDS_KEY);
					depFrame = "ON";
					desFrame = "ON";
					reqReceived = reqReceivedDmds;
					if (reqReceived.contains("PROC")) {
						callBack = "CONTEXT_MENU(MENU_FILE=VIF_Unable_Proced.xml)";  
					} else {
						callBack = "CONTEXT_MENU(MENU_FILE=VIF_Unable_Tow.xml)"; 
					}
					
				}else if ( vehicleJson.getSafeInt(DMDS_KEY) == 4 && (vehicleJson.getSafeInt(UMDS_KEY) == 255 || vehicleJson.getSafeInt(UMDS_KEY) == 5
						|| vehicleJson.getSafeInt(UMDS_KEY) == 8 || vehicleJson.getSafeInt(UMDS_KEY) == 17)) {
					data = "-1"; 
					dialogSatus = vehicleJson.getSafeInt(DMDS_KEY);
					depFrame = "ON";
					desFrame = "ON";
					reqReceived = "";
					
				} else if (vehicleJson.getSafeInt(DMDS_KEY) == 5 && (vehicleJson.getSafeInt(UMDS_KEY) == 0 || vehicleJson.getSafeInt(UMDS_KEY) == 3
						|| vehicleJson.getSafeInt(UMDS_KEY) == 4 )) {
					data = "1"; 
					dialogSatus = vehicleJson.getSafeInt(UMDS_KEY);
					depFrame = "ON";
					desFrame = "ON";
					reqReceived = "";
					
				} else if ((vehicleJson.getSafeInt(DMDS_KEY) == 5
						|| vehicleJson.getSafeInt(DMDS_KEY) == 8) && vehicleJson.getSafeInt(UMDS_KEY) == 5) {
					dialogSatus = vehicleJson.getSafeInt(UMDS_KEY);
					data = "N";
					depFrame = "OFF";
					desFrame = "OFF";
					reqReceived = "";	
					
				} else if (vehicleJson.getSafeInt(DMDS_KEY) == 5 && vehicleJson.getSafeInt(UMDS_KEY) == 6) {
					dialogSatus = vehicleJson.getSafeInt(UMDS_KEY);
					data = "V";
					depFrame = "OFF";
					desFrame = "OFF";
					reqReceived = "";	
											
				} else if (vehicleJson.getSafeInt(DMDS_KEY) == 8 && (vehicleJson.getSafeInt(UMDS_KEY) == 255 || vehicleJson.getSafeInt(UMDS_KEY) == 17)) {
					data = "-1"; 
					dialogSatus = vehicleJson.getSafeInt(DMDS_KEY);
					depFrame = "ON";
					desFrame = "ON";
					reqReceived = "";
					
				} else if (vehicleJson.getSafeInt(DMDS_KEY) == 8 && (vehicleJson.getSafeInt(UMDS_KEY) == 0 || vehicleJson.getSafeInt(UMDS_KEY) == 3
							|| vehicleJson.getSafeInt(UMDS_KEY) == 4)){
					data = "1"; 
					dialogSatus = vehicleJson.getSafeInt(UMDS_KEY);
					depFrame = "ON";
					desFrame = "ON";
					reqReceived = "";
							
				} else if (vehicleJson.getSafeInt(DMDS_KEY) == 13 && (vehicleJson.getSafeInt(UMDS_KEY) == 255|| vehicleJson.getSafeInt(UMDS_KEY) == 5
						|| vehicleJson.getSafeInt(UMDS_KEY) == 17)){
						
					data = "-1"; 
					dialogSatus = vehicleJson.getSafeInt(DMDS_KEY);
					depFrame = "ON";
					desFrame = "ON";
					reqReceived = reqReceivedDmds;
					if (reqReceived.contains("PROC")) {
						callBack = "CONTEXT_MENU(MENU_FILE=VIF_Unable_Proced.xml)";  
					} else {
						callBack = "CONTEXT_MENU(MENU_FILE=VIF_Unable_Tow.xml)"; 
					}
	
				} else if (vehicleJson.getSafeInt(DMDS_KEY) == 17 && (vehicleJson.getSafeInt(UMDS_KEY) == 255)) {
					dialogSatus = vehicleJson.getSafeInt(DMDS_KEY);
					data = "N";
					depFrame = "OFF";
					desFrame = "OFF";
					reqReceived = "";
	
				} else if (vehicleJson.getSafeInt(DMDS_KEY) == 6) {
					dialogSatus = vehicleJson.getSafeInt(DMDS_KEY);
					data = "V";
					depFrame = "OFF";
					desFrame = "OFF";
					reqReceived = "";
					
				} else if ((vehicleJson.getSafeInt(DMDS_KEY) != 255 && vehicleJson.getSafeInt(DMDS_KEY) != 0
						&& vehicleJson.getSafeInt(DMDS_KEY) != 2 && vehicleJson.getSafeInt(DMDS_KEY) != 3
						&& vehicleJson.getSafeInt(DMDS_KEY) != 4 && vehicleJson.getSafeInt(DMDS_KEY) != 5
						&& vehicleJson.getSafeInt(DMDS_KEY) != 6 && vehicleJson.getSafeInt(DMDS_KEY) != 8
						&& vehicleJson.getSafeInt(DMDS_KEY) != 13 && vehicleJson.getSafeInt(DMDS_KEY) != 17)
						|| (vehicleJson.getSafeInt(UMDS_KEY) != 255 && vehicleJson.getSafeInt(UMDS_KEY) != 0
								&& vehicleJson.getSafeInt(UMDS_KEY) != 3 && vehicleJson.getSafeInt(UMDS_KEY) != 4
								&& vehicleJson.getSafeInt(UMDS_KEY) != 5 && vehicleJson.getSafeInt(UMDS_KEY) != 6
								&& vehicleJson.getSafeInt(UMDS_KEY) != 8 && vehicleJson.getSafeInt(UMDS_KEY) != 17)) {
					logger.logError(loggerCaller,
							"Wrong Direction Datalink: UMDS " + vehicleJson.getSafeInt(UMDS_KEY)
									+ " DMDS " + vehicleJson.getSafeInt(DMDS_KEY));
				}


				if (dialogSatus == 0) { 
					COLOR = VehicleInputValuesConstants.LIGHT_BLUE; 
				}

				if (dialogSatus == 2) { 
					COLOR = VehicleInputValuesConstants.LIGHT_BLUE; 
					modBlink = "ON";
				}

				if (dialogSatus == 3) { 
					COLOR = VehicleInputValuesConstants.YELLOW; 
				}

				if (dialogSatus == 4) { 
					COLOR = VehicleInputValuesConstants.ORANGE; 
				}

				if (dialogSatus == 5) { 
					COLOR = VehicleInputValuesConstants.GRAY; 
				}
		
				if (dialogSatus == 17) {  
					COLOR = VehicleInputValuesConstants.WHITE; 
				}

				if (dialogSatus == 6 || dialogSatus == 8) { 
					COLOR = VehicleInputValuesConstants.MAGEN; 
				}
				
				if (dialogSatus == 13) { 
					COLOR = VehicleInputValuesConstants.YELLOW; 
					modBlink = "ON";
				}
			}
		}

		dataNode.addLine(VehicleInputValuesConstants.DL, data).setAttribute(EdmModelKeys.Attributes.COLOR, COLOR)
				.setAttribute(EdmModelKeys.Attributes.MOD_BLINK, modBlink);

		if (!vehicleJson.getSafeString(VehicleInputValuesConstants.DEP).isEmpty()) {
			dataNode.addLine("FRAME_DEP", depFrame).setAttribute(EdmModelKeys.Attributes.COLOR, COLOR);
		} else {
			dataNode.addLine("FRAME_DEP", "OFF").setAttribute(EdmModelKeys.Attributes.COLOR, "");
		}

		if (!vehicleJson.getSafeString(VehicleInputValuesConstants.DES).isEmpty()) {
			dataNode.addLine("FRAME_DES", desFrame).setAttribute(EdmModelKeys.Attributes.COLOR, COLOR);
		} else {
			dataNode.addLine("FRAME_DES", "OFF").setAttribute(EdmModelKeys.Attributes.COLOR, "");
		}

		dataNode.addLine("CPDLC_RQ_VIF", reqReceived).setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK,
				callBack);
		
		
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		return VehicleInputValuesConstants.DL_VID;
	}
}
