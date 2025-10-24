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
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;

import java.util.Optional;

/**
 * The Class AnalyzerMTCDCallsignColor.
 */
public class AnalyzerMTCDCallsignColor implements IObserver  {

	/**
	 * Instantiates a new analyzer MTCD callsign color.
	 */
	public AnalyzerMTCDCallsignColor() {
		
		
		
		 StorageManager.register(this,  DataType.FLIGHT_EXTFLIGHT.name());


	}

	/**
	 * Update.
	 *
	 * @param jsonflight the jsonflight
	 */
	@Override
	public void update(final IRawData jsonflight) {
		if (BlackBoardUtility.getSize("BB_MTCD_LABEL")==0)
			return ;

		final String flightNum = jsonflight.getSafeString("FLIGHT_NUM");
			
			final Optional<IRawData> bbKFlight_ListMtcdConflict =BlackBoardUtility.getDataOpt(DataType.BB_MTCD_LABEL.name(),flightNum);

			bbKFlight_ListMtcdConflict.ifPresent(mtcd->{
				for (final String key : mtcd.getKeys()) {
					final Optional<IRawData> bbkMTCDConflict = BlackBoardUtility.getDataOpt(DataType.MTCD_ITEM_NOTIFY.name(),key);
					bbkMTCDConflict.ifPresent(jsonConflict->{
						final String csFlight = jsonflight.getSafeString("CALLSIGN");

						
						
						final String templateColor = jsonflight.getSafeString("TEMPLATE_COLOR");

						final String cs1Mtcd = jsonConflict.getSafeString("FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_1");
						final String cs2Mtcd = jsonConflict.getSafeString("FLIGHT_TO_FLIGHT_CONFLICT_OBJECT_NAME_2");

						String ret_color="#FFFFFF";
						
						if (!templateColor.isEmpty())
							ret_color =templateColor;
						
						if (csFlight.trim().equals(cs1Mtcd.trim()))
							jsonConflict.put("CS1_COLOR", ret_color);

						else if (csFlight.trim().equals(cs2Mtcd.trim()))
							jsonConflict.put("CS2_COLOR", ret_color);
						
						BlackBoardUtility.addData(DataType.MTCD_ITEM_NOTIFY.name(), jsonConflict);
					});
			}
			});
	}	
}
