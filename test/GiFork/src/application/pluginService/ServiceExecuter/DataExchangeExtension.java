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

import java.net.DatagramPacket;

import com.gifork.commons.playback.Record;
import com.leonardo.infrastructure.plugins.interfaces.IExtension;

/**
 * Esegue una pre elaborazione dei dati ricevuti da BCV/Gfork.
 */
public abstract class DataExchangeExtension implements IExtension {

	/**
	 * Start Recording.
	 */
	public abstract void startRecording();

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return "Supply a notify for a received data";
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return "DataExchangeExtension";
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

	/**
	 * add a record
	 *
	 * @param record
	 */
	public abstract void addRecord(final Record record);

	/**
	 * Notifica di un messaggio JSON contenente un topic.
	 *
	 * @param message the message
	 */
	public abstract void messageInTopic(String message);

	/**
	 * Notifica di un messaggio JSON contenente un oggetto PROXY.
	 *
	 * @param message the message
	 */
	public abstract void messageInProxy(String message);

	/**
	 * Notifica del messaggio XML in arrivo dal BCV/GFork.
	 *
	 * @param message the message
	 */
	public abstract void messageIn(String message);

	/**
	 * Notifica del messaggio CDB in arrivo da CDB.
	 *
	 * @param message the message
	 */
	public abstract void messageIn(byte[] message);

	/**
	 * Notifica del messaggio DatagramPacket in arrivo da Rete.
	 *
	 * @param message the message
	 * @param i       the i
	 */
	public abstract void messageInTCA(byte[] message, int i);

	/**
	 * Notifica del messaggio DatagramPacket in arrivo da Rete.
	 *
	 * @param message the message
	 * @param i       the i
	 */
	public abstract void messageInTRK(byte[] message, int i);

	/**
	 * Notifica del messaggio DatagramPacket in arrivo da Rete.
	 *
	 * @param message the message
	 * @param i       the i
	 */
	public abstract void messageInTCT(byte[] message, int i);

	/**
	 * Notifica del messaggio DatagramPacket in arrivo da Rete.
	 *
	 * @param message the message
	 */
	public abstract void messageInSCA(DatagramPacket message);

	/**
	 * Message propery.
	 *
	 * @param serializedData
	 */
	public abstract void messageInProperty(String serializedData);

	/**
	 * Roll file.
	 *
	 * @param header the header
	 */
	public abstract void rollFile(final Record header);

	/**
	 * Message in map snet.
	 *
	 * @param msgIn the msg in
	 */
	public abstract void messageInMapSnet(String msgIn);

}
