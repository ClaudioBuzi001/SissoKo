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
package processorNew;

import java.util.Optional;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.EdmModelKeys.TrajectoryPoint;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataArray;
import com.gifork.commons.data.IRawDataElement;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightInputConstants;
import auxiliary.flight.FlightOutputConstants;
import common.ColorConstants;
import common.CommonConstants;
import common.Utils;

/**
 * The Class FlightTrjProcessor.
 */
public enum FlightTrjProcessor {
	;

	/**
	 * Process.
	 *
	 * @param rawdataFlight the rawdata flight
	 */
	public static void process(final IRawData rawdataFlight) {
		XMLCreation(rawdataFlight);
	}

	/**
	 * XML creation.
	 *
	 * @param rawdataFlight the rawdata flight
	 */
	private static void XMLCreation(final IRawData rawdataFlight) {
		final DataRoot rootData = DataRoot.createMsg();
		HeaderNode objectNode;
		String id;

		final String flight_num = rawdataFlight.getId();

		final var trk = Utils_LIS.getTrkFromFn(flight_num);
		String stn = rawdataFlight.getSafeString("STN");
		if (stn.equals(CommonConstants.ABSENT_STN) || stn.isEmpty()) {
			if (trk.isPresent()) {
				id = Utils.getTrackId(trk.get().getId());

				objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
			} else {
				id = Utils.getFlightId(flight_num);
				objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.FLIGHT);
			}
		} else {
			if (trk.isPresent()) {
				id = Utils.getTrackId(trk.get().getId());
				objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.TRACK);
			} else {
				id = Utils.getFlightId(flight_num);
				objectNode = rootData.addHeaderOfObject(Operation.UPDATE, EdmModelKeys.HeaderType.FLIGHT);
			}
		}

		if (objectNode != null) {
			analyzeFlightTrajectory(objectNode, rawdataFlight);
			objectNode.addLine(CommonConstants.ID, id);
			Utils.doTCPClientSender(rootData);
		}
	}

	/**
	 * analyzeFlightTrajectory.
	 *
	 * @param objectNode    the object node
	 * @param rawdataFlight the l array
	 */
	private static void analyzeFlightTrajectory(final HeaderNode objectNode, final IRawData rawdataFlight) {
		String colorLev = "";
		String orderLev = "";
		String orderTime = "";
		IRawDataArray sectorArray = null;
		final Optional<IRawData> sectorTableJSon = BlackBoardUtility.getDataOpt(DataType.ENV_SECTOR_TABLE.name());
		if (sectorTableJSon.isPresent()) {
			sectorArray = sectorTableJSon.get().getSafeRawDataArray("SECTOR_TABLE");
		}
		Optional<IRawData> jsonFl = BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(),
				rawdataFlight.getSafeString("FLIGHT_NUM"));
		if (jsonFl.isPresent()) {
			final boolean epp_warningLev = jsonFl.get().getSafeBoolean(FlightInputConstants.EPP_WARNING_LEVEL);
			final boolean epp_warningTime = jsonFl.get().getSafeBoolean(FlightInputConstants.EPP_WARNING_TIME);
			if (epp_warningLev) {
				colorLev = ColorConstants.RED;
				orderLev = FlightOutputConstants.EPP_WARNING_LEVEL_ACK_EXTERNAL_ORDER;
			}
			if (epp_warningTime) {
				orderTime = FlightOutputConstants.EPP_WARNING_TIME_ACK_EXTERNAL_ORDER;
			}
		}

		IRawDataArray rawTrjArra = rawdataFlight.getSafeRawDataArray(CommonConstants.TRJ_ARRAY);
		final int numPoint = rawTrjArra.size();
		objectNode.addLine(TrajectoryPoint.FP_POINT_NUM, String.valueOf(numPoint));
		for (int i = 0; i < numPoint; i++) {
			final int sopsectorID = rawTrjArra.get(i).getSafeInt(TrajectoryPoint.SOP);

			objectNode.addLine(EdmModelKeys.TrajectoryPoint.POINT + i, String.valueOf(i))
					.setAttribute(EdmModelKeys.TrajectoryPoint.LEV,
							rawTrjArra.get(i).getSafeString(TrajectoryPoint.LEV))
					.setAttribute(EdmModelKeys.Attributes.COLOR, colorLev)
					.setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, orderLev)
					.setAttribute(EdmModelKeys.TrajectoryPoint.OVF,
							rawTrjArra.get(i).getSafeString(TrajectoryPoint.OVF))
					.setAttribute(EdmModelKeys.TrajectoryPoint.SCT, getRespName(i, sopsectorID, sectorArray))
					.setAttribute(EdmModelKeys.TrajectoryPoint.ETO,
							rawTrjArra.get(i).getSafeString(TrajectoryPoint.ETO))
					.setAttribute(EdmModelKeys.Attributes.LEFT_MOUSE_CALLBACK, orderTime)
					.setAttribute(EdmModelKeys.TrajectoryPoint.X, rawTrjArra.get(i).getSafeString(TrajectoryPoint.X))
					.setAttribute(EdmModelKeys.TrajectoryPoint.Y, rawTrjArra.get(i).getSafeString(TrajectoryPoint.Y))
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
					.setAttribute(EdmModelKeys.TrajectoryPoint.PT, rawTrjArra.get(i).getSafeString(TrajectoryPoint.PT))
					.setAttribute(EdmModelKeys.TrajectoryPoint.TAS,
							rawTrjArra.get(i).getSafeString(TrajectoryPoint.TAS))
					.setAttribute(EdmModelKeys.TrajectoryPoint.CDIS,
							rawTrjArra.get(i).getSafeString(TrajectoryPoint.CDIS))
					.setAttributeIfNotEmpty(EdmModelKeys.TrajectoryPoint.LRP,
							rawTrjArra.get(i).getSafeString(TrajectoryPoint.LRP))
					.setAttribute(EdmModelKeys.TrajectoryPoint.FLAGS,
							rawTrjArra.get(i).getSafeString(TrajectoryPoint.FLAGS))
					.setAttribute(EdmModelKeys.TrajectoryPoint.EDIT,
							rawTrjArra.get(i).getSafeString(TrajectoryPoint.EDIT))
					.setAttribute(EdmModelKeys.TrajectoryPoint.VISIBLE,
							rawTrjArra.get(i).getSafeString(TrajectoryPoint.VISIBLE));
		}
	}

	/**
	 * Get RespName Point.
	 *
	 * @param idx         the idx
	 * @param sopsectorID the sop of point
	 * @param sectorArray
	 * @return respName
	 */
	private static String getRespName(final int idx, int sopsectorID, IRawDataArray sectorArray) {
		if (sectorArray != null) {
			for (final IRawDataElement sector : sectorArray) {
				final int sectorID = sector.getSafeInt("SECTOR_ID");
				if (sopsectorID == sectorID) {
					return sector.getSafeString("SECTOR_NAME");
				}
			}
		}
		return "";
	}

}