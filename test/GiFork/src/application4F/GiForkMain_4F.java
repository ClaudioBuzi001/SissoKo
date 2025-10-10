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

import org.omg.CORBA.ORB;

import com.fourflight.Common.TSC.ISMData.TechnicalSupervisionData.ManagedObjectStatus.TypeClasses.PhysicalCommunicationTechnicalStatusType;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.AMADConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.AirServerConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.AirSurveillanceConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.CommonDataConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.RPDConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.TSNETConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.WorkingPositionConfiguration;
//import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.AMADConfiguration;
import com.fourflight.WP.ECI.WpCommon.WpCommonMainTsc;
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
 * The Class GiForkMain_4F. CRS FF-3759 AMAD
 */
public class GiForkMain_4F extends Application {

	/** The primary stage. */
	private static Stage m_primaryStage;

	/** The root layout. */
	private static Region m_rootLayout;

	/** The eda cd domain. */
	private static CommonDataConfiguration edaCdDomain;

	/** The eda wp domain. */
	private static WorkingPositionConfiguration edaWpDomain;

	/** The eda comm domain. */
	private static AirServerConfiguration edaCommDomain;

	/** The eda surv domain. */
	private static AirSurveillanceConfiguration edaSurvDomain;

	/** The amad wp domain. */
	private static AMADConfiguration amadWpDomain;

	/** The printer info. */
	private static PrinterInfo_4F printerInfo;

	/** The offline RPD config. */
	private static RPDConfiguration offlineRPDConfig;

	/** The receiver. */
	private static ExternalTechSupReceiver_4F receiver;

	/** The phys communication status. */
	private static PhysicalCommunicationTechnicalStatusType physCommunicationStatus;

	/** The Constant giForkReady. */
	private static final Object GIFORK_READY = new Object();

	/** The orb. */
	private static ORB orb;

	/** The main args. */
	public static String[] mainArgs;

	/** The snet domain. */
	private static TSNETConfiguration snetDomain;

	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateMainLogger(GiForkMain_4F.class);

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println(PathUtils.getCurrentPath());
		ArgumentManager.init(args);
		GiForkMain_4F.mainArgs = args;
		for (String arg : args) {
			if (arg.startsWith("--prj")) {
				if (arg.contains("=")) {
					String argvalues = arg.split("=")[1];
					ResourceManager.setResourcesPath(argvalues);
				}
			}
		}

		if (WpCommonMainTsc.mainTsc(args, ExternalTechSupReceiver_4F.class) == -1) {
			startService();
			new Thread(() -> Application.launch(GiForkMain_4F.class)).start();
			System.out.println("Failed.");
		} else {
			System.out.println("OK.");
		}

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
		synchronized (GIFORK_READY) {
			GIFORK_READY.notifyAll();
		}

