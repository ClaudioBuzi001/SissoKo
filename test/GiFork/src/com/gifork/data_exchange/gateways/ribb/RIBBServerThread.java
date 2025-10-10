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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

/**
 * The Class JIPServer.
 */
public class RIBBServerThread extends Thread {
	
	
	
	/** The Constant TH_PREFIX. */
	private static final String TH_PREFIX = "RIBB_S";
	
	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(RIBBServerThread.class);
	
	/** The server. */
	private ServerSocket server;

	
	
	
	/**
	 * Instantiates a new JIP server.
	 *
	 * @param port the port
	 */
	public RIBBServerThread(int port) {
		super(TH_PREFIX+"@" + port);
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

				
				
				RIBBClientThread cTrhread = new RIBBClientThread(client);
				cTrhread.start();
			} catch (IOException e) {
				logger.logError(loggerCaller, e);
			}
		}
	}
}
