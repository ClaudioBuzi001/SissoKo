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

package com.gifork.data_exchange.gateways.jip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Optional;

import com.fourflight.WP.ECI.edm.HeaderNode.HeaderType;
import com.gifork.auxiliary.Capabilities;
import com.gifork.auxiliary.XmlToJsonObject;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.GiForkConstants;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.commons.log.LoggerFactory;
import com.gifork.data_exchange.gateways.HostGatewayJIP;
import com.leonardo.infrastructure.Strings;
import com.leonardo.infrastructure.log.ILogger;

import applicationLIS.BlackBoardConstants_LIS.DataType;

/**
 * The Class JIPClientThread.
 *
 */
public class JIPClientThread extends Thread {

	/** The Constant TH_PREFIX. */
	private static final String TH_PREFIX = "GF.JIPC";

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(JIPClientThread.class);

	/** The socket. */
	private Socket m_socket = null;

	/** The input reader. */

	/** The message dispatcher. */
	private final MessageDispatcher m_messageDispatcher = new MessageDispatcher();

//	/** The Constant chechFPT. */
//	private static final boolean chechFPT = ConfigurationFile.getBoolProperties("IS_NOT_VIS_FPT_TRACK");

	/**
	 * Instantiates a new JIP client thread.
	 *
	 * @param socket the socket
	 */
	public JIPClientThread(Socket socket) {
		super(TH_PREFIX + "@" + socket.getInetAddress() + ":" + socket.getPort());
		m_socket = socket;
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		final String loggerCaller = "run()";
		StringBuilder builder = new StringBuilder(65535);
		try {
			try (InputStream is = m_socket.getInputStream()) {
				try (InputStreamReader isR = new InputStreamReader(is)) {
					try (BufferedReader m_inputReader = new BufferedReader(isR)) {
						m_messageDispatcher.start();
						String message = m_inputReader.readLine();
						while (message != null && !isInterrupted()) {
							builder.append(message);
							if (isJsnoFormat(message)) {
								m_messageDispatcher.addForJSON(builder.toString());
								Capabilities.getRecorders().forEach(r -> r.messageIn(builder.toString()));
								builder.setLength(0);
							} else if (message.endsWith(HostGatewayJIP.END_MSG)) {

								String jmessage = Strings.removeEnd(message, HostGatewayJIP.END_MSG.length());
								if (isJsnoFormat(jmessage)) {
									jmessage = Strings.removeEnd(builder.toString(), HostGatewayJIP.END_MSG.length());
									m_messageDispatcher.addForJSON(jmessage);
									Capabilities.getRecorders().forEach(r -> r.messageIn(builder.toString()));
								} else {
									m_messageDispatcher.addForXML(builder.toString());
								}
								builder.setLength(0);
							}
							if (m_socket.isConnected() && m_socket.isBound()) {
								message = m_inputReader.readLine();
							}
						}
					}
				}
			}
		} catch (IOException e1) {
			logger.logError(loggerCaller, e1);
			try {
				m_socket.close();
			} catch (IOException e) {
				logger.logError(loggerCaller, "On Close Socket");
				logger.logError(loggerCaller, e);
			}
		} finally {
			m_messageDispatcher.stop();
		}

	}

	/**
	 * Checks if is jsno format.
	 *
	 * @param msg the msg
	 * @return true, if is jsno format
	 */
	private static boolean isJsnoFormat(String msg) {
		for (int i = msg.length() - 1; i >= 0; i--) {
			var c = msg.charAt(i);
			if (c != '\n' && c != ' ') {
				return c == '}';
			}
		}
		return false;
	}

