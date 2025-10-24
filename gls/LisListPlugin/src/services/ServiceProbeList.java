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

import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

import application.pluginService.ServiceExecuter.IFunctionalService;
import applicationLIS.Utils_LIS;

/**
 * The Class ServiceProbeList.
 *
 * @author latorrem
 * @version $Revision$
 */
public class ServiceProbeList implements IFunctionalService {

	/** The data type. */
	private String DATA_TYPE = "";

	/** The field to check. */
	private String FIELD_TO_CHECK = "";

	/** The id. */
	private String ID = "";

	/** The order. */
	private String order;

	/**
	 * Execute.
	 *
	 * @param input the input
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData input) {
		String result = "";
		final IRawDataElement parameters = input.getAuxiliaryData();
		this.DATA_TYPE = input.getType();
		this.ID = input.getId();
		this.FIELD_TO_CHECK = parameters.getSafeString("FIELD_TO_CHECK").replace("..", "");

		if (FIELD_TO_CHECK.equals("ORDER_NAME")) {
			result = calculateOrderName();
		}

		if (FIELD_TO_CHECK.equals("ORDER_VALUE")) {
			result = calculateOrderValue();
		}

		if (FIELD_TO_CHECK.equals("COLOR_CONFLICT")) {
			result = calculateTemplateColor();
		}

		if (FIELD_TO_CHECK.equals("TEMPLATE")) {
			result = calculateTemplateColor();
		}

		if (FIELD_TO_CHECK.equals("ACT")) {
			result = formattingOrders("ACT");
		}

		if (FIELD_TO_CHECK.equals("DEL")) {
			result = formattingOrders("DEL");
		}

		if (FIELD_TO_CHECK.equals("EDIT")) {
			result = formattingOrders("EDIT");
		}
		return result;

	}

	/** The order value. */
	private static String orderValue = "";

	/**
	 * Calculate order value.
	 *
	 * @return the string
	 */
	private String calculateOrderValue() {

		final Optional<IRawData> jsonPr = BlackBoardUtility.getDataOpt(this.DATA_TYPE, ID);
		jsonPr.ifPresent(json -> {
			final String orderName = json.getSafeString("ORDER_NAME");
			orderValue = json.getSafeString("ORDER_VALUE");
			if (orderName.equals("XFL")) {
				orderValue = json.getSafeString("CPT_XFL");
			}
			if (orderName.equals("OHD")) {
				orderValue = String.valueOf(json.getSafeInt("HDG"));
			}
		});

		return orderValue;
	}

	/**
	 * Calculate template color.
	 *
	 * @return the string
	 */
	private String calculateTemplateColor() {
		final var jsonOpt = BlackBoardUtility.getDataOpt(this.DATA_TYPE, ID);
		if (jsonOpt.isPresent()) {
			if (FIELD_TO_CHECK.equals("COLOR_CONFLICT")) {
				if (Utils_LIS.isInTentativeConflict(jsonOpt.get().getSafeString("CALLSIGN"))) {
					return "#FFA500";
				} else {
					return "#FFFFFF";
				}
			} else if (FIELD_TO_CHECK.equals("TEMPLATE")) {

				return jsonOpt.get().getSafeString("TEMPLATE_COLOR", "#FFFFFF");

			}
		}
		return "";
	}

	/**
	 * Calculate order name.
	 *
	 * @return the string
	 */
	private String calculateOrderName() {
		final var json = BlackBoardUtility.getDataOpt(this.DATA_TYPE, ID);
		if (json.isPresent()) {
			String orderName = json.get().getSafeString("ORDER_NAME");
			if (orderName.equals("COO")) {
				orderName = "XFL";
			}
			return orderName;
		}
		return "";
	}

