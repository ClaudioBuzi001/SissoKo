package com.gifork.data_exchange.gateways.aman;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.gateways.ICommGateway;
import com.gifork.data_exchange.gateways.jip.JIPClientSender;

/**
 * This class represents an external communication gateway used for handling 
 * connections and data exchange with an external AMAN (Arrival Manager) system.
 * 
 * <p>It implements the {@link ICommGateway} interface and is responsible for:
 * <ul>
 *   <li>Starting and stopping the TCP server to receive data</li>
 *   <li>Sending messages to the AMAN system using a {@link JIPClientSender}</li>
 *   <li>Providing configuration details such as host and local ports</li>
 * </ul>
 *
 * <p>The gateway uses the EOT message terminator defined by {@link #END_MSG}.
 */
public class HostGatewayExternal implements ICommGateway {

	/** The Terminator of the message. */
	public static final String END_MSG = "\u0004";

	/** The host addr. */
	private final String m_hostAddr;

	/** The host port. */
	private final int m_hostPort;

	/** The local port. */
	private final int m_localPort;

	/** The tcp server. */
	private ExternalServer m_tcpServer;

	/**
	 * Instantiates a new host gateway JIP.
	 *
	 * @param hostAddr  the host addr
	 * @param hostPort  the host port
	 * @param localPort the local port
	 */
	public HostGatewayExternal(String hostAddr, int hostPort, int localPort) {
		m_localPort = localPort;
		m_hostAddr = hostAddr;
		m_hostPort = hostPort;

	}

	/**
	 * Gets the host port.
	 *
	 * @return the host port
	 */
	public int getHostPort() {
		return m_hostPort;
	}

	/**
	 * Gets the local port.
	 *
	 * @return the local port
	 */
	public int getLocalPort() {
		return m_localPort;
	}

	/**
	 * Start.
	 */
	@Override
	public void start() {
		if (m_tcpServer == null) {
			m_tcpServer = new ExternalServer(m_localPort);
			m_tcpServer.start();
		}
	}

	/**
	 * Stop.
	 */
	@Override
	public void stop() {
		if (m_tcpServer != null) {
			m_tcpServer.stop();
		}
	}

	/**
	 * Send.
	 *
	 * @param msg the data root
	 */
	public void sendAmanText(String msg) {
		((JIPClientSender) m_tcpServer.getSender()).sendAmanText(msg);
	}

	/**
	 * Gets the host address.
	 *
	 * @return the host address
	 */
	@Override
	public String getHostAddress() {
		return m_hostAddr;
	}

	@Override
	public void send(DataRoot dataRoot) {
	}
}
