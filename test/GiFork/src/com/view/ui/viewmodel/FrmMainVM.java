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
package com.view.ui.viewmodel;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import com.gifork.auxiliary.FileLoaders;
import com.gifork.blackboard.StorageManager;
import com.gifork.commons.data.IRawData;
import com.gifork.data_exchange.model.RawStorage;
import com.leonardo.infrastructure.Strings;
import com.leonardo.infrastructure.mvvm.RelayCommand;
import com.leonardo.infrastructure.mvvm.ViewModelBase;
import com.view.ui.view.FrmMainFxml;

import application.pluginService.ServiceExecuter.ServiceExecuter;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

/**
 * The Class FrmMainVM.
 */
public class FrmMainVM extends ViewModelBase {

	/** The Text. */

	public final SimpleStringProperty Text = register(new SimpleStringProperty(""));

	/** The Find text. */
	public final SimpleStringProperty FindText = register(new SimpleStringProperty(""));

	/** The dump name text. */
	public final SimpleStringProperty dumpNameText = register(new SimpleStringProperty(""));

	/** The check tgz. */
	public final SimpleBooleanProperty checkTgz = register(new SimpleBooleanProperty());

	/** The exit command. */
	public final RelayCommand exitCommand = new RelayCommand(() -> Platform.exit(), () -> true, this);

	/** The refresh command. */
	public final RelayCommand refreshCommand = new RelayCommand(() -> onRefreshCommand());

	/** The dump command. */
	public final RelayCommand dumpCommand = new RelayCommand(() -> {
		try {
			onRefreshCommand();
			onDumpCommand();
		} catch (IOException e) {
			e.printStackTrace();
		}
	});

	/** The find command. */
	public final RelayCommand findCommand = new RelayCommand(() -> onFindCommand());

	/** The clear command. */
	public final RelayCommand clearCommand = new RelayCommand(() -> onRecording("RECORDING_CLEAR"));

	/** The start command. */
	public final RelayCommand startCommand = new RelayCommand(() -> onRecording("RECORDING_START"));

	/** The stop command. */
	public final RelayCommand stopCommand = new RelayCommand(() -> onRecording("RECORDING_STOP"));

	/** The pause command. */
	public final RelayCommand pauseCommand = new RelayCommand(() -> onRecording("RECORDING_PAUSE"));

	/** The resume command. */
	public final RelayCommand resumeCommand = new RelayCommand(() -> onRecording("RECORDING_RESUME"));

	/** The bb list. */
	public final ObservableList<RawStorage> bbList = FXCollections.observableArrayList();

	/** The bb item list. */
	public final ObservableList<IRawData> bbItemList = FXCollections.observableArrayList();

	/** The selected item. */
	public final SimpleObjectProperty<IRawData> selectedItem = register(new SimpleObjectProperty<>());

	/** The selected storage. */
	public final SimpleObjectProperty<RawStorage> selectedStorage = register(new SimpleObjectProperty<>());

	/** The bb dumped. */
	private String BB_DUMPED = "";

	/** The primary stage. */
	private static Stage primaryStage;

	/**
	 * Gets the primary stage.
	 *
	 * @return the primary stage
	 */
	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	/** The Constant MEGABYTE. */
	private static final long MEGABYTE = 1024L * 1024L;

	/**
	 * Instantiates a new frm main VM.
	 *
	 * @param _primaryStage the primary stage
	 */
	public FrmMainVM(Stage _primaryStage) {
		primaryStage = _primaryStage;

	}

	/**
	 * On refresh command.
	 */

	private void onRefreshCommand() {
		List<RawStorage> blackBoardList = StorageManager.getInstance().getItemsStorageList();
		bbList.clear();

		Platform.runLater(() -> {
			bbList.clear();
			bbList.addAll(blackBoardList);
			Collections.sort(bbList, (to1, to2) -> to1.getType().compareTo(to2.getType()));
		});
		primaryStage.setTitle(this.memoryUsage());
	}

	/**
	 * On dump command.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void onDumpCommand() throws IOException {
		String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
		BB_DUMPED = (Strings.isNullOrBlank(dumpNameText.getValue())) ? "BB_DUMPED"
				: "BB_DUMPED_" + dumpNameText.getValue();
		BB_DUMPED += "_" + dateTime;

		List<RawStorage> blackBoardList = StorageManager.getInstance().getItemsStorageList();
		blackBoardList.stream().forEach(t -> {
			String bbName = t.getType();
			String BBPAth = BB_DUMPED + "/" + bbName;
			File BB_DUMPED_DIR = new File(BB_DUMPED);
			File BBPAth_DIR = new File(BBPAth);

			BB_DUMPED_DIR.mkdir();
			BBPAth_DIR.mkdir();
			var storage = StorageManager.getItemsStorage(bbName);
			for (var entry : storage.entrySet()) {
				String kkey = entry.getKey();
				IRawData json = entry.getValue();
				String fileName = Strings.concat(BBPAth, "/", kkey, ".json");
				String txt = json.toJsonString();
				if (!txt.contains("DATA_TYPE")) {
					txt = txt.replaceFirst("[{]", Strings.concat("{\"DATA_TYPE\":\"", bbName, "\","));
				}
				FileLoaders.saveTxt(fileName, txt);
			}
		});
		if (checkTgz.get()) {
			try {
				ProcessBuilder proc = new ProcessBuilder("tar czf " + BB_DUMPED + ".tar.gz " + BB_DUMPED);
				proc.start();
			} catch (IOException e) {
				System.out.println(BB_DUMPED + ".tar.gz is impossible to create");
			}
		}
	}

	/**
	 * On find command.
	 */

	private void onFindCommand() {
		FrmMainFxml.getCodeArea().find(FindText.get());
	}

	/**
	 * On recording.
	 *
	 * @param function the function
	 */
	protected void onRecording(String function) {
		ServiceExecuter.getInstance().ExecuteService(function);
	}

	/**
	 * Memory usage.
	 *
	 * @return the string
	 */
	public String memoryUsage() {

		Runtime runtime = Runtime.getRuntime();

		long memory = runtime.totalMemory() - runtime.freeMemory();

		String memoryInfo = "Used memory is megabytes: " + bytesToMegabytes(memory) + " MB";
		return memoryInfo;
	}

	/**
	 * Bytes to megabytes.
	 *
	 * @param bytes the bytes
	 * @return the long
	 */
	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	/**
	 * On property changed.
	 *
	 * @param property the property
	 * @param oldvalue the oldvalue
	 * @param newvalue the newvalue
	 */
	@Override
	protected void onPropertyChanged(Property<?> property, Object oldvalue, Object newvalue) {
		super.onPropertyChanged(property, oldvalue, newvalue);
		if (property == selectedItem) {
			String Formatted = selectedItem.getValue().toJsonString(4);
			Text.set(Formatted);
		} else if (property == selectedStorage) {

			Platform.runLater(() -> {
				bbItemList.clear();
				bbItemList.addAll(selectedStorage.get().getItemsList());
				Collections.sort(bbItemList, (to1, to2) -> to1.getId().compareTo(to2.getId()));
				if (selectedStorage.get().getItemsList().size() > 0) {
					selectedItem.set(selectedStorage.get().getItemsList().get(0));
				}
			});

		}
	}

}