	/**
	 * Adds the json BB.
	 *
	 * @param jsonText the json text
	 */
	public static void addJsonBB(String jsonText) {
//		Capabilities.getRecorders().forEach(r -> r.messageInJson(jsonText));
		if (jsonText.startsWith("{\"" + HeaderType.SERVICE.toString())) {

			IRawData rawdata = XmlToJsonObject.transformJSONServiceToRawData(jsonText);
			if (rawdata.getSafeString("APP_SOURCE").isEmpty()) {
				String serviceName = rawdata.getType();
				rawdata.put(GiForkConstants.SERVICE_NAME, serviceName);
			}
			ManagerBlackboard.addRawData(rawdata, rawdata.getType());
		} else {
			try {
				IRawData rawdata = RawDataFactory.createFromJson(jsonText);

//				if (rawdata.getType().equals(DataType.TRACK.name())) {
//
//					String stn = rawdata.getSafeString("STN");
//					String trackId = rawdata.getSafeString("TRACK_ID");
//					rawdata.remove("ABS_MC");
//					rawdata.remove("MODE_2_G");
//					rawdata.remove("BDS30_ARA43");
//					rawdata.remove("BDS30_ARA44");
//					rawdata.remove("BDS30_ARA45");
//					rawdata.remove("BDS30_ARA46");
//					rawdata.remove("VFI");
//					rawdata.remove("BDS30_RAC");
//					rawdata.remove("DAAL");
//					rawdata.remove("MODE_2_V");
//					rawdata.remove("LSN");
//					rawdata.remove("MODE_2_L");
//					rawdata.remove("3D_HEIGHT");
//					rawdata.remove("BDS30_TTI");
//					rawdata.remove("MODE_1_V");
//					rawdata.remove("CONTR_RADAR_WORD1");
//					rawdata.remove("MODE_1_L");
//					rawdata.remove("BDS30_ARA48_54");
//					rawdata.remove("CONTR_RADAR_WORD4");
//					rawdata.remove("CONTR_RADAR_WORD3");
//					rawdata.remove("CONTR_RADAR_WORD2");
//					rawdata.remove("MODE_1_G");
//					rawdata.remove("IS_RECEIVED_CALLSIGN_SSR_DUP");
//					rawdata.remove("EXT_MODE_1");
//
//					if (rawdata.getOperation() == Operation.DELETE
//							|| rawdata.getSafeString("oper").equalsIgnoreCase(Operation.DELETE.getText())) {
//						removeTrackRawData(trackId);
//					} else {
//						IRawData stnId = RawDataFactory.create();
//						stnId.put("TRACK_ID", trackId);
//						stnId.put("STN", stn);
//
//						double xpos = rawdata.getSafeDouble("X_POS");
//						double ypos = rawdata.getSafeDouble("Y_POS");
//
//						WorldPointNM pos = new WorldPointNM(xpos / 65536.0, ypos / 65536.0);
//						LatLon latLon = ConversionFunction.XYToLL(pos);
//
//						rawdata.put("LONGITUDE", latLon.getLongitude());
//						rawdata.put("LATITUDE", latLon.getLatitude());
//
//						Utils_LIS.addCorrelationBB(stn, "");
//						ManagerBlackboard.addRawData(stnId, DataType.STN_TRKID.name());
//						ManagerBlackboard.addRawData(rawdata, DataType.TRACK.name());
//
//						boolean trackMlatModes = false;
//						if (rawdata.getSafeBoolean("IS_MLAT_TRACKERS")
//								&& (rawdata.getSafeInt("MLAT_MSG_TYPE") == 1
//										|| rawdata.getSafeInt("MLAT_MSG_TYPE") == 2)
//								|| rawdata.getSafeInt("MODE_S", 0) != 0 || rawdata.getSafeBoolean("IS_ADS_TRACKERS")) {
//							trackMlatModes = true;
//						}
//
//						if (trackMlatModes && (!rawdata.getSafeBoolean("IS_FPT_TRACKER") || !chechFPT)) {
//							elaboratefromHDIModeS(jsonText);
//						} else {
//							BlackBoardUtility.removeData(DataType.TRACK_MODES.name(), trackId);
//						}
//
//						if (rawdata.getSafeBoolean("IS_HIJ") || rawdata.getSafeBoolean("IS_EMG")
//								|| rawdata.getSafeBoolean("IS_RCF")) {
//							elaboratefromHDISnet(jsonText);
//						} else {
//							BlackBoardUtility.removeData(DataType.SNET.name(),
//									"EMG_" + rawdata.getSafeString("TRACK_ID"));
//						}
//
//						if (isTcasAlarm(rawdata)) {
//							elaborateTcas(jsonText);
//						} else {
//							BlackBoardUtility.removeData(DataType.SNET.name(),
//									"TCAS_" + rawdata.getSafeString("TRACK_ID"));
//						}
//
//					}
//				} else if (rawdata.getType().equals(DataType.PLOT.name())) {
//					if (rawdata.getOperation() == Operation.DELETE
//							|| rawdata.getSafeString("oper").equalsIgnoreCase(Operation.DELETE.getText())) {
//						BlackBoardUtility.removeData(DataType.PLOT.name(), rawdata.getId());
//					} else {
//						rawdata.put("PLOT_IDENT", rawdata.getId());
//						double xpos = rawdata.getSafeDouble("X_POS") / 65536.0;
//						double ypos = rawdata.getSafeDouble("Y_POS") / 65536.0;
//
//						WorldPointNM pos = new WorldPointNM(xpos, ypos);
//						LatLon latLon = ConversionFunction.XYToLL(pos);
//
//						rawdata.put("LONGITUDE", latLon.getLongitude());
//						rawdata.put("LATITUDE", latLon.getLatitude());
//						BlackBoardUtility.addData(DataType.PLOT.name(), rawdata);
//					}
//				} else if (rawdata.getType().equals(DataType.HDI_CHANGE_WORLD.name())) {
//
//					String type = rawdata.getSafeString("HDI_CHANGE_WORLD");
//					if (type.equals("END")) {
//						resetAllDataForHDI();
//					}
//
//				} else {

				ManagerBlackboard.addRawData(rawdata, rawdata.getType());
//				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.logError("", jsonText);
			}

		}

	}

//	/**
//	 *
//	 */
//	private static void resetAllDataForHDI() {
//
//		BlackBoardUtility.removeAllData(DataType.TRACK.name());
//		BlackBoardUtility.removeAllData(DataType.TRACK_MODES.name());
//		BlackBoardUtility.removeAllData(DataType.STN_TRKID.name());
//		BlackBoardUtility.removeAllData(DataType.PLOT.name());
//		final var deleteALLPlot = RawDataFactory.create();
//		deleteALLPlot.setId("DELETE_ALL");
//		BlackBoardUtility.addData(DataType.PLOT.name(), deleteALLPlot);
//		BlackBoardUtility.removeAllData(DataType.WEATHER_MAPS.name());
//		final var deleteAllWeatherMaps = RawDataFactory.create();
//		deleteAllWeatherMaps.setId("DELETE_ALL");
//		BlackBoardUtility.addData(DataType.WEATHER_MAPS.name(), deleteAllWeatherMaps);
//	}

//	/**
//	 * @param rawdata
//	 * @return
//	 */
//	private static boolean isTcasAlarm(IRawData rawdata) {
//		if (!rawdata.getSafeBoolean("BDS30_RAT")
//				&& (rawdata.getSafeBoolean("BDS30_ARA41") || (!rawdata.getSafeBoolean("BDS30_RAT")
//						&& (!rawdata.getSafeBoolean("BDS30_ARA41") && rawdata.getSafeBoolean("BDS30_MTE"))))) {
//			return true;
//		}
//		return false;
//	}

//	/**
//	 * Removes the track raw data.
//	 *
//	 * @param trackId the track id
//	 */
//	private static void removeTrackRawData(String trackId) {
//
//		BlackBoardUtility.removeData(DataType.STN_TRKID.name(), getStnFromTrkID(trackId));
//		BlackBoardUtility.removeData(DataType.TRACK.name(), trackId);
//		BlackBoardUtility.removeData(DataType.TRACK_MODES.name(), trackId);
//		BlackBoardUtility.removeData(DataType.SNET.name(), "EMG_" + trackId);
//		BlackBoardUtility.removeData(DataType.BB_SNET_ALARM_ACK.name(), "EMG_" + trackId);
//		BlackBoardUtility.removeData(DataType.BB_SNET_ALARM_ACK.name(), "TCAS_" + trackId);
//		BlackBoardUtility.removeData(DataType.SNET.name(), "TCAS_" + trackId);
//
//		Utils_LIS.deleteCorrelationBB(trackId, "");
//	}

//	/**
//	 * @param jsonText
//	 */
//	private static void elaboratefromHDISnet(String jsonText) {
//		IRawData json = RawDataFactory.createFromJson(jsonText);
//		String alarmCode = "";
//		String confNumber = "EMG_" + json.getSafeString("TRACK_ID");
//		if (json.getSafeBoolean("IS_HIJ")) {
//			alarmCode = "HIJ";
//		}
//		if (json.getSafeBoolean("IS_RCF")) {
//			alarmCode = "RCF";
//		}
//		if (json.getSafeBoolean("IS_EMG")) {
//			alarmCode = "EMG";
//		}
//
//		if (!BlackBoardUtility.getDataOpt(DataType.BB_SNET_ALARM_ACK.name(), confNumber).isEmpty()) {
//			json.put("IS_SOUND_SNET", false);
//		} else {
//			json.put("IS_SOUND_SNET", true);
//		}
//		json.put("CONFLICT_NUMBER", confNumber);
//		json.put("STN1", json.getSafeString("STN"));
//		json.put("STN2", "");
//		json.put("ALARM_TYPE", alarmCode);
//		BlackBoardUtility.addData(DataType.SNET.name(), json);
//	}

//	/**
//	 * @param jsonText
//	 */
//	private static void elaborateTcas(String jsonText) {
//		IRawData json = RawDataFactory.createFromJson(jsonText);
//		String confNumber = "TCAS_" + json.getSafeString("TRACK_ID");
//
//		if (!BlackBoardUtility.getDataOpt(DataType.BB_SNET_ALARM_ACK.name(), confNumber).isEmpty()) {
//			json.put("IS_SOUND_ACAS", false);
//		} else {
//			json.put("IS_SOUND_ACAS", true);
//		}
//		json.put("CONFLICT_NUMBER", confNumber);
//		json.put("STN1", json.getSafeString("STN"));
//
//		Map<String, IRawData> track = BlackBoardUtility.getSelectedData(DataType.TRACK.name(),
//				new Pair<String, String>("ICAO", json.getSafeString("BDS30_TID")));
//		Optional<IRawData> elem = track.values().stream().findAny();
//		if (elem.isPresent()) {
//			json.put("STN2", elem.get().getSafeString("STN"));
//		}
//		json.put("ALARM_TYPE", "TCAS");
//		BlackBoardUtility.addData(DataType.SNET.name(), json);
//	}

//	/**
//	 * Elaboratefrom HDI mode S.
//	 *
//	 * @param jsonText the json text
//	 */
//	private static void elaboratefromHDIModeS(String jsonText) {
//		IRawData bbKsource = RawDataFactory.createFromJson(jsonText);
//		String stn = bbKsource.getSafeString("STN");
//		String trkId = bbKsource.getSafeString("TRACK_ID");
//
//		String ICAO = bbKsource.getSafeString("ICAO").trim();
//		String ICAO_CODE = bbKsource.getSafeString("ICAO_CODE").trim();
//		String ICAO_COUNTRY = bbKsource.getSafeString("ICAO_COUNTRY").trim();
//		String RCS = bbKsource.getSafeString("RCS").trim();
//		String SSR_CODE = bbKsource.getSafeString("MODE_3A").trim();
//
//		String SAL = bbKsource.getSafeBoolean("SAL_VALIDITY_VALUE") ? bbKsource.getSafeString("SAL") : " ";
//		String IAS = bbKsource.getSafeBoolean("IAS_VALIDITY_VALUE") ? bbKsource.getSafeString("IAS") : " ";
//		String MGN = bbKsource.getSafeBoolean("MGN_VALIDITY_VALUE") ? bbKsource.getSafeString("MGN") : " ";
//		String VR = bbKsource.getSafeBoolean("VR_VALIDITY_VALUE") ? bbKsource.getSafeString("VR") : " ";
//		String MACH_SPEED = bbKsource.getSafeBoolean("MACH_SPEED_VALIDITY_VALUE")
//				? bbKsource.getSafeString("MACH_SPEED")
//				: " ";
//		String TRUE_ANGLE = bbKsource.getSafeBoolean("TRUE_ANGLE_VALIDITY_VALUE")
//				? bbKsource.getSafeString("TRUE_ANGLE")
//				: " ";
//		String ROLL_ANGLE = bbKsource.getSafeBoolean("ROLL_ANGLE_VALIDITY_VALUE")
//				? bbKsource.getSafeString("ROLL_ANGLE")
//				: " ";
//		String ANGLE_RATE = bbKsource.getSafeBoolean("ANGLE_RATE_VALIDITY_VALUE")
//				? bbKsource.getSafeString("ANGLE_RATE")
//				: " ";
//		boolean COM_CAP = bbKsource.getSafeBoolean("COM_CAP");
//		String IS_DUPLICATED_RCS = bbKsource.getSafeString("IS_DUPLICATED_RCS");
//		String IS_DUPLICATED = bbKsource.getSafeString("IS_DUPLICATED");
//
//		var modeSElemJson = RawDataFactory.create();
//
//		modeSElemJson.put("STN", stn);
//		modeSElemJson.put("TRACK_ID", trkId);
//		modeSElemJson.put("RCS", RCS);
//		modeSElemJson.put("ICAO", ICAO.equals("0") ? "" : (ICAO_CODE + ICAO_COUNTRY));
//		modeSElemJson.put("SSR_CODE", SSR_CODE);
//		modeSElemJson.put("SAL", SAL);
//
//		modeSElemJson.put("IAS", IAS);
//
//		modeSElemJson.put("MGN", MGN);
//		modeSElemJson.put("VR", VR);
//		modeSElemJson.put("MACH_SPEED", MACH_SPEED);
//		modeSElemJson.put("TRUE_ANGLE", TRUE_ANGLE);
//		modeSElemJson.put("COM_CAP", !COM_CAP ? "0" : "1");
//		modeSElemJson.put("ROLL_ANGLE", ROLL_ANGLE);
//		modeSElemJson.put("ANGLE_RATE", ANGLE_RATE);
//		modeSElemJson.put("IS_DUPLICATED_RCS", IS_DUPLICATED_RCS);
//		modeSElemJson.put("IS_DUPLICATED", IS_DUPLICATED);
//
//		Optional<IRawData> trackModes = BlackBoardUtility.getDataOpt(DataType.TRACK_MODES.name(), trkId);
//		Optional<IRawData> itemFlight = BlackBoardUtility.getDataOpt(DataType.BB_TRKID_FLIGHTNUM.name(), trkId);
//		itemFlight.ifPresentOrElse(flight -> {
//			trackModes.ifPresent(trk -> {
//				if (trk.getSafeString("FLIGHT_NUM").startsWith("PEND")) {
//					BlackBoardUtility.removeData(DataType.TRACK_MODES.name(), trkId);
//				}
//			});
//			modeSElemJson.put("FLIGHT_NUM", flight.getSafeString("FLIGHT_NUM"));
//
//		}, () -> {
//			trackModes.ifPresent(trk -> {
//				if (!trk.getSafeString("FLIGHT_NUM").startsWith("PEND")) {
//					BlackBoardUtility.removeData(DataType.TRACK_MODES.name(), trkId);
//				}
//			});
//			modeSElemJson.put("FLIGHT_NUM", "PEND_" + trkId);
//		});
//		BlackBoardUtility.addData(DataType.TRACK_MODES.name(), modeSElemJson);
//	}

