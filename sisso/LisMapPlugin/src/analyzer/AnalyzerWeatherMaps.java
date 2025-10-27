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

import java.sql.Timestamp;
import java.util.HashMap;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataArray;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.Generics;
import com.leonardo.infrastructure.collections.IReadOnlyMap;
import com.leonardo.infrastructure.log.ILogger;

import common.CommonConstants;

/**
 * The Class AnalyzerWeatherMaps.
 */
public class AnalyzerWeatherMaps implements IObserver {

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(AnalyzerWeatherMaps.class);

	/** The map array. */
	private final static HashMap<String,DataRoot> m_mapArrayNew = new HashMap<>();

	/** The last map time. */
	private long m_lastMapTime = 0;

	/** The Constant m_lock. */
	private static final Object m_lock = new Object();

	/**
	 * The Class SingletonLoader.
	 */
	private enum SingletonLoader {
		;

		/** The Constant instance. */
		private static final AnalyzerWeatherMaps instance = new AnalyzerWeatherMaps();
		static {
			StorageManager.register(instance, CommonConstants.WEATHER_MAPS);
			instance.startTimeoutThread();
		}

		/**
		 * Activate.
		 */
		public static void activate() {
			// Per uso interno
		}
	}

	/**
	 * Instantiates a new analyzer weather maps.
	 */
	private AnalyzerWeatherMaps() {
//		String json = FileLoaders.loadTxt("C:\\Users\\albanesed\\Desktop\\Nuova cartella (5)\\log_info_weather\\log_info_weather\\log_jeoviewer\\JSON_WEATHER_0643-0911.txt");
//		
//		String[] ele = json.split(
//				"\n\n"
//				);
//		int count = 0;
//		for (String string : ele) {
//			IRawData data =  RawDataFactory.createFromJson(string);
//			final float livello = data.getSafeFloat("LEVEL");
////			if(livello==1||livello==0)
//				update(data) ;
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
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

		final var operation = json.getOperation();
		if (Generics.isOneOf(operation, Operation.DELETE_ALL)) {
			autoRemoveMaps();
			logger.logDebug(loggerCaller, "Removed All Weather maps for PLB");
		} else if (Generics.isOneOf(operation, Operation.INSERT, Operation.UPDATE)) {

			/** The map tag. */
			String m_mapTag = json.getSafeString("MAP_TAG");
			if (m_mapTag.equals(CommonConstants.WEATHER_MAP_TAG_START)) {
				// send message to start Poligons
				final DataRoot rootData = DataRoot.createMsg();
				final var headerNode = rootData.addHeaderOfService("MAP_FUSION_LOAD");
				headerNode.addLine("POLYGONS_START", "true");
				ManagerBlackboard.addJVOutputList(rootData);
				//////////////////////////////////
				
				m_mapArrayNew.keySet().forEach(key->{
					createEmptyDataRoot(key);
				});
			}

			if (m_mapTag.equals(CommonConstants.WEATHER_MAP_TAG_CONTINUE)
					|| m_mapTag.equals(CommonConstants.WEATHER_MAP_TAG_START)) {
				if (json.has("MAP_RECT")) {
					final IRawDataArray mapRect = json.getSafeRawDataArray("MAP_RECT");
					final float livello = json.getSafeFloat("LEVEL");
					for (com.gifork.commons.data.IRawDataElement iRawDataElement : mapRect) {
						final String lat1 = iRawDataElement.getSafeString("LAT1");
						final String lon1 = iRawDataElement.getSafeString("LON1");
						final String lat2 = iRawDataElement.getSafeString("LAT2");
						final String lon2 = iRawDataElement.getSafeString("LON2");
						String mapName = (int) livello + "_" + "WEATHER_AREA_" + m_mapArrayNew.size();
						//float maxLevel = 7.0f;
						final double livelloPerc = 0.10 + (((double) livello - 1.0) * 0.06);
						final DataRoot rootData = DataRoot.createMsg();
						final var headerNode = rootData.addHeaderOfService("MAP_FUSION_LOAD");
						headerNode.addLine("MAP_TYPE", CommonConstants.WEATHER_MAP_TYPE);
						headerNode.addLine("MAP_SUBTYPE", "LEVEL_" + (int) livello);
						headerNode.addLine("STYLE", "-fx-fill: rgba(0, 128, 128, " + livelloPerc + ");");
						headerNode.addLine("MAP_NAME", mapName);
						headerNode.addLine("POINTS", lat1 + "," + lon1 + " " + lat1 + "," + lon2 + " " + lat2 + ","
								+ lon2 + " " + lat2 + "," + lon1);
						headerNode.addLine("SHOW", false);
						
						m_mapArrayNew.put(mapName, rootData);
					}
				}
			} else if (m_mapTag.equals(CommonConstants.WEATHER_MAP_TAG_END)) {
				m_mapArrayNew.values().forEach(ManagerBlackboard::addJVOutputList);
				//
				//send message to close all polygons
				final DataRoot rootData = DataRoot.createMsg();
				final var headerNode = rootData.addHeaderOfService("MAP_FUSION_LOAD");
				headerNode.addLine("POLYGONS_END", "true");
				ManagerBlackboard.addJVOutputList(rootData);
				///////////////////////////////////
				this.m_lastMapTime = new Timestamp(System.currentTimeMillis()).getTime();
				synchronized (m_lock) {
					m_lock.notifyAll();
				}
				m_mapArrayNew.clear();
				//clearArray();
			}

		}
	}

