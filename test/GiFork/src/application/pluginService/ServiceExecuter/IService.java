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

package application.pluginService.ServiceExecuter;

/**
 * The Interface IService.
 */
public interface IService {

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	String getServiceName();

	/**
	 * Checks if is to be carried single level.
	 *
	 * @return true, if is to be carried single level
	 */
	default boolean isToBeCarriedSingleLevel() {
		return false;
	}

}
