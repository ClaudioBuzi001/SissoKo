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
package service.tct;

import java.util.Comparator;
import java.util.Optional;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.leonardo.infrastructure.Pair;

import analyzer.AnalyzerLabel;
import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.track.TrackOutputConstants;
import common.ColorConstants;
import common.CommonConstants;
import common.Utils;
import processorNew.TrackProcessor;

/**
 * The Class EMG_TCTAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class EMG_TCTAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param rawdataInput the rawdata input
	 * @param dataNode     the data node
	 */
	@Override
	public void aggregate(final IRawData rawdataInput, final HeaderNode dataNode) {
		String colorTCT = "";
		String alarmTctType = "";
		String outAlarmTctType = "";
		String arrow = "";
		final String stn = rawdataInput.getSafeString(CommonConstants.STN);

		final var totalMap = Utils.getAllStnConflict(stn);

		/* Estrae il conflict ID tenendo presente la priorita' con il minor tempo */
		final String priorityTCTId = totalMap.values().stream()
				.map(x -> new Pair<>(x.getSafeString(CommonConstants.CONFLICT_NUMBER, ""),
						String.format("%s%04d", x.getSafeString(CommonConstants.URGENCY, "Z"),
								x.getSafeInt(CommonConstants.TIME_TO_CONFLICT, 9999))))
				.min(Comparator.comparing(Pair::getY)).map(Pair::getX).orElse("");

		final var rawdataTCT = BlackBoardUtility.getDataOpt(DataType.TCT.name(), priorityTCTId);
		if (rawdataTCT.isPresent()) {
			if (!rawdataTCT.get().getSafeString(CommonConstants.IS_TCT_TENTATIVE).equals("true")) {
				alarmTctType = rawdataTCT.get().getSafeString(CommonConstants.ALARM_TYPE);
				Optional<IRawData> trk1 = Utils_LIS
						.getTrkFromStn(rawdataTCT.get().getSafeString(CommonConstants.TRACK_NUMBER1));
				Optional<IRawData> trk2 = Utils_LIS
						.getTrkFromStn(rawdataTCT.get().getSafeString(CommonConstants.TRACK_NUMBER2));

				int GNDTrack_1 = 0;
				int GNDTrack_2 = 0;
				if (trk1.isPresent()) {
					GNDTrack_1 = trk1.get().getSafeInt("GND");
				}
				if (trk2.isPresent()) {
					GNDTrack_2 = trk2.get().getSafeInt("GND");
				}

				AnalyzerLabel.alarmedTct.put(stn, priorityTCTId);

				if (GNDTrack_1 > GNDTrack_2) {
					arrow = "LEFT";
				} else if (GNDTrack_1 < GNDTrack_2) {
					arrow = "RIGHT";
				}
			}

			if (!alarmTctType.isEmpty()) {
				if (alarmTctType.equals("TMM")) {
					colorTCT = ColorConstants.ORANGE;
					outAlarmTctType = "TMM";

				} else {
					if (alarmTctType.equals("HTSV") || alarmTctType.equals("HTTT") || alarmTctType.equals("HTTS")
							|| alarmTctType.equals("VTSV") || alarmTctType.equals("VTTT")
							|| alarmTctType.equals("VTTS")) {
						colorTCT = ColorConstants.MAGENTA;
					}

					if (alarmTctType.contains("TSV")) {
						outAlarmTctType = "TSV";
					} else if (alarmTctType.contains("TTT")) {
						outAlarmTctType = "TTT";
					} else if (alarmTctType.contains("TTS")) {
						outAlarmTctType = "TTS";
					}
				}
			}
		}
		if (BlackBoardUtility.getDataOpt(DataType.TRACK.name(), stn).isPresent()) {
			TrackProcessor.addPositionSymbol(BlackBoardUtility.getDataOpt(DataType.TRACK.name(), stn).get(), dataNode);
		}
		dataNode.addLine(TrackOutputConstants.POS_TAG_COLOR, colorTCT);
		dataNode.addLine(TrackOutputConstants.ARROW, arrow);

		dataNode.addLine(TrackOutputConstants.EMERGENCY_TCT, outAlarmTctType)
				.setAttribute(EdmModelKeys.Attributes.COLOR, colorTCT).setAttributeIf(!outAlarmTctType.isEmpty(),
						EdmModelKeys.Attributes.MIDDLE_MOUSE_CALLBACK,
						CommonConstants.SHOW_TCT_CONFLICT + priorityTCTId + CommonConstants.TCT_CALLBACK_CLOSE);
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = CommonConstants.TCT;
		return m_serviceName;
	}

}
