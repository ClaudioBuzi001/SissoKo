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

import com.fourflight.WP.ECI.edm.DataRoot;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxilliary.Constants;

/**
 * The Class AnalyzerScaRWY.
 */
public class AnalyzerScaRWY implements IObserver {

	/**
	 * Instantiates a new analyzer sca RWY.
	 */
	public void register() {
		StorageManager.register(this, DataType.RWY_ALARM.name());
		//StorageManager.register(this, DataType.RWY_SCA_CONFLICT.name());
	}

	/**
	 * Update.
	 *
	 * @param inputJson the input json
	 */
	@Override
	public void update(final IRawData inputJson) {

		final String rwyName = inputJson.getSafeString(Constants.RWY_NAME_KEY);
		if (!rwyName.isEmpty()) {
			int alarmVal = inputJson.getSafeInt("ALARM_STATUS");

			 if (alarmVal==1) { 
				 sender(rwyName, "CONFLICT"); 
			 } 
			 else if (alarmVal==0) {
				 sender(rwyName, "BUSY"); 
			 } 
			 else { 
				 sender(rwyName, ""); 
			 }
			 
		}
	}

	/**
	 * Sender.
	 *
	 * @param rwyName   the rwy name
	 * @param alarmType the alarm type
	 */
	private void sender(final String rwyName, final String alarmType) {

		final DataRoot rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService(Constants.RWY_IN_ALARM_SERVICE);
		serviceNode.addLine(Constants.RWY_NAME_KEY, rwyName);
		serviceNode.addLine(Constants.ALARM_TYPE, alarmType);
		ManagerBlackboard.addJVOutputList(rootData);
	}

}
