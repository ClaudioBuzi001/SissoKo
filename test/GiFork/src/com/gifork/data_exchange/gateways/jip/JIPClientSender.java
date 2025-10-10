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

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.gateways.IDataRootSender;
import com.gifork.commons.log.LoggerFactory;
import com.gifork.data_exchange.gateways.HostGatewayJIP;
import com.gifork.data_exchange.gateways.aman.HostGatewayExternal;
import com.leonardo.infrastructure.log.ILogger;
import com.leonardo.infrastructure.threads.Threads;

/**
 * The Class JIPClientSender.
 *
 * @author natoags02
 * @version
 */
public class JIPClientSender implements IDataRootSender {

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(JIPClientSender.class);

	/** The out stream. */
	private PrintStream m_outStream = null;

	/** The socket. */
	private Socket m_socket = null;

	/** The port. */
	private final int m_port;

	/** The address. */
	private final String m_address;

	/** The alias. */
	private String m_alias = "NotDefined";

	/**
	 * Instantiates a new JIP client sender.
	 *
	 * @param address the address
	 * @param port    the port
	 */
	public JIPClientSender(String address, int port) {
		m_port = port;
		m_address = address;
	}

	/**
	 * Sezione AMAN
	 * 
	 * Instantiates a new JIP client sender.
	 *
	 * @param socket the address
	 */
	public JIPClientSender(Socket socket) {
		m_socket = socket;
		m_port = m_socket.getPort();
		m_address = m_socket.getInetAddress().getHostAddress();
		try {
			m_outStream = new PrintStream(m_socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the alias.
	 *
	 * @return the alias
	 */
	public String getAlias() {
		return m_alias;
	}

	/**
	 * Sets the alias.
	 *
	 * @param value the new alias
	 */
	public void setAlias(String value) {
		m_alias = value;
	}

	/**
	 * Gets the host port.
	 *
	 * @return the host port
	 */
	public int getHostPort() {
		return m_port;
	}

	/**
	 * Send text.
	 *
	 * @param textMsg the text msg
	 */
	public synchronized void sendText(String textMsg) {
		final String loggerCaller = "sendText()";
		assert loggerCaller.equals(Thread.currentThread().getStackTrace()[1].getMethodName() + "()");
		logger.logTrace(loggerCaller, "ENTER:" + textMsg);
		if (!textMsg.endsWith("\n")) {
			textMsg += "\n";
		}

		try {
			if (m_socket != null) {
				if (!m_socket.isConnected()) {
					logger.logInfo(loggerCaller, "CONNESIONE CADUTA isConnected");
					close();
				} else if (m_socket.isClosed()) {
					logger.logInfo(loggerCaller, "CONNESIONE CADUTA isClosed");
					close();
				} else if (m_socket.isInputShutdown()) {
					logger.logInfo(loggerCaller, "CONNESIONE CADUTA isInputShutdown");
					close();
				} else if (m_socket.isOutputShutdown()) {
					logger.logInfo(loggerCaller, "CONNESIONE CADUTA isOutputShutdown");
					close();
				}
			}
			if (m_socket == null) {
				connect();
			}
			internalSend(textMsg);
		} catch (Exception ex) {
			logger.logError(loggerCaller, ex);
		}
	}

	/**
	 * Connect.
	 */
	public void connect() {
		final String loggerCaller = "connect()";

		close();
		while (m_socket == null) {
			try {
				m_socket = new Socket(m_address, m_port);
				m_outStream = new PrintStream(m_socket.getOutputStream(), true);
			} catch (Exception e) {
				close();
			}
			Threads.Sleep(1000);
		}
		logger.logInfo(loggerCaller, "CLIENT CONNESSO!! Porta: " + m_port);
	}

	/**
	 * Close.
	 */
	public void close() {
		final String loggerCaller = "close()";
		if (m_outStream != null) {
			m_outStream.close();
			m_outStream = null;
		}
		if (m_socket != null) {
			try {
				m_socket.close();
			} catch (IOException e) {
				logger.logError(loggerCaller, e);
			}
			m_socket = null;
		}
	}

	/**
	 * Send.
	 *
	 * @param textMsg the msg
	 */
	public void sendAmanText(String textMsg) {
		final String loggerCaller = "sendText()";
		assert loggerCaller.equals(Thread.currentThread().getStackTrace()[1].getMethodName() + "()");
		logger.logTrace(loggerCaller, "ENTER:" + textMsg);
		textMsg += HostGatewayExternal.END_MSG;
		try {
			if (m_socket != null) {
				if (!m_socket.isConnected()) {
					logger.logInfo(loggerCaller, "CONNESIONE CADUTA isConnected");
					close();
				} else if (m_socket.isClosed()) {
					logger.logInfo(loggerCaller, "CONNESIONE CADUTA isClosed");
					close();
				} else if (m_socket.isInputShutdown()) {
					logger.logInfo(loggerCaller, "CONNESIONE CADUTA isInputShutdown");
					close();
				} else if (m_socket.isOutputShutdown()) {
					logger.logInfo(loggerCaller, "CONNESIONE CADUTA isOutputShutdown");
					close();
				}
			}
			internalSend(textMsg);
		} catch (Exception ex) {
			logger.logError(loggerCaller, ex);
		}
	}

	/**
	 * Send.
	 *
	 * @param msg the msg
	 */
	@Override
	public void send(final DataRoot msg) {
		String jsonText = msg.toJson() + HostGatewayJIP.END_MSG;
		sendText(jsonText);
	}

	/**
	 * Gets the group.
	 *
	 * @return the group
	 */
	@Override
	public String getGroup() {
		return "";
	}

	/**
	 * Gets the host address.
	 *
	 * @return the host address
	 */
	@Override
	public String getHostAddress() {
		return m_address;
	}

	/**
	 * Gets the use redirection.
	 *
	 * @return the use redirection
	 */
	@Override
	public boolean getUseRedirection() {
		return true;
	}

	/**
	 * Internal send.
	 *
	 * @param msg the msg
	 */
	private void internalSend(String msg) {
		final String loggerCaller = "internalSend()";
		if (m_outStream != null) {
			m_outStream.print(msg);
			
			if(msg.getBytes().length > 65536) {
				System.out.println("**** Dimensione pacchetto maggiore di 65536 ****");
			}
			
			if (m_outStream.checkError()) {

				logger.logError(loggerCaller, "m_outStream error occurred on print");
			}
		} else {
			logger.logError(loggerCaller, "m_outStream is null!");
		}
	}

}