		Capabilities.getRecorders().forEach(r -> r.startRecording());
		Capabilities.getSnapshotRecorders().forEach(r -> r.startRecording());
	}

	/**
	 * Initializes the root layout.
	 */
	private static void initRootLayout() {
		try {

			FrmMainVM vm = new FrmMainVM(m_primaryStage);

			Optional<SceneLoaderData> data = SceneLoader.load(Strings.removeEnd(FrmMainFxml.class.getSimpleName(), 4),
					vm, new FrmMainFxml());
			data.ifPresent(sceneLoaderData -> m_rootLayout = (Region) sceneLoaderData.getFxml());
			Scene scene = new Scene(m_rootLayout);
			scene.getStylesheets().add(ResourceManager.getResourcesPath() + "scenes/xml-highlighting.css");
			m_primaryStage.setScene(scene);
			if (!ArgumentManager.getArgument("useRIBB").isEmpty()) {
				RIBBServerThread ribbServer = new RIBBServerThread(
						Integer.parseInt(ArgumentManager.getArgument("useRIBB")));
				ribbServer.start();
			}
			if (ArgumentManager.getBooleanOf("usetestid")) {
				m_primaryStage.show();
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
	 * Inits the accessor.
	 */
	public static void initAccessor() {
		ExternalTechSupReceiver_4F.initAccessor();
	}

	/**
	 * Test print connection.
	 */
	public static void testPrintConnection() {
		ExternalTechSupReceiver_4F.testStripPrinterConnectivity();
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
		final String defaultDir = ResourceManager.getResourcesPath() + File.separator + "PreLoadedBlackboard"
				+ File.separator;

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
						ManagerBlackboard.addRawData(data, dataType, itemID.replace(".json", ""));
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
		String XML = FileLoaders.loadTxt(ResourceManager.getResourcesPath() + "/GiForkConfig/configuration.xml");
		ConfigurationFile.loadFromXml(XML);
		ManagerBlackboard.addXML(XML);

		ManagerPlugIn.getInstance();
		ManagerPlugIn.init();
		preLoadedBlackboard();
		CommunicationManager.addClient();
		int arp = ConfigurationFile.getIntegerProperties("MAXARP", 0);
		if (arp > 0) {
			for (int i = 1; i <= arp; i++) {
				CommunicationManager.addArpClient(i);
			}
		}
		ManagerBlackboard.initRMIEndpoint();
	}

	/**
	 * Gets the ready.
	 *
	 * @return the ready
	 */
	public static Object getReady() {
		return GIFORK_READY;
	}

	/**
	 * Gets the orb.
	 *
	 * @return the orb
	 */
	public static ORB getOrb() {
		return orb;
	}

	/**
	 * Sets the orb.
	 *
	 * @param orb the orb to set
	 */
	public static void setOrb(ORB orb) {
		GiForkMain_4F.orb = orb;
	}

	/**
	 * Gets the edaWpDomain.
	 *
	 * @return the edaWpDomain
	 */
	public static WorkingPositionConfiguration getEdaWpDomain() {
		return edaWpDomain;
	}

	/**
	 * Sets the edaWpDomain.
	 *
	 * @param edaWpDomain the edaWpDomain to set
	 */
	public static void setEdaWpDomain(WorkingPositionConfiguration edaWpDomain) {
		GiForkMain_4F.edaWpDomain = edaWpDomain;
	}

	/**
	 * Gets the edaCdDomain.
	 *
	 * @return the edaCdDomain
	 */
	public static CommonDataConfiguration getEdaCdDomain() {
		return edaCdDomain;
	}

	/**
	 * Sets the edaCdDomain.
	 *
	 * @param edaCdDomain the edaCdDomain to set
	 */
	public static void setEdaCdDomain(CommonDataConfiguration edaCdDomain) {
		GiForkMain_4F.edaCdDomain = edaCdDomain;
	}

	/**
	 * Gets the edaCommDomain.
	 *
	 * @return the edaCommDomain
	 */
	public static AirServerConfiguration getEdaCommDomain() {
		return edaCommDomain;
	}

	/**
	 * Sets the edaCommDomain.
	 *
	 * @param edaCommDomain the edaCommDomain to set
	 */
	public static void setEdaCommDomain(AirServerConfiguration edaCommDomain) {
		GiForkMain_4F.edaCommDomain = edaCommDomain;
	}

	/**
	 * Gets the amadWpDomain.
	 *
	 * @return the amadWpDomain
	 */
	public static AMADConfiguration getAmadWpDomain() {
		return amadWpDomain;
	}

	/**
	 * Sets the amadWpDomain.
	 *
	 * @param amadWpDomain the amadWpDomain to set
	 */
	public static void setAmadWpDomain(AMADConfiguration amadWpDomain) {
		GiForkMain_4F.amadWpDomain = amadWpDomain;
	}

	/**
	 * Gets the printerInfo.
	 *
	 * @return the printerInfo
	 */
	public static PrinterInfo_4F getPrinterInfo() {
		return printerInfo;
	}

	/**
	 * Sets the printerInfo.
	 *
	 * @param printerInfo the printerInfo to set
	 */
	public static void setPrinterInfo(PrinterInfo_4F printerInfo) {
		GiForkMain_4F.printerInfo = printerInfo;
	}

	/**
	 * Gets the offline RPD config.
	 *
	 * @return the offlineRPDConfig
	 */
	public static RPDConfiguration getOfflineRPDConfig() {
		return offlineRPDConfig;
	}

	/**
	 * Sets the offline RPD config.
	 *
	 * @param offlineRPDConfig the offlineRPDConfig to set
	 */
	public static void setOfflineRPDConfig(RPDConfiguration offlineRPDConfig) {
		GiForkMain_4F.offlineRPDConfig = offlineRPDConfig;
	}

	/**
	 * Gets the receiver.
	 *
	 * @return the receiver
	 */
	public static ExternalTechSupReceiver_4F getReceiver() {
		return receiver;
	}

	/**
	 * Sets the receiver.
	 *
	 * @param receiver the receiver to set
	 */
	public static void setReceiver(ExternalTechSupReceiver_4F receiver) {
		GiForkMain_4F.receiver = receiver;
	}

	/**
	 * Gets the phys communication status.
	 *
	 * @return the physCommunicationStatus
	 */
	public static PhysicalCommunicationTechnicalStatusType getPhysCommunicationStatus() {
		return physCommunicationStatus;
	}

	/**
	 * Sets the phys communication status.
	 *
	 * @param physCommunicationStatus the physCommunicationStatus to set
	 */
	public static void setPhysCommunicationStatus(PhysicalCommunicationTechnicalStatusType physCommunicationStatus) {
		GiForkMain_4F.physCommunicationStatus = physCommunicationStatus;
	}

	/**
	 * Gets the snet domain.
	 *
	 * @return the snetDomain
	 */
	public static TSNETConfiguration getSnetDomain() {
		return snetDomain;
	}

	/**
	 * Sets the snet domain.
	 *
	 * @param snetDomain the snetDomain to set
	 */
	public static void setSnetDomain(TSNETConfiguration snetDomain) {
		GiForkMain_4F.snetDomain = snetDomain;
	}

	/**
	 * Gets the eda surv domain.
	 *
	 * @return the eda surv domain
	 */
	public static AirSurveillanceConfiguration getEdaSurvDomain() {
		return edaSurvDomain;
	}

	/**
	 * Sets the eda surv domain.
	 *
	 * @param edaSurvDomain the new eda surv domain
	 */
	public static void setEdaSurvDomain(AirSurveillanceConfiguration edaSurvDomain) {
		GiForkMain_4F.edaSurvDomain = edaSurvDomain;
	}

	/**
	 * Gets the config dir.
	 *
	 * @return the config dir
	 */
	public static String getConfigDir() {
		return ResourceManager.getResourcesPath();
	}

}
