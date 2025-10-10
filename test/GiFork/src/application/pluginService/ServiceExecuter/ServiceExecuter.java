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

package application.pluginService.ServiceExecuter;

import java.util.HashMap;

import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.blackboard.GiForkConstants;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.Generics;
import com.leonardo.infrastructure.log.ILogger;

/**
 * The Class ServiceExecuter.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public class ServiceExecuter {

	/** The Service map. */
	private final HashMap<String, IService> ServiceMap = new HashMap<>();

	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateLogger(ServiceExecuter.class);

	/**
	 * The Class SingletonLoader.
	 */
	private static class SingletonLoader {

		/** The Constant instance. */
		private static final ServiceExecuter INSTANCE = new ServiceExecuter();
	}

	/**
	 * Restituisce la istanza singleton.
	 *
	 * @return single instance of ServiceExecuter
	 */
	public static ServiceExecuter getInstance() {
		return SingletonLoader.INSTANCE;
	}

	/**
	 * Register service.
	 *
	 * @param service ServiceInterface
	 */
	public void RegisterService(IService service) {
		this.ServiceMap.put(service.getServiceName(), service);
	}

	/**
	 * Checks if is available.
	 *
	 * @param serviceName IService
	 * @return boolean
	 */
	public boolean isAvailable(String serviceName) {
		boolean ret = false;
		IService service = this.ServiceMap.get(serviceName);
		if (service != null) {
			ret = true;
		}
		return ret;
	}

	/**
	 * **.
	 *
	 * @param serviceName String
	 * @param functionMsg RawData
	 * @return Object
	 */
	public Object ExecuteService(String serviceName, IRawData functionMsg) {
		final String loggerCaller = "ExecuteService()";
		try {
			IFunctionalService service = Generics.ifTryCast(this.ServiceMap.get(serviceName), IFunctionalService.class)
					.orElse(null);
			if (service != null) {
				try {
					return service.execute(functionMsg);
				} catch (Exception e) {
					LOGGER.logError(loggerCaller, e);
				}
			}
		} catch (Exception e) {
			LOGGER.logError(loggerCaller, e);
		}
		return null;
	}

	/**
	 * Execute aggregator.
	 *
	 * @param serviceName the service name
	 * @param functionMsg the function msg
	 * @param headerNode  the header node
	 */
	public void executeAggregator(String serviceName, IRawData functionMsg, HeaderNode headerNode) {
		final String loggerCaller = "ExecuteAggregator()";
		try {
			IAggregatorService service = Generics.ifTryCast(this.ServiceMap.get(serviceName), IAggregatorService.class)
					.orElse(null);
			if (service != null) {
				try {
					service.aggregate(functionMsg, headerNode);
				} catch (Exception e) {
					LOGGER.logError(loggerCaller, e);
				}
			}
		} catch (Exception e) {
			LOGGER.logError(loggerCaller, e);
		}
	}

	/**
	 * **.
	 *
	 * @param functionMsg RawData
	 * @return Object
	 */
	public Object ExecuteService(IRawData functionMsg) {
		String serviceName = functionMsg.getSafeString(GiForkConstants.SERVICE_NAME);
		return ExecuteService(serviceName, functionMsg);
	}

	/**
	 * **.
	 *
	 * @param functionMsg RawData
	 * @return boolean
	 */
	public boolean isToBeCarriedSingleLevel(IRawData functionMsg) {
		final String loggerCaller = "isToBeCarriedSingleLevel()";

		boolean isToBeCarriedSingleLevel = false;
		try {
			String serviceName = functionMsg.getSafeString(GiForkConstants.SERVICE_NAME);
			IService service = this.ServiceMap.get(serviceName);
			isToBeCarriedSingleLevel = service.isToBeCarriedSingleLevel();
		} catch (Exception e) {
			LOGGER.logError(loggerCaller, "GENERIC SERVICE ERROR");
			LOGGER.logError(loggerCaller, e);
		}

		return isToBeCarriedSingleLevel;
	}

	/**
	 * Execute service.
	 *
	 * @param serviceName String
	 */
	public void ExecuteService(String serviceName) {
		ExecuteService(serviceName, null);
	}
}
