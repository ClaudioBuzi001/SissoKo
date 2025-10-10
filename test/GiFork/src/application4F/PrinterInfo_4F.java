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
package application4F;

import com.fourflight.Common.TSC.ISMData.TechnicalSupervisionData.SharedData.External.TypeClasses.SupObject;

/**
 * The Class PrinterInfo_4F.
 *
 * @author Leonardo Company S.p.A.
 * @version XAI_4F_B2_BETA-02_01-01RC3
 * 
 *  
 */
public class PrinterInfo_4F {

	/** The name. */
	private String name;

	/** The address. */
	private String address;

	/** The host name. */
	private String hostName;

	/** The port. */
	private String port;

	/** The is strip printer. */
	private boolean isStripPrinter;

	/** The sup object. */
	private SupObject supObject;

	/**
	 * Instantiates a new printer info 4 F.
	 *
	 * @param name the name
	 * @param address the address
	 * @param supObject the sup object
	 */
	public PrinterInfo_4F(String name, String address, SupObject supObject) {
		super();
		this.name = name;
		this.address = address;
		this.supObject = supObject;
		hostName = "";
		port = "";
		this.isStripPrinter = false;
	}

	/**
	 * Checks if is strip printer.
	 *
	 * @return true, if is strip printer
	 */
	public boolean isStripPrinter() {
		return isStripPrinter;
	}

	/**
	 * Sets the strip printer.
	 *
	 * @param isStripPrinter the new strip printer
	 */
	public void setStripPrinter(boolean isStripPrinter) {
		this.isStripPrinter = isStripPrinter;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Gets the host name.
	 *
	 * @return the host name
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Gets the sup object.
	 *
	 * @return the sup object
	 */
	public SupObject getSupObject() {
		return supObject;
	}

}
