package com.gifork.data_exchange.gateways.aman;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.fourflight.WP.ECI.edm.gateways.IDataRootSender;
import com.gifork.commons.log.LoggerFactory;
import com.gifork.data_exchange.gateways.jip.JIPClientSender;
import com.gifork.data_exchange.gateways.jip.JIPServer;
import com.leonardo.infrastructure.Strings;
import com.leonardo.infrastructure.log.ILogger;

/**
 * Represents a server component that listens for incoming TCP connections on a specified port,
 * and spawns a new {@link ExternalClientThread} to handle each client connection.
 * <p>
 * Once a client connects, a {@link JIPClientSender} is created and assigned as the active data sender.
 * This server runs in its own thread and continues accepting connections until interrupted.
 *
 * <p>Usage scenario: typically used in the AMAN data exchange gateway to receive external inputs
 * and establish communication channels for sending data back to clients.
 * 
 * @author LIL_O
 */
public class ExternalServer extends Thread {

	/** The Constant TH_PREFIX. */
	private static final String TH_PREFIX = "GF.Ext";

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(JIPServer.class);

	/** The server. */
	private ServerSocket server;

	/** The sender. */
	private IDataRootSender m_sender;

	/**
	 * Instantiates a new JIP server.
	 *
	 * @param port the port
	 */
	public ExternalServer(int port) {
		super(Strings.concat(TH_PREFIX, "@", String.valueOf(port)));
		final String loggerCaller = "<init>()";
		try {
			this.server = new ServerSocket(port);
		} catch (Exception e) {
			logger.logError(loggerCaller, e);
		}
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		final String loggerCaller = "run()";

		while (!this.isInterrupted()) {
			try {
				Socket client = this.server.accept();
				ExternalClientThread cTrhread = new ExternalClientThread(client);
				cTrhread.start();
				m_sender = new JIPClientSender(client);
			} catch (IOException e) {
				logger.logError(loggerCaller, e);
			}
		}
	}

	/**
	 * @return sender
	 */
	public IDataRootSender getSender() {
		return m_sender;
	}

}
