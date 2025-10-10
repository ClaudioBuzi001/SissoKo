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

package com.gifork.data_exchange.gateways.ribb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Optional;

import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

/**
 * The Class JIPClientThread.
 *
 */
public class RIBBClientThread extends Thread {

	/** The Constant TH_PREFIX. */
	private static final String TH_PREFIX = "RIBB_C";

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(RIBBClientThread.class);

	/** The socket. */
	private Socket m_socket = null;

	/**
	 * Instantiates a new JIP client thread.
	 *
	 * @param socket the socket
	 */
	public RIBBClientThread(Socket socket) {
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
				try (InputStreamReader isRea = new InputStreamReader(is)) {
					try (BufferedReader m_inputReader = new BufferedReader(isRea)) {
						String message = m_inputReader.readLine();
						while (message != null && !isInterrupted()) {
							builder.append(message);
							if (isJsnoFormat(message)) {
								analyzeMsg(message);
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
		}

	}

	/**
	 * @param message
	 */
	private void analyzeMsg(String message) {
		final String loggerCaller = "analyzeMsg()";

		IRawData rawdata = RawDataFactory.createFromJson(message);
		String dataType = rawdata.getSafeString("DataType");
		if (dataType.isEmpty()) {
			return;
		}
		String key = rawdata.getSafeString("Key");
		Optional<IRawData> result = BlackBoardUtility.getDataOpt(dataType, key);
		if (key.isEmpty()) {
			result = BlackBoardUtility.getDataOpt(dataType);
		}
		if (result.isPresent()) {
			try (OutputStream os = m_socket.getOutputStream()) {
				try (PrintStream m_outStream = new PrintStream(os, true)) {
					m_outStream.print(result.get().toJsonString());
					if (m_outStream.checkError()) {
						logger.logError(loggerCaller, "m_outStream error occurred on print");
					}
				}
			} catch (IOException e) {
				logger.logError(loggerCaller, e);
				e.printStackTrace();
			}
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

}
