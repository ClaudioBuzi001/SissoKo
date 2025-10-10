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

import java.io.IOException;
import java.net.InetAddress;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;

import com.fourflight.Common.TSC.ISMComponents.CommonTSUPClasses.InternalCommonTSUPClasses.L2_InternalManageableComponentControl.TSCOMProcessPhyCommImpl;
import com.fourflight.Common.TSC.ISMData.ManageableComponent.TypeClasses.RedundacyState;
import com.fourflight.Common.TSC.ISMData.ManageableComponent.TypeClasses.RestartWarmth;
import com.fourflight.Common.TSC.ISMData.TECHSUP.CoolDataCategoriesData;
import com.fourflight.Common.TSC.ISMData.TechnicalSupervisionData.ManagedObjectStatus.TypeClasses.PhysicalCommunicationTechnicalStatusType;
import com.fourflight.Common.TSC.ISMData.TechnicalSupervisionData.SharedData.External.ATSUName;
import com.fourflight.Common.TSC.ISMData.TechnicalSupervisionData.SharedData.External.TypeClasses.SupObject;
import com.fourflight.Common.TSC.ISMInterfaces.Transversal.L1_ManageableComponentControlOrders;
import com.fourflight.EDM.EDA.ISMComponents.EDMCommonClasses.ExternalEDMCommonClasses.EDAOnlineCoflightDcpsProxyImpl;
import com.fourflight.EDM.EDA.ISMComponents.EDMCommonClasses.ExternalEDMCommonClasses.EDAOnlineDcpsProxyImpl;
import com.fourflight.EDM.EDA.ISMComponents.EDMCommonClasses.ExternalEDMCommonClasses.ISM_Offline4FConfAccessImpl;
import com.fourflight.EDM.EDA.ISMComponents.EDMCommonClasses.ExternalEDMCommonClasses.ISM_OfflineCoflightConfAccessImpl;
import com.fourflight.EDM.EDA.ISMComponents.EDMCommonClasses.lifeMessage.LifeMessageAccessImpl;
import com.fourflight.EDM.EDA.ISMComponents.EDMCommonClasses.receptaclefactory.CoflightReceptacleAccessor;
import com.fourflight.EDM.EDA.ISMComponents.EDMCommonClasses.receptaclefactory.FourflightReceptacleAccessor;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.AirServerConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.AirSurveillanceConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.CommonDataConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.ExternalObject;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.RPDConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.StateInfoType;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.StringType;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.TSNETConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.WorkingPositionConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.AMADConfiguration;
import com.fourflight.WP.ECI.WpCommon.CdmwTimeAccess;
import com.fourflight.WP.ECI.WpCommon.WpLogger;
import com.gifork.auxiliary.ConfigurationFile;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

import eu.cardamom.CdmwPlatformMngt.AttributeNotFound;
import eu.cardamom.CdmwPlatformMngt.ProcessDelegatePackage.NotReadyToRun;
import javafx.application.Application;

/**
 * The Class ExternalTechSupReceiver_4F.
 *
 * @author Leonardo Company S.p.A.
 * @version XAI_4F_B2_BETA-02_01-01RC3
 */
public class ExternalTechSupReceiver_4F extends TSCOMProcessPhyCommImpl implements L1_ManageableComponentControlOrders {

	/** The Constant logger. */
	private static final ILogger LOGGER = LoggerFactory.CreateLogger(ExternalTechSupReceiver_4F.class);

	/** The Constant ROOT_POA. */
	private static final String ROOT_POA = "RootPOA";

	/** The orb. */
	private ORB orb;

	/**
	 * Instantiates a new external tech sup receiver 4 F.
	 *
	 * @param _orb  the orb
	 * @param _args the args
	 */
	public ExternalTechSupReceiver_4F(ORB _orb, String[] _args) {
		orb = _orb;

//		String configDir = getOptionValue(_args, "--prj");
//		GiForkMain_4F.setConfigDir(configDir);
		GiForkMain_4F.setOrb(this.orb);
	}

