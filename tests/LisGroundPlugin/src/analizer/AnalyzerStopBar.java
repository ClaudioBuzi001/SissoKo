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
import com.gifork.commons.data.IRawDataArray;
import com.gifork.commons.data.IRawDataElement;
import com.leonardo.infrastructure.Strings;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxilliary.Constants;

/**
 * The Class AnalyzerSegment.
 */
public class AnalyzerStopBar implements IObserver {

	/**
	 * Instantiates a new analyzer segment.
	 *
	 */
	public void register() {
		StorageManager.register(this, DataType.ENV_STOPBAR.name());
	}

	/**
	 * Update.
	 *
	 * @param inputJson the input json
	 */
	@Override
	public void update(final IRawData inputJson) {

		final IRawDataArray list = inputJson.getSafeRawDataArray("STOPBAR_LIST");

		final DataRoot rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService("STOPBAR_UPDATE");
		for (int i = 0; i < list.size(); i++) {
			final IRawDataElement elem = list.get(i);
			final String guidance = elem.getSafeString("ID_STOPBAR").trim();
			final String status = elem.getSafeString("STATUS").trim();
			serviceNode.addLine(Strings.concat("S", String.valueOf(i + 1)), Strings.concat(guidance, "=", status));
		}
		serviceNode.addLine(Constants.SNUM, "" + list.size());
		ManagerBlackboard.addJVOutputList(rootData);

	}

}
