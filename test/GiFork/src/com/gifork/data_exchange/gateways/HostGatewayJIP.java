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
package com.gifork.data_exchange.gateways;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.gateways.ICommGateway;
import com.fourflight.WP.ECI.edm.gateways.IDataRootSender;
import com.gifork.data_exchange.gateways.jip.JIPClientSender;
import com.gifork.data_exchange.gateways.jip.JIPServer;

/**
 * Gateway concreto.
 */
public class HostGatewayJIP implements ICommGateway {
	
	
	

	/** The Terminator of the JsonIP message. */
	public static final String END_MSG = "</msg>";

	/** The host addr. */
	private final String m_hostAddr;

	/** The host port. */
	private final int m_hostPort;

	/** The local port. */
	private final int m_localPort;

	/** The tcp server. */
	private JIPServer m_tcpServer;

	/** The sender. */
	private final IDataRootSender m_sender;

	/**
	 * Instantiates a new host gateway JIP.
	 *
	 * @param hostAddr  the host addr
	 * @param hostPort  the host port
	 * @param localPort the local port
	 */
	public HostGatewayJIP(String hostAddr, int hostPort, int localPort) {
		m_localPort = localPort;
		m_hostAddr = hostAddr;
		m_hostPort = hostPort;
		m_sender = new JIPClientSender(hostAddr, hostPort);
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
			m_tcpServer = new JIPServer(m_localPort);
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
	 * @param dataRoot the data root
	 */
	@Override
	public void send(DataRoot dataRoot) {
		m_sender.send(dataRoot);
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

}
