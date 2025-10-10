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

import java.util.HashMap;
import java.util.Map;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys.ArpName;
import com.fourflight.WP.ECI.edm.gateways.IDataRootSender;
import com.gifork.auxiliary.ConfigurationFile;
import com.gifork.data_exchange.gateways.aman.HostGatewayExternal;

/**
 * The Class CommunicationManager.
 */
public final class CommunicationManager {

	/** The Constant LOCAL_HOST. */
	public static final String LOCAL_HOST = ConfigurationFile.getProperties("JV_ADDR");

	/** The Constant LOCAL_PORT. */
	public static final int LOCAL_PORT = 12574;

	/** The Constant JV_PORT. */
	public static final int JV_PORT = 7050;

	/**
	 * The list of all the objects in charge of sending messages to some recipient.
	 */
	private static final Map<String, IDataRootSender> m_JeoViewerClients = new HashMap<>();

	/**
	 * The list of all the objects in charge of sending messages to some external client.
	 */
	private static final Map<String, IDataRootSender> m_ExternalClients = new HashMap<>();

	/**
	 *
	 */
	private static final HostGatewayJIP m_mainJeoViewer = new HostGatewayJIP(LOCAL_HOST, JV_PORT, LOCAL_PORT);

	/** The last local port */
	private static int lastLocalPort;

	/** The Constant logger. */

	/**
	 * Instantiates a new communication manager.
	 */
	private CommunicationManager() {
	}

	/**
	 * Gets the JV gate way.
	 *
	 * @param dataRoot
	 *
	 */
	public static void send(DataRoot dataRoot) {
		m_mainJeoViewer.send(dataRoot);
		if (dataRoot.getHeaderNode().isPresent() && dataRoot.getHeaderNode().get().isValidforArps()) {
			m_JeoViewerClients.values().forEach(client -> {
				client.send(dataRoot);
			});
		}
	}

	/**
	 * @return external clients map
	 */
	public static Map<String, IDataRootSender> getExternalClients() {
		return m_ExternalClients;
	}

	/**
	 * Aggiunge un altro client alla lista.
	 */

	public static void addClient() {
		m_mainJeoViewer.start();
	}

	/**
	 * Aggiunge un altro client alla lista.
	 *
	 * @param index
	 */
	public static void addArpClient(int index) {
		int port = JV_PORT + index;
		int localport = LOCAL_PORT + index;
		lastLocalPort = localport;
		HostGatewayJIP sender = new HostGatewayJIP(LOCAL_HOST, port, localport);
		sender.start();
		m_JeoViewerClients.put(ArpName.fromValue(index).name(), sender);
	}

	/**
	 * Adds a new external client to the list of managed clients.
	 * <p>
	 * This method creates a new {@link HostGatewayExternal} instance using the provided name and port,
	 * starts the connection, and stores it in the external clients map.
	 * </p>
	 *
	 * @param name         the identifier name for the client
	 * @param externalPort the port number used to connect to the external client
	 */
	public static void addExternalClient(String name, int externalPort) {
		lastLocalPort++;
		HostGatewayExternal sender = new HostGatewayExternal(LOCAL_HOST, externalPort, externalPort);
		sender.start();
		
		m_ExternalClients.put(name, sender);
	}

	/**
	 * @return the lastLocalPort
	 */
	public static int getLastLocalPort() {
		return lastLocalPort;
	}

	/**
	 * @param lastLocalPort the lastLocalPort to set
	 */
	public static void setLastLocalPort(int lastLocalPort) {
		CommunicationManager.lastLocalPort = lastLocalPort;
	}

}
