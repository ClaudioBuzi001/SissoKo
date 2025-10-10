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

import applicationLIS.BlackBoardConstants_LIS.DataType;
import com.fourflight.WP.ECI.edm.DataRoot;
import com.gifork.blackboard.BlackBoardUtility;
import common.Utils;

/**
 * The Class MTCDOXfNotify.
 */
public enum MTCDOXfNotify {
    ;

    /**
	 * Process oxf time notify.
	 */
	public static void processOxfTimeNotify() {
		final var ENV_OWN = BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name());
		final var MTCD_OXF_TIME_NOTIFY = BlackBoardUtility.getDataOpt(DataType.MTCD_OXF_TIME_NOTIFY.name());

		if (ENV_OWN.isPresent() && MTCD_OXF_TIME_NOTIFY.isPresent()) {
			final int OWNP = ENV_OWN.get().getSafeInt("OWNP");
			final var sectorArray = MTCD_OXF_TIME_NOTIFY.get().getSafeRawDataArray("SECTOR_TIME_ITEM");
			for (final com.gifork.commons.data.IRawDataElement SECTOR_TIME_ITEM : sectorArray) {
				final int SECTOR_ID = SECTOR_TIME_ITEM.getSafeInt("SECTOR_ID");
				if (SECTOR_ID == OWNP) {
					final String MAX_OXF_TIME = "" + SECTOR_TIME_ITEM.getSafeInt("MAX_OXF_TIME") / 60;
					final String MIN_OXF_TIME = "" + SECTOR_TIME_ITEM.getSafeInt("MIN_OXF_TIME") / 60;
					final String OXF_TIME = "" + SECTOR_TIME_ITEM.getSafeInt("OXF_TIME") / 60;
					sendXMLPPDUpdateOXFTime(MIN_OXF_TIME, MAX_OXF_TIME, OXF_TIME);
					break;
				}
			}
		}
	}
	
	
	/**
	 * Send XMLPPD update OXF time.
	 *
	 * @param _MIN_OXF_TIME the  min oxf time
	 * @param _MAX_OXF_TIME the  max oxf time
	 * @param _OXF_TIME the  oxf time
	 */
	private static void sendXMLPPDUpdateOXFTime(final String _MIN_OXF_TIME, final String _MAX_OXF_TIME , final String _OXF_TIME)	{
		final var rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService("UPDATE_OXF_TIME_CONFIG");
		serviceNode.addLine("MIN_OXF_TIME", _MIN_OXF_TIME);
		serviceNode.addLine("MAX_OXF_TIME", _MAX_OXF_TIME);
		serviceNode.addLine("OXF_TIME", _OXF_TIME);
		Utils.doTCPClientSender(rootData);
	}
	
}
