package service.track;

import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.track.TrackInputConstants;

/**
 * The Class TCASAggregatorService.
 *
 * @author esegato
 * @version $Revision$
 */
public class TCASAggregatorService implements IAggregatorService {

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode  the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {

		final boolean BDS30_RAT = inputJson.getSafeBoolean(TrackInputConstants.BDS30_RAT);
		final boolean BDS30_ARA41 = inputJson.getSafeBoolean(TrackInputConstants.BDS30_ARA41);
		final boolean BDS30_MTE = inputJson.getSafeBoolean(TrackInputConstants.BDS30_MTE);


		if (!BDS30_RAT && BDS30_ARA41) {
			dataNode.addLine("TCAS", "ACAS"); 
			dataNode.addLine("IS_SOUND_ACAS", true);
		} else if (!BDS30_RAT && !BDS30_ARA41 && BDS30_MTE) {
			dataNode.addLine("TCAS", "ACAS"); 
			dataNode.addLine("IS_SOUND_ACAS", true);
		} else {
			dataNode.addLine("TCAS", ""); 
			dataNode.addLine("IS_SOUND_ACAS", false);
		}
	}

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The m service name. */
		String m_serviceName = TrackInputConstants.TCAS;
		return m_serviceName;
	}

}
