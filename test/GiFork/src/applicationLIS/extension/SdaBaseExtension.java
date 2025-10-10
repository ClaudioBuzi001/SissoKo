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

import com.fourflight.WP.ECI.edm.DataNode;
import com.leonardo.infrastructure.plugins.interfaces.IExtension;

/**
 * The Class SdaBaseExtension.
 */
public abstract class SdaBaseExtension implements IExtension {

	/**
	 * Update.
	 *
	 * @param node the node
	 */
	public abstract void update(DataNode node);
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return "Supply a customized Sda";
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return "Customized Sda";
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
