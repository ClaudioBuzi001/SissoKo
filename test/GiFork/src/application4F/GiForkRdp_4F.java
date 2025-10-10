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

import com.fourflight.EDM.EDA.ISMComponents.EDMCommonClasses.ExternalEDMCommonClasses.EDAOnlineCoflightDcpsProxyImpl;
import com.fourflight.EDM.EDA.ISMComponents.EDMCommonClasses.ExternalEDMCommonClasses.EDAOnlineDcpsProxyImpl;
import com.fourflight.EDM.EDA.ISMComponents.EDMCommonClasses.ExternalEDMCommonClasses.ISM_Offline4FConfAccessImpl;
import com.fourflight.EDM.EDA.ISMComponents.EDMCommonClasses.ExternalEDMCommonClasses.ISM_OfflineCoflightConfAccessImpl;
import com.fourflight.EDM.EDA.ISMComponents.EDMCommonClasses.InternalEDMCommonClasses.ISM_InternalEDM.ISM_Internal_Offline4FConfAccess.logger.InitializationException;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.AirServerConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.AirSurveillanceConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.CommonDataConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.RPDConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.TSNETConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.WorkingPositionConfiguration;
import com.fourflight.EDM.EDA.ISMData.System_Data_Physical_Model.AMADConfiguration;
import com.leonardo.infrastructure.Strings;
import com.leonardo.infrastructure.messaging.Messenger;

/**
 * The Class GiForkRdp_4F.
 */
public class GiForkRdp_4F extends SupervisionObserverWPImpl_4F {

	/** The th as. */
	private Thread thAs;

	/**
	 * Perform action.
	 *
	 * @param prop the prop
	 */
	@Override
	public void performAction(String prop) {

		thAs = new Thread(() -> switchDDs(prop));
		thAs.start();
	}

	/**
	 * Switch Dds.
	 *
	 * @param value the value
	 */
	public void switchDDs(String value) {
		
        GiForkMain_4F.setEdaCdDomain(new CommonDataConfiguration());
        try {
            ISM_Offline4FConfAccessImpl.getInstance().accessGeneralData(GiForkMain_4F.getEdaCdDomain());
        } catch (InitializationException e) {
            e.printStackTrace();
        }
		
		if (!value.isEmpty() && !value.contains("DefaultSession")) {
			String sessionId = value.split("\\|")[0];
			String sessionDomainCOF = Strings.concat("COF_EXTERNAL_" , sessionId.toUpperCase() , "_DDS_DOMAIN");
			int domainIdCOF = Integer.parseInt(System.getenv(sessionDomainCOF));
			String sessionDomainFF = Strings.concat("FF_EXTERNAL_" , sessionId.toUpperCase() , "_DDS_DOMAIN");
			int domainIdFF = Integer.parseInt(System.getenv(sessionDomainFF));
			GiForkMain_4F.setEdaWpDomain(new WorkingPositionConfiguration());
			GiForkMain_4F.setEdaCdDomain(new CommonDataConfiguration());
			GiForkMain_4F.setEdaCommDomain(new AirServerConfiguration());
			GiForkMain_4F.setOfflineRPDConfig(new RPDConfiguration());
			GiForkMain_4F.setSnetDomain(new TSNETConfiguration());
			GiForkMain_4F.setEdaSurvDomain(new AirSurveillanceConfiguration());
			GiForkMain_4F.setAmadWpDomain(new AMADConfiguration());
			try {
				ISM_Offline4FConfAccessImpl.getInstance().accessGeneralData(GiForkMain_4F.getEdaCdDomain());
				ISM_Offline4FConfAccessImpl.getInstance().accessWorkingPositionData(GiForkMain_4F.getEdaWpDomain());
				ISM_Offline4FConfAccessImpl.getInstance()
						.accessCommunicationSupportData(GiForkMain_4F.getEdaCommDomain());
				ISM_Offline4FConfAccessImpl.getInstance()
						.accessAirSurveillanceConfigurationData(GiForkMain_4F.getEdaSurvDomain());
				ISM_Offline4FConfAccessImpl.getInstance()
						.accessRecordingAndPlaybackData(GiForkMain_4F.getOfflineRPDConfig());
				ISM_Offline4FConfAccessImpl.getInstance().accessTSNETConfigurationData(GiForkMain_4F.getSnetDomain());
				ISM_Offline4FConfAccessImpl.getInstance().accessAMADConfigurationData(GiForkMain_4F.getAmadWpDomain());				
			} catch (InitializationException e) {
				e.printStackTrace();
			}

			ISM_OfflineCoflightConfAccessImpl.getInstance().init(domainIdCOF);
			EDAOnlineCoflightDcpsProxyImpl.getInstance().init(domainIdCOF);
			EDAOnlineDcpsProxyImpl.getInstance().init(domainIdFF);

		}
		Messenger.send(new SwitchPlayback_4F(this, value));
	}

}