	/**
	 * Initialise component.
	 *
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 * @param arg2 the arg 2
	 * @param arg3 the arg 3
	 * @throws java.lang.SecurityException                              the java.lang. security
	 *                                                                  exception
	 * @throws java.lang.RuntimeException                               the java.lang. runtime exception
	 * @throws java.lang.IllegalArgumentException                       the java.lang. illegal argument
	 *                                                                  exception
	 * @throws com.coflight.cpr.env.common.exceptions.ENVProxyException the
	 *                                                                  com.coflight.cpr.env.common.exceptions.
	 *                                                                  ENV proxy exception
	 */
	@Override
	public void initialiseComponent(RestartWarmth arg0, RedundacyState arg1, CoolDataCategoriesData arg2,
			ATSUName arg3) {

		WpLogger.getInstance().init("GI-FORK", null, true, false, null);

		CdmwTimeAccess.getInstance().init(this.orb);

		GiForkMain_4F.setReceiver(this);

		GiForkMain_4F.startService();

		GiForkRdp_4F envObs = new GiForkRdp_4F();
		try {
			envObs.register();
			envObs.switchDDs(envObs.getProp());
		} catch (AttributeNotFound e) {
			GiForkMain_4F.initAccessor();
			envObs.performAction("");
		}

	}

	/**
	 * Gets the option value.
	 *
	 * @param args   the args
	 * @param option the option
	 * @return the option value
	 * @throws RuntimeException the runtime exception
	 */
	public static String getOptionValue(String[] args, String option) throws RuntimeException {
		if (null == args || null == option) {
			RuntimeException ex = new RuntimeException("Bad Parameter: null parameter");
			WpLogger.getInstance().log_ERROR_write(ex);
			throw ex;

		}

		String optionValue = "no";

		for (String arg : args) {

			int npos = arg.indexOf(option);

			if (npos != -1) {

				int posSep = arg.indexOf('=');

				if (posSep == -1) {
					optionValue = "yes";

				} else {
					optionValue = arg.substring(posSep + 1);
				}
			}

		}

		return optionValue;
	}

	/**
	 * Start P communication.
	 *
	 * @param arg0 the arg 0
	 */
	@Override
	public void startPCommunication(SupObject arg0) {
		if (GiForkMain_4F.getEdaCdDomain() != null) {
			for (int i = 0; i < GiForkMain_4F.getEdaCdDomain().getCommonExtObj().getItsExternalObject().size(); i++) {
				ExternalObject obj = GiForkMain_4F.getEdaCdDomain().getCommonExtObj().getItsExternalObject().get(i);
				if (obj != null && arg0.getId().contains(obj.getExternalObjectLogicalName().getValue())) {
					GiForkMain_4F.setPrinterInfo(new PrinterInfo_4F(obj.getExternalObjectLogicalName().getValue(),
							obj.getExternalObjectStandardProtocolAddress().getValue(), arg0));
					testStripPrinterConnectivity();
				}
			}
		}

	}

	/**
	 * Stop component.
	 */
	@Override
	public void stopComponent() {

	}

	/**
	 * Stop P communication.
	 *
	 * @param arg0 the arg 0
	 */
	@Override
	public void stopPCommunication(SupObject arg0) {
		setPComunication(PhysicalCommunicationTechnicalStatusType.STOPPED, arg0);
		GiForkMain_4F.setPhysCommunicationStatus(PhysicalCommunicationTechnicalStatusType.STOPPED);
	}

	/**
	 * Checks if is alive.
	 *
	 * @return true, if is alive
	 */
	@Override
	public boolean isAlive() {
		return true;

	}

	/**
	 * Gets the orb.
	 *
	 * @return the orb
	 */
	@Override
	public ORB getORB() {
		return this.orb;
	}

	/**
	 * Switch component primary backup.
	 */
	@Override
	public void switchComponentPrimaryBackup() {

	}

