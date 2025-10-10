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
import com.gifork.auxiliary.ColorGeneric;
import com.gifork.auxiliary.ConfigurationFile;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliary.track.TrackInputConstants;
import auxiliary.track.TrackOutputConstants;
import common.ColorConstants;

/**
 * The Class LEVAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class LEVAggregatorService implements IAggregatorService {

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(LEVAggregatorService.class);

	
	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		String LEV_FRAME = "OFF";
		String color = "";
		final int diffQNH = 6;
		int diffresult = -1;
		String QNHPrefix = "";
		String valueCMH = "";
		boolean absentLevelTrack = false;
		
		final double trackDataBPS = inputJson.getSafeDouble(TrackInputConstants.BPS);
		String lev;
		int trackLEV = inputJson.getSafeInt("LEV", TrackInputConstants.ABSENT_LEVEL);

		final var dev_qna_info = BlackBoardUtility.getDataOpt(DataType.ENV_QNAINFO.name());

		final String LVL_Presentation = ConfigurationFile.getProperties("LVL_PRESENTATION", "LVL");
		final IRawDataElement auxiliaryData = inputJson.getAuxiliaryData();
		int m_numBattute = auxiliaryData.getSafeInt("numBattuteBPS", 0);

		if (trackLEV == TrackInputConstants.ABSENT_LEVEL) {
			lev = "";
			absentLevelTrack = true;
			try {

				Optional<IRawData> jsonFlight = Utils_LIS.getFlightFromTrkId(inputJson.getId());
				if (jsonFlight.isPresent()) {
					final String FLIGHT_LSV = jsonFlight.get().getSafeString("LSV", "").trim();
					if (FLIGHT_LSV.isEmpty() || FLIGHT_LSV.equals("-1")) {
						lev = LVL_Presentation;
					} else {
						lev = "L" + FLIGHT_LSV;

					}
				}

				double env_qnainfo = 0.0;
				double sogliaDeviation = 0.0;
				double maxNumeroBattute = 0.0;
				if (dev_qna_info.isPresent()) {
					env_qnainfo = dev_qna_info.get().getSafeDouble("QNH");
				}

				final var deviation = BlackBoardUtility.getDataOpt(DataType.PRELOADED_DEVIATION_DOWNLINKED.name());
				if (deviation.isPresent()) {
					sogliaDeviation = deviation.get().getSafeDouble("BPS_QNH_DEVIATION", 0);
					maxNumeroBattute = deviation.get().getSafeDouble("BPS_QNH_MAX_TICK", 0);
				}

				if (trackDataBPS > 0.001) {
					if (trackDataBPS > env_qnainfo + sogliaDeviation || trackDataBPS < env_qnainfo - sogliaDeviation) {
						if (m_numBattute > maxNumeroBattute) {
							LEV_FRAME = "ON";
						}
						m_numBattute++;
					} else {
						m_numBattute = 0;
					}
				} else {
					m_numBattute = 0;
				}
				auxiliaryData.put("numBattuteBPS", m_numBattute);
			} catch (final NumberFormatException e) {
				logger.logError("Error: ", e);
			}
		} else {
			lev = String.format("%03d", trackLEV);
			if (dev_qna_info.isPresent()) {
				int envTransitionLevel = dev_qna_info.get().getSafeInt("TRANS_LEVEL", -1);
				int envQNHREc = dev_qna_info.get().getSafeInt("QNH", -1);
				int envQNHState = dev_qna_info.get().getSafeInt("QNH_STATE", -1);
				
				QNHPrefix = BlackBoardUtility.getDataOpt(DataType.GI_FORK_CONFIGURATION.name(),
						"CONFIGURATION").isPresent() ? BlackBoardUtility
								.getDataOpt(DataType.GI_FORK_CONFIGURATION.name(), "CONFIGURATION")
								.get().getSafeString("QNH_PREFIX") : "";

				
				if (QNHPrefix.isEmpty()) {
					QNHPrefix = "A";
				}
				
				if (envTransitionLevel != -1 && envQNHREc != -1) {
					if (trackLEV <= envTransitionLevel) {
						if (envQNHREc >= TrackInputConstants.FIRSTQNH || envQNHREc <= TrackInputConstants.LASTQNH) {
							
							valueCMH=QNHPrefix;
							
							if (trackDataBPS != 0.00) {
								final int result = (int) Math.round(trackDataBPS);
								diffresult = envQNHREc - result;
								if (diffresult > diffQNH) {
									if (envQNHState == 16) {
										color = ColorConstants.ORANGE;	
									}else {
										color = ColorConstants.YELLOW;
									}		
								}
							}else {
								color = ColorGeneric.getInstance().getColor(ColorGeneric.CMH_WARNING);
							}
							
							lev = String.format("%03d", trackLEV);
						}
					}
				}
				
			}
		}

//		dataNode.addLine(TrackInputConstants.CMH, QNHPrefix).setAttributeIf(diffresult > diffQNH,
//				EdmModelKeys.Attributes.COLOR, color);
		
		dataNode.addLine(TrackInputConstants.CMH, valueCMH)
				.setAttributeIf(!color.isEmpty(), EdmModelKeys.Attributes.COLOR, color);
		
		dataNode.addLine(TrackInputConstants.AFL, lev);
		
		dataNode.addLine("AFL_ECHO", lev);
		
		dataNode.addLine(TrackInputConstants.LEV, lev);
		
		dataNode.addLine("ABSENT_LEV", absentLevelTrack);
		
		dataNode.addLine("FRAME_LEV", LEV_FRAME).setAttribute(EdmModelKeys.Attributes.SHOW,
				(!lev.equals(LVL_Presentation) && LEV_FRAME.equals("ON")) ? "forced" : "");

		// Set the CMH ("A" label field) for BV4
		// TODO: fields marked as "_BV4" will be moved to a dedicated GiFork plugin in the future
		String colorBpsBV4 = "";
		// TODO {VALERIO}: check if the logic below to set the color is correct
		if (dev_qna_info.isPresent()) {
			int envQNHREc = dev_qna_info.get().getSafeInt("QNH", -1);
			int envQNHState = dev_qna_info.get().getSafeInt("QNH_STATE", -1);
			if (trackDataBPS != 0.00) {
				final int result = (int) Math.round(trackDataBPS);
				diffresult = envQNHREc - result;
				if (diffresult > diffQNH) {
					if (envQNHState == 16) {
						// the QNH value is not updated manually or by means of a received AWOS message for an
						// 		off-line configured VSP time (the timer starts at the start-up system time and
						// 		it is resets at any update)
						colorBpsBV4 = ColorConstants.ORANGE;
					} else {
						// there is a mismatch between the received BPS and QNH calculated by the system
						colorBpsBV4 = ColorConstants.YELLOW;
					}
				}
			}
		}
		if (inputJson.getSafeString(TrackInputConstants.BPS, "").isBlank() || colorBpsBV4.isBlank()) {
			// the BPS value is not available
			colorBpsBV4 = "#9C9C9C";
		}

		// Set the BPS code field and its colors (shown in FHI and MODE-S lists)
		// TODO: fields marked as "_BV4" will be moved to a dedicated GiFork plugin in the future
		String bpsString = inputJson.getSafeString(TrackInputConstants.BPS, "");
		if (!bpsString.isEmpty()) {
			dataNode.addLine(TrackOutputConstants.BPS_BV4, bpsString)
					.setAttributeIf(!colorBpsBV4.isEmpty(), EdmModelKeys.Attributes.COLOR, colorBpsBV4)
					.setAttributeIf(!colorBpsBV4.isEmpty(), EdmModelKeys.Attributes.FLIGHT_DASHBAORD_COLOR, colorBpsBV4);
		}
		// Set the CMH code field and its colors (shown in label)
		// TODO: fields marked as "_BV4" will be moved to a dedicated GiFork plugin in the future
		dataNode.addLine(TrackOutputConstants.CMH_BV4, valueCMH)
			.setAttributeIf(!colorBpsBV4.isEmpty(), EdmModelKeys.Attributes.COLOR, colorBpsBV4);


	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = TrackOutputConstants.LEV;
		return m_serviceName;
	}

}
