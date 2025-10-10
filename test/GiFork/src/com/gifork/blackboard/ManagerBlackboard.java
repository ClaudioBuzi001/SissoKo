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
package com.gifork.blackboard;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmMetaKeys;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.ConfigurationFile;
import com.gifork.auxiliary.XmlToJsonObject;
import com.gifork.commons.blackboard.IRemoteBlackboard;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.commons.log.LoggerFactory;
import com.gifork.data_exchange.gateways.CommunicationManager;
import com.gifork.data_exchange.model.RawStorage;
import com.leonardo.infrastructure.ArgumentManager;
import com.leonardo.infrastructure.collections.HashMapEx;
import com.leonardo.infrastructure.collections.IReadOnlyMap;
import com.leonardo.infrastructure.log.ILogger;
import com.leonardo.infrastructure.threads.Threads;

/**
 * The blackboard model defines three main components: <br>
 * <ul>
 * <li><b>blackboard</b><br>
 * a structured global memory containing objects from the solution space</li>
 * <li><b>knowledge sources</b><br>
 * specialized modules with their own representation</li>
 * <li><b>control component</b><br>
 * selects, configures and executes modules.</li>
 * </ul>
 *
 * @author ggiampietro
 *
 */
public final class ManagerBlackboard implements IRemoteBlackboard {

	/** The Constant BYPASS_QUEUE. */
	private static final boolean BYPASS_QUEUE = true;

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(ManagerBlackboard.class);

	/** The Constant m_storageList. */
	private static final Map<String, ConcurrentLinkedQueue<IRawData>> m_storageList = new ConcurrentHashMap<>();

	/** The Constant m_outputJVList. */
	// private static final SmartFIFO m_outputJVList = new SmartFIFO();
	private static final ConcurrentLinkedQueue<DataRoot> m_outputJVList = new ConcurrentLinkedQueue<>();

	/** The print. */
	public static boolean print = ArgumentManager.getBooleanOf("print");

	/** The Constant m_mapID. */
	private static final HashMapEx<String, String> m_mapID = new HashMapEx<>();

	/** The Constant m_mapStatisticsID. */
	private static final HashMapEx<String, Boolean> m_mapStatisticsID = new HashMapEx<>();
	/** The Constant rmiMonitor. */
	// RMI
	private static final Object rmiMonitor = new Object();
	/** The rmi attempts. */
	private static int rmiAttempts;

	/** The Constant monitor. */
	private static final ManagerBlackboardRMIEndpointMonitor monitor = new ManagerBlackboardRMIEndpointMonitor(20000);

	/** The remote registry. */
	private static Registry remoteRegistry = null;

	/** The Constant GF_BLACKBOARD. */
	public static final String GF_BLACKBOARD = "RemoteBlackboard";


	/**
	 * The Class SingletonLoader.
	 */
	private static class SingletonLoader {

		/** The Constant instance. */
		private static final ManagerBlackboard instance = new ManagerBlackboard();
	}

	/**
	 * Instantiates a new manager blackboard.
	 */
	private ManagerBlackboard() {
		createDataUpdateTimer();
		initSenderToJVMessage();
	}

	/**
	 * Restituisce la istanza singleton.
	 *
	 * @return single instance of ManagerBlackboard
	 */
	public static ManagerBlackboard getInstance() {
		return SingletonLoader.instance;
	}

	/**
	 * Inits the RMI endpoint.
	 */
	public static void initRMIEndpoint() {
		if (!ConfigurationFile.getProperties("RMI_ENDPOINT").equalsIgnoreCase("LEGACY")) {
			initRMIEndPointAlternative();
		} else {
			initRMIEndPointLegacy();
		}
	}