	/**
	 * Inits the accessor.
	 * CRS FF-3759 AMAD
	 */
	public static void initAccessor() {
		ISM_OfflineCoflightConfAccessImpl.getInstance().init(ConfigurationFile.getIntegerProperties("EXT_COF_DID", 0));
		EDAOnlineCoflightDcpsProxyImpl.getInstance().init(ConfigurationFile.getIntegerProperties("EXT_COF_DID", 0));
		EDAOnlineDcpsProxyImpl.getInstance().init(ConfigurationFile.getIntegerProperties("EXT_FF_DID", 0));

		GiForkMain_4F.setEdaWpDomain(new WorkingPositionConfiguration());
		GiForkMain_4F.setEdaCdDomain(new CommonDataConfiguration());
		GiForkMain_4F.setEdaCommDomain(new AirServerConfiguration());
		GiForkMain_4F.setOfflineRPDConfig(new RPDConfiguration());
		GiForkMain_4F.setSnetDomain(new TSNETConfiguration());
		GiForkMain_4F.setEdaSurvDomain(new AirSurveillanceConfiguration());
		GiForkMain_4F.setAmadWpDomain(new AMADConfiguration());
		try {
			ISM_Offline4FConfAccessImpl.getInstance().accessWorkingPositionData(GiForkMain_4F.getEdaWpDomain());
			ISM_Offline4FConfAccessImpl.getInstance().accessGeneralData(GiForkMain_4F.getEdaCdDomain());
			ISM_Offline4FConfAccessImpl.getInstance().accessCommunicationSupportData(GiForkMain_4F.getEdaCommDomain());
			ISM_Offline4FConfAccessImpl.getInstance()
					.accessAirSurveillanceConfigurationData(GiForkMain_4F.getEdaSurvDomain());
			ISM_Offline4FConfAccessImpl.getInstance()
					.accessRecordingAndPlaybackData(GiForkMain_4F.getOfflineRPDConfig());
			ISM_Offline4FConfAccessImpl.getInstance().accessTSNETConfigurationData(GiForkMain_4F.getSnetDomain());
			ISM_Offline4FConfAccessImpl.getInstance().accessAMADConfigurationData(GiForkMain_4F.getAmadWpDomain());
		} catch (Exception e) {
			LOGGER.logError("Error: ", e);
		}

		LifeMessageAccessImpl.getInstance().initLifeMsgParams(GiForkMain_4F.mainArgs);
		org.omg.CORBA.Object obj;

		if (GiForkMain_4F.getOrb() != null) {
			try {
				obj = GiForkMain_4F.getOrb().resolve_initial_references(ROOT_POA);
				POA rootpoa = POAHelper.narrow(obj);
				LifeMessageAccessImpl.getInstance().registerLifeMsgObservers(rootpoa, GiForkMain_4F.getOrb());
				rootpoa.the_POAManager().activate();

				/*FourflightReceptacleAccessor.getInstance().initService("OPERATIONAL", 0);
				CoflightReceptacleAccessor.getInstance().initService("OPERATIONAL", 0);*/
				StringType systemContext = GiForkMain_4F.getEdaCdDomain().getCommonMngObj().getSystem().getSystemContext();
				StateInfoType systemStateInfo = GiForkMain_4F.getEdaCdDomain().getCommonMngObj().getSystem().getSystemStateInfo();
				String systemState = systemStateInfo.name().equals("OPERATIVE")?"OPERATIONAL":systemStateInfo.name();
				FourflightReceptacleAccessor.getInstance().initService(systemState, Integer.parseInt(systemContext.getValue()));
				CoflightReceptacleAccessor.getInstance().initService(systemState, Integer.parseInt(systemContext.getValue()));
				
			} catch (InvalidName | AdapterInactive e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Test strip printer connectivity.
	 *
	 * @return true, if successful
	 */
	public static boolean testStripPrinterConnectivity() {
		boolean result = false;
		if (GiForkMain_4F.getPrinterInfo() != null) {
			if (isReachable(GiForkMain_4F.getPrinterInfo().getAddress())) {
				GiForkMain_4F.getReceiver().setPComunication(PhysicalCommunicationTechnicalStatusType.OK,
						GiForkMain_4F.getPrinterInfo().getSupObject());
				GiForkMain_4F.setPhysCommunicationStatus(PhysicalCommunicationTechnicalStatusType.OK);
				result = true;
			} else {
				GiForkMain_4F.getReceiver().setPComunication(PhysicalCommunicationTechnicalStatusType.FAILED,
						GiForkMain_4F.getPrinterInfo().getSupObject());
				GiForkMain_4F.setPhysCommunicationStatus(PhysicalCommunicationTechnicalStatusType.FAILED);
				result = false;
			}
		}
		return result;
	}

	/** The Constant PING_TIMEOUT. */
	public static final Integer PING_TIMEOUT = 3000;

	/**
	 * Checks if is reachable.
	 *
	 * @param ip the ip
	 * @return true, if is reachable
	 */
	public static boolean isReachable(String ip) {

		boolean result = false;
		try {
			result = InetAddress.getByName(ip).isReachable(PING_TIMEOUT);
		} catch (IOException e) {
			LOGGER.logError("isReachable", e);

		}

		return result;
	}

	/**
	 * On run.
	 *
	 * @throws NotReadyToRun            the not ready to run
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	@Override
	public void onRun() throws NotReadyToRun, IllegalArgumentException {
		super.onRun();
		new Thread(() -> Application.launch(GiForkMain_4F.class)).start();

		synchronized (GiForkMain_4F.getReady()) {
			try {
				GiForkMain_4F.getReady().wait();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}

}