	/**
	 * Adds the XMLBB.
	 *
	 * @param string the string
	 */
	private static void addXMLBB(String string) {
		IRawData object = XmlToJsonObject.convertXMLToRawData(string);
		String serviceName = object.getType();
		if (string.contains("<service") && object.getSafeString("APP_SOURCE").isEmpty()) {
			object.put(GiForkConstants.SERVICE_NAME, serviceName);
		}
		ManagerBlackboard.addRawData(object, serviceName);

	}

	/**
	 * Gets the stn from trk ID.
	 *
	 * @param trkId the trk id
	 * @return the stn from trk ID
	 */
	public static String getStnFromTrkID(String trkId) {

		Optional<IRawData> data = BlackBoardUtility.getDataOpt(DataType.TRACK.name(), trkId);
		if (data.isPresent()) {
			return data.get().getSafeString("STN");
		}
		return "";
	}

	/**
	 * The Class MessageDispatcher.
	 */
	private static class MessageDispatcher {

		/**
		 * Start.
		 */
		public void start() {

		}

		/**
		 * Stop.
		 */
		public void stop() {

		}

		/**
		 * Adds the for JSON.
		 *
		 * @param elem the elem
		 */
		public void addForJSON(String elem) {
			addJsonBB(elem);
		}

		/**
		 * Adds the for XML.
		 *
		 * @param elem the elem
		 */
		public void addForXML(String elem) {
			addXMLBB(elem);
		}
	}

}
