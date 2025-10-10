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
package com.gifork.auxiliary;

import com.leonardo.infrastructure.collections.IReadOnlyList;
import com.leonardo.infrastructure.collections.ReadOnlyListWrapper;
import com.leonardo.infrastructure.plugins.framework.SimplePluginManager;

import application.pluginService.ServiceExecuter.DataExchangeExtension;

// TODO: Auto-generated Javadoc
/**
 * Mappa quei dati esposti dai servizi in plugin e che sono opzionali per il CORE.<br>
 * Inoltre permette di ragionare a Capabilities e non legare in modo rigido una informazione ad uno
 * specifico servizio.
 *
 */
public class Capabilities {

	/** The recorders. */
	private static IReadOnlyList<DataExchangeExtension> m_recorders;

	/** The m snapshot recorders. */
	private static IReadOnlyList<DataExchangeExtension> m_snapshotRecorders;

	/** The Constant SNAPSHOT_RECORDER. */
	public final static String SNAPSHOT_RECORDER = "SNAPSHOT_RECORDER";

	/**
	 * Instantiates a new capabilities.
	 */
	private Capabilities() {
	}

	/**
	 * Gets the recorders.
	 *
	 * @return the recorders
	 */
	public static IReadOnlyList<DataExchangeExtension> getRecorders() {
		if (m_recorders == null) {
			m_recorders = ReadOnlyListWrapper
					.of(SimplePluginManager.getInstance().getExtensions(DataExchangeExtension.class).stream()
							.filter(r -> !r.getName().equals(SNAPSHOT_RECORDER)));
		}
		return m_recorders;
	}

	/**
	 * Gets the recorders.
	 *
	 * @return the recorders
	 */
	public static IReadOnlyList<DataExchangeExtension> getSnapshotRecorders() {
		if (m_snapshotRecorders == null) {
			m_snapshotRecorders = ReadOnlyListWrapper
					.of(SimplePluginManager.getInstance().getExtensions(DataExchangeExtension.class).stream()
							.filter(r -> r.getName().equalsIgnoreCase(SNAPSHOT_RECORDER)));
		}
		return m_snapshotRecorders;
	}

}
