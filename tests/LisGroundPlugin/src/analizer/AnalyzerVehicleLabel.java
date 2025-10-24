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

import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.leonardo.infrastructure.Generics;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import processor.VehicleProcessor;

/**
 * The Class AnalyzerVehicleLabel.
 */
public class AnalyzerVehicleLabel implements IObserver {

	/**
	 * Instantiates a new analyzer vehicle label.
	 */
	public void register() {
		StorageManager.register(this, DataType.VEHICLE.name());
	}

	/**
	 * Update.
	 *
	 * @param json the json
	 */
	@Override
	public void update(final IRawData json) {

		if (Generics.isOneOf(json.getOperation(), Operation.INSERT, Operation.UPDATE)) {
			VehicleProcessor.process(json); 
		} else {
			VehicleProcessor.delete(json);
		}
	}

}
