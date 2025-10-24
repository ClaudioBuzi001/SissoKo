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
package analyzer;

import applicationLIS.BlackBoardConstants_LIS.DataType;

import java.util.Optional;

import com.fourflight.WP.ECI.edm.DataRoot;
import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.subjectObserverEventEngine.IObserver;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;

/**
 * The Class AnalyzerDiagnosticMessage.
 */
public class AnalyzerDiagnosticMessage implements IObserver {

	/**
	 * Instantiates a new analyzer diagnostic message.
	 */
	public AnalyzerDiagnosticMessage() {
		
		
		StorageManager.register(this, DataType.ENV_SUPERVISOR_MESSAGE.name());
		StorageManager.register(this, DataType.METEO_AIS_DATA.name());
		StorageManager.register(this, DataType.FPM_LINE_DATA.name());
		StorageManager.register(this, DataType.MTCD_DIAG_NOTIFY.name());
		StorageManager.register(this, DataType.TCT_DIAG_NOTIFY.name());
		StorageManager.register(this, DataType.TCAT_MESSAGE.name());
	}

	/**
	 * Update spv msg.
	 *
	 * @param jsonMessage the json message
	 */
	private void updateSpvMsg(final IRawData jsonMessage) {
		final DataRoot rootData = DataRoot.createMsg();
		final var serviceNode = rootData.addHeaderOfService("SESAR_ORDER_RESULT");
		serviceNode.addLine("ORDER_ID", "SVR");
		serviceNode.addLine("RESULT", "OK");
		serviceNode.addLine("ERROR_DESCRIPTION", jsonMessage.getSafeString("SVR_MESSAGE"));
		serviceNode.addLine("OBJECT_TYPE", "");
		ManagerBlackboard.addJVOutputList(rootData);
	}

	/**
	 * Update meteo.
	 *
	 * @param jsonMessage the json message
	 */
	private void updateMeteo(final IRawData jsonMessage) {

		if (BlackBoardUtility.getDataOpt(DataType.BB_ORDER_METAIS.name(), IRawData.NO_ID).isPresent()) {
			if (!jsonMessage.getSafeString("MSG_STRING").isEmpty()) {
				final DataRoot rootData = DataRoot.createMsg();
				final var serviceNode = rootData.addHeaderOfService("GENERIC_TEXT");
				final String message = jsonMessage.getSafeString("MSG_STRING");
				serviceNode.addLine("DESCRIPTION", message);
				serviceNode.addLine("TITLE", "MET/AIS MESSAGE");
				ManagerBlackboard.addJVOutputList(rootData);
			}	
		}	
	}

	/**
	 * Update fpm line data.
	 *
	 *@param jsonMessage the json message
	 */
	private void updateFpmLineData(final IRawData jsonMessage) {
		if (!jsonMessage.getSafeString("TEXT").isEmpty()) {
			final DataRoot rootData = DataRoot.createMsg();
			final var serviceNode = rootData.addHeaderOfService("GENERIC_TEXT");
			serviceNode.addLine("DESCRIPTION", jsonMessage.getSafeString("TEXT"));
			serviceNode.addLine("TITLE", "FPL MESSAGE " + jsonMessage.getSafeString("CALLSIGN"));
			ManagerBlackboard.addJVOutputList(rootData);
		}

	}

	/**
	 * Update diag notify.
	 *
	 * @param jsonMessage the json message
	 */
	private void updateDiagNotify(final IRawData jsonMessage) {
		final DataRoot rootData = DataRoot.createMsg();
		final String orderID = (jsonMessage.getType().equals("MTCD_DIAG_NOTIFY") ? "MTCD" : "TCT");
		final String message = jsonMessage.getSafeString("TEXT");
		final String callsign = jsonMessage.getSafeString("CALLSIGN");

		final var serviceNode = rootData.addHeaderOfService("SESAR_ORDER_RESULT");
		serviceNode.addLine("ORDER_ID", orderID);
		serviceNode.addLine("RESULT", "OK");
		serviceNode.addLine("ERROR_DESCRIPTION", message);
		serviceNode.addLine("OBJECT_TYPE", "");
		serviceNode.addLine("CALLSIGN", callsign);
		ManagerBlackboard.addJVOutputList(rootData);
	}

	/**
	 * Update.
	 *
	 * @param subject the subject
	 */
	@Override
	public void update(final IRawData subject) {

		if (subject.getType().equals("ENV_SUPERVISOR_MESSAGE")) {
			updateSpvMsg(subject);
		}
		if (subject.getType().equals("METEO_AIS_DATA")) {
			updateMeteo(subject);
		}
		if (subject.getType().equals("FPM_LINE_DATA")) {
			updateFpmLineData(subject);
		}
		if (subject.getType().equals("MTCD_DIAG_NOTIFY") || subject.getType().equals("TCT_DIAG_NOTIFY")) {
			updateDiagNotify(subject);
		}

		if (subject.getType().equals("TCAT_MESSAGE")) {
			updateTcatMessage(subject);
		}
		
	}

	/**
	 * @param subject
	 */
	private static void updateTcatMessage(IRawData subject) {
		if (!subject.getOperation().equals(Operation.DELETE)) {
			final Optional<IRawData> tcatAck = BlackBoardUtility.getDataOpt(DataType.BB_TCAT_ACK.name(),subject.getId());
			if (!tcatAck.isPresent()) {
				final DataRoot rootData = DataRoot.createMsg();
				final var serviceNode = rootData.addHeaderOfService("STAM_RECEIVED");
				serviceNode.addLine("STAM","STAM");
				ManagerBlackboard.addJVOutputList(rootData);	
			}
		}
	}
}
