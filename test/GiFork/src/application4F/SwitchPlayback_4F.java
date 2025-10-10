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

import com.leonardo.infrastructure.messaging.MessageBase;

/**
 * Messaggio per informare sullo switch del Playback.
 */
public class SwitchPlayback_4F extends MessageBase<Object> {

	/**
	 * Instantiates a new switch playback 4 F.
	 *
	 * @param sender the sender
	 * @param value the value
	 */
	public SwitchPlayback_4F(Object sender, String value) {
		super(sender, value);
	}

}