	/**
	 * Clear array.
	 */
	private static void clearArray() {
		m_mapArrayNew.keySet().forEach(key->{
			if(m_mapArrayNew.get(key).getAttributeValue("POINTS").isEmpty()) {
				m_mapArrayNew.remove(key);
			}
		});
	}

	/**
	 * Creates the empty data root.
	 *
	 * @param key the el
	 */
	private static void createEmptyDataRoot(String key) {
		DataRoot rootData = DataRoot.createMsg(); 
		String[] lev = key.split("_");
		double livello = Double.valueOf(lev[0]);
				
		final double livelloPerc = 0.10 + (( livello - 1.0) * 0.06);			
		final var headerNode = rootData.addHeaderOfService("MAP_FUSION_LOAD");
		headerNode.addLine("MAP_TYPE", CommonConstants.WEATHER_MAP_TYPE);
		headerNode.addLine("MAP_SUBTYPE", "LEVEL_" + lev[0]);
		headerNode.addLine("POINTS", "");
		headerNode.addLine("STYLE", "-fx-fill: rgba(0, 128, 128, " + livelloPerc + ");");
		headerNode.addLine("MAP_NAME", key);
		m_mapArrayNew.put(key, rootData);
	}


	/**
	 * Auto remove maps.
	 */
	private static void autoRemoveMaps() {
		final String loggerCaller = "autoRemoveMaps()";
		logger.logDebug(loggerCaller, "Called");
		
		final IReadOnlyMap<String, IRawData> items = BlackBoardUtility.getAllData(CommonConstants.WEATHER_MAPS);
		if(items.size()>0) {
			final DataRoot rootData = DataRoot.createMsg();
			final var headerNode = rootData.addHeaderOfService("MAP_LOAD");
			headerNode.addLine("MAP_TYPE", CommonConstants.WEATHER_MAP_TYPE);
			headerNode.addLine("DELETE", true);
			headerNode.addLine("REMOVEEMPTYNODES", false);
			ManagerBlackboard.addJVOutputList(rootData);
	
			// Cancellazione delle BB
			BlackBoardUtility.removeAllData(CommonConstants.WEATHER_MAPS);
			m_mapArrayNew.clear();
			
		}
		
	}

	/**
	 * Start timeout thread.
	 */
	private void startTimeoutThread() {
		final String loggerCaller = "startTimeoutThread()";

		final Thread weatherTimeOut = new Thread("WEATHER_TIMEOUT_TIMER") {
			@Override
			public void run() {
				final long timeout = 90000; // 3 minuti in millis
				while (true) {
					synchronized (m_lock) {
						try {
							m_lock.wait(1000);
						} catch (final InterruptedException e) {
							logger.logFatal(loggerCaller, e);
						}
					}
					final long actualTime = new Timestamp(System.currentTimeMillis()).getTime();
					final long elapsedTime = actualTime - m_lastMapTime;
					if (elapsedTime > timeout) {
						/* if (m_mapTag.equals(END)) */
						autoRemoveMaps();
					}
				}
			}
		};
		weatherTimeOut.start();
	}
}
