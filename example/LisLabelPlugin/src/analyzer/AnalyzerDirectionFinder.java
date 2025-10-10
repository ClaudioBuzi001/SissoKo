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

import java.util.HashMap;
import java.util.HashSet;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.Generics;
import com.leonardo.infrastructure.log.ILogger;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxiliary.directionfinder.DirectionFinderInputConstants;
import auxiliary.directionfinder.DirectionFinderOutputConstants;
import auxiliary.directionfinder.DirectionFinderTOManager;

/**
 * The {@code AnalyzerDirectionFinder} class implements the {@link IObserver} interface and is responsible
 * for processing direction finder data received from the blackboard.
 * <p>
 * It listens to updates from two data sources:
 * <ul>
 *   <li>{@code DIRECTION_FINDER} – containing real-time bearing data</li>
 *   <li>{@code BB_DIRECTION_FINDER_JV} – managing visualization settings for the direction finder data</li>
 * </ul>
 *
 * <p>Based on incoming updates, this class sends messages to the viewer system, 
 * indicating real-time or last known direction finder bearings, or removes them if necessary.
 * It also handles time-based logic to control when data should be removed from the display.
 */
public class AnalyzerDirectionFinder implements IObserver {

	/** The Constant logger. */
	private static final ILogger LOGGER= LoggerFactory.CreateLogger(AnalyzerDirectionFinder.class);

	/** The to mng. */
	private final DirectionFinderTOManager toMng = new DirectionFinderTOManager();

	/** The id to visualize. */
	private final HashSet<String> idToVisualize = new HashSet<>();

	/** The last bearing map. */
	private final HashMap<String, String> lastBearingMap = new HashMap<>();

	/**
	 * Registers this observer to the blackboard to receive updates related to direction finder data.
	 * Also starts the {@link DirectionFinderTOManager} thread, which manages timers for auto-expiry of data.
	 */
	public void register() {

		StorageManager.register(this, DataType.DIRECTION_FINDER.name());
		StorageManager.register(this, DataType.BB_DIRECTION_FINDER_JV.name());
		final Thread toMngTh = new Thread(toMng);
		toMngTh.start();
	}

	/**
	 * Handles updates from the blackboard. Based on the data type and operation,
	 * this method performs the following:
	 * <ul>
	 *   <li>If the data is of type {@code DIRECTION_FINDER}, inserts or deletes bearing information 
	 *       and sends it to the viewer accordingly.</li>
	 *   <li>If the data is of type {@code BB_DIRECTION_FINDER_JV}, manages whether the information 
	 *       should be visualized or not, based on {@code STATUS} and {@code SUBJECT} fields.</li>
	 * </ul>
	 *
	 * @param df the blackboard data containing direction finder information or visualization commands
	 */
	@Override
	public void update(final IRawData df) {
		final String loggerCaller = "update()";
		if (df.getType().equals(DataType.DIRECTION_FINDER.name())) {
			final Operation operation = df.getOperation();
			if (Generics.isOneOf(operation, Operation.INSERT, Operation.UPDATE, Operation.DELETE)) {
				final boolean isInsert = operation == Operation.INSERT;
				final boolean isDelete = operation == Operation.DELETE;
				if (isInsert) {

					final String channelId = df.getSafeString(DirectionFinderInputConstants.CHANNEL_ID, "");
					final String bearing = df.getSafeString(DirectionFinderInputConstants.BEARING, "");
					final String id = df.getId();
					if (idToVisualize.contains(channelId)) {
						sendDirectionFinderRealtimeToViewer(df);
					}
					lastBearingMap.put(id, bearing);
					if (idToVisualize.contains(channelId + "_LAST")) {
						sendDirectionFinderLastToViewer(channelId);
					}
					toMng.setTimer(id, 4000L);
				} else if (isDelete) {
					final String id = df.getId();
					deleteDirectionFinderRealtimeFromViewer(id);
					toMng.deleteTimer(id);
				} else {
					LOGGER.logError(loggerCaller, "Not supported Operation of " + operation + "for blackboard element "
							+ DataType.DIRECTION_FINDER.name());
				}
			}
		} else if (df.getType().equals(DataType.BB_DIRECTION_FINDER_JV.name())) {
			final Operation operation = df.getOperation();
			if (Generics.isOneOf(operation, Operation.INSERT, Operation.UPDATE, Operation.DELETE)) {
				final boolean isInsert = operation == Operation.INSERT;
				final boolean isUpdate = operation == Operation.UPDATE;
				if (isInsert || isUpdate) {
					if (df.getSafeString("STATUS").equals("ON")) {

						if (df.getSafeString("SUBJECT").equals("LAST")) {
							idToVisualize.add(df.getId() + "_LAST");
							if (lastBearingMap.containsKey(df.getId())) {
								sendDirectionFinderLastToViewer(df.getId());
							}
						} else {
							idToVisualize.add(df.getId());
						}

					} else if (df.getSafeString("STATUS").equals("OFF")) {

						if (df.getSafeString("SUBJECT").equals("LAST")) {
							idToVisualize.remove(df.getId() + "_LAST");
							deleteDirectionFinderLastFromViewer(df.getId() + "_LAST");

						} else {
							idToVisualize.remove(df.getId());
							deleteDirectionFinderRealtimeFromViewer(df.getId());
						}
					} else {
						LOGGER.logError(loggerCaller, "Not supported Operation of " + operation
								+ "for blackboard element " + DataType.BB_DIRECTION_FINDER_JV.name());
					}
				}
			}
		} else {
			LOGGER.logError(loggerCaller, "Wrong Direction Finder Type, of Type : " + df.getType());
		}

	}

