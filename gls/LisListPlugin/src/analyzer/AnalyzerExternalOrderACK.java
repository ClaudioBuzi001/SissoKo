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

import java.util.Optional;

import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;
import com.gifork.commons.data.RawDataFactory;

import applicationLIS.BlackBoardConstants_LIS.DataType;

/**
 * The Class AnalyzerExternalOrderACK.
 */
public class AnalyzerExternalOrderACK implements IObserver {

	/**
	 * Instantiates a new analyzer external order ACK.
	 */
	public AnalyzerExternalOrderACK() {

		StorageManager.register(this, DataType.EXTERNAL_ORDER.name());
	}

	/**
	 * Update.
	 *
	 * @param jsonOrder the json order
	 */
	@Override
	public void update(final IRawData jsonOrder) {

		if (jsonOrder.getType().equals("EXTERNAL_ORDER")) {
			final String orderID = jsonOrder.getSafeString("ORDER_ID");
			final String DATA = jsonOrder.getSafeString("DATA", "{}").replace("'", "\"");
			final String dataType = jsonOrder.getSafeString("OBJECT_TYPE");
			String dataId = jsonOrder.getSafeString("FLIGHT_NUM");
			final String dataValue = "";
			if (dataId.isEmpty()) {
				dataId = jsonOrder.getSafeString("OBJECT_ID");
			}

			dataId = dataId.replace("MTCD_", "");
			final Optional<IRawData> BBKSData = BlackBoardUtility.getDataOpt(dataType, dataId);
			BBKSData.ifPresent(js -> {
				final IRawDataElement jsonDataAux = js.getAuxiliaryData();

				final IRawData dataJson = RawDataFactory.createFromJson(DATA);
				if (orderID.equals("SET_AUXILIARY_DATA")) {

					jsonDataAux.put(dataJson.getSafeString("DATA_NAME"), dataJson.getSafeString("DATA_VALUE"));

					BlackBoardUtility.addData("EXTERNAL_ORDER", js);

				} else if (orderID.equals("ACK")) {
					String ackType = dataJson.getSafeString("ACK_TYPE");

					if (!dataValue.isEmpty()) {
						jsonDataAux.put(orderID, dataValue);
					} else {
						if (ackType.equals("ON_OFF")) {
							jsonDataAux.put(orderID, !jsonDataAux.getSafeBoolean(orderID, false));
						} else {
							jsonDataAux.put(orderID, true);
						}
					}
					BlackBoardUtility.addData("EXTERNAL_ORDER", js);
				}

			});

		}
	}
}
