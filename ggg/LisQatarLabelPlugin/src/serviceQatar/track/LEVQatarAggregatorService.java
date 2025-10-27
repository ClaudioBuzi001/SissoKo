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
package serviceQatar.track;

import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.auxiliary.ConfigurationFile;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

import application.pluginService.ServiceExecuter.IAggregatorService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxiliaryQatar.QatarInputConstants;
import common.ColorConstants;

/**
 * The Class LEVQatarAggregatorService.
 *
 * @author latorrem
 * @version $Revision$
 */
public class LEVQatarAggregatorService implements IAggregatorService {

	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(LEVQatarAggregatorService.class);

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		String color = "";
		final int diffQNH = 6;
		int diffresult = -1;
		String QNHPrefix = "";

		final double trackDataBPS = inputJson.getSafeDouble(QatarInputConstants.BPS);
		String lev = "";
		int trackLEV = inputJson.getSafeInt("LEV", QatarInputConstants.ABSENT_LEVEL);

		final var dev_qna_info = BlackBoardUtility.getDataOpt(DataType.ENV_QNAINFO.name());
		final int[] correctionTableQnh = { -31, -31, -31, -30, -30, -30, -30, -29, -29, -29, -29, -28, -28, -28, -27,
				-27, -27, -27, -26, -26, -26, -26, -25, -25, -25, -24, -24, -24, -24, -23, -23, -23, -22, -22, -22, -22,
				-21, -21, -21, -21, -20, -20, -20, -19, -19, -19, -19, -18, -18, -18, -17, -17, -17, -17, -16, -16, -16,
				-16, -15, -15, -15, -14, -14, -14, -14, -13, -13, -13, -13, -12, -12, -12, -11, -11, -11, -11, -10, -10,
				-10, -10, -9, -9, -9, -8, -8, -8, -8, -7, -7, -7, -6, -6, -6, -6, -5, -5, -5, -4, -4, -4, -4, -3, -3,
				-3, -3, -2, -2, -2, -1, -1, -1, -1, 0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 5, 6,
				6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 9, 9, 9, 9, 10, 10, 10, 10, 11, 11, 11, 11, 12, 12, 12, 13, 13, 13, 13,
				14, 14, 14, 14 };

		final String LVL_Presentation = ConfigurationFile.getProperties("LVL_PRESENTATION", "LVL");
		if (trackLEV == QatarInputConstants.ABSENT_LEVEL) {
			lev = "";
			try {

				final var jsonFlight = Utils_LIS.getFlightFromTrkId(inputJson.getId());
				if (jsonFlight.isPresent()) {
					final String FLIGHT_LSV = jsonFlight.get().getSafeString("LSV", "").trim();
					if (FLIGHT_LSV.isEmpty() || FLIGHT_LSV.equals("-1")) {
						lev = LVL_Presentation;
					} else {
						lev = "L" + FLIGHT_LSV;
					}
				}

			} catch (final NumberFormatException e) {
				logger.logError("Error: ", e);
			}
		} else {

			int envTransitionLevel = -1;
			if (dev_qna_info.isPresent() && dev_qna_info.get().has("TRANS_LEVEL")) {
				envTransitionLevel = dev_qna_info.get().getSafeInt("TRANS_LEVEL", -1);
			}

			String envQNHREc = "-1";
			if (dev_qna_info.isPresent() && dev_qna_info.get().has("QNH")) {
				envQNHREc = dev_qna_info.get().getSafeString("QNH", "-1");
			}
			String qhnValue_new;

			int envQNH = -1;
			final int qnh;

			final String qnhInt;

			if (!envQNHREc.equals("-1")) {
				qhnValue_new = envQNHREc.trim().replace(".", "-");
				final String[] qnhValueArr = qhnValue_new.split("-");
				qnhInt = qnhValueArr[0];
				qnh = Integer.parseInt(qnhInt);
				envQNH = qnh;
			}

			if (trackLEV != -1) {
				lev = String.format("%03d", trackLEV);

				if (envTransitionLevel != -1 && envQNH != -1) {
					int LEVCorrection;
					if (trackLEV <= envTransitionLevel) {
						if (envQNH >= QatarInputConstants.FIRSTQNH || envQNH <= QatarInputConstants.LASTQNH) {
							final int tableindex = envQNH - QatarInputConstants.FIRSTQNH;
							LEVCorrection = correctionTableQnh[tableindex];
							trackLEV += LEVCorrection;
							QNHPrefix = BlackBoardUtility
									.getDataOpt(DataType.GI_FORK_CONFIGURATION.name(), "CONFIGURATION").isPresent()
											? BlackBoardUtility
													.getDataOpt(DataType.GI_FORK_CONFIGURATION.name(), "CONFIGURATION")
													.get().getSafeString("QNH_PREFIX")
											: "";

							if (QNHPrefix.isEmpty()) {
								QNHPrefix = "A";
							}

							if (trackDataBPS != 0.00) {
								final int result = (int) Math.round(trackDataBPS);
								diffresult = envQNH - result;
								if (diffresult > diffQNH) {
									color = ColorConstants.YELLOW;
								}
							}

							lev = String.format("%03d", trackLEV);

						}
					}
				}
			}
		}

		dataNode.addLine(QatarInputConstants.CMH, QNHPrefix).setAttributeIf(diffresult > diffQNH,
				EdmModelKeys.Attributes.COLOR, color);
		dataNode.addLine(QatarInputConstants.AFL, lev);
		dataNode.addLine(QatarInputConstants.LEV, lev);

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String service_name = QatarInputConstants.LEV;
		return service_name;
	}
}
