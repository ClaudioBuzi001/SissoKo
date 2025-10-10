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
package com.gifork.auxiliary;

import java.io.IOException;

import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

/**
 * The Class SystemCommand.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public class SystemCommand extends Thread {

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(SystemCommand.class);

	/** The Command string. */
	private String CommandString;

	/**
	 * ctor.
	 */
	public SystemCommand() {
		super("SystemCommand");
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		exec();
	}

	/**
	 * Exec.
	 */
	private void exec() {
		final String loggerCaller = "exec()";
		logger.logInfo(loggerCaller, "executing:" + CommandString);
		ProcessBuilder pb = new ProcessBuilder(CommandString);
		try {
			pb.start();
		} catch (IOException e2) {
			logger.logError(loggerCaller, e2.getMessage());
		}

	}
}
