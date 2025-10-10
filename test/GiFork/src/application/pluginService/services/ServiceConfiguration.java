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
package application.pluginService.services;

import com.gifork.auxiliary.ConfigurationFile;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

import application.pluginService.ServiceExecuter.IFunctionalService;

/**
 * The Class ServiceConfiguration.
 *
 * @author ggiampietro
 */
public class ServiceConfiguration implements IFunctionalService {

	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateLogger(ServiceConfiguration.class);

	/** DATO MANDATORIO PER I SERVIZI. */
	private final String serviceName = "CONFIGURATION";

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		return this.serviceName;
	}

	/**
	 * Execute.
	 *
	 * @param jsonObject the json object
	 * @return the string
	 */
	@Override
	public String execute(IRawData jsonObject) {
		final String loggerCaller = "execute()";
		LOGGER.logDebug(loggerCaller, "STO ESEGUENDO IL SERVIZIO DI CONFIGURAZIONE");

		for (String key : jsonObject.getKeys()) {
			String value = jsonObject.get(key).toString();
			ConfigurationFile.setProperties(key, value);
		}
		return "";
	}

}