	/**
	 * Sends a real-time bearing update for a direction finder channel to the viewer system.
	 *
	 * @param df the raw data containing channel ID and bearing information
	 */
	private static void sendDirectionFinderRealtimeToViewer(final IRawData df) {

		final String channelId = df.getSafeString(DirectionFinderInputConstants.CHANNEL_ID, "");
		final String bearing = df.getSafeString(DirectionFinderInputConstants.BEARING, "");
		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.VDF_REALTIME);

		objectNode.addLine(DirectionFinderOutputConstants.ID, channelId);
		objectNode.addLine(DirectionFinderOutputConstants.BEARING, bearing);

		ManagerBlackboard.addJVOutputList(rootData);
	}

	/**
	 * Sends the last known bearing for the specified direction finder channel to the viewer system.
	 *
	 * @param channelId the identifier of the direction finder channel
	 */
	private void sendDirectionFinderLastToViewer(final String channelId) {

		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.VDF_LAST);

		objectNode.addLine(DirectionFinderOutputConstants.ID, channelId);
		objectNode.addLine(DirectionFinderOutputConstants.BEARING, lastBearingMap.get(channelId));

		ManagerBlackboard.addJVOutputList(rootData);
	}

	/**
	 * Removes the real-time bearing display for the specified direction finder channel from the viewer.
	 *
	 * @param channelId the identifier of the direction finder channel to be removed
	 */
	private static void deleteDirectionFinderRealtimeFromViewer(final String channelId) {
		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.DELETE, EdmModelKeys.HeaderType.VDF_REALTIME);
		objectNode.addLine(DirectionFinderOutputConstants.ID, channelId);
		ManagerBlackboard.addJVOutputList(rootData);
	}

	/**
	 * Removes the last known bearing display for the specified direction finder channel from the viewer.
	 *
	 * @param channelId the identifier of the direction finder channel to be removed
	 */
	private static void deleteDirectionFinderLastFromViewer(final String channelId) {
		final DataRoot rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(Operation.DELETE, EdmModelKeys.HeaderType.VDF_LAST);
		objectNode.addLine(DirectionFinderOutputConstants.ID, channelId);
		ManagerBlackboard.addJVOutputList(rootData);
	}

}
