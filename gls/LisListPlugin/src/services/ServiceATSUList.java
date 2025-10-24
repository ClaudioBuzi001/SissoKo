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

import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

import application.pluginService.ServiceExecuter.IFunctionalService;
import applicationLIS.BlackBoardUtility_LIS;
import applicationLIS.Utils_LIS;

/**
 * The Class ServiceATSUList.
 *
 * @author latorrem
 * @version $Revision$
 */
public class ServiceATSUList implements IFunctionalService {

	/** The data type. */
	private String DATA_TYPE = "";

	/** The val 1. */
	private String VAL_1 = "";

	/**
	 * Execute.
	 *
	 * @param input the input
	 * @return the object
	 */
	@Override
	public Object execute(final IRawData input) {
		String result = "";

		this.DATA_TYPE = input.getType();
		final IRawDataElement parameters = input.getAuxiliaryData();
		/** The field to check. */
		String FIELD_TO_CHECK = parameters.getSafeString("FIELD_TO_CHECK").replace("..", "");
		this.VAL_1 = parameters.getSafeString("VAL_1");

		if (FIELD_TO_CHECK.equals("MENU_FIR")) {
			result = calculateMenu_Fir();
		}

		if (FIELD_TO_CHECK.equals("CALLBACK_CHANGE_MAIN_FREQ")) {
			result = calculateCallback_MainFreqSector();
		}

		if (FIELD_TO_CHECK.equals("CALLBACK_CHANGE_BCK_FREQ")) {
			result = calculateCallback_BckFreqSector();
		}

		if (FIELD_TO_CHECK.equals("SCT")) {
			result = calculateSectName(input);
		}

		if (FIELD_TO_CHECK.equals("CFS")) {
			result = calculateCFS();
		}

		return result;

	}

	/**
	 * Calculate CFS.
	 *
	 * @return the string
	 */
	private String calculateCFS() {

		String ret_value = "";
		if (!this.VAL_1.isEmpty()) {

			if (this.VAL_1.equals("true")) {
				ret_value = "Y";
			} else if (this.VAL_1.equals("false")) {
				ret_value = "N";
			}
		}
		return ret_value;

	}

	/**
	 * Calculate callback main freq sector.
	 *
	 * @return the string
	 */
	private String calculateCallback_MainFreqSector() {
		String ret_call = "";
		if (!this.DATA_TYPE.isEmpty()) {

			/** The CCF callback main. */

			String CCFCallbackMain = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW ,ORDER_ID=CCF , OBJECT_TYPE=CCF ,  PREVIEW_NAME=previewCCF)";
			if (this.DATA_TYPE.equals("ENV_MYRESPONSIBILITIES")) {
				ret_call = CCFCallbackMain;
			}

		}
		return ret_call;

	}

	/**
	 * Calculate callback bck freq sector.
	 *
	 * @return the string
	 */
	private String calculateCallback_BckFreqSector() {
		String ret_call = "";
		if (!this.DATA_TYPE.isEmpty()) {

			/** The CCF callback bck. */

			String CCFCallbackBck = "EXTERNAL_ORDER(GRAPHIC_ORDER=PREVIEW ,ORDER_ID=CCF , OBJECT_TYPE=CCF ,  PREVIEW_NAME=previewCCF)";
			if (this.DATA_TYPE.equals("ENV_MYRESPONSIBILITIES")) {
				ret_call = CCFCallbackBck;
			}

		}
		return ret_call;

	}

	/**
	 * Calculate menu fir.
	 *
	 * @return the string
	 */
	private String calculateMenu_Fir() {
		String ret_call = "";
		if (!this.DATA_TYPE.isEmpty()) {

			if (this.DATA_TYPE.equals("ENV_ATSO_COP")) {
				if (!this.VAL_1.isEmpty()) {

					final String firCpdlCEnable = this.VAL_1;

					if (firCpdlCEnable.equals("true")) {
						/** The FCF menu disable. */

						String FCFMenuDisable = "CONTEXT_MENU(MENU_FILE=ATSUFirDisableContextMenu.xml)";
						ret_call = FCFMenuDisable;
					} else if (firCpdlCEnable.equals("false")) {
						/** The FCF menu enable. */
						String FCFMenuEnable = "CONTEXT_MENU(MENU_FILE=ATSUFirEnableContextMenu.xml)";
						ret_call = FCFMenuEnable;
					}

				}

			}

		}
		return ret_call;

	}

	/**
	 * Calculate sect name.
	 *
	 * @param input the input
	 * @return the string
	 */
	private String calculateSectName(final IRawData input) {
		String ret_value = "";
		if (!this.VAL_1.isEmpty()) {

			if (input.getType().equals("ENV_ATSO_COP")) {
				ret_value = this.VAL_1;
			}

			if (input.getType().equals("ENV_MYRESPONSIBILITIES")) {

				final String SCT = this.VAL_1;
				ret_value = Utils_LIS.searchSectName_FRomHDI_SCT_Level(SCT);
				
				if (ret_value.isEmpty()){
					ret_value =BlackBoardUtility_LIS.searchSECTOR_NAME(SCT);
				}		
			}
		}
		
		return ret_value;

	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The My name. */
		String myName = "ATSU_LIST";
		return myName;
	}

}
