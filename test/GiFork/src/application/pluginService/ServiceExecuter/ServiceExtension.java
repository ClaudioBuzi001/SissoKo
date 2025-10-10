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

import com.leonardo.infrastructure.plugins.interfaces.IExtension;

/**
 * The Class ServiceExtension.
 */
public abstract class ServiceExtension implements IExtension {

	/**
	 * Gets the service interface.
	 *
	 * @return the service interface
	 */
	public abstract ServiceInterfaceExtended getServiceInterface();

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	public abstract String getServiceName();
	
	/**
	 * Checks if is ignored.
	 *
	 * @return true, if is ignored
	 */
	@Override
	public boolean isIgnored() {
		return false;
	}

	/**
	 * Corrisponde all'attributo Command del tag Item del menu'.
	 *
	 * @return il comando associato al servizio
	 */
	public String getAssociatedCommand() {
		return getServiceName();
	}

	/**
	 * Corrisponde all'attributo Param del tag Item del menu'.
	 *
	 * @return i parametri del comando
	 */
	public String getAssociatedParam() {
		return "";
	}

	/**
	 * Definisce la struttura dell'albero di menu corrispondente alla voce da aggiungere. es: 'File->Solution'.
	 * 
	 * Note: 
	 * Non contiene la voce dell'item foglia. 
	 * Se vuoto il comando nonviene preso in considerazione per lo StartContextMenu'
	 *
	 * @return the menu path
	 */
	public String getMenuPath() {
		return "";
	}
	

}