	/**
	 * Inits the RMI end point with the legacy code.
	 */
	private static void initRMIEndPointLegacy() {
		final String loggerCaller = "initRMIEndpoint()";
		try {
			synchronized (rmiMonitor) {
				IRemoteBlackboard obj = null;
				if (remoteRegistry == null) {
					obj = (IRemoteBlackboard) UnicastRemoteObject.exportObject(ManagerBlackboard.getInstance(),
							Registry.REGISTRY_PORT);
					rmiAttempts = 0;
				}
				if (obj != null) {
					remoteRegistry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
					remoteRegistry.rebind(GF_BLACKBOARD, obj);
				}
				monitor.start();
			}

		} catch (RemoteException e) {
			logger.logDebug(loggerCaller, "WARNING In Blackboard : Unable to create remoteRegistry ");
			logger.logDebug(loggerCaller, "RETRYING Creating RMI UnicastRemoteObject");

			if (rmiAttempts > 5) {
				logger.logDebug(loggerCaller,
						"WARNING In Blackboard : Unable to create remoteRegistry. Max number of rmiAttempts reached");
				remoteRegistry = null;
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					logger.logError(loggerCaller, e);
				}
				rmiAttempts++;
				initRMIEndpoint();
			}
		}
	}

	/**
	 * Inits the RMI end point with the alternative mode.
	 */
	private static void initRMIEndPointAlternative() {
		final String loggerCaller = "initEndpoint()";
		boolean loopTerminated = false;
		int attempts = 0;
		while (!loopTerminated) {
			IRemoteBlackboard obj = null;
			try {
				obj = (IRemoteBlackboard) UnicastRemoteObject.exportObject(ManagerBlackboard.getInstance(), 0);
				if (obj != null) {
					try {
						remoteRegistry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
						remoteRegistry.rebind(GF_BLACKBOARD, obj);
					} catch (RemoteException e) {
						logger.logWarn(loggerCaller, e);
						remoteRegistry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
						remoteRegistry.rebind(GF_BLACKBOARD, obj);
					}

					loopTerminated = true;
				}
			} catch (RemoteException e) {
				logger.logDebug(loggerCaller, "WARNING In RMI : Unable to create remoteRegistry ");
				if (obj != null) {
					try {
						UnicastRemoteObject.unexportObject(obj, true);
					} catch (NoSuchObjectException e1) {
						logger.logWarn(loggerCaller, e1);
					}
				}
				if (remoteRegistry != null) {
					try {
						remoteRegistry.unbind(GF_BLACKBOARD);
					} catch (Exception e1) {
						logger.logWarn(loggerCaller, e1);
					}
					remoteRegistry = null;
				}

				if (attempts > 5) {
					logger.logDebug(loggerCaller, "WARNING In Blackboard : Max number of rmiAttempts reached");
					loopTerminated = true;
				} else {
					logger.logDebug(loggerCaller, "RETRYING Creating RMI UnicastRemoteObject");
					Threads.Sleep(1000);
					attempts++;
				}
			}
		}
	}

	/**
	 * Adds the raw data.
	 *
	 * @param objectMsg the object msg
	 * @return true, if successful
	 */
	public static boolean addRawData(IRawData objectMsg) {
		if (objectMsg.getOperation() == Operation.NONE) {
			objectMsg.setOperation(Operation.INSERT);
		}
		objectMsg.setId(extractId(objectMsg));
		return addToStorageList(objectMsg.getType(), objectMsg);
	}

	/**
	 * Attenzione! Questo metodo altera il RawData passato.
	 *
	 * @param objectMsg the object msg
	 * @param dataType  the data type
	 * @return true, if successful
	 */
	public static boolean addRawData(IRawData objectMsg, String dataType) {
		if (objectMsg.getOperation() == Operation.NONE) {
			objectMsg.setOperation(Operation.INSERT);
		}
		objectMsg.setType(dataType);
		objectMsg.setId(extractId(objectMsg));
		return addToStorageList(objectMsg.getType(), objectMsg);
	}

	/**
	 * Attenzione! Questo metodo altera il RawData passato.
	 *
	 * @param objectMsg the object msg
	 * @param dataType  the data type
	 * @param id        the id
	 * @return true, if successful
	 */
	public static boolean addRawData(IRawData objectMsg, String dataType, String id) {
		if (objectMsg.getOperation() == Operation.NONE) {
			objectMsg.setOperation(Operation.INSERT);
		}
		objectMsg.setType(dataType);
		objectMsg.setId(id);
		return addToStorageList(objectMsg.getType(), objectMsg);
	}

	/**
	 * Adds the to storage list.
	 *
	 * @param dataType  the data type
	 * @param objectMsg the object msg
	 * @return true, if successful
	 */
	private static boolean addToStorageList(String dataType, IRawData objectMsg) {
		if (BYPASS_QUEUE) {
			StorageManager.manageStorage(objectMsg);
			return true;
		}
		boolean retValue = m_storageList.computeIfAbsent(dataType, k -> new ConcurrentLinkedQueue<>()).add(objectMsg);
		synchronized (m_storageList) {
			m_storageList.notifyAll();
		}
		return retValue;
	}

	/**
	 * Removes the raw data.
	 *
	 * @param dataType the data type
	 * @param key      the key
	 * @return true, if successful
	 */
	public static boolean removeRawData(String dataType, String key) {
		RawStorage storage = StorageManager.getInstance().getStorage(dataType);
		if (storage != null) {
			var data = storage.getItem(key);
			if (data.isPresent()) {
				data.get().setOperation(Operation.DELETE);
				return storage.removeItem(data.get());
			}
		}
		return false;
	}

	/**
	 * Adds the JV output list.
	 *
	 * @param dataRoot the data root
	 */
	public static void addJVOutputList(DataRoot dataRoot) {
		m_outputJVList.offer(dataRoot);
		synchronized (m_outputJVList) {
			m_outputJVList.notifyAll();
		}
	}

	/**
	 * Adds the XML.
	 *
	 * @param xml the xml
	 */
	public static void addXML(String xml) {
		if (xml.contains("</msg>")) {
			IRawData object = XmlToJsonObject.convertXMLToRawData(xml);

			String dataType = object.getSafeString(IRawData.KEY_DATATYPE, IRawData.UNKNOWN_DATA_TYPE);
			object.setType(dataType);
			String APP_SOURCE = object.getSafeString("APP_SOURCE", "");

			if (xml.contains("<service") && APP_SOURCE.isEmpty()) {
				String service_name = dataType;
				dataType = GiForkConstants.SERVICE;
				object.setType(dataType);
				object.put(GiForkConstants.SERVICE_NAME, service_name);
			}

			ManagerBlackboard.addRawData(object);
		}
	}

	/**
	 * Forced update.
	 *
	 * @param dataType the data type
	 * @param id       the id
	 */
	public static void forcedUpdate(String dataType, String id) {
		var data = StorageManager.getItemStorageOpt(dataType, id);
		if (!data.isEmpty()) {
			if (print) {
				long timestamp = System.currentTimeMillis();
				System.out.println("forcedUpdate GF: " + timestamp + " ItemType: " + dataType + " Id: " + id);
			}
			StorageManager.notifyChange(data.get());
		}
	}

	/**
	 * Forced all update.
	 *
	 * @param dataType the data type
	 */
	public static void forcedAllUpdate(String dataType) {
		var dataList = StorageManager.getItemsStorage(dataType);
		for (var entrySet : dataList.entrySet()) {
			IRawData rawDa = entrySet.getValue();
			if (!rawDa.isEmpty()) {
				StorageManager.notifyChange(rawDa);
			}
		}
	}

	/**
	 * @param dataType
	 * @param millis
	 */
	public static void forcedAllUpdate(String dataType, int millis) {
		Timer t = new Timer();
		t.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				var dataList = StorageManager.getItemsStorage(dataType);
				for (var entrySet : dataList.entrySet()) {
					IRawData rawDa = entrySet.getValue();
					if (!rawDa.isEmpty()) {
						StorageManager.notifyChange(rawDa);
					}
				}
				t.cancel();
			}
		}, millis);
	}

	/**
	 * Gets the remote blackboard data.
	 *
	 * @param storageName the storage name
	 * @param dataID      the data ID
	 * @return the remote blackboard data
	 * @throws RemoteException the remote exception
	 */
	@Override
	public String getRemoteBlackboardData(String storageName, String dataID) throws RemoteException {
		return BlackBoardUtility.getDataOpt(storageName, dataID).map(rd -> rd.toString()).orElse("{}");
	}

	/**
	 * Gets the remote blackboard source from id.
	 *
	 * @param storageName the storage name
	 * @param key         the key
	 * @return the remote blackboard source from id
	 * @throws RemoteException the remote exception
	 */
	@Override
	public String getRemoteBlackboardSourceFromId(String storageName, String key) throws RemoteException {
		return BlackBoardUtility.getDataOpt(storageName, key).map(rd -> rd.toString()).orElse("{}");
	}

	/**
	 * Gets the all remote blackboard data.
	 *
	 * @param storageName the storage name
	 * @return the all remote blackboard data
	 * @throws RemoteException the remote exception
	 */
	@Override
	public String getAllRemoteBlackboardData(String storageName) throws RemoteException {
		var map = StorageManager.getItemsStorage(storageName);
		var elem = RawDataFactory.createElem();
		for (var entry : map.entrySet()) {
			elem.put(entry.getKey(), entry.getValue());
		}
		return elem.toJsonString();
	}

	/**
	 * Creates the data update timer.
	 */
	private static void createDataUpdateTimer() {
		Runnable task = () -> {
			while (true) {
				try {
					checkForDataUpdate();
					synchronized (m_storageList) {
						m_storageList.wait(1000 * 5L);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		Thread thread = new Thread(task, "checkForDataUpdate");
		if (!BYPASS_QUEUE) {
			thread.start();
		}
	}

	/**
	 * Check for data update.
	 */
	private static void checkForDataUpdate() {
		final String loggerCaller = "checkForDataUpdate()";
		try {
			m_storageList.forEach((key, value) -> {
				while (value.peek() != null) {
					StorageManager.manageStorage(value.poll());
				}
			});
		} catch (Exception ex) {
			logger.logError(loggerCaller, ex);
			ex.printStackTrace();
		}

	}

	/**
	 * Inizializza il thread che invia i messaggi accodati in m_outputJVList al JeoViewer.
	 */
	private static void initSenderToJVMessage() {
		final String loggerCaller = "initSenderToJVMessage()";
		Thread scodatoreOutputList = new Thread("GF.ManagerBlackboard.initSenderToJVMessage") {
			@Override
			public void run() {
				while (true) {
					try {
						while (m_outputJVList.peek() != null) {
							CommunicationManager.send(m_outputJVList.poll());
						}
						synchronized (m_outputJVList) {
							m_outputJVList.wait(1000 * 5L);
						}
					} catch (Exception e) {
						logger.logError(loggerCaller, e);
						Threads.Sleep(200);
					}
				}
			}
		};
		scodatoreOutputList.setPriority(Thread.MAX_PRIORITY);
		scodatoreOutputList.start();
	}

	/**
	 * Extract id.
	 *
	 * @param internalJsonObjectStruct the internal json object struct
	 * @return the string
	 */
	private static String extractId(IRawData internalJsonObjectStruct) {

		if (m_mapID.isEmpty()) {
			var myDataTypeIdBB = StorageManager.getItemStorageOpt("PRELOADED_IDENTIFIERS_FOR_DATA_TYPE", "ID_CONFIG");
			if (!myDataTypeIdBB.isEmpty()) {
				myDataTypeIdBB.get().getKeys()
						.forEach(keyId -> m_mapID.put(keyId, myDataTypeIdBB.get().getSafeString(keyId)));
			} else {
				return IRawData.NO_ID;
			}
		}
		return internalJsonObjectStruct.getSafeString(m_mapID.getOrDefault(internalJsonObjectStruct.getType(), ""),
				IRawData.NO_ID);
	}
	// ===========================================================================================
		// Nested Class
		// ===========================================================================================
		/**
		 * The Class SmartFIFO.
		 */
		static class SmartFIFO {

			/** The queue. */
			private final ConcurrentLinkedQueue<String> m_queue = new ConcurrentLinkedQueue<>();

			/** The map. */
			private final Map<String, DataRootWrapper> m_map = new ConcurrentHashMap<>();

			/**
			 * Offer.
			 *
			 * @param dataRoot the data root
			 */
			public void offer(DataRoot dataRoot) {
				var curr = new DataRootWrapper(dataRoot);
				var prev = m_map.put(curr.getId(), curr);
				if (prev == null) {
					m_queue.offer(curr.getId());
					// } else {
					// System.out.println("===>> REPLACED "+prev.getId()+"-"+curr.getId());
				}
			}

			/**
			 * Size.
			 *
			 * @return the int
			 */
			public int size() {
				System.out.println("M=" + m_map.size());
				return m_queue.size();
			}

			/**
			 * Peek.
			 *
			 * @return the string
			 */
			public String peek() {
				return m_queue.peek();
			}

			/**
			 * Poll.
			 *
			 * @return the data root
			 */
			public DataRoot poll() {
				String id = m_queue.poll();
				DataRootWrapper data = m_map.remove(id);
				if (data != null) {
					return data.getM_data();
				}
				return null;
			}

			/**
			 * Poll opt.
			 *
			 * @return the optional
			 */
			public Optional<DataRoot> pollOpt() {
				return Optional.ofNullable(poll());
			}
		}

	/**
	 * Extract id.
	 *
	 * @return the string
	 */
	public static HashMapEx<String, Boolean> extractStatistics() {

		if (m_mapStatisticsID.isEmpty()) {
			var myDataTypeIdBB = StorageManager.getItemStorageOpt("PRELOADED_STATISTICS_FOR_DATA_TYPE",
					"STATISTICS_CONFIG");
			if (!myDataTypeIdBB.isEmpty()) {
				myDataTypeIdBB.get().getKeys()
						.forEach(keyId -> m_mapStatisticsID.put(keyId, myDataTypeIdBB.get().getSafeBoolean(keyId)));
			}
		}
		return m_mapStatisticsID;
	}

	/** The id counter. */
	private static long m_idCounter = 0;

	/**
	 * The Class DataRootWrapper.
	 */
	static class DataRootWrapper {

		/** The data. */
		private final DataRoot m_data;

		/** The id. */
		private String m_id;

		/**
		 * Instantiates a new data root wrapper.
		 *
		 * @param dataRoot the data root
		 */
		public DataRootWrapper(DataRoot dataRoot) {
			m_data = dataRoot;
		}

		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public String getId() {
			if (m_id == null) {
				var type = getM_data().getHeaderType();
				if (type.isPresent()) {
					switch (type.get()) {
					case TIMELINES:
						var nodeT = getM_data().getHeaderNode();
						if ((nodeT.isPresent())) {
							m_id = new StringBuilder(nodeT.get().getOperation()).append("-")
									.append(nodeT.get().getPairValue(EdmMetaKeys.NODE_T, "ID")).append("-")
									.append(nodeT.get().getPairValue(EdmMetaKeys.NODE_T, "ROW_ID")).toString();
						} else {
							m_id = new StringBuilder("T").append(m_idCounter++).toString();
						}
						break;
					case SERVICE:
						m_id = new StringBuilder("S").append(m_idCounter++).toString();
						break;
					case OBJECT:
						var node = getM_data().getHeaderNode();
						if (node.isPresent()) {
							m_id = new StringBuilder(node.get().getOperation()).append("-")
									.append(node.get().getPairValue(EdmMetaKeys.NODE_V, "ID")).toString();
						} else {
							m_id = new StringBuilder("O").append(m_idCounter++).toString();
						}
						break;
					case LIST:
						var nodeL = getM_data().getHeaderNode();
						if ((nodeL.isPresent())) {
							m_id = new StringBuilder(nodeL.get().getOperation()).append("-")
									.append(nodeL.get().getPairValue(EdmMetaKeys.NODE_L, "ID")).append("-")
									.append(nodeL.get().getPairValue(EdmMetaKeys.NODE_L, "ROW_ID")).toString();
						} else {
							m_id = new StringBuilder("L").append(m_idCounter++).toString();
						}
						break;
					default:
						m_id = new StringBuilder("U").append(m_idCounter++).toString();
						break;
					}
				}
			}
			return m_id;
		}

		/**
		 * Gets the m data.
		 *
		 * @return the m_data
		 */
		private DataRoot getM_data() {
			return m_data;
		}

	}

	/**
	 * The Class ManagerBlackboardRMIEndpointMonitor.
	 */
	static class ManagerBlackboardRMIEndpointMonitor extends Thread {

		/** The running. */
		private boolean running = true;

		/** The interval. */
		private int interval = 20000;

		/**
		 * Instantiates a new manager blackboard RMI endpoint monitor.
		 *
		 * @param interval the interval
		 */
		ManagerBlackboardRMIEndpointMonitor(int interval) {
			this.interval = interval;
		}

		/**
		 * Run.
		 */
		@Override
		public void run() {
			while (running) {
				try {

					if (LocateRegistry.getRegistry(CommunicationManager.LOCAL_HOST, Registry.REGISTRY_PORT) == null) {
						IRemoteBlackboard obj = null;
						if (ManagerBlackboard.remoteRegistry == null) {
							obj = (IRemoteBlackboard) UnicastRemoteObject.exportObject(ManagerBlackboard.getInstance(),
									Registry.REGISTRY_PORT);
						}
						if (obj != null) {
							ManagerBlackboard.remoteRegistry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
							ManagerBlackboard.remoteRegistry.rebind(GF_BLACKBOARD, obj);
						}
					}
					if (LocateRegistry.getRegistry(CommunicationManager.LOCAL_HOST, 1099) != null
							&& LocateRegistry.getRegistry(CommunicationManager.LOCAL_HOST, Registry.REGISTRY_PORT)
									.lookup(GF_BLACKBOARD) == null) {
						IRemoteBlackboard obj = null;
						if (ManagerBlackboard.remoteRegistry == null) {
							obj = (IRemoteBlackboard) UnicastRemoteObject.exportObject(ManagerBlackboard.getInstance(),
									Registry.REGISTRY_PORT);
						}
						if (obj != null) {
							ManagerBlackboard.remoteRegistry.rebind(GF_BLACKBOARD, obj);
						}
					}
					Thread.sleep(this.interval);
				} catch (Exception e) {
					logger.logError("ManagerBlackboardRMIEndpointMonitor", e);
					this.running = false;
				}
			}

			try {
				System.out.println("RMI-Unbind");
				ManagerBlackboard.remoteRegistry.unbind(GF_BLACKBOARD);
			} catch (RemoteException | NotBoundException e) {
				logger.logError("ManagerBlackboardRMIEndpointMonitor", e);
			}
			System.out.println("RMI-Init");
			ManagerBlackboard.remoteRegistry = null;
			ManagerBlackboard.initRMIEndpoint();
		}
	}

	/** {@inheritDoc} */
	@Override
	public IReadOnlyMap<String, String> getAllRemoteBBData(String dataType) throws RemoteException {
		return StorageManager.getItemsStore(dataType);
	}
}
