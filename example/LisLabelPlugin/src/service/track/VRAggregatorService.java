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
package service.track;

import java.util.Optional;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;
import com.gifork.commons.data.RawDataFactory;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.flight.FlightInputConstants;
import auxiliary.track.TrackInputConstants;

/**
 * The Class VRAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class VRAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		int numBattute = 0;
		int maxNumeroBattute = 0;
		final var jsondeviation = BlackBoardUtility.getDataOpt(DataType.PRELOADED_DEVIATION_DOWNLINKED.name());
		if (jsondeviation.isPresent()) {
			maxNumeroBattute = jsondeviation.get().getSafeInt("VR_ARC_MAX_TICK", 0);
		}

		final String dataType = "r";
		String dataArrow = "";
		final int vr_threshold = TrackInputConstants.VR_THRESHOLD;
		String vr_data = inputJson.getSafeString(TrackInputConstants.VR).trim();
		final String roc_data = inputJson.getSafeString(TrackInputConstants.ROCD).trim();
		final String vr_atd = inputJson.getSafeString(TrackInputConstants.VR_ATD).trim();
		final String vr_validity_value = inputJson.getSafeString(TrackInputConstants.VR_VALIDITY_VALUE);
		String arc;
		String rateclimind;
		int iarc = 0;
		final int signed_vr = 0;
		IRawDataElement auxiliaryData = RawDataFactory.createElem();

		Optional<IRawData> jsonFlight = Utils_LIS.getFlightFromTrkId(inputJson.getId());
		if (jsonFlight.isPresent()) {

			auxiliaryData = inputJson.getAuxiliaryData();
			numBattute = auxiliaryData.getSafeInt("numBattuteVR", 0);

			arc = jsonFlight.get().getSafeString(FlightInputConstants.ARC);
			rateclimind = jsonFlight.get().getSafeString(FlightInputConstants.RATECLIMIND);
		} else {
			arc = "0";
			rateclimind = "0";
		}

		if (!arc.isEmpty()) {
			iarc = Integer.parseInt(arc);
		}

		if (vr_data.isEmpty()) {
			vr_data = "0";
		}

		if (!vr_atd.isEmpty() && !vr_atd.equals("0")) {
			final float dataFloat = Integer.parseInt(vr_data);
			int dataInt = (Math.round(dataFloat / 100));
			if (Math.negateExact(dataInt) > 0) {
				dataArrow = "2";
				dataInt = (dataInt * -1);
			} else if (Math.abs(dataInt) > 0) {
				dataArrow = "1";
			}
			vr_data = String.valueOf(dataInt);
			if (Math.abs(dataInt) < 10) {
				vr_data = "0" + vr_data;
			}
		}

		String VR_FRAME = "OFF";

		if (vr_data.equals(TrackInputConstants.VR_0) || vr_data.isEmpty()) {
			vr_data = "00";
		}

		if (vr_validity_value.equalsIgnoreCase(TrackInputConstants.VR_VALIDITY_VALUE_FALSE)) {
			numBattute = 0;
		} else {
			if (iarc > 0) {
				if (((rateclimind.equals(TrackInputConstants.RATECLIMIND_NOT_MORE))
						&& (signed_vr > (iarc + vr_threshold)))
						|| ((rateclimind.equals(TrackInputConstants.RATECLIMIND_NOT_LESS))
								&& (signed_vr < (iarc - vr_threshold)))) {
					if (numBattute > maxNumeroBattute) {
						VR_FRAME = "ON";
					}
					numBattute++;
				} else {
					numBattute = 0;
				}
			} else {
				if (((rateclimind.equals(TrackInputConstants.RATECLIMIND_NOT_MORE))
						&& (signed_vr < (iarc - vr_threshold)))
						|| ((rateclimind.equals(TrackInputConstants.RATECLIMIND_NOT_LESS))
								&& (signed_vr < (iarc + vr_threshold)))) {
					if (numBattute > maxNumeroBattute) {
						VR_FRAME = "ON";
					}
					numBattute++;
				} else {
					numBattute = 0;
				}
			}
		}

		if (!auxiliaryData.isEmpty()) {
			auxiliaryData.put("numBattuteVR", numBattute);
		}

		String visibleArrow = "";

		if ((arc.isEmpty() || arc.equals("0")) && (roc_data.isEmpty() || roc_data.equals("0"))) {
			if (!dataArrow.isEmpty()) {
				visibleArrow = "true";
			}
		}

		dataNode.addLine(FlightInputConstants.ARC_ARROWV, dataArrow).setAttribute(EdmModelKeys.Attributes.VISIBLE,
				visibleArrow);

		String visible = "";
		if ((arc.isEmpty() || arc.equals("0")) && (roc_data.isEmpty() || roc_data.equals("0"))
				&& (!vr_data.equals("00"))) {
			visible = "true";
		}

		dataNode.addLine(TrackInputConstants.VR_ARC, dataType + vr_data).setAttribute(EdmModelKeys.Attributes.VISIBLE,
				visible);

		String show = "";

		if (VR_FRAME.equals("ON")) {
			show = "forced";
		}

		dataNode.addLine("FRAME_VR", VR_FRAME).setAttribute(EdmModelKeys.Attributes.SHOW, show);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = TrackInputConstants.VR;
		return service_name;
	}

}
