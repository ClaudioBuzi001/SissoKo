package com.gifork.data_exchange.gateways.aman;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.commons.log.LoggerFactory;
import com.gifork.data_exchange.gateways.CommunicationManager;
import com.leonardo.infrastructure.collections.IReadOnlyMap;
import com.leonardo.infrastructure.log.ILogger;

import application4F.BlackBoardConstants_4F.DataType;

/**
 * Handles the communication with an external client (e.g., AMAN) over a socket connection.
 * <p>
 * This thread listens for incoming messages from the external system, processes them,
 * and interacts with the internal blackboard system accordingly. It supports handling
 * heartbeat messages, restart commands, and track selection for one or more flights.
 * </p>
 * 
 * <p>
 * Messages are read from the socket input stream until an End Of Transmission (EOT)
 * character is encountered. Specific logic is executed based on message content,
 * such as extracting flight information, building structured messages (e.g., SET_TRACK_HOOK,
 * AMAN_MULTI_SELECTION), and sending them to other system components.
 * </p>
 *
 * <p>
 * The thread runs independently for each external client connection and safely manages
 * the lifecycle of the socket and associated message dispatcher.
 * </p>
 * 
 * <p><b>Expected message formats:</b></p>
 * <ul>
 *   <li>Heartbeat messages containing {@code "AMANHEARTBEAT"}</li>
 *   <li>Restart command: {@code "RESTART"}</li>
 *   <li>Track selection: callsigns ending with {@code "\u0004"}</li>
 * </ul>
 *
 * @author LIL_O
 */
public class ExternalClientThread extends Thread {

	/** The Constant TH_PREFIX. */
	private static final String TH_PREFIX = "GF.EXTCli";

	/**
	 * 
	 */
	private static final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	
	/** The Terminator of the message. */
	public static final String END_MSG = "\u0004";
	
	/** The Constant AMAN_HEART_BEAT. */
	private static final String AMAN_HEART_BEAT = "AMANHEARTBEAT";
	
	/** The Constant STN. */
	private static final String STN = "STN";
	
	/** The Constant FLIGHT_NUM. */
	private static final String FLIGHT_NUM = "FLIGHT_NUM";
	
	/** The Constant NONE. */
	private static final String NONE = "NONE";

	/** The Constant RESTART. */
	private static final String RESTART = "RESTART";

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(ExternalClientThread.class);

	/** The loggerCaller */
	private static final String loggerCaller = "run()";

	/** The Constant AMAN_SENDER . */
	private static final String AMAN_SENDER = "AMAN_SENDER";
	
	/** The Constant OBJ_ID . */
	private static final String OBJ_ID = "OBJ_ID";
	
	/** The Constant DEFAULT_MULTI_SELECTION_DATA. */
	private static final String DEFAULT_MULTI_SELECTION_DATA = "{}";

	/** The socket. */
	private Socket m_socket = null;

	/** The message dispatcher. */
	private final MessageDispatcher m_messageDispatcher = new MessageDispatcher();

	/**
	 * Instantiates a new External client thread.
	 *
	 * @param socket the socket
	 */
	public ExternalClientThread(Socket socket) {
		super(TH_PREFIX + "@" + socket.getInetAddress() + ":" + socket.getPort());
		m_socket = socket;
	}

