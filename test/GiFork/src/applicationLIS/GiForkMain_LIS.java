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
package applicationLIS;

import java.io.File;
import java.util.Optional;

import com.fourflight.WP.ECI.edm.Operation;
import com.gifork.auxiliary.Capabilities;
import com.gifork.auxiliary.ConfigurationFile;
import com.gifork.auxiliary.FileLoaders;
import com.gifork.auxiliary.pluginEngine.ManagerPlugIn;
import com.gifork.blackboard.BlackBoardUtility;
import com.gifork.blackboard.ManagerBlackboard;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.data.RawDataFactory;
import com.gifork.commons.log.LoggerFactory;
import com.gifork.data_exchange.gateways.CommunicationManager;
import com.gifork.data_exchange.gateways.ribb.RIBBServerThread;
import com.leonardo.infrastructure.ArgumentManager;
import com.leonardo.infrastructure.PathUtils;
import com.leonardo.infrastructure.Strings;
import com.leonardo.infrastructure.log.ILogger;
import com.view.manager.ResourceManager;
import com.view.manager.ResourceManager.IconsFile;
import com.view.manager.SceneLoader;
import com.view.manager.SceneLoader.SceneLoaderData;
import com.view.ui.view.FrmMainFxml;
import com.view.ui.viewmodel.FrmMainVM;

import application.pluginService.ServiceExecuter.ServiceExecuter;
import application.pluginService.services.ServiceConfiguration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The Class GiForkMain_LIS.
 */
public class GiForkMain_LIS extends Application {

	/** The primary stage. */
	private static Stage m_primaryStage;

	/** The root layout. */
	private static Region m_rootLayout;

	/** The main args. */
	public static String[] mainArgs;

	/** The config dir. */
	private static String configDir;

	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateMainLogger(GiForkMain_LIS.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println(PathUtils.getCurrentPath());
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			LOGGER.logError("Eccezione: " + thread.getName(), throwable);
		});

		ArgumentManager.init(args);
		GiForkMain_LIS.mainArgs = args;
		for (String arg : args) {
			if (arg.startsWith("--prj")) {
				if (arg.contains("=")) {
					String argvalues = arg.split("=")[1];
					GiForkMain_LIS.configDir = argvalues;
				}
			}
		}

		startService();

	}

	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		m_primaryStage = primaryStage;
		m_primaryStage.setTitle("GI-FORK");
		m_primaryStage.getIcons().add(ResourceManager.getImageResource(IconsFile.IcoApp));
		initRootLayout();

		m_primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				Platform.setImplicitExit(false);
				m_primaryStage.close();
			}
		});
	}

	/**
	 * Initializes the root layout.
	 */
	public static void initRootLayout() {
		try {

			preLoadedBlackboard();
			ManagerBlackboard.initRMIEndpoint();
			ManagerPlugIn.getInstance();
			ManagerPlugIn.init();

			CommunicationManager.addClient();

			int arp = ConfigurationFile.getIntegerProperties("MAXARP", 0);
			if (arp > 0) {
				for (int i = 1; i <= arp; i++) {
					CommunicationManager.addArpClient(i);
				}
			}
			FrmMainVM vm = new FrmMainVM(m_primaryStage);

			Optional<SceneLoaderData> data = SceneLoader.load(Strings.removeEnd(FrmMainFxml.class.getSimpleName(), 4),
					vm, new FrmMainFxml());
			data.ifPresent(sceneLoaderData -> m_rootLayout = (Region) sceneLoaderData.getFxml());
			Scene scene = new Scene(m_rootLayout);
			scene.getStylesheets().add(ResourceManager.getResourcesPath() + "scenes/xml-highlighting.css");
			m_primaryStage.setScene(scene);
			if (ArgumentManager.getBooleanOf("usetestid")) {
				m_primaryStage.show();
			}
			if (!ArgumentManager.getArgument("useRIBB").isEmpty()) {
				RIBBServerThread ribbServer = new RIBBServerThread(
						Integer.parseInt(ArgumentManager.getArgument("useRIBB")));
				ribbServer.start();
			}
			int time = ConfigurationFile.getIntegerProperties("STATISTICS_BB_TIME", 0);
			if (time > 0) {
				BlackBoardUtility.start(time);
			}
			Capabilities.getRecorders().forEach(r -> r.startRecording());
			Capabilities.getSnapshotRecorders().forEach(r -> r.startRecording());

		} catch (Exception e) {
			LOGGER.logError("Error", e);
			e.printStackTrace();
		}
	}

	/**
	 * Sets the visibility.
	 */
	public static void setVisibility() {
		Platform.runLater(() -> {
			m_primaryStage.show();
			m_primaryStage.setAlwaysOnTop(true);
		});

	}

	/**
	 * Pre loaded blackboard.
	 */
	private static void preLoadedBlackboard() {
		String defaultDir = configDir + "/GI-FORK/PreLoadedBlackboard/";

		File PreLoadedBlackboardDir = new File(defaultDir);
		if (PreLoadedBlackboardDir.exists()) {
			String[] listaBlackboard = PreLoadedBlackboardDir.list();
			for (String dataType : listaBlackboard) {
				String BBDirPath = Strings.concat(defaultDir, dataType);
				File bbDir = new File(BBDirPath);
				if (bbDir.isDirectory()) {
					String[] ItemList = bbDir.list();
					for (String itemID : ItemList) {
						String ItemPath = Strings.concat(BBDirPath, "/", itemID);

						String jsonStr = FileLoaders.loadTxt(ItemPath);
						IRawData data = RawDataFactory.createFromJson(jsonStr);
						data.setOperation(Operation.INSERT);
						ManagerBlackboard.getInstance().addRawData(data, dataType, itemID.replace(".json", ""));
					}
				}
			}
		}

	}

	/**
	 * Gets the primary stage.
	 *
	 * @return the primary stage
	 */
	public static Stage getPrimaryStage() {
		return m_primaryStage;
	}

	/**
	 * Start service.
	 */
	public static void startService() {

		ResourceManager.setResourcesPath(GiForkMain_LIS.configDir + "/GI-FORK/");
		ServiceExecuter.getInstance().RegisterService(new ServiceConfiguration());
		String XML = FileLoaders.loadTxt(ResourceManager.getResourcesPath() + "/GiForkConfig/configuration.xml");
		ConfigurationFile.loadFromXml(XML);
		ManagerBlackboard.addXML(XML);
		launch(mainArgs);

	}
}
