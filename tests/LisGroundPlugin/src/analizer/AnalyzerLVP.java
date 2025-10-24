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
package analizer;

import java.util.Optional;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataArray;
import com.gifork.commons.data.IRawDataElement;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

import XAI.XAIData.env.EnvBlackboardTags;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxilliary.Constants;

/**
 * AnalyzerLVP is an implementation of the {@link IObserver} interface, designed to monitor changes
 * in aerodrome information and runway usage from the system blackboard. 
 * It detects changes in the Low Visibility Procedures (LVP) category of the main aerodrome and, 
 * if a change is detected, constructs and sends a response XML message containing the new category.
 *
 * <p>The observer listens specifically to the {@code ENV_AERODROMEINFO} and {@code ENV_RWY_IN_USE} 
 * data types and updates internal state accordingly.
 *
 * <p>This component is intended to be used within the LIS plugin environment to ensure proper 
 * monitoring and broadcasting of airfield category changes relevant for LVP operations.
 *
 * <p><strong>Responsibilities:</strong>
 * <ul>
 *   <li>Register itself to the blackboard to observe aerodrome-related data.</li>
 *   <li>Track and compare the currently used aerodrome and its category.</li>
 *   <li>React to changes and publish updates as XML messages when needed.</li>
 * </ul>
 *
 */
public class AnalyzerLVP implements IObserver {

	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateLogger(AnalyzerLVP.class);

	/** The category in use. */
	private Integer categoryInUse = -1;

	/** The aerodrome name. */
	private String aerodromeName = "";

	/** The is changed. */
	private boolean isChanged;

	/**
	 * Registers this observer to the blackboard for receiving updates related to aerodrome info
	 * and runway usage.
	 */
	public void register() {
		StorageManager.register(this, DataType.ENV_AERODROMEINFO.name());
		StorageManager.register(this, DataType.ENV_RWY_IN_USE.name());
	}

	/**
	 * Called when observed data is updated. Checks for aerodrome info updates and runway usage.
	 * If a change in LVP category is detected, constructs and sends an output message to the blackboard.
	 *
	 * @param inputJson the updated data from the blackboard
	 */
	@Override
	public void update(final IRawData inputJson) {
		final String loggerCaller = "update()";
		if (inputJson.getType().equals(DataType.ENV_AERODROMEINFO.name())) {
			final IRawDataArray aerodromes = inputJson.getSafeRawDataArray(EnvBlackboardTags.AERODROME_LIST);

			for (final IRawDataElement aerodrome : aerodromes) {
				if (aerodrome.getSafeBoolean(EnvBlackboardTags.AERODROME_INFO_ISMAIN)) {
					aerodromeName = aerodrome.getSafeString(EnvBlackboardTags.AERODROME_INFO_AERODROME_NAME);
				}
			}
		}
		if (!aerodromeName.isEmpty()) {
			changeCategory(inputJson);
		}

		if (isChanged) {

			final DataRoot rootData = DataRoot.createMsg();

			final var headerNode = rootData.addHeaderOfService(Constants.LVP_RESP_SERVICE);
			headerNode.addLine("AIRPCAT", categoryInUse.toString());
			ManagerBlackboard.addJVOutputList(rootData);

			LOGGER.logInfo(loggerCaller, "AnalyzerLVP - xml send: " + rootData.toXml());
		}
	}

	/**
	 * Compares the current runway category for the active aerodrome with the last known value.
	 * If a difference is found, the internal category is updated and a flag is set.
	 *
	 * @param inputJson the updated data from the blackboard
	 */
	private void changeCategory(final IRawData inputJson) {
		isChanged = false;
		final Optional<IRawData> listRwyBB = BlackBoardUtility.getDataOpt(DataType.ENV_RWY_IN_USE.name());
		if (listRwyBB.isPresent()) {
			final IRawDataArray airpList = listRwyBB.get().getSafeRawDataArray(EnvBlackboardTags.AIRP_LIST);
			for (final IRawDataElement airp : airpList) {
				if (aerodromeName.equalsIgnoreCase(airp.getSafeString("airpName"))
						&& airp.getSafeInt(EnvBlackboardTags.AIRP_CAT) != categoryInUse) {
					categoryInUse = airp.getSafeInt("airpCat");
					isChanged = true;
					return;
				}
			}
		}

	}

}
