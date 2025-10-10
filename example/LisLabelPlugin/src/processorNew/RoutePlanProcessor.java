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

import java.util.HashMap;

import com.fourflight.WP.ECI.edm.EdmModelKeys.TrajectoryGroundPoint;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

/**
 * The Class RoutePlanProcessor.
 */
enum RoutePlanProcessor {
	;

	/**
	 * To xml.
	 *
	 * @param routePlane the route plane
	 * @param objectNode the object node
	 */
	static void toXml(final IRawData routePlane, final HeaderNode objectNode) {
		if (routePlane != null) {
			if (routePlane.getSafeInt(TrajectoryGroundPoint.GT_POINT_NUM) > 0) {

				final var jsonArr = routePlane.getSafeRawDataArray("GT_POINT");
				objectNode.addLine(TrajectoryGroundPoint.GT_POINT_NUM,
						routePlane.getSafeString(TrajectoryGroundPoint.GT_POINT_NUM));

				for (int i = 0; i < jsonArr.size(); i++) {

					final var jsonObj = jsonArr.get(i);

					final HashMap<String, String> attributes = new HashMap<>();
					attributes.put("LAT", jsonObj.getSafeString("GT_Y"));
					attributes.put("LON", jsonObj.getSafeString("GT_X"));
					attributes.put("ID", jsonObj.getSafeString("GT_ID"));
					attributes.put("CLEARED", jsonObj.getSafeString("GT_CLEARED"));
					attributes.put("REPORTED", jsonObj.getSafeString("GT_REPORTED"));
					//attributes.put("ETO", jsonObj.getSafeString("GT_ETO"));
					//attributes.put("STATUS", jsonObj.getSafeString("GT_STATUS"));
					attributes.put("TYPE", jsonObj.getSafeString("GT_TYPE"));
					attributes.put("ALIAS", jsonObj.getSafeString("GT_ALIAS"));
					//attributes.put("SECTOR", jsonObj.getSafeString("GT_SECTOR"));
					//attributes.put("PERCENTOFGREEN", jsonObj.getSafeString("GT_PERCENTOFGREEN"));

					objectNode.addLine("GT_POINT_" + (i + 1), "", attributes);
				}

			}
		}
	}
}
