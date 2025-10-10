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

import java.io.File;
import java.util.Optional;

import com.fourflight.WP.ECI.edm.Operation;
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
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * The Class GiForkMain_FDH.
 */
public class GiForkMain_FDH extends Application {

	/** The primary stage. */
	private static Stage m_primaryStage;

	/** The root layout. */
	private static Region m_rootLayout;

	/** The main args. */
	public static String[] mainArgs;

	/** The config dir. */
//	public static String configDir;

	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateMainLogger(GiForkMain_FDH.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println(PathUtils.getCurrentPath());
		ArgumentManager.init(args);
		GiForkMain_FDH.mainArgs = args;
		for (String arg : args) {
			if(arg.startsWith("--prj")) {
				if (arg.contains("=")) {
					String argvalues = arg.split("=")[1];
					ResourceManager.setResourcesPath(argvalues);
				}
			}
		}
		startService();

	}

	/**
	 * Gets the main args.
	 *
	 * @return the mainArgs
	 */
	public static String[] getMainArgs() {
		return mainArgs;
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
	}

	/**
	 * Initializes the root layout.
	 */
	public static void initRootLayout() {
		try {

			ManagerBlackboard.initRMIEndpoint();
			ManagerPlugIn.getInstance();
			ManagerPlugIn.init();
			preLoadedBlackboard();

			CommunicationManager.addClient();

			FrmMainVM vm = new FrmMainVM(m_primaryStage);

			Optional<SceneLoaderData> data = SceneLoader.load(Strings.removeEnd(FrmMainFxml.class.getSimpleName(), 4),
					vm, new FrmMainFxml());
			data.ifPresent(sceneLoaderData -> m_rootLayout = (Region) sceneLoaderData.getFxml());
			Scene scene = new Scene(m_rootLayout);
			scene.getStylesheets().add(ResourceManager.getResourcesPath() + "/scenes/xml-highlighting.css");
			m_primaryStage.setScene(scene);
			m_primaryStage.show();
			if (!ArgumentManager.getArgument("useRIBB").isEmpty()) {
				RIBBServerThread ribbServer = new RIBBServerThread(
						Integer.parseInt(ArgumentManager.getArgument("useRIBB")));
				ribbServer.start();
			}
			int time = ConfigurationFile.getIntegerProperties("STATISTICS_BB_TIME", 0);
			if (time > 0) {
				BlackBoardUtility.start(time);
			}

		} catch (Exception e) {
			LOGGER.logError("Error", e);
		}
	}

	/**
	 * Sets the visibility.
	 */
	public static void setVisibility() {
		m_primaryStage.show();
	}

	/**
	 * Pre loaded blackboard.
	 */
	private static void preLoadedBlackboard() {
		String defaultDir = ResourceManager.getResourcesPath() + "/PreLoadedBlackboard/";

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

		
		ServiceExecuter.getInstance().RegisterService(new ServiceConfiguration());
		String XML = FileLoaders.loadTxt(ResourceManager.getResourcesPath()  + "/GiForkConfig/configuration.xml");
		ConfigurationFile.loadFromXml(XML);
		ManagerBlackboard.addXML(XML);
		launch(mainArgs);

	}
}
