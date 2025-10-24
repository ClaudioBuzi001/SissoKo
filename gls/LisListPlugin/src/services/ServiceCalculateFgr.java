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
package services;

import java.util.Optional;

import com.gifork.auxiliary.ColorGeneric;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

import application.pluginService.ServiceExecuter.IFunctionalService;
import applicationLIS.BlackBoardConstants_LIS.DataType;
import applicationLIS.Utils_LIS;
import auxliary.PluginListConstants;


/**
 * The Class ServiceCalculateFgr.
 *
 * @author ggiampietro
 * @version $Revision$
 */
public class ServiceCalculateFgr implements IFunctionalService {

	/** The value to check. */
	private String VALUE_TO_CHECK = "";

	/** The ret_color. */
	private String ret_color = "";

	/** The Constant FIRSTQNH. */
	public static final int FIRSTQNH = 900;

	/** The Constant LASTQNH. */
	public static final int LASTQNH = 1065;

	/** The Constant ABSENT_LEVEL. */
	public static final int ABSENT_LEVEL = 32768;

	/** The Constant YELLOW. */
	public static final String YELLOW = "#FFFF00";

	/** The Constant ORANGE. */
	public static final String ORANGE = "#FFA500";

	/**
	 * Execute.
	 *
	 * @param inputJson the input json
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData inputJson) {

		/** The id. */
		String ID = inputJson.getId();
		/** The data type. */
		String DATA_TYPE = inputJson.getType();
		final IRawDataElement parameters = inputJson.getAuxiliaryData();
		/** The default. */
		String DEFAULT = parameters.getSafeString("DEFAULT");
		/** The field to check. */
		String FIELD_TO_CHECK = parameters.getSafeString("FIELD_TO_CHECK").replace("..", "");
		this.VALUE_TO_CHECK = parameters.getSafeString("VALUE_TO_CHECK");
		/** The check color. */
		String CHECK_COLOR = parameters.getSafeString("CHECK_COLOR");
		this.ret_color = "";

		if (DATA_TYPE.equals("AMAN_NSL_ITEM_NOTIFY")) {
			DATA_TYPE = "FLIGHT_EXTFLIGHT";
		}

		final Optional<IRawData> listColorGeneric = BlackBoardUtility.getDataOpt(DataType.PRELOADED_COLOR_SET.name(),
				"LIST_COLOR_GENERIC");

		/** The field to check value. */
		if (FIELD_TO_CHECK.equals("CFL_WARNING_FLAG") && DATA_TYPE.equals("FLIGHT_EXTFLIGHT")) {
			final boolean cflDeviation = parameters.getSafeBoolean("VERT_DEV_FLAG", false);
			final String template = parameters.getSafeString("TEMPLATE");
			final boolean isControlled = template.equals(PluginListConstants.TEMPLATE_CONTROLLED);
			final boolean cflWarningFlag = parameters.getSafeBoolean("CFL_WARNING_FLAG", false);

			if (cflDeviation && isControlled && cflWarningFlag) {
				ret_color = "#ffff00";
			}

		} else if (FIELD_TO_CHECK.equals("CONFLICT_OR_RISK") && DATA_TYPE.equals("MTCD_ITEM_NOTIFY")) {

			final Optional<IRawData> jsonMtcd = BlackBoardUtility.getDataOpt(DATA_TYPE, ID);
			if (jsonMtcd.isPresent()) {
				final String appoDataSUA = jsonMtcd.get().getSafeString("SUA");
				if (appoDataSUA.equals("1")) {
					ret_color = "#FFA500";
				} else {
					if (!listColorGeneric.get().isNull(FIELD_TO_CHECK)) {

						String FIELD_TO_CHECK_VALUE = inputJson.getSafeString(FIELD_TO_CHECK);

						final IRawDataElement fieldColorMap = listColorGeneric.get().getSafeElement(FIELD_TO_CHECK);

						final IRawDataElement valueParameters = fieldColorMap.getSafeElement(FIELD_TO_CHECK_VALUE);

						ret_color = valueParameters.getSafeString("COLOR");
					}
				}
			}
		} else if (FIELD_TO_CHECK.equals("EMERGENCY_RPAS") && DATA_TYPE.equals("TRACK")) {
			if (!listColorGeneric.get().isNull(FIELD_TO_CHECK)) {
				String FIELD_TO_CHECK_VALUE = this.VALUE_TO_CHECK;

				final IRawDataElement fieldColorMap = listColorGeneric.get().getSafeElement(FIELD_TO_CHECK);

				final IRawDataElement valueParameters = fieldColorMap.getSafeElement(FIELD_TO_CHECK_VALUE);

				ret_color = valueParameters.getSafeString("COLOR");
			}
		} else if (FIELD_TO_CHECK.equals("SHAPE") && DATA_TYPE.equals("SNET_MAP_LIST")) {
			final Optional<IRawData> json = BlackBoardUtility.getDataOpt(DataType.SNET_MAP_LIST.name(), ID);
			if (json.isPresent()) {
				final String shape = json.get().getSafeString("shape");
				final String points = json.get().getSafeString("points");
				if (shape.isEmpty() && points.isEmpty()) {
					ret_color = (!CHECK_COLOR.isEmpty()) ? CHECK_COLOR : DEFAULT;
				}
			}

		} else if (FIELD_TO_CHECK.equals("TEMPLATE") && DATA_TYPE.equals("TCT")) {

			final Optional<IRawData> TCT_bbSource = BlackBoardUtility.getDataOpt(DATA_TYPE, ID);
			TCT_bbSource.ifPresent(js -> {
				ret_color = Utils_LIS.getFlightValue(this.VALUE_TO_CHECK, "TEMPLATE_COLOR");
			});
		} else if (FIELD_TO_CHECK.equals("TEMPLATE") && DATA_TYPE.equals(DataType.FLIGHT_COORDINATION.name())) {

			final Optional<IRawData> flightCoordbbSource = BlackBoardUtility.getDataOpt(DATA_TYPE, ID);

			flightCoordbbSource.ifPresent(js -> {
				final String flightNum = flightCoordbbSource.get().getSafeString("FLIGHT_NUM");
				final Optional<IRawData> jsonFlight = BlackBoardUtility.getDataOpt(DataType.FLIGHT_EXTFLIGHT.name(),
						flightNum);
				jsonFlight.ifPresent(fl -> ret_color = jsonFlight.get().getSafeString("TEMPLATE_COLOR"));
			});
		} else if (FIELD_TO_CHECK.equals("BPS") && DATA_TYPE.equals("TRACK_MODES")) {
			ret_color = calculateBPSColor(inputJson, false);
		} else if (FIELD_TO_CHECK.equals("BPS_BV4")) {
			ret_color = calculateBPSColor(inputJson, true);
		} else {

			if (FIELD_TO_CHECK.equals("TEMPLATE") && DATA_TYPE.equals("FLIGHT_EXTFLIGHT")) {
				ret_color = inputJson.getSafeString("TEMPLATE_COLOR");
			}

			if (!listColorGeneric.get().isNull(FIELD_TO_CHECK)) {

				String FIELD_TO_CHECK_VALUE = inputJson.getSafeString(FIELD_TO_CHECK);

				final IRawDataElement fieldColorMap = listColorGeneric.get().getSafeElement(FIELD_TO_CHECK);

				final IRawDataElement valueParameters = fieldColorMap.getSafeElement(FIELD_TO_CHECK_VALUE);

				ret_color = valueParameters.getSafeString("COLOR");
			}

		}

