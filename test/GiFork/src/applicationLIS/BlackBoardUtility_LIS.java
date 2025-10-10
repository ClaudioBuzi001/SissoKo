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
package applicationLIS;

import java.util.Optional;

import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.IRawDataElement;

import applicationLIS.BlackBoardConstants_LIS.DataType;

/**
 * The Class BlackBoardUtility_LIS.
 */
public class BlackBoardUtility_LIS {
	/**
	 * Gets the sector index TWR.
	 *
	 * @return the sector index TWR
	 */
	
	public static int getSectorIndexTWR() {

		int sectID = -1;
		
		
		int sectFamily = 0;
		Optional<IRawData> bbKsource = BlackBoardUtility.getDataOpt(DataType.ENV_SECTOR_TABLE.name());

		if (bbKsource.isPresent()) {
			IRawData json = bbKsource.get();

			var jsonTable = json.getSafeRawDataArray("SECTOR_TABLE");
			for (int i = 0; i < jsonTable.size(); i++) {
				var jsonElement = jsonTable.get(i);
				if (jsonElement.getSafeInt("SECTOR_FAMILY")==sectFamily) {
					sectID = jsonElement.getSafeInt("SECTOR_ID");
					break;
				}
			}
		}
		return sectID;

	}
	
	/**
	 * Gets the sector index TWR.
	 *
	 * @return the sector index TWR
	 */
	public static boolean isGroundFamily() {

		Optional<IRawData> bbKsource = BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name());

		if (bbKsource.isPresent()) {
			IRawData json = bbKsource.get();
			var valueFamily = json.getSafeString("FAMILY_TYPE");
			if (valueFamily.equals("GND")||valueFamily.equals("CDC"))
				return true;
		}
		return false;

	}
	
	/**
	 * Gets the sector index TWR.
	 *
	 * @return the sector index TWR
	 */
	public static boolean isMixGroundAirFamily() {

		Optional<IRawData> bbKsource = BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name());

		if (bbKsource.isPresent()) {
			IRawData json = bbKsource.get();
			var valueFamily = json.getSafeString("FAMILY_TYPE");
			if (valueFamily.equals("TWR"))
				return true;
		}
		return false;

	}
	
	/**
	 * Search SECTOR Family.
	 *
	 * @param sectorId the sectorId
	 * @return the int
	 */
	public static int getFamilySector(int sectorId) {

		int sectFamily = -1;
		var sectorTableJSon = BlackBoardUtility.getDataOpt(DataType.ENV_SECTOR_TABLE.name());
		if (sectorTableJSon.isPresent()) {
			var jsonTable = sectorTableJSon.get().getSafeRawDataArray("SECTOR_TABLE");
			
			for (int i = 0; i < jsonTable.size(); i++) {
				var jsonElement = jsonTable.get(i);
				if (sectorId!=-1 && jsonElement.getSafeInt("SECTOR_ID")==sectorId) {
					return jsonElement.getSafeInt("SECTOR_FAMILY");
				}
			}
		}
		return sectFamily;

	}
	
	/**
	 * Search SECTO R NAME.
	 *
	 * @param sct the sct
	 * @return the string
	 */
	public static String searchSECTOR_NAME(String sct) {
		
		String sectName = "";
		var sectorTableJSon = BlackBoardUtility.getDataOpt(DataType.ENV_SECTOR_TABLE.name());
		if (sectorTableJSon.isPresent()) {
			var jsonTable = sectorTableJSon.get().getSafeRawDataArray("SECTOR_TABLE");

			
			for (int i = 0; i < jsonTable.size(); i++) {
				var jsonElement = jsonTable.get(i);
				if (sct!=null && !sct.isEmpty() && jsonElement.getSafeInt("SECTOR_ID")==Integer.parseInt(sct)) {
					return jsonElement.getSafeString("SECTOR_NAME");
				}
			}
		}
		return sectName;
	}
	
	/**
	 * Search SECTO R ID.
	 *
	 * @param sctName the sct name
	 * @return the int
	 */
	public static int searchSECTOR_ID(String sctName) {
		
		var json = BlackBoardUtility.getDataOpt(DataType.ENV_SECTOR_TABLE.name());
		if (json.isPresent()) {
			var jsonArray = json.get().getSafeRawDataArray("SECTOR_TABLE");
			for (IRawDataElement jsonElement : jsonArray) {
				if (sctName!=null && !sctName.isEmpty() && jsonElement.getSafeString("SECTOR_NAME").equals(sctName)) {
					return jsonElement.getSafeInt("SECTOR_ID", -1);
				}
			}
		}

		return 0;
	}
	
	
	/**
	 * @return true if CDBINFO_CRAV_TORRE = 1
	 */
	public static Boolean isRemoteTower() {

		Optional<IRawData> bbKsource = BlackBoardUtility.getDataOpt(DataType.ENV_CDBINFO.name());

		if (bbKsource.isPresent()) {
			IRawData json = bbKsource.get();
			var cravTorre = json.getSafeString("CDBINFO_CRAV_TORRE");
			if (cravTorre.equals("1"))
				return true;
		}
		return false;	
	}
	
	
	/**
	 * @return isStandBy
	 */
	public static boolean isWPStandBy() {
		boolean isStandBy = true;
		final var env_OWN = BlackBoardUtility.getDataOpt(DataType.ENV_OWN.name());
		if (env_OWN.isPresent()) {
			final String mySector_Own = env_OWN.get().getSafeString("SUITE");
			if (searchSECTOR_ID(mySector_Own) < 3) {
				isStandBy = true;
			}else {
				isStandBy = false;
			}
		}
		return isStandBy;
	}
	
	
}
