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
package analyzer;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

/**
 * The Class AnalyzerSupervisorMap.
 */
public class AnalyzerSupervisorMap implements IObserver {
	
	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateLogger(AnalyzerSupervisorMap.class);

	/**
	 * The Class SingletonLoader.
	 */
	private enum SingletonLoader {
        ;

        /** The Constant instance. */
		private static final AnalyzerSupervisorMap INSTANCE = new AnalyzerSupervisorMap();
		static {
			StorageManager.register(INSTANCE, DataType.SUPERVISOR_MAP.name());
		}
		
		/**
		 * Activate.
		 */
		public static void activate() {
			
		}		
	}

	/**
	 * Instantiates a new analyzer supervisor map.
	 */
	private AnalyzerSupervisorMap() {
	}

	/**
	 * Activate.
	 */
	public static void activate() {
		SingletonLoader.activate();
	}
	
	/**
	 * Update.
	 *
	 * @param json the json
	 */
	@Override
	public void update(final IRawData json) {
		final String loggerCaller = "update()";

		final DataRoot rootData = DataRoot.createMsg();
		final var headerNode = rootData.addHeaderOfService("RECEIVE_MAP");
		
		headerNode.addLine("MAP_NAME", json.getSafeString("SUPERVISOR_MAP_NAME"));
		headerNode.addLine("IS_SUPERVISOR", true);
		headerNode.addLine("MAP_DATA", json.getSafeString("SUPERVISOR_MAP_DATA"));

		if(json.getOperation() == Operation.DELETE) {
			headerNode.addLine("DELETE", "true");
		}
		
		ManagerBlackboard.addJVOutputList(rootData);
		
		LOGGER.logDebug(loggerCaller, "INVIO DATI PER MAP : \n" + rootData.toXml(true));
	}
}