		if (ret_color.isEmpty()) {
			ret_color = DEFAULT;
		}

		return ret_color;
	}

	

	/**
	 * @param inputJson
	 * @param useAlternativeColoring 
	 * @return retColor
	 */
	private static String calculateBPSColor(IRawData inputJson, boolean useAlternativeColoring) {

		String color = "";
		if (!useAlternativeColoring) {
			final double trackDataBPS = inputJson.getSafeDouble("BPS");
			int trackLEV = inputJson.getSafeInt("LEV", ABSENT_LEVEL);
			int diffresult = -1;
			final int diffQNH = 6;
			final var dev_qna_info = BlackBoardUtility.getDataOpt(DataType.ENV_QNAINFO.name());
			if (trackLEV != ABSENT_LEVEL) {
				if (dev_qna_info.isPresent()) {
					int envTransitionLevel = dev_qna_info.get().getSafeInt("TRANS_LEVEL", -1);
					int envQNHREc = dev_qna_info.get().getSafeInt("QNH", -1);
					int qnhState = dev_qna_info.get().getSafeInt("QNH_STATE", -1);
					if (envTransitionLevel != -1 && envQNHREc != -1) {
						if (trackLEV <= envTransitionLevel) {
							if (envQNHREc >= FIRSTQNH || envQNHREc <= LASTQNH) {
								if (trackDataBPS != 0.00) {
									final int result = (int) Math.round(trackDataBPS);
									diffresult = envQNHREc - result;
									if (diffresult > diffQNH) {
										if (qnhState == 16) {
											color = ORANGE;
										} else {
											color = YELLOW;
										}
									}
								} else {
									color = ColorGeneric.getInstance().getColor(ColorGeneric.CMH_WARNING);
								}
							}
						}
					}

				}
			}
		} else {
			final int diffQNH = 6;
			final double trackDataBPS = inputJson.getSafeDouble("BPS");
			final var dev_qna_info = BlackBoardUtility.getDataOpt(DataType.ENV_QNAINFO.name());
			if (dev_qna_info.isPresent()) {
				int envQNHREc = dev_qna_info.get().getSafeInt("QNH", -1);
				int envQNHState = dev_qna_info.get().getSafeInt("QNH_STATE", -1);
				if (trackDataBPS != 0.00) {
					final int result = (int) Math.round(trackDataBPS);
					int diffResult = envQNHREc - result;
					if (diffResult > diffQNH) {
						if (envQNHState == 16) {
							// the QNH value is not updated manually or by means of a received AWOS message for an
							// 		off-line configured VSP time (the timer starts at the start-up system time and
							// 		it is resets at any update)
							color = ORANGE;	// ColorConstants.ORANGE
						} else {
							// there is a mismatch between the received BPS and QNH calculated by the system
							color = YELLOW;	// ColorConstants.YELLOW
						}
					}
				}
			}
			if (inputJson.getSafeString("BPS", "").isBlank() || color.isBlank()) {
				// the BPS value is not available
				color = "#9C9C9C";
			}
		}
		return color;
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The My name. */
		String myName = "LIST_FGR_COLOR";
		return myName;
	}

}
