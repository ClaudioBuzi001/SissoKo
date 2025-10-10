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
package com.view.ui.view;

import com.gifork.commons.data.IRawData;
import com.gifork.data_exchange.model.RawStorage;
import com.leonardo.infrastructure.Generics;
import com.leonardo.infrastructure.mvvm.ViewModelBase;
import com.leonardo.infrastructure.mvvm.binder.MvvmBinder;
import com.leonardo.infrastructure.mvvm.interfaces.IView;
import com.view.ui.controls.JsonCodeArea;
import com.view.ui.viewmodel.FrmMainVM;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * The Class FrmMainFxml.
 */
public class FrmMainFxml implements IView {

	/** The mnu start. */
	@FXML
	private MenuItem mnuStart;

	/** The mnu stop. */
	@FXML
	private MenuItem mnuStop;

	/** The mnu pause. */
	@FXML
	private MenuItem mnuPause;

	/** The mnu clear. */
	@FXML
	private MenuItem mnuClear;

	/** The mnu resume. */
	@FXML
	private MenuItem mnuResume;

	/** The refresh button. */
	@FXML
	private Button refreshButton;

	/** The bb list. */
	@FXML
	private ListView<RawStorage> bbList;

	/** The bb item list. */
	@FXML
	private ListView<IRawData> bbItemList;

	/** The dump button. */
	@FXML
	private Button dumpButton;

	/** The dump name. */
	@FXML
	private TextField dumpName;

	/** The tgz check. */
	@FXML
	private CheckBox tgzCheck;

	/** The find text field. */
	@FXML
	private TextField findTextField;

	/** The find button. */
	@FXML
	private Button findButton;

	/** The text area anchor. */
	@FXML
	private AnchorPane textAreaAnchor;

	/** The simple text area. */
	@FXML
	private TextArea simpleTextArea;

	/** The Constant m_codeArea. */
	private static final JsonCodeArea m_codeArea = new JsonCodeArea();

	/**
	 * Gets the code area.
	 *
	 * @return the code area
	 */
	public static JsonCodeArea getCodeArea() {
		return m_codeArea;
	}

	/**
	 * Initialize.
	 */
	@FXML
	void initialize() {
		assert refreshButton != null : "fx:id=\"refreshButton\" was not injected: check your FXML file 'FrmMain.fxml'.";
		assert dumpButton != null : "fx:id=\"dumpButton\" was not injected: check your FXML file 'FrmMain.fxml'.";
		assert bbItemList != null : "fx:id=\"bbItemList\" was not injected: check your FXML file 'FrmMain.fxml'.";
		assert bbList != null : "fx:id=\"bbList\" was not injected: check your FXML file 'FrmMain.fxml'.";
		assert textAreaAnchor != null : "fx:id=\"textAreaAnchor\" was not injected: check your FXML file 'FrmMain.fxml'.";
		assert simpleTextArea != null : "fx:id=\"simpleTextArea\" was not injected: check your FXML file 'FrmMain.fxml'.";

		textAreaAnchor.getChildren().add(m_codeArea.getNode());
		AnchorPane.setLeftAnchor(m_codeArea.getNode(), 0.);
		AnchorPane.setRightAnchor(m_codeArea.getNode(), 0.);
		AnchorPane.setTopAnchor(m_codeArea.getNode(), 0.);
		AnchorPane.setBottomAnchor(m_codeArea.getNode(), 0.);
		simpleTextArea.setVisible(false);

		bbList.setStyle("-fx-font-size:10;");
		bbItemList.setStyle("-fx-font-size:10;");
		simpleTextArea.setStyle("-fx-font-size:10;");

	}

	/**
	 * On data context.
	 *
	 * @param dataContext the data context
	 */
	@Override
	public void onDataContext(ViewModelBase dataContext) {
		Generics.ifTryCast(dataContext, FrmMainVM.class).ifPresent(vm -> {
			bbList.setCellFactory(new Callback<ListView<RawStorage>, ListCell<RawStorage>>() {
				@Override
				public ListCell<RawStorage> call(ListView<RawStorage> arg0) {

					return new StorageCell();
				}
			});

			bbItemList.setCellFactory(new Callback<ListView<IRawData>, ListCell<IRawData>>() {
				@Override
				public ListCell<IRawData> call(ListView<IRawData> arg0) {
					return new DataCell();
				}
			});

			bbList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

			bbItemList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

			bbItemList.getSelectionModel().selectedItemProperty().addListener((ob, ol, nw) -> {
				if (nw != null) {
					vm.selectedItem.set(nw);
				}
			});
			bbList.getSelectionModel().selectedItemProperty().addListener((ob, ol, nw) -> {
				if (nw != null) {
					vm.selectedStorage.set(nw);
				}
			});

			m_codeArea.bindBidirectional(vm.Text);
			MvvmBinder.bindBidirectional(bbList, vm.bbList, vm.selectedStorage);
			MvvmBinder.bindBidirectional(bbItemList, vm.bbItemList, vm.selectedItem);
			MvvmBinder.bind(dumpButton, vm.dumpCommand);
			MvvmBinder.bindBidirectional(tgzCheck, vm.checkTgz);
			MvvmBinder.bind(refreshButton, vm.refreshCommand);
			MvvmBinder.bindBidirectional(findTextField, vm.FindText);
			MvvmBinder.bindBidirectional(dumpName, vm.dumpNameText);
			MvvmBinder.bind(findButton, vm.findCommand);

			MvvmBinder.bind(mnuClear, vm.clearCommand);
			MvvmBinder.bind(mnuPause, vm.pauseCommand);
			MvvmBinder.bind(mnuResume, vm.resumeCommand);
			MvvmBinder.bind(mnuStart, vm.startCommand);
			MvvmBinder.bind(mnuStop, vm.stopCommand);

		});
	}

	/** The map. */
	static final ObservableMap<String, Label> map = FXCollections.observableHashMap();

	/**
	 * On disable.
	 *
	 * @param event the event
	 */
	@FXML
	void onDisable(ActionEvent event) {
		bbList.getSelectionModel().getSelectedItem()
				.setEnable(!bbList.getSelectionModel().getSelectedItem().isEnable());
		Label lab = map.get(bbList.getSelectionModel().getSelectedItem().getType());
		lab.setTextFill(bbList.getSelectionModel().getSelectedItem().isEnable() ? Color.BLACK : Color.RED);
	}

	/**
	 * On remove all.
	 *
	 * @param event the event
	 */
	@FXML
	void onRemoveAll(ActionEvent event) {
		bbList.getSelectionModel().getSelectedItem().removeAll();
	}

	/**
	 * The Class StorageCell.
	 */
	private static class StorageCell extends ListCell<RawStorage> {

		/** The label. */
		Label label = new Label();

		/**
		 * Update item.
		 *
		 * @param item  the item
		 * @param empty the empty
		 */
		@Override
		protected void updateItem(RawStorage item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setGraphic(null);
			} else {
				label.setText(item.getType() + "(" + item.getSize() + ")");
				setGraphic(label);
				map.put(item.getType(), label);
			}
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		}
	}

	/**
	 * The Class DataCell.
	 */
	private static class DataCell extends ListCell<IRawData> {

		/**
		 * Update item.
		 *
		 * @param item  the item
		 * @param empty the empty
		 */
		@Override
		protected void updateItem(IRawData item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setGraphic(null);
			} else {
				Label label = new Label();
				label.setText(item.getId());
				setGraphic(label);
			}
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		}
	}

}
