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

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.EdmModelKeys.TrajectoryPoint;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataArray;
import com.gifork.commons.data.IRawDataElement;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxiliary.flight.FlightAggregatorMap;
import auxiliary.flight.FlightAliasMap;
import auxiliary.flight.FlightBlackMap;
import common.CommonConstants;

/**
 * AnalyzerProbe is an observer class that listens to updates from the blackboard
 * regarding flight probes and trajectory probes. It converts received data into
 * a visual format and sends it to the viewer system.
 */
public class AnalyzerProbe implements IObserver {

	/** The Constant flightAliasMap. */
	private static final FlightAliasMap FLIGHT_ALIAS_MAP = new FlightAliasMap();

	/** The Constant flightBlackMap. */
	private static final FlightBlackMap FLIGHT_BLACK_MAP = new FlightBlackMap();

	/** The Constant flightAggregatorMap. */
	private static final FlightAggregatorMap FLIGHT_AGGREGATOR_MAP = new FlightAggregatorMap();

    /**
     * Registers this observer to receive updates for FLIGHT_PROBE and FLIGHT_TRJ_PROBE data types.
     */
	public void register() {
		StorageManager.register(this, DataType.FLIGHT_PROBE.name());
		StorageManager.register(this, DataType.FLIGHT_TRJ_PROBE.name());
	}

	 /**
     * Called when an update is received from the blackboard. Depending on the data type,
     * it processes flight probe or trajectory probe information and sends it to the viewer.
     *
     * @param probe the probe data received
     */
	@Override
	public void update(final IRawData probe) {

		IRawDataArray sectorArray = null;
		final Optional<IRawData> sectorTableJSon = BlackBoardUtility.getDataOpt(DataType.ENV_SECTOR_TABLE.name());
		if (sectorTableJSon.isPresent()) {
			sectorArray = sectorTableJSon.get().getSafeRawDataArray("SECTOR_TABLE");
		}

		final var type = EdmModelKeys.HeaderType.FLIGHT_PLAN_PROBE;
		final var rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(
				probe.getOperation() == Operation.DELETE ? Operation.DELETE : Operation.INSERT, type);
		String fn=probe.getSafeString("FLIGHT_NUM");
		if (probe.getType().equals(DataType.FLIGHT_PROBE.name())) {
			AnalyzerLabel.analyze(FLIGHT_BLACK_MAP, FLIGHT_AGGREGATOR_MAP, FLIGHT_ALIAS_MAP, probe, objectNode);
		} else if (probe.getType().equals(DataType.FLIGHT_TRJ_PROBE.name())) {
			IRawDataArray rawTrjArra = probe.getSafeRawDataArray(CommonConstants.TRJ_ARRAY);
			final int numPoint = rawTrjArra.size();
			objectNode.addLine(TrajectoryPoint.FP_POINT_NUM, "" + numPoint);
			
			objectNode.addLine("FLIGHT_NUM", fn);
			for (int i = 0; i < numPoint; i++) {
				final int sopsectorID = rawTrjArra.get(i).getSafeInt(TrajectoryPoint.SOP);

				objectNode.addLine(EdmModelKeys.TrajectoryPoint.POINT + i, "" + i)
						.setAttribute(EdmModelKeys.TrajectoryPoint.LEV,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.LEV))
						.setAttribute(EdmModelKeys.TrajectoryPoint.OVF,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.OVF))
						.setAttribute(EdmModelKeys.TrajectoryPoint.SCT, getRespName(i, sopsectorID, sectorArray))
						.setAttribute(EdmModelKeys.TrajectoryPoint.ETO,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.ETO))
						.setAttribute(EdmModelKeys.TrajectoryPoint.X,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.X))
						.setAttribute(EdmModelKeys.TrajectoryPoint.Y,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.Y))
						.setAttributeIfNotEmpty(EdmModelKeys.TrajectoryPoint.SOP,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.SOP))
						.setAttribute(EdmModelKeys.TrajectoryPoint.SPT,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.SPT))
						.setAttributeIfNotEmpty(EdmModelKeys.TrajectoryPoint.CTIM,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.CTIM))
						.setAttributeIfNotEmpty(EdmModelKeys.TrajectoryPoint.XRF,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.XRF))
						.setAttributeIfNotEmpty(EdmModelKeys.TrajectoryPoint.PTIM,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.PTIM))
						.setAttribute(EdmModelKeys.TrajectoryPoint.NAME,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.NAME))
						.setAttribute(EdmModelKeys.TrajectoryPoint.PDIS,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.PDIS))
						.setAttribute(EdmModelKeys.TrajectoryPoint.PT,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.PT))
						.setAttribute(EdmModelKeys.TrajectoryPoint.TAS,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.TAS))
						.setAttribute(EdmModelKeys.TrajectoryPoint.CDIS,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.CDIS))
						.setAttributeIfNotEmpty(EdmModelKeys.TrajectoryPoint.LRP,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.LRP))
						.setAttribute(EdmModelKeys.TrajectoryPoint.FLAGS,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.FLAGS))
						.setAttribute(EdmModelKeys.TrajectoryPoint.EDIT,
								rawTrjArra.get(i).getSafeString(TrajectoryPoint.EDIT));
			}

		}
		String id=probe.getSafeString("PROBE_ID");
		objectNode.addLine(CommonConstants.ID, id);
		ManagerBlackboard.addJVOutputList(rootData);
	}

	 /**
     * Returns the responsible sector name for a given trajectory point index and sector ID.
     *
     * @param idx the index of the trajectory point
     * @param sopsectorID the sector ID of the point
     * @param sectorArray the array of sector data from the environment
     * @return the responsible sector name if found; an empty string otherwise
     */
	private static String getRespName(final int idx, int sopsectorID, IRawDataArray sectorArray) {

		for (final IRawDataElement sector : sectorArray) {
			final int sectorID = sector.getSafeInt("SECTOR_ID");
			if (sopsectorID == sectorID) {
				return sector.getSafeString("SECTOR_NAME");
			}
		}
		return "";
	}
}