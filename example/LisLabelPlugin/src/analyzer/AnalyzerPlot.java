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

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.EdmModelKeys;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;

import applicationLIS.BlackBoardConstants_LIS.DataType;
import auxiliary.track.TrackInputConstants;

/**
 * The Class AnalyzerPlot.
 * <p>
 * #SRS
 */
public class AnalyzerPlot implements IObserver {

	/**
	 * Instantiates a new analyzer plot.
	 */
	public void register() {
		StorageManager.register(this, DataType.PLOT.name());
	}

	/**
	 * Update.
	 *
	 * @param plot the plot
	 */
	@Override
	public void update(final IRawData plot) {
		processPlotLabel(plot);
	}

	/**
	 * Process plot label.
	 *
	 * @param obj the obj
	 */
	private static void processPlotLabel(final IRawData obj) {
		final var operation = obj.getOperation();
		final var rootData = DataRoot.createMsg();
		final var objectNode = rootData.addHeaderOfObject(operation, EdmModelKeys.HeaderType.PLOT);
		objectNode.addLine("ID", "PLOT_" + obj.getId());
		if (!obj.getId().equals("DELETE_ALL")) {

				objectNode.addLine("SECT_NUMB", obj.getSafeString("SECT_NUMB"));
				objectNode.addLine("NEW_SECTOR", obj.getSafeString("NEW_SECTOR"));
				objectNode.addLine("PLOTS_NUMBER", obj.getSafeString("PLOTS_NUMBER"));
			int lev = obj.getSafeInt("LEV");
			if (!obj.getSafeString("PLOTS_NUMBER").equals("0")) {
				// POSITION
				objectNode.addLine("LATITUDE", obj.getSafeString("LAT"));
				objectNode.addLine("LONGITUDE", obj.getSafeString("LONG"));
				
				String ssrCode = obj.getSafeString("SSR_CODE");
				String ssrMode = "";

				int modeFieldMode = obj.getSafeInt("SSR_MODE", 0);

				switch (modeFieldMode) {
				case 2:
					ssrMode = "1";
					break;
				case 4:
					ssrMode = "4";
					break;
				case 6:
					ssrMode = "A";
					break;
				case 8:
					ssrMode = "B";
					break;
				case 10:
					ssrMode = "D";
					break;
				case 14:
					ssrMode = "R";
					break;
				case 15:
					ssrMode = " ";
					break;
				default:
					break;
				}

				String SCODE = ssrMode + ssrCode.replace(" ", "");

				objectNode.addLine("SCODE", SCODE);

				
				if (lev == TrackInputConstants.ABSENT_LEVEL_PLOT) {
					objectNode.addLine("LEV", "");
				} else {
					objectNode.addLine("LEV", obj.getSafeString("LEV"));
				}

				
				String plotSym = "";
				if (obj.getSafeString("TST").equals("1")) {
					plotSym = "TRANSP";
				} else {
					if (obj.getSafeString("PSC").equals("0")) {
						plotSym = "PLOT_MLAT_ADS";
					} else if (obj.getSafeString("PSC").equals("1")) {
						plotSym = "PLOT_PRIM";
					} else if (obj.getSafeString("PSC").equals("2")) {
						plotSym = "PLOT_SEC";
					} else if (obj.getSafeString("PSC").equals("3")) {
						plotSym = "PLOT_MLAT_ADS"; 
													
													
					}
				}
				objectNode.addLine("POS_TAG", plotSym);
	
			}

		}
		ManagerBlackboard.addJVOutputList(rootData);
	}

}