	/**
	 * Formatting orders.
	 *
	 * @param type the type
	 * @return the string
	 */
	private String formattingOrders(final String type) {

		final StringBuilder editData = new StringBuilder();
		final StringBuilder execData = new StringBuilder();
		final StringBuilder graphicOrder = new StringBuilder();
		final StringBuilder graphicOrderEdit = new StringBuilder();
		final StringBuilder editProbe = new StringBuilder();
		final StringBuilder execProbe = new StringBuilder();
		final StringBuilder probe_group_data = new StringBuilder();

		final StringBuilder id = new StringBuilder();
		final StringBuilder flightNum = new StringBuilder();
		final StringBuilder valueForQuickOrder = new StringBuilder();

		final String flight = ID.split("_")[0];
		final String stn = Utils_LIS.getTrkIdFromFn(flight);
		final StringBuilder objectId = new StringBuilder().append("LBL_").append(stn);
		final var json = BlackBoardUtility.getDataOpt(this.DATA_TYPE, ID);
		String orderName = "";
		String order_value = "";
		String CALLSIGN = "";
		if (json.isPresent()) {

			orderName = json.get().getSafeString("ORDER_NAME");
			order_value = json.get().getSafeString("ORDER_VALUE");
			CALLSIGN = json.get().getSafeString("CALLSIGN");
		}

		String preview_type = "quick";
		String preview_typeEdit = "quick";

		String valueProbeGroup = "SI";

		switch (orderName) {
		case "TRR":

			graphicOrderEdit.append("GRAPHIC_ORDER=REROUTING , ");

			valueForQuickOrder.append("'PROBE_PARAMS_BB' : 'true',");
			preview_typeEdit = "quickTRR";
			execProbe.append("GRAPHIC_PROBE=EXEC  , ");
			id.append("ID=").append(objectId).append(" , ");
			editProbe.append("GRAPHIC_PROBE=TRY , ");
			editData.append("DATA={").append(order_value).append("} ,");
			execData.append("DATA={").append(valueForQuickOrder).append("'TRY' : 'false','VALUE' : '")
			.append(order_value).append("' , 'PROBE_ID' : '").append(this.ID).append("' , 'CALLSIGN' : '").append(CALLSIGN).append("'} ,");
			break;
		case "OHD":

			graphicOrderEdit.append("GRAPHIC_ORDER=OPEN_HEADING , ");

			preview_typeEdit = "quickOHD";
			valueForQuickOrder.append("'PROBE_PARAMS_BB' : 'true',");
			execProbe.append("GRAPHIC_PROBE=EXEC  , ");
			id.append("ID=").append(objectId).append(" , ");
			editProbe.append("GRAPHIC_PROBE=TRY , ");
			editData.append("DATA={").append(order_value).append("} ,");
			execData.append("DATA={").append(valueForQuickOrder).append("'TRY' : 'false','VALUE' : '")
			.append(order_value).append("' , 'PROBE_ID' : '").append(this.ID).append("' , 'CALLSIGN' : '").append(CALLSIGN).append("'} ,");
			break;
		case "HDG":

			valueForQuickOrder.append("'PROBE_PARAMS_BB' : 'true',");
			execProbe.append("GRAPHIC_PROBE=EXEC  , ");
			id.append("ID=").append(this.ID).append(" , ");
			editProbe.append("GRAPHIC_PROBE=TRY , ");
			editData.append("DATA={").append(order_value).append("} ,");
			execData.append("DATA={").append(valueForQuickOrder).append("'TRY' : 'false','VALUE' : '")
			.append(order_value).append("' , 'PROBE_ID' : '").append(this.ID).append("' , 'CALLSIGN' : '").append(CALLSIGN).append("'} ,");
			break;
		case "DCT":

			graphicOrderEdit.append("GRAPHIC_ORDER=DIRECT_TO , ");

			valueForQuickOrder.append("'PROBE_PARAMS_BB' : 'true',");
			execProbe.append("GRAPHIC_PROBE=EXEC  , ");
			id.append("ID=").append(objectId).append(" , ");
			editProbe.append("GRAPHIC_PROBE=TRY , ");

			editData.append("DATA={'VALUE' : '").append(order_value)
					.append("' , 'TRY' : 'true' , 'TRY_TOGGLE_ENABLED' : 'true'} , ");
			execData.append("DATA={").append(valueForQuickOrder).append("'TRY' : 'false','VALUE' : '")
			.append(order_value).append("' , 'PROBE_ID' : '").append(this.ID).append("' , 'CALLSIGN' : '").append(CALLSIGN).append("'} ,");
			flightNum.append("FLIGHT_NUM= ").append(ID).append(" , ");
			break;
		default:
			switch (orderName) {
			case "XFL":
			case "COO":
				orderName = "XFL";

				preview_type = "quick";
				preview_typeEdit = "choiceXFL";
				valueForQuickOrder.append("'PROBE_PARAMS_BB' : 'true',");
				break;
			case "OHD":

				preview_type = "quick";
				break;
			case "RJP":
				preview_type = "quick";
				break;
			case "HDG":

				preview_type = "quick";
				break;
			case "PEL":
				preview_type = "choice";
				graphicOrder.append("GRAPHIC_ORDER=PREVIEW , ");
				break;
			case "ALV":
				preview_type = "quick";
				preview_typeEdit = "choiceALV";
				valueForQuickOrder.append("'CFL' : '").append(order_value).append("' , 'PROBE_GROUP':'")
						.append(valueProbeGroup).append("',");
				graphicOrderEdit.append("GRAPHIC_ORDER=PREVIEW , ");
				break;
			case "REV":
				preview_type = "preview";
				preview_typeEdit = "previewREV";
				graphicOrder.append("GRAPHIC_ORDER=PREVIEW , ");
				graphicOrderEdit.append("GRAPHIC_ORDER=PREVIEW , ");
				break;
			}

			editProbe.append("GRAPHIC_PROBE=TRY , ");
			execProbe.append("GRAPHIC_PROBE=EXEC  ,");

			editData.append("DATA={'VALUE' : '").append(order_value)
					.append("' , 'TRY' : 'true' , 'TRY_TOGGLE_ENABLED' : 'true'} , ");
			execData.append("DATA={ ").append(valueForQuickOrder).append("'VALUE' : '").append(order_value).append("' , 'PROBE_ID' : '").append(this.ID).append("' , 'CALLSIGN' : '").append(CALLSIGN)
			.append("' , 'TRY' : 'false', 'TRY_TOGGLE_ENABLED' : 'false'} , ");
			id.append("ID=").append(objectId).append(" , ");
			flightNum.append("FLIGHT_NUM= ").append(ID).append(" , ");
			break;
		}

		probe_group_data.append("DATA={'CALLSIGN' : '").append(CALLSIGN).append("' , 'RESET_TRY' : '2'}  ");

		switch (type) {
		case "ACT":
			order = "EXTERNAL_ORDER(" + graphicOrder + execProbe + id + flightNum + execData + " ORDER_ID=" + orderName
					+ ", PREVIEW_NAME=" + preview_type + orderName + ",  OBJECT_TYPE=" + orderName + ")";
			break;
		case "EDIT":
			if (!orderName.equals("RJP")) {
				order = "EXTERNAL_ORDER(" + graphicOrderEdit + editProbe + id + flightNum + editData + " ORDER_ID="
						+ orderName + ", PREVIEW_NAME=" + preview_typeEdit + ",  OBJECT_TYPE=" + orderName + ")";
			} else {
				order = "";
			}
			break;
		case "DEL":
			order = "EXTERNAL_ORDER(" + probe_group_data + ", GRAPHIC_PROBE=EXEC ," + id
					+ " ORDER_ID=PGD , PREVIEW_NAME=quickPGD" + ",  OBJECT_TYPE=PGD )";
			break;
		default:
			break;
		}

		return order;

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The My name. */
		String myName = "PROBE_LIST";
		return myName;
	}

}
