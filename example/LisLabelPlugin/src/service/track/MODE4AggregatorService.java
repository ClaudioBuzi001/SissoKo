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
package service.track;

import application.pluginService.ServiceExecuter.IAggregatorService;
import auxiliary.track.TrackInputConstants;
import com.fourflight.WP.ECI.edm.HeaderNode;
import com.gifork.commons.data.IRawData;
import com.gifork.commons.log.LoggerFactory;
import com.leonardo.infrastructure.log.ILogger;

/**
 * The Class MODE4AggregatorService.
 */
public class MODE4AggregatorService implements IAggregatorService {
	
	/** The Constant logger. */
	private static final ILogger logger = LoggerFactory.CreateLogger(MODE4AggregatorService.class);

	/** The mode 4 map. */
	/*
	 * Traduzione MODE_4 mode-4 reply |00| = not interrogated |01| = friend |10| =
	 * unknown |11| = no reply
	 */
	private final String[] mode4map = { "NOT INTER", "FRIEND", "UNKNOWN", "NO REPLY" };

	/**
	 * Aggregate.
	 *
	 * @param inputJson the input json
	 * @param dataNode the data node
	 */
	@Override
	public void aggregate(final IRawData inputJson, final HeaderNode dataNode) {
		final String loggerCaller = "execute()";
		String mode4value = " "; 
		try {
			mode4value = mode4map[inputJson.getSafeInt("MODE_4")];
		} catch (final Exception e) {
			logger.logDebug(loggerCaller, "MODE_4 value not valid [allowed 0-3]: " + inputJson.getSafeString("MODE_4"));
		}

		dataNode.addLine(TrackInputConstants.MODE_4, mode4value);
    }

	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	@Override
	public String getServiceName() {
		/** The service name. */
		String m_serviceName = "MODE4";
		return m_serviceName;
	}

}
