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
package applicationLIS.extension;

import java.util.ArrayList;

import com.gifork.commons.data.IRawData;
import com.leonardo.infrastructure.plugins.interfaces.IExtension;

/**
 * The Class ListBaseExtension.
 */
public abstract class ListBaseExtension implements IExtension {

	/**
	 * Register.
	 *
	 * @param dataType the data type
	 */


	/**
	 * Gets the data type.
	 *
	 * @return the data type
	 */
	public abstract ArrayList<String> getDataType();

	/**
	 * Update.
	 *
	 * @param map the map
	 */
	public abstract void update(IRawData map);

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return "Supply a customized List";
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return "Customized List";
	}

	/**
	 * Gets the ordinal.
	 *
	 * @return the ordinal
	 */
	@Override
	public int getOrdinal() {
		return 0;
	}

	/**
	 * Checks if is ignored.
	 *
	 * @return true, if is ignored
	 */
	@Override
	public boolean isIgnored() {
		return false;
	}

}