	/**
	 * Continuously reads messages from a socket input stream and processes them.
	 * <p>
	 * This method is executed when the thread starts. It listens for incoming messages from the
	 * connected socket. Messages are read one at a time, appended to a buffer, and passed to
	 * {@link #handleMessage(String)} for further processing. The loop continues until either the input
	 * stream ends or the thread is interrupted.
	 * </p>
	 * <p>
	 * If an {@link IOException} occurs during reading, it logs the error and attempts to close the
	 * socket gracefully. When the thread finishes execution, it stops the internal message dispatcher.
	 * </p>
	 *
	 * @see #handleMessage(String)
	 * @see #readMessage(InputStream)
	 */
	@Override
	public void run() {
	    try {
	        try (InputStream is = m_socket.getInputStream()) {
	            try (InputStreamReader isR = new InputStreamReader(is)) {
	                try (BufferedReader m_inputReader = new BufferedReader(isR)) {
	                    m_messageDispatcher.start();
						while (true) {
							if (is.available() > 0) { // Check if there are bytes available to read
								String message = readMessage(is);
								if (!message.isEmpty() && !isInterrupted()) {
									handleMessage(message);
								}
							} else {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
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
	 * Reads the incoming message byte by byte until EOT (End Of Transmission) is encountered.
	 * 
	 * @param is the InputStream to read from
	 * @return the full message received without the EOT character
	 * @throws IOException if an I/O error occurs
	 */
	private static String readMessage(InputStream is) throws IOException {
		buffer.reset(); // Clear the buffer before reading a new message
		int b;
		while ((b = is.read()) != -1) {
			if (b == 0x04) { // EOT (End of Transmission)
				buffer.write(b);
				break;
			} else {
				buffer.write(b);
			}
		}
		
		return buffer.size() > 0 ? buffer.toString(StandardCharsets.UTF_8) : "";
	}

	/**
	 * Handles incoming messages from the AMAN HMI and triggers actions based on message content.
	 * <p>
	 * Supported message types:
	 * <ul>
	 *   <li><b>Heartbeat messages</b> (containing {@code AMAN_HEART_BEAT}) – no action taken (placeholder for future logic).</li>
	 *   <li><b>Restart messages</b> (containing {@code RESTART}) – logs the event and clears the AMAN blackboard.</li>
	 *   <li><b>Callsign messages</b> (ending with {@code END_MSG}) – processes one or more callsigns for track selection and sends related data.</li>
	 * </ul>
	 *
	 * @param message the received message to be parsed and handled
	 */
	private static void handleMessage(String message) {
		if (message.contains(AMAN_HEART_BEAT)) {
			// Heartbeat logic

		} else if (message.contains(RESTART)) {
			logger.logInfo(loggerCaller, "AMANHMI on RESTART");
//			System.out.println(message);
			HostGatewayExternal amanClient = (HostGatewayExternal) CommunicationManager.getExternalClients().get("AMAN");
			amanClient.sendAmanText(NONE);
		
		} else if (message.endsWith(END_MSG)) {
//			System.out.println("****** End message detected: " + message);
			String[] parts = message.split(HostGatewayExternal.END_MSG);
			if (parts.length > 0) {
				message = parts[0]; // Assign the part before END_MSG
				
				if (!message.isEmpty()) {
					String firstCallsign = message.trim();
					String[] secondaryCallsigns = new String[0];

					/// ****** MULTI-SELECTION ******* ///
					if (message.contains(" ")) {
						String[] allCallsigns = Arrays.stream(message.split(" "))
													  .map(String::trim)
													  .filter(s -> !s.isEmpty())
													  .toArray(String[]::new);

						if (allCallsigns.length > 1) {
							firstCallsign = allCallsigns[0];
							secondaryCallsigns = Arrays.copyOfRange(allCallsigns, 1, allCallsigns.length);
							logger.logInfo(loggerCaller,
									"AMANHMI secondary callsign sent: " + Arrays.toString(secondaryCallsigns));
						}
					}
					/// ****** MULTI-SELECTION ******* ///

					logger.logInfo(loggerCaller, "AMANHMI callsign sent: " + firstCallsign);
					
					saveAmanMessageInBlackboard(firstCallsign);
					buildAndSendTrackHook(firstCallsign);
					buildAndSendMultiTrackSelection(secondaryCallsigns);
				}
			}
		}
	}

	/**
	 * Stores the first callsign received from AMAN into the blackboard.
	 * <p>
	 * The data is saved under the key {@code NO_ID} within a {@link IRawData} object, and placed in the
	 * blackboard under the {@code BB_AMAN_HOOK} data type. This mechanism is used to identify whether a
	 * hook operation was triggered by AMAN, allowing for conditional logic in subsequent processing
	 * (e.g., in {@code updateUserSetting}).
	 *
	 * @param firstCallsign the callsign received from AMAN; must be non-null and represent the primary
	 *                      hooked track.
	 */
	public static void saveAmanMessageInBlackboard(String firstCallsign) {
		IRawData json = RawDataFactory.create();
		json.put(OBJ_ID, firstCallsign); 

		BlackBoardUtility.addData(DataType.BB_AMAN_HOOK.name(), IRawData.NO_ID, json);
	}

	/**
	 * Builds and sends a multi-track selection message to external clients.
	 * <p>
	 * This method processes an array of callsigns to identify associated flight numbers,
	 * and then retrieves the corresponding STN values. If any valid STNs are found,
	 * they are added to the message payload under the "DATA" field. Otherwise, an error is logged.
	 * </p>
	 *
	 * @param callsigns an array of callsign identifiers used to retrieve related flight numbers and STNs
	 */
	private static void buildAndSendMultiTrackSelection(String[] callsigns) {
		logger.logInfo(loggerCaller, "Received callsigns: " + Arrays.toString(callsigns));
		final DataRoot rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService("AMAN_MULTI_SELECTION");
		String data = DEFAULT_MULTI_SELECTION_DATA;
		
		if(callsigns.length > 0) {
			Set<String> callsignSet = new HashSet<>(Arrays.asList(callsigns));
			// callsign -> flightNum
			Set<String> collectedFlightNum = extractValuesByKey(callsignSet, DataType.BB_CALLSIGN_FLIGHTNUM, FLIGHT_NUM);
			// flightNum -> stn
			Set<String> stnToSelect = extractValuesByKey(collectedFlightNum, DataType.BB_FLIGHTNUM_STN, STN);

			// Add the LBL_ prefix to each stn
			stnToSelect = stnToSelect.stream()
				    				 .map(stn -> "LBL_" + stn)
				    				 .collect(Collectors.toSet());
			
			if (!stnToSelect.isEmpty()) {
				data = "{" + String.join(",", stnToSelect) + "}"; // If any stn, 
				logger.logInfo(loggerCaller, "Collected STNs: " + stnToSelect);
			} else {
				// No STN found for the provided callsigns
				logger.logError(loggerCaller, "No STN found for the provided callsigns.");
			}
		}
				
		logger.logInfo(loggerCaller, "Data to send: " + data);
		serviceNode.addLine("SENDER", AMAN_SENDER);
		serviceNode.addLine("DATA", data);
		serviceNode.setValidForExternalClient(true);
		ManagerBlackboard.addJVOutputList(rootData);
	}

	/**
	 * Processes the incoming callsign to generate and send a track hook message.
	 * <p>
	 * This method extracts the flight number from the provided callsign, retrieves the corresponding
	 * STN (if available), If no flight number or STN is found, default values are used.
	 *
	 * @param callsign of the flight
	 */
	private static void buildAndSendTrackHook(String callsign) {
		String flightNum = BlackBoardUtility.getDataOpt(DataType.BB_CALLSIGN_FLIGHTNUM.name(), callsign)
											.map(data -> data.getSafeString(FLIGHT_NUM))
											.orElse(NONE);
									
		final DataRoot rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService("AMAN_SET_TRACK_HOOK"); 
		
		if (!flightNum.equals(NONE)) {
			Optional<IRawData> stnDataOpt = BlackBoardUtility.getDataOpt(DataType.BB_FLIGHTNUM_STN.name(), flightNum);

			stnDataOpt.ifPresent(stnData -> {
				String stn = stnData.getSafeString(STN);
				if (!stn.isEmpty()) {
					final String id = "LBL_" + stn;
					serviceNode.addLine("ID", id);
				}
			});
		} else {
			serviceNode.addLine("ID", "NONE");
		}

		logger.logInfo(loggerCaller, "Track id to send: " + serviceNode.getAttributeValue("ID"));
		serviceNode.addLine("SENDER", AMAN_SENDER);
		serviceNode.setValidForArps(true);
		serviceNode.setValidForExternalClient(true);
		ManagerBlackboard.addJVOutputList(rootData);
	}
	
	
	/**
	 * Extracts a set of non-null, non-empty values from a BlackBoard data map based on a set of keys.
	 *
	 * <p>This method filters the entries of a given BlackBoard data type by matching the provided keys,
	 * then retrieves a specific field (parameter) from each matching entry using {@code getSafeString}.
	 * It collects and returns only the values that are not null and not empty.</p>
	 *
	 * @param keys      the set of keys to filter the entries by (e.g., callsigns or flight numbers)
	 * @param dataType  the {@link DataType} indicating which BlackBoard data map to use
	 * @param parameter the field name to extract from each matched data entry (e.g., "FLIGHT_NUM", "STN")
	 * @return a {@code Set<String>} containing all valid (non-null and non-empty) values found
	 */
	private static Set<String> extractValuesByKey(Set<String> keys, DataType dataType, String parameter) {
		IReadOnlyMap<String, IRawData> allData = BlackBoardUtility.getAllData(dataType.name());
		return keys.stream()
			       .map(allData::get)
			       .filter(Objects::nonNull)
			       .map(obj -> obj.getSafeString(parameter))
			       .filter(val -> val != null && !val.isEmpty())
			       .collect(Collectors.toSet());
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
	}
}
